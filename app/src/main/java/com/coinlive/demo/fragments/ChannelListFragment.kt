package com.coinlive.demo.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.coinlive.chat.api.model.Channel
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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var selectItem: Channel? = null
    private val adapter: ChannelListAdapter by lazy {
        ChannelListAdapter()
    }

    private val menuProvider = object : MenuProvider {
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
                        bundle.putParcelable("channel", selectItem)
                        bundle.putParcelable("myInfo", viewModel.myInfo)

                        // use FragmentManager
//                        val manager = requireActivity().supportFragmentManager
//                        manager.beginTransaction()
//                            .add(R.id.nav_host_fragment_content_main, ChattingFragment::class.java, bundle)
//                            .addToBackStack(null)
//                            .commit()

                        findNavController().navigate(R.id.action_ChannelListFragment_to_ChatFragment, bundle)
                    }

                    true
                }
                else -> false

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate")
        arguments?.let {
            customerName = it.getString("customerName")
        }
        viewModel = ViewModelProvider(this)[ChannelListFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        Log.d(TAG,"onCreateView")
        _binding = FragmentChannelListBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(menuProvider)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG,"onViewCreated")

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
        Log.d(TAG, "onDestroyView")

        super.onDestroyView()
        _binding = null
        val menuHost: MenuHost = requireActivity()
        menuHost.removeMenuProvider(menuProvider)
    }

    override fun onClick(item: Channel) {
        selectItem = item
    }
}


