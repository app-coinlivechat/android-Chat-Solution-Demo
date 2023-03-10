package com.coinlive.demo.viewmodels

import androidx.lifecycle.ViewModel
import com.coinlive.uikit.CoinliveUikit
import com.coinlive.uikit.callbacks.InputViewUnknownUserListener

class MainActivityViewModel : ViewModel() {
    fun initUiKit(inputViewUnknownUserListener: InputViewUnknownUserListener) {
        CoinliveUikit.inputViewUnknownUserListener = inputViewUnknownUserListener
    }

}