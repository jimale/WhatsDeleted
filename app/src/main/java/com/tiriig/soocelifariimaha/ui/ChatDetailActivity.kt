package com.tiriig.soocelifariimaha.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tiriig.soocelifariimaha.databinding.ActivityChatDetailBinding
import com.tiriig.soocelifariimaha.ui.util.ChatDetailAdapter

class ChatDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatDetailBinding
    private val adapter = ChatDetailAdapter()
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        fetchChatsByUser()
    }

    private fun fetchChatsByUser() {
        val user = intent.getStringExtra("user") ?: ""
        viewModel.getMessagesByUser(user).observe(this) {
            adapter.submitList(it)
            binding.recyclerView.adapter = adapter
        }
    }
}