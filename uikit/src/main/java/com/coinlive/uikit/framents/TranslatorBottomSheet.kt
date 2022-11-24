package com.coinlive.uikit.framents

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.MutableLiveData
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.BottomSheetProfileBinding
import com.coinlive.uikit.databinding.BottomSheetTranslatorBinding
import com.coinlive.uikit.utils.Constants
import com.coinlive.uikit.utils.PreferenceHelper
import com.coinlive.uikit.utils.PreferenceHelper.translatorLanguage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TranslatorBottomSheet : BottomSheetDialogFragment(), OnClickListener {
    private var binding: BottomSheetTranslatorBinding? = null
    private var selectPosition: MutableLiveData<String?> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectPosition.value = PreferenceHelper.defaultPreference(requireContext()).translatorLanguage
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetTranslatorBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectPosition.observe(viewLifecycleOwner) {
            binding!!.rbtnKo.isChecked = it == "ko"
            binding!!.rbtnEn.isChecked = it == "en"
            binding!!.rbtnZh.isChecked = it == "zh"
            binding!!.clKo.setBackgroundColor(getColor(it == "ko", binding!!.clKo.context))
            binding!!.clEn.setBackgroundColor(getColor(it == "en", binding!!.clKo.context))
            binding!!.clZh.setBackgroundColor(getColor(it == "zh", binding!!.clKo.context))
        }

        binding!!.rbtnKo.setOnClickListener(this)
        binding!!.rbtnEn.setOnClickListener(this)
        binding!!.rbtnZh.setOnClickListener(this)
        binding!!.clKo.setOnClickListener(this)
        binding!!.clEn.setOnClickListener(this)
        binding!!.clZh.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setFragmentResult(Constants.reqKeyTranslator,
            bundleOf(Constants.argKeySelectTransLanguage to selectPosition.value))
        selectPosition.value = null
    }

    private fun getColor(isSelect: Boolean, context: Context): Int {
        return if (isSelect) context.getColor(R.color.translator_item_background) else context.getColor(R.color.translator_bottom_sheet_background)
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rbtn_ko, R.id.cl_ko -> {
                selectPosition.value = "ko"
            }
            R.id.rbtn_en, R.id.cl_en -> {
                selectPosition.value = "en"
            }
            R.id.rbtn_zh, R.id.cl_zh -> {
                selectPosition.value = "zh"
            }
        }
        dismiss()
    }
}