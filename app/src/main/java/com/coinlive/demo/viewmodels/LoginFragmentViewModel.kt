package com.coinlive.demo.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coinlive.chat.api.CoinliveRestApi
import com.coinlive.chat.api.ResponseCallback
import com.coinlive.chat.api.model.Customer
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.api.model.CustomerUserSignUp
import com.coinlive.chat.api.model.CustomerUserSignUpBody
import com.coinlive.chat.api.model.enum.UserStatus
import com.coinlive.chat.exception.CoinliveException
import com.coinlive.chat.firebase.service.CoinliveAuthentication
import kotlinx.coroutines.launch


class LoginFragmentViewModel : ViewModel() {
    private val TAG = LoginFragmentViewModel::class.java.simpleName

    private val apiKey = "testsite"
    private val clApi = CoinliveRestApi()
    private var customer: Customer? = null
    var loginResultMsg: MutableLiveData<String> = MutableLiveData()
    var memberCheckMsg: MutableLiveData<UserStatus> = MutableLiveData()
//    var loginResultCode: MutableLiveData<String> = MutableLiveData()

    fun getCustomerInfo() = viewModelScope.launch {
        clApi.getCustomerInfo(apiKey, object : ResponseCallback<Customer> {
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
            getCustomToken(uuid, nickName)

        }
    }

    fun signUpCheck() = viewModelScope.launch {
        clApi.getCustomerMemberInfo(object :ResponseCallback<CustomerUser>{
            override fun onSuccess(value: CustomerUser) {
                memberCheckMsg.value = value.status
            }

            override fun onFail(exception: CoinliveException) {
                memberCheckMsg.value = UserStatus.NONE
            }

        })
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
        CoinliveAuthentication.signIn(customToken)
        signUpCustomerUser(nickName)
    }

    private suspend fun getCustomToken(uuid: String, nickName: String) {
        clApi.getCustomToken(apiKey, uuid, object : ResponseCallback<CustomerUserSignUp> {
            override fun onSuccess(value: CustomerUserSignUp) {
                Log.d(TAG, "customToken : ${value.customToken}")
                viewModelScope.launch { clSignUp(customToken = value.customToken, nickName = nickName) }
            }

            override fun onFail(exception: CoinliveException) {
                Log.e(TAG, "${exception.stackTrace}")
                loginResultMsg.value = exception.message
            }

        })
    }

}