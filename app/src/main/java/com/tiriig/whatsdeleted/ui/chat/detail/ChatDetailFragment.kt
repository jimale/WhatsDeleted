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

    val sampleData = listOf(
        // Date Item for a specific date
        ChatItem.DateItem(date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(System.currentTimeMillis() - 86400000L))), // 1 day ago

        // Messages
        ChatItem.MessageItem(Chat(id = "1", user = "Alice", message = "Hey, how's everything going?", dateTime = System.currentTimeMillis() - 86400000L, app = "WhatsApp")),
        ChatItem.MessageItem(Chat(id = "2", user = "Bob", message = "It's going great, thanks for asking!", dateTime = System.currentTimeMillis() - 81000000L, app = "WhatsApp")),
        ChatItem.MessageItem(Chat(id = "3", user = "Alice", message = "Got any plans for the weekend?", dateTime = System.currentTimeMillis() - 75600000L, app = "WhatsApp")),

        // Another Date Item for a different day
        ChatItem.DateItem(date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(System.currentTimeMillis() - 432000000L))), // 5 days ago

        // More Messages
        ChatItem.MessageItem(Chat(id = "4", user = "Bob", message = "I'm thinking of going hiking.", dateTime = System.currentTimeMillis() - 431000000L, app = "WhatsApp")),
        ChatItem.MessageItem(Chat(id = "5", user = "Alice", message = "Sounds fun! I should join you sometime.", dateTime = System.currentTimeMillis() - 430000000L, app = "WhatsApp")),
        ChatItem.MessageItem(Chat(id = "6", user = "Bob", message = "Yeah, let's plan for next week.", dateTime = System.currentTimeMillis() - 429000000L, app = "WhatsApp")),

        // Date Item for a week ago
        ChatItem.DateItem(date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(System.currentTimeMillis() - 604800000L))), // 7 days ago

        // More Messages
        ChatItem.MessageItem(Chat(id = "7", user = "Alice", message = "I watched the latest movie last night!", dateTime = System.currentTimeMillis() - 603000000L, app = "WhatsApp")),
        ChatItem.MessageItem(Chat(id = "8", user = "Bob", message = "How was it? I'm thinking of watching it too.", dateTime = System.currentTimeMillis() - 602000000L, app = "WhatsApp")),
        ChatItem.MessageItem(Chat(id = "9", user = "Alice", message = "It was awesome! Highly recommend it.", dateTime = System.currentTimeMillis() - 601000000L, app = "WhatsApp")),
        ChatItem.MessageItem(Chat(id = "10", user = "Bob", message = "Great! I'll check it out this weekend.", dateTime = System.currentTimeMillis() - 600000000L, app = "WhatsApp"))
    )

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}