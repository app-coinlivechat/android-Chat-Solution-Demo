package com.coinlive.demo.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coinlive.chat.api.CoinliveRestApi
import com.coinlive.chat.api.ResponseCallback
import com.coinlive.chat.api.model.*
import com.coinlive.chat.api.model.enums.UserStatus
import com.coinlive.chat.exception.CoinliveException
import com.coinlive.chat.firebase.service.CoinliveAuthentication
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.demo.DemoApplication
import kotlinx.coroutines.launch
import okhttp3.MultipartBody


class LoginFragmentViewModel : ViewModel() {
    private val TAG = LoginFragmentViewModel::class.java.simpleName

    var customer: Customer? = null

    var loginResultMsg: MutableLiveData<String> = MutableLiveData()
    var memberCheckMsg: MutableLiveData<UserStatus> = MutableLiveData()
    var profile: MultipartBody.Part? = null

    fun signUp(customerName: String, uuid: String, nickName: String) = viewModelScope.launch {
        /// 1. 닉네임 체크
        /// 2. 커스텀 토큰 get
        /// 3. firebase signIn
        /// 4. 중복 유저 체크
        /// 5. 프로필 사진 업로드
        /// 6. 가입

        CoinliveRestApi.getCustomerInfo(customerName, object : ResponseCallback<Customer> {
            override fun onSuccess(value: Customer) {
                customer = value
                viewModelScope.launch {
                    CoinliveRestApi.isAvailableNickName(nickName, customer!!.id, object : ResponseCallback<Boolean> {
                        override fun onSuccess(value: Boolean) {
                            viewModelScope.launch {
                                CoinliveRestApi.getCustomToken(DemoApplication.password,
                                    customer!!.name,
                                    uuid,
                                    object : ResponseCallback<CustomerUserSignUp> {
                                        override fun onSuccess(value: CustomerUserSignUp) {
                                            viewModelScope.launch {
                                                val firebaseUuid =
                                                    CoinliveAuthentication.signInWithCustomToken(value.customToken,customer!!.name)
                                                CoinliveRestApi.signupCheck(firebaseUuid,
                                                    object : ResponseCallback<MemberSignupCheck> {
                                                        override fun onSuccess(value: MemberSignupCheck) {
                                                            when (value.status) {
                                                                UserStatus.ACTIVE -> loginResultMsg.value =
                                                                    "등록되어 있는 유저입니다"
                                                                UserStatus.BLOCK -> loginResultMsg.value = "정지 대상 유저입니다"
                                                                UserStatus.DORMANT -> loginResultMsg.value = "휴면 유저입니다"
                                                                UserStatus.INACTIVE -> loginResultMsg.value =
                                                                    "비활성화된 유저입니다"
                                                                UserStatus.NONE -> {
                                                                    if (profile != null) {
                                                                        viewModelScope.launch {
                                                                            CoinliveRestApi.uploadProfileImage(profile!!, object :
                                                                                ResponseCallback<String> {
                                                                                override fun onSuccess(value: String) {
                                                                                    viewModelScope.launch {
                                                                                        signUpCustomerUser(nickName,
                                                                                            value)
                                                                                    }
                                                                                }

                                                                                override fun onFail(exception: CoinliveException) {
                                                                                    loginResultMsg.value =
                                                                                        exception.message
                                                                                }
                                                                            })
                                                                        }
                                                                    } else {
                                                                        viewModelScope.launch {
                                                                            signUpCustomerUser(nickName, null)
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        }

                                                        override fun onFail(exception: CoinliveException) {
                                                            loginResultMsg.value = exception.message
                                                        }
                                                    })
                                            }
                                        }

                                        override fun onFail(exception: CoinliveException) {
                                            Log.e(TAG, "${exception.stackTrace}")
                                            loginResultMsg.value = exception.message
                                        }

                                    })
                            }
                        }

                        override fun onFail(exception: CoinliveException) {
                            LoggerHelper.de("exception code : ${exception.code}, message : ${exception.message}")
                            loginResultMsg.value = exception.message
                        }
                    })
                }

            }

            override fun onFail(exception: CoinliveException) {
                loginResultMsg.value = "customer 정보를 불러오지 못했습니다."
                customer = null
                exception.printStackTrace()
            }
        })
    }

    fun logIn(customerName: String, uuid: String) = viewModelScope.launch {

        CoinliveRestApi.getCustomerInfo(customerName, object : ResponseCallback<Customer> {
            override fun onSuccess(value: Customer) {
                customer = value
                viewModelScope.launch {
                    CoinliveRestApi.getCustomToken(DemoApplication.password, customer!!.name, uuid, object :
                        ResponseCallback<CustomerUserSignUp> {
                        override fun onSuccess(value: CustomerUserSignUp) {
                            viewModelScope.launch {
                                val firebaseUuid = CoinliveAuthentication.signInWithCustomToken(value.customToken,customer!!.name)
                                CoinliveRestApi.signupCheck(firebaseUuid, object : ResponseCallback<MemberSignupCheck> {
                                    override fun onSuccess(value: MemberSignupCheck) {
                                        memberCheckMsg.value = value.status
                                    }

                                    override fun onFail(exception: CoinliveException) {
                                        memberCheckMsg.value = UserStatus.NONE
                                    }
                                })
                            }
                        }

                        override fun onFail(exception: CoinliveException) {
                            Log.e(TAG, "${exception.stackTrace}")
                            loginResultMsg.value = exception.message
                        }
                    })
                }

            }

            override fun onFail(exception: CoinliveException) {
                loginResultMsg.value = "customer 정보를 불러오지 못했습니다."
                customer = null
                exception.printStackTrace()
            }
        })
    }

    fun signInAnonymously(customerName: String, callback: ResponseCallback<Boolean>) = viewModelScope.launch {
        CoinliveRestApi.getCustomerInfo(customerName, object : ResponseCallback<Customer> {
            override fun onSuccess(value: Customer) {
                customer = value
                viewModelScope.launch {
                    CoinliveAuthentication.signInWithUnknownUser(customerName)
                    callback.onSuccess(true)
                }
            }

            override fun onFail(exception: CoinliveException) {
                loginResultMsg.value = "customer 정보를 불러오지 못했습니다."
                customer = null
                exception.printStackTrace()
                callback.onFail(exception)
            }
        })

    }


    fun setProfileImage(multiPart: MultipartBody.Part) {
        profile = multiPart
    }

    private suspend fun signUpCustomerUser(nickName: String, profileUrl: String?) = viewModelScope.launch {
        CoinliveRestApi.customerUserSignUp(customer!!.id,
            profileUrl ?: customer!!.defaultImageUrl,
            nickName,
            object : ResponseCallback<Boolean> {

                override fun onFail(exception: CoinliveException) {
                    Log.e(TAG, "가입 실패!!!!!!!!! ${exception.message}")
                    loginResultMsg.value = exception.message
                }

                override fun onSuccess(value: Boolean) {
                    loginResultMsg.value = "가입완료"
                }

            })
    }

}