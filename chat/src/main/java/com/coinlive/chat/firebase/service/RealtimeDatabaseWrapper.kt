package com.coinlive.chat.firebase.service

import com.coinlive.chat.Coinlive
import com.coinlive.chat.firebase.listener.AmaListener
import com.coinlive.chat.firebase.listener.CmNoticeListener
import com.coinlive.chat.firebase.model.Ama
import com.coinlive.chat.firebase.model.Cm
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class RealtimeDatabaseWrapper(coinId: String, val cmListener: CmNoticeListener, val amaListener: AmaListener) {
    companion object {
        private val BASE_PATH = if (Coinlive.isDebug) "clc-dev" else "clc-prod"
    }

    private val amaRef: DatabaseReference = Firebase.database.reference.child("$BASE_PATH/ama/$coinId")
    private val cmRef: DatabaseReference = Firebase.database.reference.child("$BASE_PATH/cm/$coinId")

    var ama: Ama? = null

    private val cmValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if(!snapshot.hasChildren()) return

            val cm = snapshot.children.first().getValue<Cm>() ?: return
            cmListener.getCmNotice(cm.message)
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    private val amaValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            if(!snapshot.hasChildren()) return

            val ama = snapshot.children.first().getValue<Ama>() ?: return
            this@RealtimeDatabaseWrapper.ama = ama
            amaListener.getAma(ama)
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

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
        cmRef.orderByChild("t").limitToLast(1).addValueEventListener(cmValueEventListener)
    }

    /**
     * AMA 상태를 로드하고 데이터를 [AmaListener.getAma] 를 통해 전달합니다.
     *
     */
    private fun initAma() {
        amaRef.limitToLast(1).addValueEventListener(amaValueEventListener)
    }
}