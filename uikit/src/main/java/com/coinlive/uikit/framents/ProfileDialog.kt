package com.coinlive.uikit.framents

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.coinlive.uikit.R
import com.coinlive.uikit.databinding.DialogProfileBinding
import com.coinlive.uikit.utils.Constants
import com.coinlive.uikit.utils.ViewUtils.screenSize

class ProfileDialog : DialogFragment() {
    private var binding: DialogProfileBinding? = null
    private var profileUrl: String? = null

    private val galleryReqLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            displayGalleryFragment()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { uri ->
                profileUrl = uri.toString()
                binding?.url = profileUrl
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(STYLE_NO_TITLE, R.style.dialog)
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceSize = context?.screenSize() ?: Size(0, 0)
        params?.width = (deviceSize.width * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            binding!!.url = it.getString(Constants.argKeyUrl)
            binding!!.nickName = it.getString(Constants.argKeyNickName)
        }


        binding!!.rlImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                galleryReqLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                displayGalleryFragment()
            }
        }
        binding!!.etNickname.doOnTextChanged { text, _, _, _ ->
            binding!!.ibtnDelete.visibility = if (text.toString().isEmpty()) View.GONE else View.VISIBLE
        }
        binding!!.ibtnDelete.setOnClickListener {
            binding!!.etNickname.text.clear()
        }

        binding!!.btnConfirm.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(Constants.argKeyIsConfirmClick, true)
            bundle.putString(Constants.argKeyNickName, binding!!.etNickname.text.toString())
            if (profileUrl != null) {
                bundle.putString(Constants.argKeyUrl, profileUrl)
            }
            setFragmentResult(Constants.reqKeyProfile, bundle)
            dismiss()
        }
        binding!!.btnCancel.setOnClickListener {
            setFragmentResult(Constants.reqKeyProfile, bundleOf(Constants.argKeyIsConfirmClick to false))
            dismiss()
        }
    }

    private fun displayGalleryFragment() {
        galleryLauncher.launch(Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        })
    }

}