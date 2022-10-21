package com.coinlive.chat.api

import com.coinlive.chat.api.model.Coin
import com.coinlive.chat.api.repository.*
import com.coinlive.chat.exception.CoinliveException
import com.coinlive.chat.exception.FirebaseIdTokenException
import com.coinlive.chat.firebase.service.Authentication

class CoinliveRestApi {
    private val coinRepo = CoinRepository();
    private val channelRepo = ChannelRepository();
    private val chattingMemberRepo = ChattingMemberRepository();
    private val memberRepo = MemberRepository();
    private val notificationRepo = NotificationRepository();
    private val uploadRepo = UploadRepository();


    /**
     * @param[coinId] 코인라이브에서 받아온 코인 uuid
     * @param[callback] 성공,실패 callback,
     */
    suspend fun getCoin(coinId: String, callback: ResponseCallback<Coin>) {
        try {
            val coin = coinRepo.getCoin(coinId, getAuth())
            callback.onSuccess(coin)
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }





    private suspend fun getAuth(): String {
        return Authentication.getFirebaseToken() ?: throw FirebaseIdTokenException("getAuth error")
    }


}