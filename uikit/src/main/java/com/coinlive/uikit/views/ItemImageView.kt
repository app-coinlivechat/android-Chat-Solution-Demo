package com.coinlive.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.uikit.databinding.ItemImageBinding
import com.coinlive.uikit.databinding.ViewMyTextMessageBinding

class ItemImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ItemImageBinding by lazy {
        ItemImageBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setUrl(url: String) {
        binding.url = url
    }

}