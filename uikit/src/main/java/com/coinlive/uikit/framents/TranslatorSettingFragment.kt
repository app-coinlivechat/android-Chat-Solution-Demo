package com.coinlive.uikit.framents

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.FragmentTranslatorSettingBinding
import com.coinlive.uikit.utils.Constants
import com.coinlive.uikit.utils.PreferenceHelper
import com.coinlive.uikit.utils.PreferenceHelper.enableTranslator
import com.coinlive.uikit.utils.PreferenceHelper.translatorLanguage
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.*

class TranslatorSettingFragment : BaseFragment() {
    private val TAG = TranslatorSettingFragment::class.java.simpleName

    private var binding: FragmentTranslatorSettingBinding? = null
    private val koTranslateRemoteModel by lazy {
        TranslateRemoteModel.Builder(TranslateLanguage.KOREAN).build()
    }
    private val enTranslateRemoteModel by lazy {
        TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build()
    }

    private val zhTranslateRemoteModel by lazy {
        TranslateRemoteModel.Builder(TranslateLanguage.CHINESE).build()
    }

    private var oldSelectLanguage : String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTranslatorSettingBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        oldSelectLanguage = PreferenceHelper.defaultPreference(requireContext()).translatorLanguage

        setFragmentResultListener(Constants.reqKeyTranslatorDownload) { key, bundle ->
           downloadDialogCallback(key,bundle)
        }
        setFragmentResultListener(Constants.reqKeyTranslatorSelect) { key, bundle ->
            selectBottomSheetCallback(key,bundle)
        }

        binding!!.sTranslator.isChecked = PreferenceHelper.defaultPreference(requireContext()).enableTranslator
        binding!!.sTranslator.setOnCheckedChangeListener { button, isChecked ->
            PreferenceHelper.defaultPreference(requireContext()).enableTranslator = isChecked
            if (isChecked) {
                button.findNavController().navigate(R.id.action_translatorSettingFragment_to_translatorBottomSheet)
            } else {
                PreferenceHelper.defaultPreference(requireContext()).translatorLanguage = null
                binding!!.selectLanguage = null
                binding?.clSelectLanguage?.visibility = View.GONE
            }
        }
        if (!binding!!.sTranslator.isChecked) {
            binding?.clSelectLanguage?.visibility = View.GONE
        }
        binding!!.ibtnBack.setOnClickListener {
            popFragment()
        }
        binding!!.clSelectLanguage.setOnClickListener {
            if (binding?.sTranslator?.isChecked == false) {
                return@setOnClickListener
            }
            it.findNavController().navigate(R.id.action_translatorSettingFragment_to_translatorBottomSheet)
        }
        binding!!.selectLanguage =
            getLanguageString(PreferenceHelper.defaultPreference(requireContext()).translatorLanguage)
    }

    private fun downloadDialogCallback(key:String,bundle: Bundle) {
        val isConfirmClick = bundle.getBoolean(Constants.argKeyIsConfirmClick)
        val selectLanguage = bundle.getString(Constants.argKeySelectTransLanguage)
        if (!isConfirmClick) {
            binding?.sTranslator?.isChecked = false
            binding?.clSelectLanguage?.visibility = View.GONE
            return
        }

        binding?.clProgress?.visibility = View.VISIBLE

        RemoteModelManager.getInstance().download(getTranslateRemoteModel(selectLanguage!!), DownloadConditions
            .Builder().build())
            .addOnSuccessListener {
                binding?.clProgress?.visibility = View.GONE
                PreferenceHelper.defaultPreference(requireContext()).translatorLanguage = selectLanguage
                binding!!.selectLanguage =
                    getLanguageString(PreferenceHelper.defaultPreference(requireContext()).translatorLanguage)
                binding?.clSelectLanguage?.visibility = View.VISIBLE
                Log.e(TAG, "download 성공")

            }.addOnFailureListener {
                binding?.clProgress?.visibility = View.GONE
                Log.e(TAG, "download 실패")
            }
    }

    private fun selectBottomSheetCallback(key: String,bundle: Bundle) {
        val selectLanguage: String? = bundle.getString(Constants.argKeySelectTransLanguage)
        if (selectLanguage == null) {
            binding?.sTranslator?.isChecked = false
            return
        }
        Log.e(TAG, "selectLanguage : $selectLanguage")
        RemoteModelManager.getInstance().getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener {
                var isContain = false

                it.forEach { model ->
                    if (model.language == selectLanguage) {
                        isContain = true
                        return@forEach
                    }
                }
                if (!isContain) {
                    binding?.root?.findNavController()
                        ?.navigate(R.id.action_translatorSettingFragment_to_translatorConfirmDialog, bundleOf
                            (Constants.argKeySelectTransLanguage to selectLanguage))
                } else {
                    PreferenceHelper.defaultPreference(requireContext()).translatorLanguage = selectLanguage
                    binding!!.selectLanguage =
                        getLanguageString(PreferenceHelper.defaultPreference(requireContext()).translatorLanguage)
                    binding?.clSelectLanguage?.visibility = View.VISIBLE
                }
            }
    }


    override fun onDestroyView() {
        setFragmentResult(Constants.reqKeyTranslator, bundleOf(Constants.argKeyOldTransLanguage to oldSelectLanguage))
        super.onDestroyView()
    }

    private fun getLanguageString(language: String?): String? {
        return when (language) {
            null -> null
            "ko" -> requireContext().getString(R.string.translator_ko)
            "en" -> requireContext().getString(R.string.translator_en)
            else -> requireContext().getString(R.string.translator_zh)
        }
    }

    private fun getTranslateRemoteModel(language: String): TranslateRemoteModel {
        return when (language) {
            "ko" -> koTranslateRemoteModel
            "en" -> enTranslateRemoteModel
            else -> zhTranslateRemoteModel
        }
    }


}