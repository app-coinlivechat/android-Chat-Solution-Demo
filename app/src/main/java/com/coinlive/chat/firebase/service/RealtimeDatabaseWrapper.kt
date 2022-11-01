package com.coinlive.chat.firebase.service

import com.coinlive.chat.firebase.CoinliveChat
import com.coinlive.chat.firebase.model.Ama
import com.coinlive.chat.firebase.model.Cm
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


interface CmNoticeListener{
    fun getCmNotice(msg:String?)
}

interface AmaListener{
    fun getAma(ama:Ama?)
}

/**
 * Ama 진행 상태와 CM 공지사항을 실시간으로 받아오기 위한 클래스 입니다.
 * RealTimeDatabase를 사용하기 위해서는 [CoinliveAuthentication.signIn] 을 선행애햐 합니다.
 */
class RealtimeDatabaseWrapper(coinId: String, val cmListener: CmNoticeListener, val amaListener: AmaListener) {
    companion object {
        private val BASE_PATH = if (CoinliveChat.isDebug) "clc-dev" else "clc-prod"
    }

    private val amaRef: DatabaseReference = Firebase.database.reference.child("$BASE_PATH/ama/$coinId")
    private val cmRef: DatabaseReference = Firebase.database.reference.child("$BASE_PATH/cm/$coinId")

    var ama: Ama? = null

    init {
        initCm()
        initAma()
    }

    fun close() {
        amaRef.removeEventListener(amaValueEventListener)
        cmRef.removeEventListener(cmValueEventListener)
    }


    /**
     * CM 공지사항을 로드하고 데이터를 [CmNoticeListener.getCmNotice]를 통해 전달합니다.
     */
    private fun initCm() {

        val query = cmRef.orderByChild("t").limitToFirst(1)
        query.get().addOnSuccessListener {
            val cm = it.getValue<Cm>()
            cmListener.getCmNotice(cm?.message)
        }
        query.addValueEventListener(cmValueEventListener)
    }

    private val cmValueEventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            val cm = snapshot.getValue<Cm>()
            cmListener.getCmNotice(cm?.message)
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    /**
     * AMA 상태를 로드하고 데이터를 [AmaListener.getAma] 를 통해 전달합니다.
     *
     */
    private fun initAma(){
        val query  = amaRef.limitToFirst(1)
        query.get().addOnSuccessListener {
            val ama = it.getValue<Ama>()
            this.ama = ama
            amaListener.getAma(ama)
        }

        query.addValueEventListener(amaValueEventListener)
    }

    private val amaValueEventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            val ama = snapshot.getValue<Ama>()
            this@RealtimeDatabaseWrapper.ama = ama
            amaListener.getAma(ama)
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

}