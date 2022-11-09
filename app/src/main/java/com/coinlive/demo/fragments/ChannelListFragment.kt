package com.coinlive.demo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.coinlive.chat.api.model.Channel
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.demo.R
import com.coinlive.demo.adapters.ChannelItemOnClick
import com.coinlive.demo.adapters.ChannelListAdapter
import com.coinlive.demo.databinding.FragmentChannelListBinding
import com.coinlive.demo.viewmodels.ChannelListFragmentViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ChannelListFragment : Fragment(), ChannelItemOnClick {
    private val TAG = ChannelListFragment::class.java.simpleName

    private var _binding: FragmentChannelListBinding? = null
    private lateinit var viewModel: ChannelListFragmentViewModel
    private var customerName:String? = null
    private var myInfo:CustomerUser? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customerName = it.getString("customerName")
            myInfo = it.getParcelable("myInfo")
        }
        viewModel = ViewModelProvider(this)[ChannelListFragmentViewModel::class.java]
        viewModel.getChannelList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChannelListBinding.inflate(inflater, container, false)


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ChannelListAdapter(requireContext())
        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(requireContext()) //레이아웃 매니저 연결
        adapter.itemOnClick(this)
        viewModel.itemList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.items.addAll(it)
                adapter.notifyItemRangeInserted(0, it.size)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(item: Channel) {
        val bundle = Bundle()
        customerName?.let {
            bundle.putString("customerName",it)
        }
        myInfo?.let {
            bundle.putParcelable("myInfo",it)
        }
        bundle.putParcelable("channel",item)

        findNavController().navigate(R.id.action_ChannelListFragment_to_ChattingFragment,bundle)

    }
}


