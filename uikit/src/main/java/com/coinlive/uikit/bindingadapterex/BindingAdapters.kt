package com.coinlive.uikit.bindingadapterex

import android.text.format.DateFormat
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.coinlive.chat.firebase.model.enum.MessageType
import com.coinlive.uikit.R
import com.coinlive.uikit.utils.ViewUtils.dpToPx
import java.text.DecimalFormat
import java.util.*


object BindingAdapters {
    @BindingAdapter("setDate")
    @JvmStatic
    fun setDate(view: TextView, timeStamp: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        view.text = DateFormat.format("MM.dd", calendar).toString()
    }

    @BindingAdapter("setTime")
    @JvmStatic
    fun setTime(view: TextView, timeStamp: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        view.text = DateFormat.format("a hh:mm", calendar).toString()
    }

    @BindingAdapter("loadIcon")
    @JvmStatic
    fun loadIcon(view: ImageView, url: String?) {

        url?.let {
            Glide.with(view.context).load(url).circleCrop()
//            .placeholder(holder)
                .error(R.drawable.icon_additonal)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view)
        }
    }

    @BindingAdapter("loadImageMessage")
    @JvmStatic
    fun loadImageMessage(view: ImageView, url: String) {
        Glide.with(view.context)
            .load(url)
            .transform(CenterCrop(), RoundedCorners(view.dpToPx(6F)))
            .error(R.drawable.icon_additonal)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)
    }

    @BindingAdapter("loadProfile")
    @JvmStatic
    fun loadProfile(view: ImageView, url: String?) {

        if(url == null) return

        Glide.with(view.context).load(url)
            .error(R.drawable.icon_profile)
            .placeholder(R.drawable.icon_profile)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .circleCrop()
            .into(view)
    }

    @BindingAdapter("loadExchangeImage")
    @JvmStatic
    fun loadExchangeImage(view: ImageView, exchange: String) {
        //TODO 거래소 이미지
        when (exchange) {
            "UPBIT" -> {
                view.setImageResource(R.drawable.icon_additonal)
            }
            "BITHUM" -> {
                view.setImageResource(R.drawable.icon_additonal)

            }
            "BINANCE" -> {
                view.setImageResource(R.drawable.icon_additonal)

            }
            "METAMASK" -> {
                view.setImageResource(R.drawable.icon_additonal)
            }
            else -> {
                view.setImageResource(R.drawable.icon_additonal)
            }
        }
    }

    @BindingAdapter("futureTitle")
    @JvmStatic
    fun futureTitle(view: TextView, messageType: String) {
        when (messageType) {
            MessageType.BUY.name -> view.text = view.context.getString(R.string.sell_futures_chat_title)
            MessageType.SELL.name -> view.text = view.context.getString(R.string.buy_futures_chat_title)
        }
    }

    @BindingAdapter("priceTitle")
    @JvmStatic
    fun priceTitle(view: TextView, messageType: String) {
        when (messageType) {
            MessageType.DROP.name -> view.text = view.context.getString(R.string.price_drop_chat_title)
            MessageType.JUMP.name -> view.text = view.context.getString(R.string.price_jump_chat_title)
        }
    }

    @BindingAdapter("userCont")
    @JvmStatic
    fun userCont(view: TextView, userCont: Int) {
        view.text = DecimalFormat("#,###,###").format(userCont).toString()
    }



}