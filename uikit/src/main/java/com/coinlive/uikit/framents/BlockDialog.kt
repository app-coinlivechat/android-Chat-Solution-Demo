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
import com.coinlive.uikit.databinding.DialogBlockBinding
import com.coinlive.uikit.utils.Constants
import com.coinlive.uikit.utils.ViewUtils.screenSize
import com.coinlive.uikit.views.OnButtonEventListener

class BlockDialog : AppCompatDialogFragment() {
    private var binding: DialogBlockBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding = DialogBlockBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val isReadyBlock = it.getBoolean(Constants.argKeyIsReadyBlock)
            binding!!.dialog.setDescription(if (isReadyBlock) binding!!.root.context.getString(R.string
                .block_delete_description) else binding!!.root.context.getString(R.string
                .block_description))
        }

        binding!!.dialog.setListener(object : OnButtonEventListener {
            override fun onConfirmClick() {
                setFragmentResult(Constants.reqKeyBlock, bundleOf(Constants.argKeyIsConfirmClick to true))
                dismiss()
            }

            override fun onCancelClick() {
                setFragmentResult(Constants.reqKeyBlock, bundleOf(Constants.argKeyIsConfirmClick to false))
                dismiss()
            }
        })
    }

}