package com.coinlive.uikit.views

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.coinlive.chat.firebase.model.Ama
import com.coinlive.chat.util.CalendarHelper
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.databinding.ViewAmaBinding
import java.util.concurrent.TimeUnit

class AmaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: ViewAmaBinding by lazy {
        ViewAmaBinding.inflate(LayoutInflater.from(context), this, true)
    }
    private var timer: CountDownTimer? = null
    private var ama: Ama? = null

    init {
        if (!isInEditMode) {
        }
    }


    fun setAma(ama: Ama?) {
        this.ama = ama
        ama?.let {
            ama.endTime?.let { endTime ->
                if (endTime > 0) {
                    binding.llLive.visibility = View.GONE
                    binding.llReady.visibility = View.GONE
                    closeCountDownTimer()
                } else {
                    binding.llLive.visibility = View.VISIBLE
                    binding.llReady.visibility = View.GONE
                }
            } ?: run {
                if (ama.startTime > CalendarHelper.nowCalendar().timeInMillis) {
                    binding.llLive.visibility = View.GONE
                    binding.llReady.visibility = View.VISIBLE
                    startCountDownTimer()
                } else {
                    binding.llLive.visibility = View.VISIBLE
                    binding.llReady.visibility = View.GONE
                    closeCountDownTimer()
                }
            }
        } ?: run {
            binding.llLive.visibility = View.GONE
            binding.llReady.visibility = View.GONE
        }
    }

    fun closeCountDownTimer() {
        timer?.cancel()
    }

    // 3600000 : 1시간
    // 86400000 : 하루
    private fun startCountDownTimer() {
        val diffTime = this.ama!!.startTime - CalendarHelper.nowCalendar().timeInMillis
        LoggerHelper.de("diff time : $diffTime")

        if (diffTime < 0) return

        timer = if (diffTime < (3600 * 1000)) {   // 24간보다 적게 남았을때
            secondTimer(diffTime)
        } else if (diffTime < (86400 * 1000)) {
            minuteTimer(diffTime)
        } else {
            dayTimer(CalendarHelper.getMidnightTimeStampByMillis(this.ama!!.startTime) - CalendarHelper.nowCalendar().timeInMillis)
        }
        timer!!.start()
    }

    private fun secondTimer(diffTime: Long): CountDownTimer {
        return object : CountDownTimer(diffTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvDday.text = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(
                        millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(
                        millisUntilFinished)))
            }

            override fun onFinish() {
                timer = null
                binding.llLive.visibility = View.VISIBLE
                binding.llReady.visibility = View.GONE
            }

        }
    }

    private fun minuteTimer(diffTime: Long): CountDownTimer {
        return object : CountDownTimer(diffTime, (60 * 1000)) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvDday.text = String.format(
                    "%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(
                        millisUntilFinished)))
                if (millisUntilFinished < 3600000) {
                    timer?.cancel()
                    timer = null
                    timer = secondTimer(millisUntilFinished).start()
                }

            }

            override fun onFinish() {
                timer = null
            }

        }
    }

    private fun dayTimer(diffTime: Long): CountDownTimer {
        return object : CountDownTimer(diffTime, 3600 * 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvDday.text = "D-${TimeUnit.MILLISECONDS.toDays(millisUntilFinished)}"
                if (millisUntilFinished < 86400000) {
                    timer?.cancel()
                    timer = null
                    timer = minuteTimer(CalendarHelper.nowCalendar().timeInMillis - ama!!.startTime).start()
                }
            }

            override fun onFinish() {
                timer = null
            }

        }
    }


}