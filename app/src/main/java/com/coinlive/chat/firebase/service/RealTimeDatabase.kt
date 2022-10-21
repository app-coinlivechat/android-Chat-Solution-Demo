package com.coinlive.chat.firebase.service

import com.coinlive.chat.firebase.CoinliveChat
import com.coinlive.chat.firebase.model.Ama
import com.coinlive.chat.firebase.model.Cm
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase


interface CmNoticeListener{
    fun getCmNotice(msg:String)
}

/**
 * Ama 진행 상태와 CM 공지사항을 실시간으로 받아오기 위한 클래스 입니다.
 * RealTimeDatabase를 사용하기 위해서는 [Authentication.signIn] 을 선행애햐 합니다.
 */
class RealTimeDatabase(coinId: String,val listener: CmNoticeListener) {
    companion object {
        private val BASE_PATH = if (CoinliveChat.isDebug) "clc-dev" else "clc-prod"
    }

    private val cmRef: DatabaseReference = Firebase.database.reference.child("$BASE_PATH/ama/$coinId")
    private val amaRef: DatabaseReference = Firebase.database.reference.child("$BASE_PATH/cm/$coinId")

    var ama: Ama? = null

    init {
        initCm()
        initAma()
    }


    /**
     * CM 공지사항의 Query 를 전달합니다.
     * @return[Query] CM 공지사항의 Query를 전달합니다.
     */
    private fun initCm() {
        cmRef.orderByChild("t").limitToFirst(1).addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
//                    val post = snapshot.value<Cm>
//                    listener.getCmNotice()
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
        )
    }

    /**
     *
     *
     */
    private fun initAma(){
        amaRef.limitToFirst(1).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


}