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
import kotlinx.coroutines.launch


class LoginFragmentViewModel : ViewModel() {
    private val TAG = LoginFragmentViewModel::class.java.simpleName

    private val customerName = "testsite"
    private val password = "testsite"
    private val clApi = CoinliveRestApi()
    var customer: Customer? = null
        get() = field
        private set(value) {
            field = value
        }

    var customToken: String? = null

    var loginResultMsg: MutableLiveData<String> = MutableLiveData()
    var memberCheckMsg: MutableLiveData<UserStatus> = MutableLiveData()
//    var loginResultCode: MutableLiveData<String> = MutableLiveData()

    fun getCustomerInfo() = viewModelScope.launch {
        clApi.getCustomerInfo(customerName,object : ResponseCallback<Customer> {
            override fun onSuccess(value: Customer) {
                customer = value
            }

            override fun onFail(exception: CoinliveException) {
                customer = null
            }
        })
    }

    fun signUp(uuid: String, nickName: String) = viewModelScope.launch {
        if (customer == null) {
            loginResultMsg.value = "customer 정보를 불러오지 못했습니다."
            Log.e(TAG, "customer 정보를 불러오지 못했습니다.")
        } else {
            getCustomToken(uuid, nickName,continueSignUp = true)
        }
    }

    suspend fun signInAnonymously() {
        CoinliveAuthentication.signInWithUnknownUser()
    }


    fun signUpCheck() = viewModelScope.launch {

        try{
            val fId = CoinliveAuthentication.getFirebaseUuid()
            clApi.signupCheck(fId, object : ResponseCallback<MemberSignupCheck> {
                override fun onSuccess(value: MemberSignupCheck) {
                    memberCheckMsg.value = value.status
                }

                override fun onFail(exception: CoinliveException) {
                    Log.e(TAG, "${exception.message}\n${exception.stackTrace}")
                    memberCheckMsg.value = UserStatus.NONE
                }

            })
        }catch (exception : Exception) {
            Log.e(TAG, "${exception.message}\n${exception.stackTrace}")
            memberCheckMsg.value = UserStatus.NONE
        }
    }


    fun loginCheck() : Boolean {
        return try {
            CoinliveAuthentication.getFirebaseUuid()
            true
        }catch (e:Exception) {
            false
        }
    }

    fun firebaseSignInWithCustomToken() = viewModelScope.launch{
        customToken?.let {
                CoinliveAuthentication.signInWithCustomToken(it)
        } ?: run {
            Log.e(TAG, "customToken is null. please before call getCustomToken fun")
        }
    }


    private suspend fun signUpCustomerUser(nickName: String) = viewModelScope.launch {

        clApi.customerUserSignUp(CustomerUserSignUpBody(customer!!.id, customer!!.defaultImageUrl, nickName), object
            : ResponseCallback<Boolean> {
            override fun onSuccess(value: Boolean) {
                loginResultMsg.value = "가입완료"
                Log.d(TAG, if (value) "가입완료!!!!!!" else "가입 실패!!!!!!!!!")
            }

            override fun onFail(exception: CoinliveException) {
                Log.e(TAG, "가입 실패!!!!!!!!! ${exception.message}")
                loginResultMsg.value = exception.message
            }

        })
    }

    private suspend fun clSignUp(customToken: String, nickName: String) {
        CoinliveAuthentication.signInWithCustomToken(customToken)
        signUpCustomerUser(nickName)
    }

    private suspend fun getCustomToken(uuid: String, nickName: String, continueSignUp : Boolean = false) {
        clApi.getCustomToken(password,customerName, uuid, object : ResponseCallback<CustomerUserSignUp> {
            override fun onSuccess(value: CustomerUserSignUp) {
                Log.d(TAG, "customToken : ${value.customToken}")
                customToken = value.customToken
                viewModelScope.launch { clSignUp(customToken = value.customToken, nickName = nickName) }
            }

            override fun onFail(exception: CoinliveException) {
                Log.e(TAG, "${exception.stackTrace}")
                loginResultMsg.value = exception.message
            }

        })
    }

}