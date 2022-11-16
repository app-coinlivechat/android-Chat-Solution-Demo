package com.coinlive.uikit

import java.time.Duration

class CoinliveUikit {
    private var enableMetaDataCache = true
    private var metaDataSaveDuration : Duration = Duration.ofDays(30)


    constructor()
    constructor(enableMetaDataCache : Boolean, metaDataSaveDuration : Duration) {
        this.metaDataSaveDuration = metaDataSaveDuration
        this.enableMetaDataCache = enableMetaDataCache
    }

    init {
        checkMetaDataCache()
    }


    /**
     * client가 원하는 시점에서 삭제 ([metaDataSaveDuration] 상관 없음)
     */
    fun removeMetaDataCache() {

    }

    /**
     * [metaDataSaveDuration] 기간이 지난 데이터 삭제
     */
    private fun checkMetaDataCache() {

    }

}