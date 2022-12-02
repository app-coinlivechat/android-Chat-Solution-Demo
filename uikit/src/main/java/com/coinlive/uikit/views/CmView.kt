package com.coinlive.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.ViewCmBinding
import com.coinlive.uikit.models.NoticeStatus
import com.coinlive.uikit.utils.Constants

class CmView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: ViewCmBinding by lazy {
        ViewCmBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        if (!isInEditMode) {
            binding.ibtnCm.setOnClickListener {
                binding.status = NoticeStatus.SMALL
            }
            binding.clSmall.setOnClickListener {
                binding.status = NoticeStatus.BIG
            }
            binding.clBig.setOnClickListener {
                binding.status = NoticeStatus.SMALL
            }
            binding.btnClose.setOnClickListener {
                binding.status = NoticeStatus.FLOAT
            }

            binding.btnAll.setOnClickListener {
                it.findNavController().navigate(R.id.action_chatFragment_to_textFragment, bundleOf(Constants
                    .argKeyTitle to it.context.getString(R.string.cm), Constants.argKeyDescription to binding.message))
            }
        }
    }

    fun setNoticeStatus(status: NoticeStatus) {
        binding.status = status
    }

    fun setMessage(message: String?) {
        if(message == null) {
            binding.status = NoticeStatus.NONE
            return
        }
        binding.message = message
        if(binding.status == NoticeStatus.NONE) {
            binding.status = NoticeStatus.SMALL
        }
    }
}