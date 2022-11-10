package com.coinlive.uikit.bindingadapterex

import android.text.format.DateFormat
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.coinlive.chat.firebase.model.enum.MessageType
import com.coinlive.chat.util.CalendarHelper
import com.coinlive.uikit.R
import java.text.DecimalFormat
import java.util.*

object BindingAdapters {
    @BindingAdapter("setDate")
    @JvmStatic
    fun setDate(view: TextView, timeStamp: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        view.text = DateFormat.format("a hh:mm", calendar).toString()
    }

    @BindingAdapter("loadImage")
    @JvmStatic
    fun loadImage(view: ImageView, url: String?) {

        url?.let {
            Glide.with(view.context).load(url)
//            .placeholder(holder)
                .error(R.drawable.icon_additonal)
                .into(view)
        }

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
    fun futureTitle(view: TextView,messageType: String) {
        when(messageType) {
            MessageType.SELL.name -> view.text = view.context.getString(R.string.sell_futures_chat_title)
            MessageType.BUY.name -> view.text = view.context.getString(R.string.buy_futures_chat_title)
        }
    }

    @BindingAdapter("priceTitle")
    @JvmStatic
    fun priceTitle(view: TextView,messageType: String) {
        when(messageType) {
            MessageType.DROP.name -> view.text = view.context.getString(R.string.price_drop_chat_title)
            MessageType.JUMP.name -> view.text = view.context.getString(R.string.price_jump_chat_title)
        }
    }

    @BindingAdapter("userCont")
    @JvmStatic
    fun userCont(view:TextView,userCont:Int) {
        view.text = DecimalFormat("#,###,###").format(userCont).toString()
    }

}