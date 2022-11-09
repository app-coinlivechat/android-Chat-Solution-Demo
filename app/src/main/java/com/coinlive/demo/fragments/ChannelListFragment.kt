package com.coinlive.demo.fragments

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
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
    private var customerName: String? = null
    private var myInfo: CustomerUser? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var selectItem: Channel? = null
    private val adapter: ChannelListAdapter by lazy {
        ChannelListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.channel_list_menu, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.m_confirm -> {
                        selectItem?.let {
                            val bundle = Bundle()
                            customerName?.let {
                                bundle.putString("customerName", it)
                            }
                            myInfo?.let {
                                bundle.putParcelable("myInfo", it)
                            }
                            bundle.putParcelable("channel", selectItem)

                            findNavController().navigate(R.id.action_ChannelListFragment_to_ChattingFragment, bundle)
                        }

                        true
                    }
                    else -> false

                }
            }
        })

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

        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(requireContext()) //레이아웃 매니저 연결
        adapter.itemOnClick(this)
        viewModel.itemList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty() && adapter.items.size == 0) {
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
        selectItem = item
    }
}


