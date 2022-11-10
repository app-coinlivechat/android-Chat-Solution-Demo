package com.coinlive.uikit.framents

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.coinlive.chat.api.model.Channel
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.firebase.listener.AmaListener
import com.coinlive.chat.firebase.listener.CmNoticeListener
import com.coinlive.chat.firebase.listener.MessageListener
import com.coinlive.chat.firebase.model.Ama
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.R
import com.coinlive.uikit.adapters.MessageListAdapter
import com.coinlive.uikit.databinding.FragmentCoinBinding
import com.coinlive.uikit.viewmodels.ChattingViewModel
import kotlin.collections.ArrayList

class ChattingFragment : Fragment(), MessageListener, CmNoticeListener, AmaListener, OnClickListener {
    private val TAG = ChattingFragment::class.java.simpleName
    private var binding: FragmentCoinBinding? = null
    private lateinit var viewModel: ChattingViewModel
    private lateinit var adapter: MessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChattingViewModel::class.java]
        arguments?.let {
            val customerName = it.getString("customerName")
            val channel = it.getParcelable<Channel>("channel")
            val myInfo = it.getParcelable<CustomerUser>("myInfo")
            if (customerName == null || channel == null || myInfo == null) {
                LoggerHelper.de("please check channel or myInfo or customerName")
                return@let
            }
            viewModel.initCoinLiveChat(myInfo, channel, customerName, this, this, this, requireContext())
            viewModel.fetchMessage()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCoinBinding.inflate(inflater, container, false)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        binding!!.apply {
            lifecycleOwner = this@ChattingFragment
            chattingViewModel = viewModel
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        if (viewModel.myInfo != null && viewModel.channel != null) {
            adapter = MessageListAdapter(coinName = viewModel.channel!!.name!!, myInfo = viewModel.myInfo!!)
            binding!!.rvList.adapter = adapter
            val layoutManager = LinearLayoutManager(requireContext())
            layoutManager.reverseLayout = true
            layoutManager.stackFromEnd = true
            binding!!.rvList.layoutManager = layoutManager //레이아웃 매니저 연결
        }
        binding!!.ibtnDown.setOnClickListener(this)
        binding!!.ibtnMore.setOnClickListener(this)
    }

    override fun onDestroyView() {
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        super.onDestroyView()

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        viewModel.destroy()
    }

    override fun getAma(ama: Ama) {
//        TODO("Not yet implemented")
    }

    override fun getCmNotice(msg: String?) {
//        TODO("Not yet implemented")
    }

    override fun deletedMessage(chat: Chat) {
        adapter.items.remove(chat)
        adapter.notifyDataSetChanged()
    }

    override fun modifyMessage(chat: Chat) {
    }

    override fun oldMessages(chatList: ArrayList<Chat>, isReload: Boolean) {
        adapter.items.addAll(chatList)
        adapter.notifyItemRangeInserted(0, chatList.size)
    }

    override fun newMessages(chat: Chat) {
        adapter.items.add(0, chat)
        adapter.notifyItemInserted(0)
        binding?.rvList?.scrollToPosition(0)
    }

    override fun failSendMessage(chat: Chat) {
//        TODO("Not yet implemented")
    }

    override fun retrySendMessageSuccess(messageId: String) {
//        TODO("Not yet implemented")
    }

    private fun removeFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .remove(this)
            .commit()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ibtn_down -> {
                try {
                    val result = findNavController().popBackStack()
                    if(!result) {
                        removeFragment()
                    }
                } catch (exception: Exception) {
                    removeFragment()
                }
            }
            R.id.ibtn_more -> {

            }
        }
    }

}