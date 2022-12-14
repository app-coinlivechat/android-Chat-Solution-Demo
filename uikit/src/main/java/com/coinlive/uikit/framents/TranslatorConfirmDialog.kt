package com.coinlive.uikit.framents

import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.DialogTranslatorDownloadBinding
import com.coinlive.uikit.utils.Constants
import com.coinlive.uikit.utils.ViewUtils.screenSize
import com.coinlive.uikit.views.OnButtonEventListener

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

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceSize = context?.screenSize() ?: Size(0,0)
        params?.width = (deviceSize.width * 0.8).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogTranslatorDownloadBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.dialog.setListener(object : OnButtonEventListener {
            override fun onConfirmClick() {
                isConfirmClick = true
                dismiss()
            }

            override fun onCancelClick() {
                dismiss()
            }
        })
    }

    override fun onDestroyView() {
        setFragmentResult(Constants.reqKeyTranslatorDownload,
            bundleOf(Constants.argKeyIsConfirmClick to isConfirmClick, Constants.argKeySelectTransLanguage to selectLanguage))
        super.onDestroyView()
    }


}