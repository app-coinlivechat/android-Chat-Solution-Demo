package com.coinlive.uikit.framents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coinlive.uikit.databinding.FragmentTranslatorSettingBinding

class TranslatorSettingFragment : BaseFragment() {
    private val TAG = TranslatorSettingFragment::class.java.simpleName

    private var binding: FragmentTranslatorSettingBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentTranslatorSettingBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.ibtnBack.setOnClickListener {
            popFragment()
        }
    }
}