package com.coinlive.uikit.framents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.BottomSheetProfileBinding
import com.coinlive.uikit.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileBottomSheet : BottomSheetDialogFragment() {
    private var binding: BottomSheetProfileBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetProfileBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            binding!!.nickName = it.getString(Constants.argKeyNickName)
            binding!!.url = it.getString(Constants.argKeyUrl)
            binding!!.appName = it.getString(Constants.argKeyAppName)
        }

        binding!!.ibtnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}