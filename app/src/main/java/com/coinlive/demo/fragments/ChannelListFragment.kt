package com.coinlive.demo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.coinlive.chat.api.model.Channel
import com.coinlive.demo.adapters.ChannelListAdapter
import com.coinlive.demo.databinding.FragmentChannelListBinding
import com.coinlive.demo.viewmodels.ChannelListFragmentViewModel
import com.coinlive.demo.viewmodels.LoginFragmentViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ChannelListFragment : Fragment() {

    private var _binding: FragmentChannelListBinding? = null
    private lateinit var viewModel: ChannelListFragmentViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChannelListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ChannelListFragmentViewModel::class.java]
        viewModel.getChannelList()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ChannelListAdapter(requireContext())
        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(requireContext()) //레이아웃 매니저 연결


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
}