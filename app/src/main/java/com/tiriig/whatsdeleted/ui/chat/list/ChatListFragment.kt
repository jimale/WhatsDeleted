package com.tiriig.whatsdeleted.ui.chat.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tiriig.whatsdeleted.databinding.FragmentChatListBinding
import com.tiriig.whatsdeleted.ui.chat.ChatViewModel
import com.tiriig.whatsdeleted.utility.hide
import com.tiriig.whatsdeleted.utility.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListFragment : Fragment() {

    private val adapter = ChatListAdapter()
    private val viewModel: ChatViewModel by viewModels()

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchChat()
    }

    private fun fetchChat() {
        viewModel.getChatList.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) run {
                binding.emptyTv.show()
            } else binding.emptyTv.hide()
            adapter.submitList(it)
            binding.recyclerView.adapter = adapter
            binding.loading.hide()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}