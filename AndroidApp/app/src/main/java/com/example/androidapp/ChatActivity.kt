package com.example.androidapp

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
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
                val userMsg = Message(content = userMessage, isUser = true)
                messageAdapter.addMessage(userMsg)
                recyclerViewConversation.scrollToPosition(messages.size - 1)

                editTextUserInput.setText("")

                sendChatMessageToServer(userMessage)
            } else {
                Toast.makeText(this, "Te rog să introduci un mesaj", Toast.LENGTH_SHORT).show()
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

    private fun sendChatMessageToServer(message: String) {
        progressBar.visibility = View.VISIBLE

        val chatRequest = ChatRequest(message)

        chatService.sendChatMessage(chatRequest).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val chatResponse = response.body()
                    if (chatResponse != null) {
                        if (chatResponse.reply != null) {
                            val aiMsg = Message(content = chatResponse.reply, isUser = false)
                            messageAdapter.addMessage(aiMsg)
                            recyclerViewConversation.scrollToPosition(messages.size - 1)
                        } else {
                            val errorMsg = chatResponse.error ?: "Eroare necunoscută"
                            val errorMessage = Message(content = "Eroare: $errorMsg", isUser = false)
                            messageAdapter.addMessage(errorMessage)
                            recyclerViewConversation.scrollToPosition(messages.size - 1)
                        }
                    } else {
                        val errorMessage = Message(content = "Eroare: Răspunsul de la server este gol.", isUser = false)
                        messageAdapter.addMessage(errorMessage)
                        recyclerViewConversation.scrollToPosition(messages.size - 1)
                    }
                } else {
                    val errorMessage = Message(content = "Eroare server: ${response.message()}", isUser = false)
                    messageAdapter.addMessage(errorMessage)
                    recyclerViewConversation.scrollToPosition(messages.size - 1)
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                val failureMessage = Message(content = "Eroare de rețea: ${t.message}", isUser = false)
                messageAdapter.addMessage(failureMessage)
                recyclerViewConversation.scrollToPosition(messages.size - 1)
            }
        })
    }
}
