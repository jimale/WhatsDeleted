package com.tiriig.whatsdeleted.ui.chat.detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tiriig.whatsdeleted.R
import com.tiriig.whatsdeleted.data.model.Chat
import com.tiriig.whatsdeleted.data.model.ChatItem
import com.tiriig.whatsdeleted.databinding.ItemDateBinding
import com.tiriig.whatsdeleted.databinding.ItemMessageBinding
import com.tiriig.whatsdeleted.utility.formatTime

class ChatDetailAdapter :
    ListAdapter<ChatItem, RecyclerView.ViewHolder>(ChatDetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE -> {
                val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DateViewHolder(binding)
            }
            VIEW_TYPE_MESSAGE -> {
                val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        when (holder) {
            is DateViewHolder -> holder.bind((item as ChatItem.DateItem).date)
            is MessageViewHolder -> holder.bind((item as ChatItem.MessageItem).chat)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatItem.DateItem -> VIEW_TYPE_DATE
            is ChatItem.MessageItem -> VIEW_TYPE_MESSAGE
        }
    }

    inner class DateViewHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: String) {
            binding.dateTv.text = date
        }
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(chat: Chat) {
            if (chat.isDeleted) binding.root.setBackgroundResource(R.drawable.deleted_message_background)
            binding.message.text = chat.message
            binding.date.text = chat.dateTime.formatTime()
        }
    }

    class ChatDetailDiffCallback : DiffUtil.ItemCallback<ChatItem>() {
        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return when {
                oldItem is ChatItem.DateItem && newItem is ChatItem.DateItem -> oldItem.date == newItem.date
                oldItem is ChatItem.MessageItem && newItem is ChatItem.MessageItem -> oldItem.chat.id == newItem.chat.id
                else -> false
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_DATE = 0
        private const val VIEW_TYPE_MESSAGE = 1
    }
}
