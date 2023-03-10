package com.coinlive.demo.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.coinlive.chat.api.model.Channel
import com.coinlive.chat.api.model.Customer
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.demo.R
import com.coinlive.demo.adapters.ChannelItemOnClick
import com.coinlive.demo.adapters.ChannelListAdapter
import com.coinlive.demo.databinding.FragmentChannelListBinding
import com.coinlive.demo.viewmodels.ChannelListFragmentViewModel
import com.coinlive.uikit.utils.Constants
import com.coinlive.uikit.views.CoinLiveToast

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ChannelListFragment : Fragment(), ChannelItemOnClick {

    private var _binding: FragmentChannelListBinding? = null
    private lateinit var viewModel: ChannelListFragmentViewModel
    private var customer: Customer? = null
    private val binding get() = _binding!!
    private var selectItem: Channel? = null
    private val adapter: ChannelListAdapter by lazy {
        ChannelListAdapter()
    }

    private val callback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveLoginFragment()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoggerHelper.d("onCreate")
        arguments?.let {
            customer = it.getParcelable(Constants.argKeyCustomer)
        }
        viewModel = ViewModelProvider(this)[ChannelListFragmentViewModel::class.java]
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

        LoggerHelper.d("onCreateView")
        _binding = FragmentChannelListBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LoggerHelper.d("onViewCreated")
        viewModel.loadMyInfo()
        LoggerHelper.d("adapter.selectPosition : ${adapter.selectPosition}")

        if(adapter.selectPosition > -1) {
            binding.tvConfirm.setTextColor(requireContext().getColor(R.color.blue_text))
        }
        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(requireContext()) //레이아웃 매니저 연결
        adapter.itemOnClick(this)
        viewModel.itemList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty() && adapter.items.size == 0) {
                adapter.items.addAll(it)
                adapter.notifyItemRangeInserted(0, it.size)
            }
        }
        binding.tvConfirm.setOnClickListener {
            selectItem?.let {channel ->
                if(viewModel.myInfoLoading) {
                    CoinLiveToast.make(binding.root, "내 정보를 불러오는 중입니다.\n잠시후 다시 시도해주세요").show()
                    return@let
                }

                val bundle = Bundle()
                customer?.let {
                    bundle.putParcelable("customer", it)
                }
                bundle.putParcelable("channel", channel)
                bundle.putParcelable("myInfo", viewModel.myInfo)

                findNavController().navigate(R.id.action_ChannelListFragment_to_ChatFragment, bundle)
            }
        }

        binding.ibtnBack.setOnClickListener {
            moveLoginFragment()
        }
    }

    private fun moveLoginFragment() {
        if(::viewModel.isInitialized) {
            viewModel.logout()
        }

        findNavController().navigate(R.id.action_ChannelListFragment_to_LoginFragment)
    }

    override fun onDestroyView() {
        LoggerHelper.d("onDestroyView")

        super.onDestroyView()
        _binding = null
    }

    override fun onClick(item: Channel) {
        selectItem = item
        binding.tvConfirm.setTextColor(requireContext().getColor(R.color.blue_text))
    }
}


