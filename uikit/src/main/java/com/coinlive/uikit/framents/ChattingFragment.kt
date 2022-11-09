package com.coinlive.uikit.framents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.coinlive.chat.api.model.Channel
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.firebase.listener.AmaListener
import com.coinlive.chat.firebase.listener.CmNoticeListener
import com.coinlive.chat.firebase.listener.MessageListener
import com.coinlive.chat.firebase.model.Ama
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.util.CalendarHelper
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.adapters.MessageListAdapter
import com.coinlive.uikit.databinding.FragmentCoinBinding
import com.coinlive.uikit.viewmodels.ChattingViewModel
import java.util.*
import kotlin.collections.ArrayList

class ChattingFragment : Fragment(), MessageListener, CmNoticeListener, AmaListener {
    private val TAG = ChattingFragment::class.java.simpleName
    private var _binding: FragmentCoinBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChattingViewModel
    private lateinit var adapter: MessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChattingViewModel::class.java]
        arguments?.let {
            val customerName = it.getString("customerName")
            val channel = it.getParcelable<Channel>("channel")
            val myInfo = it.getParcelable<CustomerUser>("myInfo")
            if (customerName == null || channel == null || myInfo == null ) {
                LoggerHelper.de("please check channel or myInfo or customerName")
                return@let
            }
            viewModel.initCoinLiveChat(myInfo,channel, customerName, this, this, this,requireContext())
            viewModel.fetchMessage()
//            test()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCoinBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun test(standardTime: Calendar = CalendarHelper.getTodayMidnight()) {
        standardTime.set(Calendar.DATE, standardTime.get(Calendar.DATE) - 1)
        val now = CalendarHelper.nowCalendar()
        val result = now.get(Calendar.DATE) - standardTime.get(Calendar.DATE)
        LoggerHelper.d("result : $result, standardTime : ${standardTime.timeInMillis}")
        if(result in 0..7) {
            test(standardTime)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if(viewModel.myInfo != null && viewModel.channel != null) {
            adapter = MessageListAdapter(coinName = viewModel.channel!!.name!!,myInfo = viewModel.myInfo!!)
            binding.rvList.adapter = adapter
            val layoutManager =LinearLayoutManager(requireContext())
            layoutManager.reverseLayout = true
            layoutManager.stackFromEnd = true
            binding.rvList.layoutManager = layoutManager //레이아웃 매니저 연결
        }

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
        adapter.items.add(0,chat)
        adapter.notifyItemInserted(0)
        binding.rvList.scrollToPosition(0)
    }

    override fun failSendMessage(chat: Chat) {
//        TODO("Not yet implemented")
    }

    override fun retrySendMessageSuccess(messageId: String) {
//        TODO("Not yet implemented")
    }

}