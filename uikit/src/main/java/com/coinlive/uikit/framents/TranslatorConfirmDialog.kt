package com.coinlive.uikit.framents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.DialogTranslatorDownloadBinding
import com.coinlive.uikit.utils.Constants

class TranslatorConfirmDialog : AppCompatDialogFragment() {
    private var binding: DialogTranslatorDownloadBinding? = null
    private var isConfirmClick = false
    private lateinit var selectLanguage:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            selectLanguage = it.getString(Constants.argKeySelectTransLanguage,"")
        }

        isCancelable = false
        setStyle(STYLE_NO_TITLE, R.style.dialog)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogTranslatorDownloadBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.btnConfirm.setOnClickListener {
            isConfirmClick = true
            dismiss()
        }
        binding!!.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        setFragmentResult(Constants.reqKeyTranslatorDownload,
            bundleOf(Constants.argKeyIsConfirmClick to isConfirmClick, Constants.argKeySelectTransLanguage to selectLanguage))
        super.onDestroyView()
    }


}