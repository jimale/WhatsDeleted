package com.tiriig.whatsdeleted.ui.chat.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tiriig.whatsdeleted.data.model.Chat
import com.tiriig.whatsdeleted.data.model.ChatItem
import com.tiriig.whatsdeleted.databinding.FragmentChatDetailBinding
import com.tiriig.whatsdeleted.ui.main.MainActivity
import com.tiriig.whatsdeleted.ui.chat.ChatViewModel
import com.tiriig.whatsdeleted.utility.formatDate
import com.tiriig.whatsdeleted.utility.hide
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ChatDetailFragment : Fragment() {

    private val adapter = ChatDetailAdapter()
    private val viewModel: ChatViewModel by viewModels()

    private var _binding: FragmentChatDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchChatsByUser()
    }

    private fun fetchChatsByUser() {
        val user = arguments?.getString("user") ?: ""
        //Display user in toolbar title
        (activity as MainActivity).supportActionBar?.title = user

        viewModel.getChatByUser.observe(viewLifecycleOwner) {
            val groupedChats = groupChatsByDate(it)

            adapter.submitList(groupedChats)
            binding.recyclerView.adapter = adapter
            binding.loading.hide()
        }
    }


    fun groupChatsByDate(chats: List<Chat>): List<ChatItem> {
        // Group chats by formatted date
        val groupedChats = chats.groupBy {
            it.dateTime.formatDate()
        }

        val chatItems = mutableListOf<ChatItem>()

        // Iterate over each group of messages by date
        groupedChats.forEach { (date, messages) ->
            // Add the date header item
            chatItems.add(ChatItem.DateItem(date))

            // Add all the messages for that date
            messages.forEach { message ->
                chatItems.add(ChatItem.MessageItem(message))
            }
        }

        return chatItems
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}