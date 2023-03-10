package com.coinlive.demo.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coinlive.chat.api.CoinliveRestApi
import com.coinlive.chat.api.ResponseCallback
import com.coinlive.chat.api.model.*
import com.coinlive.chat.exception.CoinliveException
import com.coinlive.chat.firebase.service.CoinliveAuthentication
import com.coinlive.demo.DemoApplication
import kotlinx.coroutines.launch


class ChannelListFragmentViewModel : ViewModel() {
    private val TAG = ChannelListFragmentViewModel::class.java.simpleName

    val itemList = MutableLiveData<List<Channel>>()
    var myInfo:CustomerUser? = null
    var myInfoLoading : Boolean = false

    init {
        getChannelList()
    }

    fun loadMyInfo() = viewModelScope.launch {
        try{
            if (!CoinliveAuthentication.isAnonymously()) {
                myInfoLoading = true
                CoinliveRestApi.getCustomerMemberInfo(object : ResponseCallback<CustomerUser> {
                    override fun onSuccess(value: CustomerUser) {
                        myInfo = value
                        myInfoLoading = false
                    }

                    override fun onFail(exception: CoinliveException) {
                        myInfo = null
                        myInfoLoading = false
                    }
                })
            } else {
                myInfoLoading = false
            }
        } catch (exception : Exception) {
            myInfoLoading = false
        }

    }

    private fun getChannelList() = viewModelScope.launch {
        CoinliveRestApi.getChannelList(DemoApplication.appName,object : ResponseCallback<List<Channel>>{
            override fun onSuccess(value: List<Channel>) {
                itemList.value = value
            }
            override fun onFail(exception: CoinliveException) {
                Log.e(TAG,"${exception.message}")
            }
        })
    }

    fun logout() {
        CoinliveAuthentication.signOutFirebase()
    }





}