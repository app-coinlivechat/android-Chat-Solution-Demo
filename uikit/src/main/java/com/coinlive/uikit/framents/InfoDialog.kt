package com.coinlive.uikit.framents

import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.DialogInfoBinding
import com.coinlive.uikit.utils.Constants
import com.coinlive.uikit.utils.ViewUtils.screenSize

class InfoDialog : DialogFragment() {
    private var binding: DialogInfoBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(STYLE_NO_TITLE, R.style.dialog)
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceSize = context?.screenSize() ?: Size(0, 0)
        params?.width = (deviceSize.width * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogInfoBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            binding!!.description = it.getString(Constants.argKeyDescription)
        }

        binding!!.btnConfirm.setOnClickListener {
            dismiss()
        }
    }

}