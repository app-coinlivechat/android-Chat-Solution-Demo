package com.coinlive.uikit.viewholders

import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import com.coinlive.chat.Coinlive
import com.coinlive.chat.firebase.model.Chat
import com.coinlive.chat.util.LoggerHelper
import com.coinlive.uikit.R
import com.coinlive.uikit.adapters.MessageEventListener
import com.coinlive.uikit.bindingadapterex.BindingAdapters
import com.coinlive.uikit.bindingadapterex.BindingAdapters.loadImageMessage
import com.coinlive.uikit.databinding.ViewOtherImageChatItemBinding
import com.coinlive.uikit.utils.ViewUtils.dpToPx
import com.coinlive.uikit.views.OnEmojiEventListener


class OtherImageMessageViewHolder(
    private val binding: ViewOtherImageChatItemBinding,
    isMessageMenu: Boolean = false,
    private val eventListener: MessageEventListener? = null,
    private val itemListener: ItemListener?,
) : BaseViewHolder(binding, eventListener, itemListener) {
//    val gridLayoutManager = GridLayout.LayoutParams()

    init {
        binding.isMessageMenu = isMessageMenu
        if (!isMessageMenu) {

            binding.glList.setOnLongClickListener {
                onLonClick(it)
            }

            binding.ivOne.setOnLongClickListener { onLonClick(it) }

            binding.ibtnProfile.setOnClickListener {
                itemListener?.getItem(adapterPosition)?.let { chat ->
                    eventListener?.onProfileClick(chat, it)
                }
            }

            binding.emoji.setEmojiListener(object : OnEmojiEventListener {
                override fun addEmoji(key: String) {
                    itemListener?.getItem(adapterPosition)?.let { chat ->
                        eventListener?.addEmoji(chat, key)
                    }
                }

                override fun deleteEmoji(key: String) {
                    itemListener?.getItem(adapterPosition)?.let { chat ->
                        eventListener?.deleteEmoji(chat, key)
                    }
                }
            })
        }

    }

//    private fun itemImageLonClick() {
//        onLonClick(binding.rvList)
//    }

    override fun bind(item: Chat, isSameDate: Boolean, isRoundMessage: Boolean) {
        super.bind(item, isSameDate, isRoundMessage)
        val myInfo = itemListener?.getMyInfo()

        binding.chat = item
        binding.locale = Coinlive.locale.language
        binding.isRoundMessage = isRoundMessage
        binding.isSameDate = isSameDate
        binding.isBlockUser = myInfo?.blockUserMidList?.contains(item.memberId) ?: false

        binding.emoji.setMyMid(myInfo?.id)

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.clRoot)

        if (binding.isBlockUser!!) {
            binding.ivOne.setImageResource(R.drawable.img_block_image)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.ivOne.id, ConstraintSet.END)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.BOTTOM, binding.ivOne.id, ConstraintSet.BOTTOM)
            constraintSet.connect(binding.emoji.id, ConstraintSet.TOP, binding.ivOne.id, ConstraintSet.BOTTOM)
            constraintSet.applyTo(binding.clRoot)
            return
        }

        // <ImageView
        //                android:background="@android:color/black"
        //                android:layout_column="0"
        //                android:layout_row="4"
        //                android:layout_columnSpan="3"
        //                android:layout_gravity="fill"
        //                android:minWidth="75dp"
        //                android:minHeight="75dp"/>

        if (item.images!!.size > 1) {
            LoggerHelper.d("no block user image!!")
            binding.glList.removeAllViews()
            binding.glList.apply {
                columnCount = if (item.images!!.size > 4) 3 else 2
                val height = if (item.images!!.size > 4) 75F else 119F
                val width = this.dpToPx(if (item.images!!.size > 4) 75F else 119F)
                val divider = item.images!!.size / columnCount
//                LoggerHelper.de("(item.images!!.size : ${item.images!!.size}, divider : ${divider + (if ((item.images!!.size % columnCount) > 0) 1 else 0)}")

                rowCount = divider + (if ((item.images!!.size % columnCount) > 0) 1 else 0)
//                val lastIndex = item.images!!.size - 1
                item.images!!.forEachIndexed { index, it ->

                    val columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1,  GridLayout.FILL,1f)
                    val rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, GridLayout.FILL,1f)

//                    if((item.images!!.size % 2) > 0) {  // 홀수

//                        if(index == lastIndex || index == (lastIndex -1)) {
//                            if(item.images!!.size - index <=2) {
//                                columnSpec = GridLayout.spec(0 ,GridLayout.FILL,1.5F)
//                                rowSpec = GridLayout.spec(rowCount - 1 ,GridLayout.FILL,1.5F)
//                                width = ViewGroup.LayoutParams.WRAP_CONTENT
//                            }
//                        }
//                    } else if(index ==9) {  //item index 9 (총 10개)
//                        columnSpec = GridLayout.spec(0, GridLayout.FILL,3F)
//                        rowSpec = GridLayout.spec(rowCount - 1 ,GridLayout.FILL,1.5F)
//                        width = ViewGroup.LayoutParams.WRAP_CONTENT
//                    }
                    val imageView = ImageView(context)
                    imageView.layoutParams = ViewGroup.LayoutParams(width, imageView.dpToPx(height))
                    loadImageMessage(imageView,it)
                    val gridParam: GridLayout.LayoutParams = GridLayout.LayoutParams(rowSpec,columnSpec)
                    binding.glList.addView(imageView,gridParam)
                }


//                this.layoutManager = gridLayoutManager
//                this.addItemDecoration(object : RecyclerView.ItemDecoration() {
//                    override fun getItemOffsets(
//                        outRect: Rect,
//                        view: View,
//                        parent: RecyclerView,
//                        state: RecyclerView.State,
//                    ) {
//                        super.getItemOffsets(outRect, view, parent, state)
//                        // Column Index
//                        val columIndex = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
//
//                        val spanCount = gridLayoutManager.spanCount
//                        // Item 포지션
//                        val position: Int = parent.getChildLayoutPosition(view)
//                        val divide = if (position == item.images!!.size - 1) 0 else (position + 1) % spanCount
//
//                        LoggerHelper.de("parent.childCount : ${parent.childCount}, position : $position , spanCount :$spanCount, divide :$divide")
//                        if (divide > 0) {
//                            //좌측 Spacing 절반
//                            outRect.right = 4
//                        } else {
//                            outRect.right = 0
//                        }
//                        // 상단 탑 Spacing 맨 위에 포지션 0, 1은 Spacing을 안 줍니다.
//                        if (columIndex == 0) {
//                            outRect.top = 0
//                        } else {
//                            outRect.top = 4
//                        }
//                    }
//                })
//                this.adapter = ImageMessageListAdapter(item.images!!) { itemImageLonClick() }

            }

            constraintSet.connect(binding.emoji.id, ConstraintSet.TOP, binding.glList.id, ConstraintSet.BOTTOM)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.glList.id, ConstraintSet.END)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.BOTTOM, binding.glList.id, ConstraintSet.BOTTOM)
        } else {
            loadImageMessage(binding.ivOne, item.images!![0])
            constraintSet.connect(binding.tvTime.id, ConstraintSet.START, binding.ivOne.id, ConstraintSet.END)
            constraintSet.connect(binding.tvTime.id, ConstraintSet.BOTTOM, binding.ivOne.id, ConstraintSet.BOTTOM)
            constraintSet.connect(binding.emoji.id, ConstraintSet.TOP, binding.ivOne.id, ConstraintSet.BOTTOM)
        }
        constraintSet.applyTo(binding.clRoot)
    }
}