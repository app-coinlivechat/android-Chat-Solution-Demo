package com.coinlive.demo.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.coinlive.demo.databinding.LoginResultDialogBinding
import com.coinlive.demo.utils.DpHelper

interface OkCallback {
    fun okClick(dialog: Dialog)
}

class LoginResultDialog(context: Context, private val okCallback: OkCallback, private val description: String) : Dialog
    (context) {

    private lateinit var binding: LoginResultDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginResultDialogBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        window?.setLayout(DpHelper.dpToPx(context, 311.0F).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        initViews()
    }

    private fun initViews() = with(binding) {
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tDesc.text = description
        btnOk.setOnClickListener {
//            dismiss()
            okCallback.okClick(this@LoginResultDialog)

        }

    }
}