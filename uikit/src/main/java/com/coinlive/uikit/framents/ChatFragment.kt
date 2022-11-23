package com.coinlive.uikit.framents

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coinlive.chat.Coinlive
import com.coinlive.chat.api.model.Channel
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.api.model.enums.UserStatus
import com.coinlive.chat.firebase.listener.AmaListener
import com.coinlive.chat.firebase.listener.CmNoticeListener
import com.coinlive.chat.firebase.listener.MessageListener
import com.coinlive.chat.firebase.model.Ama
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.R
import com.coinlive.uikit.adapters.MessageEventListener
import com.coinlive.uikit.adapters.MessageListAdapter
import com.coinlive.uikit.databinding.FragmentCoinBinding
import com.coinlive.uikit.models.Notification
import com.coinlive.uikit.utils.Constants
import com.coinlive.uikit.utils.KeyboardHelper
import com.coinlive.uikit.viewmodels.ChatViewModel
import com.coinlive.uikit.views.SendMessageListener


class ChatFragment : BaseFragment(), MessageListener, CmNoticeListener, AmaListener, OnClickListener,
    SendMessageListener, MessageEventListener, ViewTreeObserver.OnGlobalLayoutListener {
    private val TAG = ChatFragment::class.java.simpleName
    private var binding: FragmentCoinBinding? = null
    private lateinit var viewModel: ChatViewModel
    private lateinit var adapter: MessageListAdapter


    private val scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
           if(newState != RecyclerView.SCROLL_STATE_DRAGGING) {
               recyclerView.layoutManager?.let {
                   val visibleItemPosition = (it as LinearLayoutManager).findFirstVisibleItemPosition()
                   if (visibleItemPosition > 0 && binding?.btnBottom?.visibility == View.GONE) {
                       binding?.btnBottom?.visibility = View.VISIBLE
                   } else if(visibleItemPosition == 0 && binding?.btnBottom?.visibility == View.VISIBLE){
                       binding?.btnBottom?.visibility = View.GONE
                   }
               }
           }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {



        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoggerHelper.d("onCreate")
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        arguments?.let { it ->
            val customerName = it.getString(Constants.argKeyCustomerName)
            val channel = it.getParcelable<Channel>(Constants.argKeyChannel)
            val myInfo = it.getParcelable<CustomerUser>(Constants.argKeyMyInfo)
            if (customerName == null || channel == null) {
                LoggerHelper.de("please check channel or myInfo or customerName")
                return@let
            }
            viewModel.initCoinLiveChat(myInfo, 50, channel, customerName, this, this, this, requireContext())
        }
        adapter =
            MessageListAdapter(coinName = viewModel.channel!!.name!!, myInfo = viewModel.myInfo, eventListener = this)

    }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        registerForActivityResult()
        LoggerHelper.d("onCreateView")

        binding = FragmentCoinBinding.inflate(inflater, container, false)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding!!.apply {
            lifecycleOwner = viewLifecycleOwner
            chattingViewModel = viewModel
            locale = Coinlive.locale.language
            clInput.setLoginUser(viewModel.myInfo != null)
            root.viewTreeObserver.addOnGlobalLayoutListener(this@ChatFragment)
        }
        viewModel.userStatus.observe(viewLifecycleOwner) { status ->
            updateUserStatus(status)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LoggerHelper.d("onViewCreated")

        setFragmentResultListener(Constants.reqKeyNotification) { _, bundle ->
            val newList = bundle.getParcelableArrayList<Notification>(Constants.argKeyList)
            newList?.let {
                viewModel.setNotification(it)
            }
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        if (viewModel.myInfo != null && viewModel.channel != null) {

            binding!!.rvList.apply {
                this.adapter = this@ChatFragment.adapter
                val layoutManager = LinearLayoutManager(requireContext())
                layoutManager.reverseLayout = true
                layoutManager.stackFromEnd = true
                this.layoutManager = layoutManager //레이아웃 매니저 연결
                addOnScrollListener(scrollListener)
            }
        }
        binding!!.ibtnDown.setOnClickListener(this)
        binding!!.ibtnMore.setOnClickListener(this)
        binding!!.refresh.setColorSchemeColors(ContextCompat.getColor(binding!!.refresh.context,
            R.color.swipe_progress))

        binding!!.refresh.setOnRefreshListener {
            viewModel.fetchMessage()
        }
        binding!!.clInput.setSendMessageListener(this)
        binding!!.clNew.setOnClickListener(this)
        binding!!.btnBottom.setOnClickListener(this)

    }

    override fun onDestroyView() {
        binding?.root?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        binding?.rvList?.removeOnScrollListener(scrollListener)

        super.onDestroyView()
        Log.i(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        viewModel.destroy()
    }

    private fun updateUserStatus(userStatus: UserStatus) {
        binding?.clInput?.setActiveUser(userStatus)
    }

    override fun getAma(ama: Ama) {
        binding?.clInput?.setAma(ama.endTime == null)
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

        if (chatList.size > 0) {
            val pushIndex = adapter.items.size

            LoggerHelper.d("oldMessages!!!, message Size : ${chatList.size} pushIndex : $pushIndex")
            binding?.refresh?.isRefreshing = false
            adapter.items.addAll(pushIndex, chatList)
            adapter.notifyItemRangeChanged(pushIndex - 1, chatList.size + 1)
            if (binding?.rvList?.layoutManager != null) {
                binding?.rvList?.scrollToPosition(pushIndex)
            }
        }
    }

    override fun newMessages(chat: Chat) {
        LoggerHelper.d("newMessages!!!: ${chat.messageId}")
        adapter.items.add(0, chat)

        binding?.rvList?.layoutManager?.let {
            val visibleItemPosition = (it as LinearLayoutManager).findFirstVisibleItemPosition()
            if (visibleItemPosition > 0) {
                binding?.newMessage = chat
                binding?.btnBottom?.visibility = View.GONE
            }
        }

        adapter.notifyItemInserted(0)

    }

    override fun failSendMessage(chat: Chat) {
//        TODO("Not yet implemented")
    }

    override fun retrySendMessageSuccess(messageId: String) {
//        TODO("Not yet implemented")
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cl_new -> {
                binding?.newMessage = null
                binding?.rvList?.scrollToPosition(0)
            }
            R.id.btn_bottom -> {
                binding?.btnBottom?.visibility = View.GONE
                binding?.rvList?.scrollToPosition(0)
            }

            R.id.ibtn_down -> {
                popFragment()
            }
            R.id.ibtn_more -> {
//                val wrapper = ContextThemeWrapper(v.context, R.style.PopupMenu)
//                val popupMenu = PopupMenu(wrapper,v)
                val popupMenu = PopupMenu(v.context, v)
                popupMenu.inflate(R.menu.menu_chat)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.m_shared -> {

                        }
                        R.id.m_notification -> {
                            if (viewModel.originNotiList.isNotEmpty()) {
                                val bundle = Bundle()

                                bundle.putParcelableArrayList(Constants.argKeyList,
                                    ArrayList(viewModel.originNotiList.map { notification ->
                                        notification.copy()
                                    }))
                                v.findNavController()
                                    .navigate(R.id.action_chatFragment_to_notificationSettingFragment, bundle)
                            }
                        }
                        R.id.m_tranlator -> {
                            v.findNavController().navigate(R.id.action_chatFragment_to_translatorSettingFragment)

                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }

    override fun sendMessage(text: String) {
        viewModel.sendMessage(text)
        binding?.rvList?.scrollToPosition(0)
    }

    override fun onClick(item: Chat, view: View) {
        binding?.clInput?.clearFocusEditeText()
        KeyboardHelper.hideKeyboard(view)
    }

    override fun onLongClick(item: Chat, view: View) {
        binding?.clInput?.clearFocusEditeText()
        KeyboardHelper.hideKeyboard(view)

    }

    override fun onProfileClick(item: Chat, view: View) {
        val bundle = Bundle()
        bundle.putString(Constants.argKeyNickName, item.userNickname)
        bundle.putString(Constants.argKeyUrl, item.profileUrl)
        bundle.putString(Constants.argKeyAppName, item.appName)

        view.findNavController().navigate(R.id.action_chatFragment_to_profileBottomSheet,bundle)

    }

    private var isShowKeyboard = false
    override fun onGlobalLayout() {
        val rootView = requireActivity().window.decorView.rootView

        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        val location = IntArray(2)
        rootView.getLocationOnScreen(location)
        val screenHeight = rootView.height
        val keyboardHeight = screenHeight - rect.height() - location[1]
        if (keyboardHeight > screenHeight * 0.15 && binding?.rvList?.layoutManager != null && !isShowKeyboard) {
            isShowKeyboard = true
            //show keyboard
            val linearLayoutManager: LinearLayoutManager = this.binding?.rvList?.layoutManager!! as LinearLayoutManager
            val visibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
            Log.e(TAG, "visible position : $visibleItemPosition")
            if (visibleItemPosition <= 7) {
                binding?.rvList?.scrollToPosition(0)
            }
        } else {
            // hide keyboard
            isShowKeyboard = false
        }
    }

}