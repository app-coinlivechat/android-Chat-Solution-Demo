package com.coinlive.chat.api

import com.coinlive.chat.api.model.*
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
    fun getCoin(coinId: String, callback: ResponseCallback<Coin>) {
        try {
            val coin = coinRepo.getCoin(coinId, getAuth())
            callback.onSuccess(coin)
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun getUserCount(coinId: String,callback: ResponseCallback<UserCount>) {
        try {
            callback.onSuccess(channelRepo.getUserCount(coinId, getAuth()))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun userJoin(coinId: String, callback: ResponseCallback<UserCount>) {
        try {
            callback.onSuccess(channelRepo.userJoin(coinId, getAuth()))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun userLeave(coinId: String, callback: ResponseCallback<UserCount>) {
        try {
            callback.onSuccess(channelRepo.userLeave(coinId, getAuth()))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun getChannelList(apiKey: String, callback: ResponseCallback<List<Channel>>) {
        try {
            callback.onSuccess(chattingMemberRepo.getChannelList(apiKey))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun getCustomerInfo(apiKey: String, callback: ResponseCallback<Customer>) {
        try {
            callback.onSuccess(chattingMemberRepo.getCustomerInfo(apiKey))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun getCustomerMemberInfo(callback: ResponseCallback<CustomerUser>) {
        try {
            callback.onSuccess(chattingMemberRepo.getCustomerMemberInfo(getAuth()))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun customerUserSignUp(user: CustomerUserSignUpBody,callback: ResponseCallback<CustomerUserSignUp>) {
        try {
            callback.onSuccess(chattingMemberRepo.customerUserSignUp(getAuth(),user))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    /**
     * 사용자를 Firebase Authentication에 로그인하고 coinlive에 가입하기 위해 Firebase Authentication [CustomerUserSignUp]을
     * 전달 받습니다.
     * @param[apiKey] coinlive에서 전달 받은 customerApiKey
     * @param[uuid] 사용자 uuid
     * @return[CustomerUserSignUp] 사용자 uuid를 이용하여 Firebase Authentication에 로그인 할 수 있는 정보를 전달합니다.
     * 전달받은 데이터를 이용하여 [Authentication.signIn] 또는 [customerUserSignUp]을 이용하는데 사용하십시요.
     */
    fun getCustomToken(apiKey: String, uuid: String,callback: ResponseCallback<CustomerUserSignUp>) {
        try {
            callback.onSuccess(chattingMemberRepo.getCustomToken(apiKey,uuid))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun isAvailableNickName(nickName: String, customerId:String,callback: ResponseCallback<String>) {
        try {
            callback.onSuccess(memberRepo.isAvailableNickName(nickName,customerId))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun setNickName(nickName: String, customerId:String,callback: ResponseCallback<String>) {
        try {
            callback.onSuccess(memberRepo.setNickName(nickName,getAuth(),customerId))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun signupCheck(firebaseUuid: String,callback: ResponseCallback<MemberSignupCheck>) {
        try {
            callback.onSuccess(memberRepo.signupCheck(firebaseUuid))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun setBasicProfile(callback: ResponseCallback<Upload>) {
        try {
            callback.onSuccess(memberRepo.setBasicProfile(getAuth()))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun setNotification(coinId: String, notiType: String,callback: ResponseCallback<Boolean>) {
        try {
            callback.onSuccess(notificationRepo.setNotification(getAuth(),coinId,notiType))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun deleteNotification(coinId: String, notiType: String,callback: ResponseCallback<Boolean>) {
        try {
            callback.onSuccess(notificationRepo.deleteNotification(getAuth(),coinId,notiType))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun getNotificationType(coinId: String,callback: ResponseCallback<List<NotificationType>>) {
        try {
            callback.onSuccess(notificationRepo.getNotificationType(getAuth(),coinId))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun uploadImage(image: List<Int>,callback: ResponseCallback<String>) {
        try {
            callback.onSuccess(uploadRepo.uploadImage(getAuth(),image))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun deleteImage(url: String,callback: ResponseCallback<String>) {
        try {
            callback.onSuccess(uploadRepo.deleteImage(getAuth(),url))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    fun uploadProfileImage(image: List<Int>,callback: ResponseCallback<String>) {
        try {
            callback.onSuccess(uploadRepo.uploadProfileImage(getAuth(),image))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    private fun getAuth(): String {
        return Authentication.getFirebaseToken() ?: throw FirebaseIdTokenException("getAuth error")
    }

}