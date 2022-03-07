package com.tiriig.soocelifariimaha.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tiriig.soocelifariimaha.data.model.Chat
import com.tiriig.soocelifariimaha.databinding.ItemChatBinding
import com.tiriig.soocelifariimaha.ui.util.getTime


class MainAdapter : ListAdapter<Chat, MainAdapter.ViewHolder>(MainDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //Used to store the clicked data
        private var currentData: Chat? = null

        init {
            binding.root.setOnClickListener {
                val intent = Intent(it.context, ChatDetailActivity::class.java)
                intent.putExtra("user", currentData?.user)
                it.context.startActivity(intent)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: Chat?) {
            item?.let {
                binding.user.text = item.user
                binding.lastMessage.text = item.message
                binding.date.text = item.time.getTime()
            }
            currentData = item
        }
    }

    class MainDiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.user == newItem.user
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

}