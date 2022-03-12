package com.tiriig.soocelifariimaha.ui.chat.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tiriig.soocelifariimaha.databinding.FragmentChatDetailBinding
import com.tiriig.soocelifariimaha.ui.chat.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

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
        viewModel.getMessagesByUser(user).observe(this) {
            adapter.submitList(it)
            binding.recyclerView.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}