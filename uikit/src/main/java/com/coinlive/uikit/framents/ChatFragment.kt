package com.coinlive.uikit.framents

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coinlive.chat.Coinlive
import com.coinlive.chat.api.ResponseCallback
import com.coinlive.chat.api.model.Channel
import com.coinlive.chat.api.model.CustomerUser
import com.coinlive.chat.api.model.ReportType
import com.coinlive.chat.exception.CoinliveException
import com.coinlive.chat.firebase.listener.AmaListener
import com.coinlive.chat.firebase.listener.CmNoticeListener
import com.coinlive.chat.firebase.listener.DynamicLinkListener
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
import com.coinlive.uikit.utils.PreferenceHelper
import com.coinlive.uikit.utils.PreferenceHelper.translatorLanguage
import com.coinlive.uikit.viewmodels.ChatViewModel
import com.coinlive.uikit.views.CoinLiveToast
import com.coinlive.uikit.views.MessageMenuView
import com.coinlive.uikit.views.OnInputViewListener
import com.coinlive.uikit.views.OnMessageMenuEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatFragment : BaseFragment(), MessageListener, CmNoticeListener, AmaListener, OnClickListener,
    OnInputViewListener, MessageEventListener, ViewTreeObserver.OnGlobalLayoutListener, DynamicLinkListener {
    private val TAG = ChatFragment::class.java.simpleName
    private var binding: FragmentCoinBinding? = null
    private lateinit var viewModel: ChatViewModel
    private lateinit var adapter: MessageListAdapter
    private val messageMenuView by lazy { MessageMenuView(requireContext()) }
    private val messagePopupWindow by lazy {
        PopupWindow(messageMenuView.rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
            .LayoutParams.MATCH_PARENT).apply {
            isOutsideTouchable = true
            isFocusable = false
            overlapAnchor = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private val scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState != RecyclerView.SCROLL_STATE_DRAGGING) {
                recyclerView.layoutManager?.let {
                    val visibleItemPosition = (it as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (visibleItemPosition > 0 && binding?.btnBottom?.visibility == View.GONE) {
                        binding?.btnBottom?.visibility = View.VISIBLE
                    } else if (visibleItemPosition == 0 && binding?.btnBottom?.visibility == View.VISIBLE) {
                        binding?.btnBottom?.visibility = View.GONE
                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        }
    }

    private val messageMenuEventListener: OnMessageMenuEventListener = object : OnMessageMenuEventListener {
        override fun onClickBlockMenu(chat: Chat, isReadyBlock: Boolean) {
            if (chat.memberId == null) return
            setFragmentResultListener(Constants.reqKeyBlock) { _, bundle ->
                val isConfirmClick = bundle.getBoolean(Constants.argKeyIsConfirmClick)
                if (isConfirmClick) {
                    if (isReadyBlock) {
                        viewModel.deleteBlock(chat.memberId!!, object : ResponseCallback<java.util.ArrayList<String>> {
                            override fun onSuccess(value: java.util.ArrayList<String>) {
                                adapter.deleteBlockUser(viewModel.myInfo!!, chat.memberId!!)

                            }

                            override fun onFail(exception: CoinliveException) {
                                Toast.makeText(requireContext(), "차단 해제 실패", Toast.LENGTH_SHORT).show()
                            }
                        })

                    } else {
                        viewModel.addBlock(chat.memberId!!, object : ResponseCallback<java.util.ArrayList<String>> {
                            override fun onSuccess(value: java.util.ArrayList<String>) {
                                adapter.addBlockUser(viewModel.myInfo!!, chat.memberId!!)
                            }

                            override fun onFail(exception: CoinliveException) {
                                Toast.makeText(requireContext(), "차단 추가 실패", Toast.LENGTH_SHORT).show()
                            }

                        })
                    }
                }
            }
            binding?.root?.findNavController()?.navigate(R.id.action_chatFragment_to_blockDialog, bundleOf(Constants
                .argKeyIsReadyBlock to isReadyBlock))
            messageMenuDismiss()
        }

        override fun onClickCopyMenu(chat: Chat) {
            messageMenuDismiss()
            (if (Coinlive.locale.language.equals("ko")) chat.koMessage else chat.enMessage)?.let {
                setClipboard(it)
            }

        }

        override fun onClickDeleteMenu(chat: Chat) {
            viewModel.deleteMessage(chat)
            messageMenuDismiss()
        }

        override fun onClickReportMenu(chat: Chat) {
            if (chat.memberId == null) return
            if (viewModel.reportType.size == 0) return
            binding?.root?.findNavController()?.navigate(R.id.action_chatFragment_to_reportDialog, bundleOf(Constants
                .argKeyReportTypeList to viewModel.reportType))
            setFragmentResultListener(Constants.reqKeyReport) { _, bundle ->
                val isConfirmClick = bundle.getBoolean(Constants.argKeyIsConfirmClick)
                if (isConfirmClick) {
                    val selectType = bundle.getParcelable<ReportType>(Constants.argKeyReportType) ?: run {
                        Log.e(TAG, "selectType is null!!!!!")
                        return@setFragmentResultListener
                    }
                    viewModel.report(selectType, chat.memberId!!, object : ResponseCallback<Boolean> {
                        override fun onSuccess(value: Boolean) {
                            Toast.makeText(requireContext(), "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show()
                        }

                        override fun onFail(exception: CoinliveException) {
                            Toast.makeText(requireContext(), "신고 접수 실패하였습니다.,", Toast.LENGTH_SHORT).show()
                        }

                    })
                }
            }
            messageMenuDismiss()
        }

        override fun onClickCancelMenu() {
            messageMenuDismiss()
        }

        override fun onDeleteEmoji(chat: Chat, key: String) {
            viewModel.deleteEmoji(chat, key)
            messageMenuDismiss()
        }

        override fun onAddEmoji(chat: Chat, key: String) {
            viewModel.addEmoji(chat, key)
            messageMenuDismiss()
        }

        override fun onClickOutSide() {
            messageMenuDismiss()
        }

    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoggerHelper.d("onCreate")
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        arguments?.let { it ->
            val customerName = it.getString(Constants.argKeyCustomerName)
            val channel = it.getParcelable<Channel>(Constants.argKeyChannel)
            val myInfo = it.getParcelable<CustomerUser>(Constants.argKeyMyInfo)
            if (customerName == null || channel == null) {
                LoggerHelper.de("please check channel or myInfo or customerName")
                return@let
            }
            messageMenuView.setMyMid(myInfo?.id)
            viewModel.initCoinLiveChat(myInfo, 50, channel, customerName, this, this, this, requireContext())
        }
        messageMenuView.setListener(messageMenuEventListener)
        adapter =
            MessageListAdapter(coinName = viewModel.channel!!.name!!, myInfo = viewModel.myInfo, eventListener = this)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        LoggerHelper.d("onCreateView")

        binding = FragmentCoinBinding.inflate(inflater, container, false)
        binding!!.apply {
            lifecycleOwner = viewLifecycleOwner
            chattingViewModel = viewModel
            locale = Coinlive.locale.language
            root.viewTreeObserver.addOnGlobalLayoutListener(this@ChatFragment)
        }

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LoggerHelper.d("onViewCreated")

        setFragmentResultListener(Constants.reqKeyTranslator) { _, bundle ->
            val originLanguage = bundle.getString(Constants.argKeyOldTransLanguage) ?: return@setFragmentResultListener
            val newSelectLanguage = PreferenceHelper.defaultPreference(requireContext()).translatorLanguage
            if (originLanguage != newSelectLanguage) {
                adapter.clearTansMsg()
            }
        }

        setFragmentResultListener(Constants.reqKeyNotification) { _, bundle ->
            val newList =
                bundle.getParcelableArrayList<Notification>(Constants.argKeyList) ?: return@setFragmentResultListener
            viewModel.setNotification(newList)
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        if (viewModel.myInfo != null && viewModel.channel != null) {

            binding!!.rvList.apply {
                this.adapter = this@ChatFragment.adapter
                val layoutManager = LinearLayoutManager(requireContext())
                layoutManager.reverseLayout = true
//                layoutManager.stackFromEnd = true
                layoutManager.isItemPrefetchEnabled = true
                this.layoutManager = layoutManager //레이아웃 매니저 연결
                addOnScrollListener(scrollListener)
            }
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getOldFailMessage()?.let {
                    adapter.addFailItems(it)
                }
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
        messageMenuDismiss()
        binding?.vAma?.closeCountDownTimer()
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


    override fun getAma(ama: Ama) {
        binding?.isAma = ama.endTime == null
        binding?.ama = ama
    }

    override fun getCmNotice(msg: String?) {
        LoggerHelper.d("cm : $msg")
        viewModel.cm.value = msg
    }


    override fun modifyMessage(chat: Chat) {
        adapter.modifyMessage(chat)
    }

    override fun oldMessages(chatList: ArrayList<Chat>, isReload: Boolean) {
        if (chatList.size > 0) {
            adapter.addOldMessage(chatList)
            if (binding?.rvList?.layoutManager != null) {
                binding?.rvList?.scrollToPosition(adapter.itemCount)
            }
        }
    }

    override fun newMessages(chat: Chat) {
        adapter.addNewItem(chat)

        binding?.rvList?.layoutManager?.let {
            val visibleItemPosition = (it as LinearLayoutManager).findFirstVisibleItemPosition()
            if (visibleItemPosition > 0) {
                binding?.newMessage = chat
                binding?.btnBottom?.visibility = View.GONE
            }
        }
    }

    override fun deletedMessage(chat: Chat) {
        adapter.deleteFailItem(chat.messageId)
    }

    override fun failSendMessage(chat: Chat) {
        adapter.addFailItem(chat)
    }

    override fun retrySendMessageSuccess(messageId: String) {
        adapter.deleteFailItem(messageId)

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
                        R.id.m_shared -> viewModel.getDynamicLink(this)
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
                        R.id.m_tranlator -> v.findNavController()
                            .navigate(R.id.action_chatFragment_to_translatorSettingFragment)
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

    override fun onLongClick(item: Chat, view: View, viewType: Int) {

        if (item.memberId != null) {
            binding?.rvList?.suppressLayout(true)
            messageMenuView.addMessage(view, viewType, item,adapter)
            messageMenuView.setChat(item)
            messageMenuView.setIsReadyBlock(viewModel.isBlockUser(item.memberId!!))
            messagePopupWindow.showAsDropDown(view)
        }
    }

    override fun onProfileClick(item: Chat, view: View) {
        val bundle = Bundle()
        bundle.putString(Constants.argKeyNickName, item.userNickname)
        bundle.putString(Constants.argKeyUrl, item.profileUrl)
        bundle.putString(Constants.argKeyAppName, item.appName)

        view.findNavController().navigate(R.id.action_chatFragment_to_profileBottomSheet, bundle)
    }

    override fun addEmoji(item: Chat, emojiKey: String) {
        viewModel.addEmoji(item, emojiKey)
    }

    override fun deleteEmoji(item: Chat, emojiKey: String) {
        viewModel.deleteEmoji(item, emojiKey)

    }

    override fun onClickDelete(item: Chat, view: View) {
        viewModel.deleteMessage(item)
    }

    override fun onClickRetry(item: Chat, view: View) {
        viewModel.retryFailMessage(item)
    }

    private var isShowKeyboard = false
    override fun onGlobalLayout() {
        val rootView = activity?.window?.decorView?.rootView ?: return

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
            if (visibleItemPosition in 1..7) {
                binding?.rvList?.scrollToPosition(0)
            }
        } else {
            // hide keyboard
            isShowKeyboard = false
        }
    }

    override fun onSuccess(uri: Uri) {
        setClipboard(uri.toString())
    }

    override fun onFail(exception: Exception) {
        exception.printStackTrace()
        showToast("공유 링크 생성 실패")
    }

    private fun setClipboard(text: String) {
        val clipboard: ClipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
        showToast("URL이 복사 완료 되었습니다.")
    }

    private fun showToast(msg: String) {
        binding?.rvList?.let {
            CoinLiveToast.make(it, msg).show()

        }
    }

    private fun messageMenuDismiss() {
        binding?.rvList?.suppressLayout(false)
        messagePopupWindow.dismiss()

    }

}