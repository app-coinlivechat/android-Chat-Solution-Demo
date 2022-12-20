package com.coinlive.chat.api.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MemberReportBody(
    @SerializedName("rmid") val reportMid: String,
    @SerializedName("rtid") val reportTypeId: String,
)

@Keep
data class ReportTypeList(
    val list: ArrayList<ReportType>,
)

@Parcelize
@Keep
data class ReportType(
    @SerializedName("rtid") val typeId: String,
    val type: String,
):Parcelable