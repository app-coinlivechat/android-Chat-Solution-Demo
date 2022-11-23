package com.coinlive.chat.firebase.service

import com.coinlive.chat.Coinlive
import com.coinlive.chat.api.model.enums.CoinNotiType
import com.coinlive.chat.exception.Error
import com.coinlive.chat.firebase.listener.MessageListener
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.enum.MessageType
import com.coinlive.chat.util.CalendarHelper
import com.coinlive.chat.util.LoggerHelper
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

interface SendEventListener {
    fun fail(chat: Chat)
    fun success(chat: Chat)
}

class FirestoreWrapper(private val coinId: String, private val listener: MessageListener) :
    EventListener<QuerySnapshot> {
    companion object {
        private val BASE_PATH = if (Coinlive.isDebug) "msg-dev" else "msg-prod"
    }

    private var documentSnapshotList: ArrayList<DocumentSnapshot> = ArrayList()
    private var existCollectionSnapshot: ArrayList<String> = ArrayList()
    private var initTimeStamp: Calendar? = null
    private var notificationMap: Map<String, Boolean>? = null
    private var timer: Timer? = null
    private var isLoading: Boolean = false


    init {
        initTimeStamp = CalendarHelper.getTodayMidnight()
        Firebase.firestore.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
            cacheSizeBytes = FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
        }
        startMidnightCheck()
    }

    /**
     * 자정 체크 스레드 생성 및 시작
     */
    private fun startMidnightCheck() {
        if (timer != null) {
            endMidnightCheck()
        }

        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                if (initTimeStamp == null) return
                val now = CalendarHelper.getTodayMidnight()

                if (initTimeStamp!!.time.after(now.time)) {
                    val collectionPath = "$BASE_PATH/${now.timeInMillis}/$coinId"
                    Firebase.firestore.collection(collectionPath).addSnapshotListener(this@FirestoreWrapper)
                    existCollectionSnapshot.add(collectionPath)
                }
            }
        }, 1000 * 60, 1000 * 60)
    }


    /**
     * 자정 체크 스레드 종료 및 초기화
     */
    private fun endMidnightCheck() {
        timer?.cancel()
        timer = null
    }

    /**
     * Firestore 해제?! 할떼
     */
    fun close() {
        endMidnightCheck()
        documentSnapshotList.clear()
        existCollectionSnapshot.clear()
        initTimeStamp = null
        notificationMap = null
    }

    fun fetchMessage(
        standardSize: Long = 50,
        notificationMap: Map<String, Boolean>,
        standardTime: Calendar = CalendarHelper.getTodayMidnight(),
        diffSize: Long = 50,
    ) {
        this.notificationMap = notificationMap

        if (isLoading) return
        isLoading = true

        val now = CalendarHelper.nowCalendar()
        val result = now.get(Calendar.DATE) - standardTime.get(Calendar.DATE)
        if (result in 0..7) {
            LoggerHelper.d("day diff : $result")
            var queryCalendar: Calendar = standardTime.clone() as Calendar
            var collectionPath = "$BASE_PATH/${standardTime.timeInMillis}/$coinId"
            var collection: CollectionReference = Firebase.firestore.collection(collectionPath)
            var query: Query = collection.orderBy("t", Query.Direction.DESCENDING).limit(standardSize)

            if (documentSnapshotList.size > 0) {
                val lastDocument = documentSnapshotList.last()
                val lastChat = convertChat(lastDocument)
                val lastChatCalendar = CalendarHelper.getMidnightCalendarByMillis(lastChat!!.insertTime)

                if (lastChatCalendar.timeInMillis < standardTime.timeInMillis) {
                    queryCalendar = lastChatCalendar.clone() as Calendar
                    collectionPath = "$BASE_PATH/${lastChatCalendar.timeInMillis}/$coinId"
                    collection = Firebase.firestore.collection(collectionPath)
                    query =
                        collection.limit(standardSize).orderBy("t", Query.Direction.DESCENDING).startAfter(lastDocument)
                } else {
                    query =
                        collection.limit(standardSize).orderBy("t", Query.Direction.DESCENDING).startAfter(lastDocument)
                }
            }

            query.get().addOnSuccessListener {
                val documents = it.documents
                if (documents.size == 0) {    // standardTime 에 더이상 불러올 데이터가 없으니 이전 날짜의 데이터를 불러온다.
                    queryCalendar.set(Calendar.DATE, queryCalendar.get(Calendar.DATE) - 1)
                    isLoading = false
                    fetchMessage(standardSize, notificationMap, queryCalendar)
                    return@addOnSuccessListener
                }

                documentSnapshotList.addAll(documents)
                val oldMessage = filterWithConvert(documents)
                sort(oldMessage)

                listener.oldMessages(oldMessage, false)

                if (!existCollectionSnapshot.contains(collection.path)) {
                    collection.addSnapshotListener(this)
                    existCollectionSnapshot.add(collection.path)
                }

                if (oldMessage.size < diffSize) {
                    isLoading = false
                    fetchMessage(standardSize, notificationMap, queryCalendar, diffSize - oldMessage.size)
                    return@addOnSuccessListener
                } else {
                    isLoading = false
                }
            }.addOnFailureListener {
                isLoading = false
                LoggerHelper.de("FirestoreWrapper.fetchMessage error!!\n" +
                        "${Error.QUERY_FETCH_MESSAGE.code}, ${Error.QUERY_FETCH_MESSAGE.msg}\n" +
                        "${it.message}")
            }
        } else {
            listener.oldMessages(arrayListOf(), false)
            isLoading = false
        }
    }

    fun reload(notificationMap: Map<String, Boolean>) {
        this.notificationMap = notificationMap

        val documents = documentSnapshotList.toList()
        val oldMessage = filterWithConvert(documents)
        sort(oldMessage)
        listener.oldMessages(oldMessage, true)
    }


    private fun convertChat(document: DocumentSnapshot): Chat? {
        val chat: Chat = document.toObject<Chat>() ?: return null
        var changeId: String? = null
        var changeInsertTime: Long? = null
        if (document.id != chat.messageId) { // document key와 맞추기 위한 로직 (server demon에서 추가된 메세지들은 messageId가 없기 때문에
            // Chat 모델에서 자동 생성된다.)
            changeId = document.id
        }

        if (chat.st != null) {
            changeInsertTime = chat.st!!.toDate().time
        }

        if (changeId != null || changeInsertTime != null) {
            return chat.copy(messageId = changeId ?: chat.messageId, insertTime = changeInsertTime ?: chat.insertTime)
        }

        return chat
    }

    private fun sort(chat: List<Chat>) {
        chat.sortedByDescending { it.insertTime }
    }

    private fun filterWithConvert(documents: List<DocumentSnapshot>): ArrayList<Chat> {
        val chatNotiMap = getChatNotiMap()

        val oldMessage = ArrayList<Chat>()

        documents.forEach { documentSnapshot ->
            val chat: Chat = convertChat(documentSnapshot) ?: return@forEach
            val isShow = isShowMessage(chat, chatNotiMap)
            if (isShow) {
                oldMessage.add(chat)
            }

        }
        return oldMessage
    }

    private fun getChatNotiMap(): Map<String, Boolean>? {
        return this.notificationMap?.filterKeys { key ->
            key == CoinNotiType.CHAT_TWITTER.name || key == CoinNotiType.CHAT_MEDIUM.name || key == CoinNotiType.CHAT_LIQUIDATION.name
        }
    }

    private fun isShowMessage(chat: Chat, chatNotiMap: Map<String, Boolean>?): Boolean {
        if (chatNotiMap == null) return true

        when (chat.messageType) {
            MessageType.TWITTER.toLowName() -> {
                return chatNotiMap[CoinNotiType.CHAT_TWITTER.name] ?: true
            }
            MessageType.MEDIUM.toLowName() -> {
                return chatNotiMap[CoinNotiType.CHAT_MEDIUM.name] ?: true
            }
            MessageType.BUY.toLowName(), MessageType.SELL.toLowName() -> {
                return chatNotiMap[CoinNotiType.CHAT_LIQUIDATION.name] ?: true
            }
        }
        return true
    }

    fun sendMessage(chat: Chat, sendEventListener: SendEventListener, isRetry: Boolean = false) {
        Firebase.firestore.collection("$BASE_PATH/${CalendarHelper.getTodayMidnightTimeStamp()}/$coinId")
            .document(chat.messageId).set(chat).addOnFailureListener {
                listener.failSendMessage(chat)
                sendEventListener.fail(chat)
            }.addOnSuccessListener {
                sendEventListener.success(chat)
                if (isRetry) listener.retrySendMessageSuccess(chat.messageId)
            }
    }

    fun deletedMessage(chat: Chat) {
        val insertPath = CalendarHelper.getMidnightTimeStampByMillis(chat.insertTime)
        Firebase.firestore.collection("$BASE_PATH/$insertPath/$coinId").document(chat.messageId).delete()
    }

    fun updateEmoji(chat: Chat) {
        val insertPath = CalendarHelper.getMidnightTimeStampByMillis(chat.insertTime)
        Firebase.firestore.collection("$BASE_PATH/$insertPath/$coinId").document(chat.messageId).set(chat)
    }

    fun getServerTimeStamp(): Timestamp {
        return Timestamp.now()
    }

    override fun onEvent(snapshots: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            LoggerHelper.de("FirebaseFirestoreException\ncode : ${error.code}, msg : ${error.message}")
        } else if (snapshots != null) {
            for (dc in snapshots.documentChanges) {
                val document = dc.document
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        val newTime: Long = document.data["t"] as Long
                        val firstTime: Long = if (documentSnapshotList.size > 0) documentSnapshotList.first()["t"] as
                                Long else 0
                        if (firstTime < newTime) {
                            convertChat(document)?.let {
                                if (isShowMessage(it, getChatNotiMap())) {
                                    listener.newMessages(it)
                                }
                            }
                            documentSnapshotList.add(0, document)
                        }

                    }
                    DocumentChange.Type.MODIFIED -> {
                        if (isExistDocument(document)) {
                            convertChat(document)?.let {
                                listener.modifyMessage(it)
                            }
                        }
                    }
                    DocumentChange.Type.REMOVED -> {
                        if (isExistDocument(document)) {
                            convertChat(document)?.let {
                                listener.deletedMessage(it)
                                documentSnapshotList.remove(document)
                            }

                        }

                    }
                }
            }
        }
    }

    private fun isExistDocument(document: QueryDocumentSnapshot): Boolean {
        var isExist = false
        documentSnapshotList.forEach {
            if (it.id == document.id) {
                isExist = true
                return@forEach
            }
        }
        return isExist
    }

}