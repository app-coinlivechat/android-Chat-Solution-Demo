package com.coinlive.chat.api

import com.coinlive.chat.api.model.RestApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ResponseCallback<T> : Callback<RestApiResponse<T>> {
    override fun onResponse(call: Call<RestApiResponse<T>>, response: Response<RestApiResponse<T>>)
    override fun onFailure(call: Call<RestApiResponse<T>>, throwable: Throwable)
}