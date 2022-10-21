package com.coinlive.chat.firebase.service

import com.coinlive.chat.firebase.CoinliveChat
import com.coinlive.chat.firebase.MessageListener
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.util.CalendarHelper
import com.coinlive.chat.util.LoggerHelper
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

interface SendFailListener {
    fun sendFail(chat: Chat)
}

/**
 * coin 채팅을 받아오기 위한 클래스 입니다.
 * Firestore 를 사용하기 위해서는 [Authentication.signIn] 을 선행애햐 합니다.
 */
class Firestore(val coinId: String, val listener: MessageListener) {
    companion object {
        private val BASE_PATH = if (CoinliveChat.isDebug) "clc-dev" else "clc-prod"
    }


    private var documentSnapshotList: ArrayList<DocumentSnapshot> = ArrayList()
    private var existCollectionSnapshot: ArrayList<String> = ArrayList()
    private var initTimeStamp: Calendar? = null
    private var notificationMap: Map<String, Boolean>? = null


    init {
        initTimeStamp = CalendarHelper.getTodayMidnight()
        startMidnightCheck()
    }

    /**
     * 자정 체크 스레드 생성 및 시작
     */
    private fun startMidnightCheck() {
        //TODO 스레드 생성
        // 비교 후 collection 연경
        val now = CalendarHelper.getTodayMidnight()
        val collectionPath = "$BASE_PATH/${now.timeInMillis}/$coinId"
        Firebase.firestore.collection(collectionPath).addSnapshotListener { snapshots, e ->
            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        //신규 메세지
//                        listener.newMessages()
                    }
                    DocumentChange.Type.MODIFIED -> {
                        if (documentSnapshotList.contains(dc.document)) {
//                        listener.modifyMessage()
                        }
                    }
                    DocumentChange.Type.REMOVED -> {
                        if (documentSnapshotList.contains(dc.document)) {
                            //                        listener.removeMessage()

                        }

                    }
                }
            }
        }

        existCollectionSnapshot.add(collectionPath)
    }


    /**
     * 자정 체크 스레드 종료 및 초기화
     */
    private fun endMidnightCheck() {}

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
        diffSize: Long = 50
    ) {
        this.notificationMap = notificationMap

        val collectionPath = "$BASE_PATH/${standardTime.timeInMillis}/$coinId"
        val collection = Firebase.firestore.collection(collectionPath)

        val query = collection.limitToLast(standardSize).orderBy("t", Query.Direction.DESCENDING).limit(standardSize)
        if (documentSnapshotList.size > 0) {
            query.startAfter(documentSnapshotList.last())
        }


        query.get().addOnSuccessListener {
            if (it.size() == 0) {    // standardTime 에 더이상 불러올 데이터가 없으니 이전 날짜의 데이터를 불러온다.
                standardTime.set(Calendar.DATE, standardTime.get(Calendar.DATE) - 1)
                val now = CalendarHelper.nowCalendar()

                if (now.get(Calendar.DATE) - standardTime.get(Calendar.DATE) <= 7) {
                    fetchMessage(standardSize, notificationMap, standardTime)
                } else {
                    // 7일치 데이터 모두 검색 후 더이상 보내줄 데이터가 없을 경우
//                    listener.oldMessages()
                }
            }

            documentSnapshotList.addAll(it)

            //TODO filtering
//            it.filter {  }

            if (it.size() < diffSize) {
                fetchMessage(standardSize, notificationMap, standardTime, diffSize - it.size())
            } else {
//                listener.oldMessages()
            }
        }

        if (!existCollectionSnapshot.contains(collectionPath)) {
            collection.addSnapshotListener { snapshots, e ->
                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            //TODO filtering & 신규 메세지
//                        listener.newMessages()
                            documentSnapshotList.add(dc.document)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            if (documentSnapshotList.contains(dc.document)) {
//                        listener.modifyMessage()
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            if (documentSnapshotList.contains(dc.document)) {
                                // listener.removeMessage()
                                documentSnapshotList.remove(dc.document)

                            }

                        }
                    }
                }
            }
            existCollectionSnapshot.add(collectionPath)
        }

    }


    fun reload(notificationMap: Map<String, Boolean>) {
        this.notificationMap = notificationMap

        //TODO filter -> sort
    }

    fun sort() {

    }

    fun sendMessage(chat:Chat, failListener: SendFailListener) {
        chat.messageId?.let {
            Firebase.firestore.collection("$BASE_PATH/${CalendarHelper.getTodayMidnightTimeStamp()}/$coinId")
                .document(it).set(chat).addOnFailureListener {
                    listener.failSendMessage(chat)
                    failListener.sendFail(chat)
                }
        } ?: run {
            LoggerHelper.de("chat messageId is null")
        }
    }

    fun getServerTimeStamp() :FieldValue {
        return FieldValue.serverTimestamp()
    }



//    private val listener : object = object : EventListener {
//
//    }


}