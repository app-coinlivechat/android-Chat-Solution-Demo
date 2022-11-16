package com.coinlive.uikit.framents

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

open class BaseFragment : Fragment() {
    private fun removeFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .remove(this)
            .commit()
    }

   fun popFragment() {
       try {
           val result = findNavController().popBackStack()
           if (!result) {
               removeFragment()
           }
       } catch (exception: Exception) {
           removeFragment()
       }
   }
}