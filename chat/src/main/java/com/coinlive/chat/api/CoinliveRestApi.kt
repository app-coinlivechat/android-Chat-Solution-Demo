package com.coinlive.chat.api

import androidx.annotation.Keep
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.repository.*
import com.coinlive.chat.exception.*
import com.coinlive.chat.firebase.service.CoinliveAuthentication
import okhttp3.MultipartBody

@Keep
class CoinliveRestApi {
    private val channelRepo = ChannelRepository()
    private val chattingMemberRepo = ChattingMemberRepository()
    private val memberRepo = MemberRepository()
    private val notificationRepo = NotificationRepository()
    private val uploadRepo = UploadRepository()

    suspend fun getUserCount(coinId: String, callback: ResponseCallback<UserCount>) {
        try {

            val firebaseUuid: String? = if (CoinliveAuthentication.isAnonymously()) CoinliveAuthentication
                .getFirebaseUuid() else null
            callback.onSuccess(channelRepo.getUserCount(coinId, firebaseUuid, getAuth()))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun getChannelList(customerName: String, callback: ResponseCallback<List<Channel>>) {
        try {
            callback.onSuccess(chattingMemberRepo.getChannelList(customerName))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun getCustomerInfo(customerName: String, callback: ResponseCallback<Customer>) {
        try {
            callback.onSuccess(chattingMemberRepo.getCustomerInfo(customerName))
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

    suspend fun customerUserSignUp(user: CustomerUserSignUpBody, callback: ResponseCallback<Boolean>) {
        try {
            callback.onSuccess(chattingMemberRepo.customerUserSignUp(getAuth(), user))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    /**
     * 사용자를 Firebase Authentication에 로그인하고 coinlive에 가입하기 위해 Firebase Authentication [CustomerUserSignUp]을
     * 전달 받습니다.
     * @param[uuid] 사용자 uuid
     * @param[password] customer password
     * @param[customerName] customer 이름
     * @return[CustomerUserSignUp] 사용자 uuid를 이용하여 Firebase Authentication에 로그인 할 수 있는 정보를 전달합니다.
     * 전달받은 데이터를 이용하여 [CoinliveAuthentication.signInWithCustomToken] 또는 [customerUserSignUp]을 이용하는데 사용하십시요.
     */
    suspend fun getCustomToken(
        password: String,
        customerName: String,
        uuid: String,
        callback: ResponseCallback<CustomerUserSignUp>,
    ) {
        try {
            callback.onSuccess(chattingMemberRepo.getCustomToken(password, customerName, uuid))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun isAvailableNickName(nickName: String, customerId: String, callback: ResponseCallback<Boolean>) {
        try {
            callback.onSuccess(memberRepo.isAvailableNickName(nickName, customerId))
        } catch (e: CoinliveException) {
            callback.onFail(e)
        }
    }

    suspend fun setNickName(nickName: String, customerId: String, callback: ResponseCallback<Boolean>) {
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