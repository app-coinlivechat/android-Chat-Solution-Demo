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
import kotlinx.coroutines.launch


class ChannelListFragmentViewModel : ViewModel() {
    private val TAG = ChannelListFragmentViewModel::class.java.simpleName

    private val clApi = CoinliveRestApi()
    val itemList = MutableLiveData<List<Channel>>()
    var myInfo:CustomerUser? = null

    init {
        loadMyInfo()
        getChannelList("testsite")
    }

    private fun loadMyInfo() = viewModelScope.launch {

        if (!CoinliveAuthentication.isAnonymously()) {
            clApi.getCustomerMemberInfo(object : ResponseCallback<CustomerUser> {
                override fun onSuccess(value: CustomerUser) {
                    myInfo = value
                }

                override fun onFail(exception: CoinliveException) {
                    myInfo = null
                }
            })
        }
    }

    private fun getChannelList(customerName : String) = viewModelScope.launch {
        clApi.getChannelList(customerName,object : ResponseCallback<List<Channel>>{
            override fun onSuccess(value: List<Channel>) {
                itemList.value = value
            }
            override fun onFail(exception: CoinliveException) {
                Log.e(TAG,"${exception.message}")
            }
        })
    }



}