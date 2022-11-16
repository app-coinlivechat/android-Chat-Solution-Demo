package com.coinlive.uikit.framents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.adapters.NotificationListAdapter
import com.coinlive.uikit.databinding.FragmentNotificationSettingBinding
import com.coinlive.uikit.models.Notification
import com.coinlive.uikit.viewmodels.LoadNotificationListListener
import com.coinlive.uikit.viewmodels.NotificationViewModel

class NotificationSettingFragment : BaseFragment() {
    private val TAG = NotificationSettingFragment::class.java.simpleName

    private lateinit var viewModel: NotificationViewModel
    private var binding: FragmentNotificationSettingBinding? = null
    private val adapter: NotificationListAdapter by lazy {
        NotificationListAdapter()
    }
    private var coinId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        arguments?.let {
            coinId = it.getString("coinId")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentNotificationSettingBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.rvList.adapter = adapter
        binding!!.rvList.layoutManager = LinearLayoutManager(requireContext()) //레이아웃 매니저 연결
        coinId?.let { it ->
            viewModel.loadNotificationType(it,object : LoadNotificationListListener {
                override fun success(list: ArrayList<Notification>) {
                    binding!!.progressBar.visibility = View.GONE
                    binding!!.rvList.visibility = View.VISIBLE
                    adapter.items.addAll(list)
                    adapter.notifyDataSetChanged()
                }

                override fun fail(exception: Exception) {
                    LoggerHelper.de("${exception.message}")
                }

            })
        }
        binding!!.ibtnBack.setOnClickListener {
            popFragment()
        }
    }
}