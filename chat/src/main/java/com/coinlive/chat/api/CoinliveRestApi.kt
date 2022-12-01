package com.coinlive.chat.api

import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.repository.*
import com.coinlive.chat.exception.*
import com.coinlive.chat.firebase.service.CoinliveAuthentication
import okhttp3.MultipartBody

class CoinliveRestApi {
    private val channelRepo = ChannelRepository()
    private val chattingMemberRepo = ChattingMemberRepository()
    private val memberRepo = MemberRepository()
    private val notificationRepo = NotificationRepository()
    private val uploadRepo = UploadRepository()

    suspend fun getUserCount(coinId: String, callback: ResponseCallback<UserCount>) {
        try {
            callback.onSuccess(channelRepo.getUserCount(coinId, getAuth()))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun getChannelList(apiKey: String, callback: ResponseCallback<List<Channel>>) {
        try {
            callback.onSuccess(chattingMemberRepo.getChannelList(apiKey))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun getCustomerInfo(apiKey: String, callback: ResponseCallback<Customer>) {
        try {
            callback.onSuccess(chattingMemberRepo.getCustomerInfo(apiKey))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun getCustomerMemberInfo(callback: ResponseCallback<CustomerUser>) {
        try {
            callback.onSuccess(chattingMemberRepo.getCustomerMemberInfo(getAuth()))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    //    suspend fun customerUserSignUp(user: CustomerUserSignUpBody,callback: ResponseCallback<CustomerUserSignUp>) {
    suspend fun customerUserSignUp(user: CustomerUserSignUpBody, callback: ResponseCallback<Boolean>) {
        try {
            val result = chattingMemberRepo.customerUserSignUp(getAuth(), user)
            if (result.isSuccess()) {
                callback.onSuccess(true)
            } else {
                callback.onFail(CoinliveException(result.code.name, result.code.ordinal))
            }

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
     * 전달받은 데이터를 이용하여 [CoinliveAuthentication.signIn] 또는 [customerUserSignUp]을 이용하는데 사용하십시요.
     */
    suspend fun getCustomToken(apiKey: String, uuid: String, callback: ResponseCallback<CustomerUserSignUp>) {
        try {
            callback.onSuccess(chattingMemberRepo.getCustomToken(apiKey, uuid))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun isAvailableNickName(nickName: String, customerId: String, callback: ResponseCallback<String>) {
        try {
            callback.onSuccess(memberRepo.isAvailableNickName(nickName, customerId))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun setNickName(nickName: String, customerId: String, callback: ResponseCallback<String>) {
        try {
            callback.onSuccess(memberRepo.setNickName(nickName, getAuth(), customerId))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun signupCheck(firebaseUuid: String, callback: ResponseCallback<MemberSignupCheck>) {
        try {
            callback.onSuccess(memberRepo.signupCheck(firebaseUuid))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun setBasicProfile(callback: ResponseCallback<Upload>) {
        try {
            callback.onSuccess(memberRepo.setBasicProfile(getAuth()))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun getReportType(callback: ResponseCallback<List<ReportType>>) {
        try {
            callback.onSuccess(memberRepo.getReportType(getAuth()))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun setReport(reportMid: String, reportTypeId: String, callback: ResponseCallback<Boolean>) {
        try {
            callback.onSuccess(memberRepo.setReport(getAuth(), reportMid, reportTypeId))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun deleteBlock(blockMid: String, callback: ResponseCallback<ArrayList<String>>) {
        try {
            callback.onSuccess(memberRepo.deleteBlock(getAuth(), blockMid))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun addBlock(blockMid: String, callback: ResponseCallback<ArrayList<String>>) {
        try {
            callback.onSuccess(memberRepo.addBlock(getAuth(), blockMid))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun setNotification(coinId: String, notiType: String, callback: ResponseCallback<Boolean>) {
        try {
            callback.onSuccess(notificationRepo.setNotification(getAuth(), coinId, notiType))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun deleteNotification(coinId: String, notiType: String, callback: ResponseCallback<Boolean>) {
        try {
            callback.onSuccess(notificationRepo.deleteNotification(getAuth(), coinId, notiType))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun getNotificationType(callback: ResponseCallback<List<NotificationType>>) {
        try {
            callback.onSuccess(notificationRepo.getNotificationType(getAuth()))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun getNotificationSetting(coinId: String, callback: ResponseCallback<Map<String, Boolean>>) {
        try {
            callback.onSuccess(notificationRepo.getNotificationSetting(getAuth(), coinId))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun uploadImage(image: MultipartBody.Part, callback: ResponseCallback<String>) {
        try {
            if (!checkMaxSize(image.body.contentLength())) {
                callback.onSuccess(uploadRepo.uploadImage(getAuth(), image))
            } else {
                callback.onFail(UploadImageException("upload fail"))
            }

        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun deleteImage(url: String, callback: ResponseCallback<String>) {
        try {
            callback.onSuccess(uploadRepo.deleteImage(getAuth(), url))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun uploadProfileImage(image: MultipartBody.Part, callback: ResponseCallback<String>) {
        try {
            if (!checkMaxSize(image.body.contentLength())) {
                callback.onSuccess(uploadRepo.uploadProfileImage(getAuth(), image))
            } else {
                callback.onFail(UploadImageException("upload fail"))
            }
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    private fun checkMaxSize(byte: Long): Boolean {
        return ((byte / 1024) / 1024) > 15
    }

    private suspend fun getAuth(): String {
        return CoinliveAuthentication.getFirebaseToken()
    }

}