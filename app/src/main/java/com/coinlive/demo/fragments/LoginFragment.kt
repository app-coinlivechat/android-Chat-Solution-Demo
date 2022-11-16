package com.coinlive.demo.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.coinlive.chat.api.model.enums.UserStatus
import com.coinlive.demo.R
import com.coinlive.demo.databinding.FragmentLoginBinding
import com.coinlive.demo.dialogs.LoginResultDialog
import com.coinlive.demo.dialogs.OkCallback
import com.coinlive.demo.viewmodels.LoginFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : Fragment(), OkCallback {
    private val TAG = LoginFragment::class.java.simpleName


    lateinit var viewModel: LoginFragmentViewModel
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[LoginFragmentViewModel::class.java]
        viewModel.getCustomerInfo()
        viewModel.loginResultMsg.observe(viewLifecycleOwner) {
            LoginResultDialog(requireContext(), this, it).show()
        }
        viewModel.memberCheckMsg.observe(viewLifecycleOwner) {
            if (it == UserStatus.ACTIVE) {
                moveChannelListFragment()
            } else {
                showLoginResultDialog(it.name)
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bSignUp.setOnClickListener {
            viewModel.signUp(binding.etUuId.text.toString(), binding.etNickname.text.toString())
        }
        binding.bLogIn.setOnClickListener {
            val result = viewModel.loginCheck()
            if (!result) {
                viewModel.firebaseSignInWithCustomToken()
            }
            viewModel.signUpCheck()


        }
        binding.bAnonymouslyLogIn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    viewModel.signInAnonymously()
                    moveChannelListFragment()
                } catch (e: Exception) {
                    showLoginResultDialog(e.message ?: "signInAnonymously 실패")
                }
            }

        }
    }

    private fun showLoginResultDialog(message: String) {
        LoginResultDialog(requireContext(), this, message).show()
    }

    private fun moveChannelListFragment() {

        val bundle = Bundle()
        viewModel.customer?.let {
            bundle.putString("customerName", it.name)
        }
        // use FragmentManager
//        requireActivity().supportFragmentManager.beginTransaction()
//            .add(R.id.nav_host_fragment_content_main, ChannelListFragment::class.java, bundle)
//            .addToBackStack(null)
//            .commit()
        findNavController().navigate(R.id.action_LoginFragment_to_ChannelListFragment,bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun okClick(dialog: Dialog) {
        dialog.dismiss()
        if (viewModel.loginResultMsg.value == "가입완료") {
            moveChannelListFragment()
        }
    }


}