package com.coinlive.chat.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MemberReportBody(
    @SerializedName("rmid") val reportMid: String,
    @SerializedName("rtid") val reportTypeId: String,
)

data class ReportTypeList(
    val list: ArrayList<ReportType>,
)

@Parcelize
data class ReportType(
    @SerializedName("rtid") val typeId: String,
    val type: String,
):Parcelable