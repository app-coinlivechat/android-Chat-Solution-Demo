package com.coinlive.uikit.framents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.coinlive.uikit.adapters.NotificationListAdapter
import com.coinlive.uikit.databinding.FragmentNotificationSettingBinding
import com.coinlive.uikit.models.Notification

class NotificationSettingFragment : BaseFragment() {
    private val TAG = NotificationSettingFragment::class.java.simpleName

    private var binding: FragmentNotificationSettingBinding? = null
    private val adapter: NotificationListAdapter by lazy {
        NotificationListAdapter()
    }
    private var originList : ArrayList<Notification>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            originList = it.getParcelableArrayList("list")
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
        originList?.let { it ->
            binding!!.progressBar.visibility = View.GONE
            binding!!.rvList.visibility = View.VISIBLE
            adapter.items.addAll(it)
            adapter.notifyDataSetChanged()
        }
        binding!!.ibtnBack.setOnClickListener {
            setFragmentResult("notification", bundleOf("newList" to adapter.items))
            popFragment()
        }
    }

}