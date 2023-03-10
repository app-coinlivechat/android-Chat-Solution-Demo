package com.coinlive.demo.fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.coinlive.chat.api.ResponseCallback
import com.coinlive.chat.api.model.enums.UserStatus
import com.coinlive.chat.exception.CoinliveException
import com.coinlive.chat.util.CalendarHelper
import com.coinlive.demo.DemoApplication
import com.coinlive.demo.R
import com.coinlive.demo.databinding.FragmentLoginBinding
import com.coinlive.demo.dialogs.LoginResultDialog
import com.coinlive.demo.dialogs.OkCallback
import com.coinlive.demo.utils.RandomStringHelper
import com.coinlive.demo.viewmodels.LoginFragmentViewModel
import com.coinlive.uikit.utils.MultipartHelper
import com.coinlive.uikit.views.CoinLiveToast
import kotlin.system.exitProcess

class LoginFragment : Fragment(), OkCallback {
    lateinit var viewModel: LoginFragmentViewModel
    private var _binding: FragmentLoginBinding? = null
    var waitTime = 0L

    private val binding get() = _binding!!

    private val galleryReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                displayGalleryFragment()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { uri ->
                val input = requireContext().contentResolver.openInputStream(uri)
                val img: Bitmap = BitmapFactory.decodeStream(input)
                input?.close()
                val fileName = "${CalendarHelper.nowCalendar().timeInMillis}_image.png"
                viewModel.setProfileImage(MultipartHelper.buildBitmapBodyPart(fileName, img, requireContext()))
                binding.tvProfile.text = fileName
            }
        }

    private val callback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - waitTime >= 1500) {
                    waitTime = System.currentTimeMillis()
                    CoinLiveToast.make(binding.root, getString(R.string.please_back_press)).show()
                } else {
                    activity?.finishAffinity()
                    exitProcess(0)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[LoginFragmentViewModel::class.java]
        viewModel.loginResultMsg.observe(viewLifecycleOwner) {
            hideProgress()
            LoginResultDialog(requireContext(), this, it).show()
        }
        viewModel.memberCheckMsg.observe(viewLifecycleOwner) {
            hideProgress()
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
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        val spannable = binding.tvTitle.text as Spannable
        spannable.setSpan(ForegroundColorSpan(requireContext().getColor(R.color.blue_text)), 9, 13, Spanned
            .SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.appName = DemoApplication.appName
        binding.etUuId.setText("etgkqo")
        binding.etNickname.setText("etgkqo")

        binding.btnImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                galleryReqLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                displayGalleryFragment()
            }
        }
        binding.ibtnRefresh.setOnClickListener {
            binding.etUuId.setText(RandomStringHelper.generateString())
        }
        binding.bSignUp.setOnClickListener {
            showProgress()
            viewModel.signUp(binding.etBandId.text.toString(), binding.etUuId.text.toString(), binding.etNickname.text
                .toString())
        }
        binding.bLogIn.setOnClickListener {
            showProgress()
            viewModel.logIn(binding.etBandId.text.toString(), binding.etUuId.text.toString())
        }
        binding.bAnonymouslyLogIn.setOnClickListener {
            showProgress()
            try {
                viewModel.signInAnonymously(binding.etBandId.text.toString(),object :ResponseCallback<Boolean> {
                    override fun onSuccess(value: Boolean) {
                        moveChannelListFragment()
                    }

                    override fun onFail(exception: CoinliveException) {
                    }
                })
            } catch (e: Exception) {
                showLoginResultDialog(e.message ?: "signInAnonymously 실패")
            }
        }
    }

    private fun displayGalleryFragment() {
        galleryLauncher.launch(Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        })
    }

    private fun showLoginResultDialog(message: String) {
        LoginResultDialog(requireContext(), this, message).show()
    }

    private fun moveChannelListFragment() {

        val bundle = Bundle()
        viewModel.customer?.let {
            bundle.putParcelable("customer", it)
        }
        // use FragmentManager
//        requireActivity().supportFragmentManager.beginTransaction()
//            .add(R.id.nav_host_fragment_content_main, ChannelListFragment::class.java, bundle)
//            .addToBackStack(null)
//            .commit()
        findNavController().navigate(R.id.action_LoginFragment_to_ChannelListFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressBar.visibility = View.GONE

    }

    override fun okClick(dialog: Dialog) {
        dialog.dismiss()
        if (viewModel.loginResultMsg.value == "가입완료") {
            moveChannelListFragment()
        }
    }


}