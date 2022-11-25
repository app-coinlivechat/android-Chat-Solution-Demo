package com.coinlive.uikit.framents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coinlive.chat.Coinlive
import com.coinlive.uikit.databinding.FragmentTextBinding
import com.coinlive.uikit.utils.Constants
import com.coinlive.uikit.utils.PreferenceHelper
import com.coinlive.uikit.utils.PreferenceHelper.enableTranslator
import com.coinlive.uikit.utils.PreferenceHelper.translatorLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class TextFragment : BaseFragment() {
    private val TAG = TextFragment::class.java.simpleName

    private var binding: FragmentTextBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTextBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            binding!!.title = it.getString(Constants.argKeyTitle)
            binding!!.description = it.getString(Constants.argKeyDescription)
            binding!!.isMyMessage = it.getBoolean(Constants.argKeyIsMyMessage)
            binding!!.isAutoTranslator = it.getBoolean(Constants.argKeyAutoTranslator)
        }
        binding!!.enableTranslator = PreferenceHelper.defaultPreference(requireContext()).enableTranslator

        if(binding!!.isAutoTranslator!! && binding!!.enableTranslator!!) {
            transDescription()
        }

        binding!!.ibtnTranslator.setOnClickListener {
            transDescription()
        }
        binding!!.ibtnBack.setOnClickListener {
            popFragment()
        }
    }

    private fun transDescription() {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(Coinlive.locale.language)
            .setTargetLanguage(PreferenceHelper.defaultPreference(requireContext()).translatorLanguage!!)
            .build()
        val translator = Translation.getClient(options)
        translator.translate(binding!!.description!!).addOnSuccessListener { transMsg ->
            binding?.transMsg = transMsg
            translator.close()
            binding?.ibtnTranslator?.visibility = View.GONE
        }
    }


}