package com.coinlive.chat.api.model

import com.google.gson.annotations.SerializedName

data class MemberReportBody(
    @SerializedName("rmid") val reportMid: String,
    @SerializedName("rtid") val reportTypeId: String,
)

data class ReportTypeList(
    val list: ArrayList<ReportType>,
)

data class ReportType(
    @SerializedName("rtid") val typeId: String,
    val type: String,
)