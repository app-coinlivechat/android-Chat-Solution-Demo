package com.coinlive.chat.firebase.service

import com.coinlive.chat.api.model.enum.CoinNotiType
import com.coinlive.chat.firebase.CoinliveChat
import com.coinlive.chat.firebase.`interface`.MessageListener
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.firebase.model.enum.MessageType
import com.coinlive.chat.util.CalendarHelper
import com.coinlive.chat.util.LoggerHelper
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

interface SendEventListener {
    fun fail(chat: Chat)
    fun success(chat: Chat)
}

class FirestoreWrapper(val coinId: String, private val listener: MessageListener) :
    EventListener<QuerySnapshot> {
    companion object {
        private val BASE_PATH = if (CoinliveChat.isDebug) "clc-dev" else "clc-prod"
    }


    private var documentSnapshotList: ArrayList<DocumentSnapshot> = ArrayList()
    private var existCollectionSnapshot: ArrayList<String> = ArrayList()
    private var initTimeStamp: Calendar? = null
    private var notificationMap: Map<String, Boolean>? = null
    private var timer: Timer? = null


    init {
        initTimeStamp = CalendarHelper.getTodayMidnight()
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
        }, 100 * 60, 100 * 60)
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

        val collectionPath = "$BASE_PATH/${standardTime.timeInMillis}/$coinId"
        val collection = Firebase.firestore.collection(collectionPath)

        val query = collection.limitToLast(standardSize).orderBy("t", Query.Direction.DESCENDING).limit(standardSize)
        if (documentSnapshotList.size > 0) {
            query.startAfter(documentSnapshotList.last())
        }

        query.get().addOnSuccessListener {
            val documents = it.documents
            if (documents.size == 0) {    // standardTime 에 더이상 불러올 데이터가 없으니 이전 날짜의 데이터를 불러온다.
                standardTime.set(Calendar.DATE, standardTime.get(Calendar.DATE) - 1)
                val now = CalendarHelper.nowCalendar()

                if (now.get(Calendar.DATE) - standardTime.get(Calendar.DATE) <= 7) {
                    fetchMessage(standardSize, notificationMap, standardTime)
                }
            }

            documentSnapshotList.addAll(documents)
            filter(documents)
            val oldMessage = toChatList(documents)
            sort(oldMessage)

            listener.oldMessages(oldMessage, false)

            if (oldMessage.size < diffSize) {
                fetchMessage(standardSize, notificationMap, standardTime, diffSize - oldMessage.size)
            }
        }

        if (!existCollectionSnapshot.contains(collectionPath)) {
            collection.addSnapshotListener(this)
            existCollectionSnapshot.add(collectionPath)
        }
    }

    fun reload(notificationMap: Map<String, Boolean>) {
        this.notificationMap = notificationMap

        val documents = documentSnapshotList.toList()
        filter(documents)
        val oldMessage = toChatList(documents)
        sort(oldMessage)
        listener.oldMessages(oldMessage, true)
    }

    private fun toChatList(documents: List<DocumentSnapshot>): ArrayList<Chat> {
        val oldMessage = ArrayList<Chat>()
        documents.forEach { documentSnapshot ->
            val chat = convertChat(documentSnapshot) ?: return@forEach
            oldMessage.add(chat)
        }
        return oldMessage
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
            changeInsertTime = chat.st.toDate().time
            LoggerHelper.d("t : ${chat.insertTime}, st : $changeInsertTime")
        }

        if (changeId != null || changeInsertTime != null) {
            return chat.copy(messageId = changeId ?: chat.messageId, insertTime = changeInsertTime ?: chat.insertTime)
        }

        return chat
    }

    private fun sort(chat: List<Chat>) {
        chat.sortedWith(compareBy<Chat> { it.insertTime }.thenBy { it.symbol })
    }

    private fun filter(documents: List<DocumentSnapshot>) {
        val chatNotiMap = getChatNotiMap()
        documents.filter { documentSnapshot ->
            val chat: Chat = convertChat(documentSnapshot) ?: return@filter false
            isShowMessage(chat, chatNotiMap)
        }
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
            LoggerHelper.de("startMidnightCheck FirebaseFirestoreException\ncode : ${error.code}, msg : ${error.message}")
        } else if (snapshots != null) {
            for (dc in snapshots.documentChanges) {
                val document = dc.document
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        convertChat(document)?.let {
                            if (isShowMessage(it, getChatNotiMap())) {
                                listener.newMessages(it)
                            }
                        }
                        documentSnapshotList.add(document)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        if (documentSnapshotList.contains(document)) {
                            convertChat(document)?.let {
                                listener.modifyMessage(it)
                            }

                        }
                    }
                    DocumentChange.Type.REMOVED -> {
                        if (documentSnapshotList.contains(document)) {
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
}