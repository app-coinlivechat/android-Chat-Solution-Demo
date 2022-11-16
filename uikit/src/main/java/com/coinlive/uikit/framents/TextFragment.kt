package com.coinlive.uikit.framents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coinlive.uikit.databinding.FragmentTextBinding

class TextFragment : BaseFragment() {
    private val TAG = TextFragment::class.java.simpleName

    private var binding: FragmentTextBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentTextBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.ibtnBack.setOnClickListener {
            popFragment()
        }
    }


}