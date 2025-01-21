package com.example.androidapp

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.api.RetrofitClient
import com.example.androidapp.models.ChatRequest
import com.example.androidapp.models.ChatResponse
import com.example.androidapp.models.Message
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerViewConversation: RecyclerView
    private lateinit var editTextUserInput: EditText
    private lateinit var buttonSend: ImageButton
    private lateinit var progressBar: ProgressBar

    private lateinit var messageAdapter: MessageAdapter
    private val messages = mutableListOf<Message>()

    private val chatService = RetrofitClient.chatService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        recyclerViewConversation = findViewById(R.id.recyclerViewConversation)
        editTextUserInput = findViewById(R.id.editTextUserInput)
        buttonSend = findViewById(R.id.buttonSend)
        progressBar = findViewById(R.id.progressBarLoading)

        messageAdapter = MessageAdapter(messages)
        recyclerViewConversation.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = messageAdapter
            this.scrollToPosition(messageAdapter.itemCount - 1)
        }

        editTextUserInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                buttonSend.performClick()
                true
            } else {
                false
            }
        }

        buttonSend.setOnClickListener {
            val userMessage = editTextUserInput.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                sendChatMessageToServer(userMessage)
                editTextUserInput.setText("")
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendChatMessageToServer(currentMessage: String) {
        val userMsg = Message(content = currentMessage, isUser = true)
        messages.add(userMsg)

        val conversationBuilder = StringBuilder()
        for (msg in messages) {
            if (msg.isUser) {
                conversationBuilder.append("You: ${msg.content}\n")
            } else {
                conversationBuilder.append("AI: ${msg.content}\n")
            }
        }
        val conversation = conversationBuilder.toString().trim()

        Log.d("ChatActivityLOG", "Sending conversation:\n$conversation")

        progressBar.visibility = View.VISIBLE

        val chatRequest = ChatRequest(conversation)

        chatService.sendChatMessage(chatRequest).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                progressBar.visibility = View.GONE
                Log.d("ChatActivityLOG", "Response received: ${response.raw()}")

                if (response.isSuccessful) {
                    val chatResponse = response.body()
                    Log.d("ChatActivityLOG", "Parsed response: $chatResponse")

                    if (chatResponse != null) {
                        if (!chatResponse.message.isNullOrEmpty()) {
                            val aiMsg = Message(content = chatResponse.message, isUser = false)
                            messages.add(aiMsg)

                            messageAdapter.notifyItemInserted(messages.size - 1)
                            recyclerViewConversation.scrollToPosition(messages.size - 1)
                        } else {
                            val errorMsg = chatResponse.error ?: "Unknown error"
                            Log.e("ChatActivityLOG", "Error from server: $errorMsg")
                            val errorMessage = Message(content = "Error: $errorMsg", isUser = false)
                            messages.add(errorMessage)
                            messageAdapter.notifyItemInserted(messages.size - 1)
                            recyclerViewConversation.scrollToPosition(messages.size - 1)
                        }
                    } else {
                        Log.e("ChatActivityLOG", "Response body is null!")
                        val errorMessage =
                            Message(content = "Error: Empty response from server.", isUser = false)
                        messages.add(errorMessage)
                        messageAdapter.notifyItemInserted(messages.size - 1)
                        recyclerViewConversation.scrollToPosition(messages.size - 1)
                    }
                } else {
                    Log.e(
                        "ChatActivityLOG",
                        "Server error: ${response.code()} - ${response.message()}"
                    )
                    val errorMessage = Message(
                        content = "Server error: ${response.code()} - ${response.message()}",
                        isUser = false
                    )
                    messages.add(errorMessage)
                    messageAdapter.notifyItemInserted(messages.size - 1)
                    recyclerViewConversation.scrollToPosition(messages.size - 1)
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("ChatActivityLOG", "Network failure: ${t.message}", t)
                val failureMessage =
                    Message(content = "Network error: ${t.message}", isUser = false)
                messages.add(failureMessage)
                messageAdapter.notifyItemInserted(messages.size - 1)
                recyclerViewConversation.scrollToPosition(messages.size - 1)
            }
        })
    }

}
