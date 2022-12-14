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
import androidx.recyclerview.widget.LinearLayoutManager
import com.coinlive.chat.api.model.ReportType
import com.coinlive.uikit.R
import com.coinlive.uikit.adapters.ReportListAdapter
import com.coinlive.uikit.databinding.DialogReportBinding
import com.coinlive.uikit.utils.Constants
import com.coinlive.uikit.utils.ViewUtils.screenSize

class ReportDialog : AppCompatDialogFragment() {
    private var binding: DialogReportBinding? = null
    private val adapter = ReportListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(STYLE_NO_TITLE, R.style.dialog)
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceSize = context?.screenSize() ?: Size(0,0)
        params?.width = (deviceSize.width * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogReportBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val reportList = it.getParcelableArrayList<ReportType>(Constants.argKeyReportTypeList) ?: return@let
            binding!!.rvList.apply {
                this@ReportDialog.adapter.setItems(reportList)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@ReportDialog.adapter
            }

        }
        binding!!.btnConfirm.setOnClickListener {

            if(adapter.selectType == null) return@setOnClickListener

            setFragmentResult(Constants.reqKeyReport, bundleOf(Constants.argKeyIsConfirmClick to true, Constants
                .argKeyReportType to adapter.selectType))
            dismiss()
        }
        binding!!.btnCancel.setOnClickListener {
            setFragmentResult(Constants.reqKeyReport, bundleOf(Constants.argKeyIsConfirmClick to false))
            dismiss()
        }
    }

}