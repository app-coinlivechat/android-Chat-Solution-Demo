package com.coinlive.demo.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coinlive.chat.api.CoinliveRestApi
import com.coinlive.chat.api.ResponseCallback
import com.coinlive.chat.api.model.*
import com.coinlive.chat.exception.CoinliveException
import kotlinx.coroutines.launch


class ChannelListFragmentViewModel : ViewModel() {
    private val TAG = ChannelListFragmentViewModel::class.java.simpleName

    private val apiKey = "testsite"
    private val clApi = CoinliveRestApi()
    val itemList = MutableLiveData<List<Channel>>()

    fun getChannelList() = viewModelScope.launch {
        clApi.getChannelList(apiKey,object : ResponseCallback<List<Channel>>{
            override fun onSuccess(value: List<Channel>) {
                itemList.value = value
            }

            override fun onFail(exception: CoinliveException) {
                Log.e(TAG,"${exception.message}")
            }

        })
    }



}