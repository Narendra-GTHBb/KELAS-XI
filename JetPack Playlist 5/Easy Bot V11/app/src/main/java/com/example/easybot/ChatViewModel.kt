package com.example.easybot

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.serialization.ExperimentalSerializationApi
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.serialization.MissingFieldException
import kotlinx.coroutines.launch

@OptIn(ExperimentalSerializationApi::class)
class ChatViewModel : ViewModel() {
    
    // Senarai mesej yang boleh diperhatikan oleh UI
    val messageList = mutableStateListOf<MessageModel>()
    
    // Model candidates: try these in order until one works
    private val modelCandidates = listOf(
        "gemini-pro",
        "gemini-1.0",
        "gemini-1.0-mini",
        "chat-bison"
    )

    init {
        // In DEBUG build, check available models at startup to help debugging
        if (Constants.APP_DEBUG) {
            viewModelScope.launch {
                if (Constants.GEMINI_API_KEY.isBlank() || Constants.GEMINI_API_KEY.contains("YOUR_GEMINI_API_KEY")) {
                    // don't attempt network calls
                    messageList.add(MessageModel("Debug: SEKAT - Sila tetapkan GEMINI_API_KEY anda di Constants.kt untuk pemeriksaan model.", MessageModel.ROLE_MODEL))
                } else {
                    val available = testAvailableModels()
                    val text = if (available.isEmpty()) "Debug: Tiada model tersedia atau API key tidak sah." else "Debug: Model(s) tersedia: ${available.joinToString(", ")}" 
                    messageList.add(MessageModel(text, MessageModel.ROLE_MODEL))
                }
            }
        }
    }
    
    /**
     * Hantar mesej kepada Gemini dan dapatkan respons
     * @param question Soalan atau mesej daripada pengguna
     */
    fun sendMessage(question: String) {
        // Jangan hantar mesej kosong
        if (question.isBlank()) return
        
        // Validate API key first
        if (Constants.GEMINI_API_KEY.isBlank() || Constants.GEMINI_API_KEY.contains("YOUR_GEMINI_API_KEY")) {
            messageList.add(MessageModel("Ralat: Sila tetapkan GEMINI_API_KEY anda di Constants.kt", MessageModel.ROLE_MODEL))
            return
        }

        viewModelScope.launch {
            try {
                // Tambah mesej pengguna ke senarai
                messageList.add(MessageModel(question, MessageModel.ROLE_USER))
                
                // Tambah mesej "typing..." sementara
                val typingMessage = MessageModel("Typing...", MessageModel.ROLE_MODEL)
                messageList.add(typingMessage)
                
                // Bina sejarah perbualan untuk konteks
                val history = messageList
                    .filter { it.message != "Typing..." }
                    .map { msg ->
                        content(role = msg.role) {
                            text(msg.message)
                        }
                    }
                
                // Cubaan: cuba beberapa model jika perlu
                val sendResult = trySendWithModels(history.dropLast(1), question)
                
                // sendResult akan mengandungi respons teks jika berjaya, atau null jika gagal
                val responseText = sendResult ?: "Maaf, tiada respons diterima."
                
                // Buang mesej "typing..." jika masih ada dan ganti dengan respons sebenar
                val typingIndex = messageList.indexOfFirst { it.message == "Typing..." && it.role == MessageModel.ROLE_MODEL }
                if (typingIndex >= 0) messageList.removeAt(typingIndex)
                
                // Tambah respons daripada Gemini
                messageList.add(MessageModel(responseText, MessageModel.ROLE_MODEL))
                
            } catch (e: Exception) {
                    val typingIndexCatch = messageList.indexOfFirst { it.message == "Typing..." && it.role == MessageModel.ROLE_MODEL }
                        if (typingIndexCatch >= 0) messageList.removeAt(typingIndexCatch)
                    // Tampilkan error mentah
                    val raw = e.localizedMessage ?: e.toString()
                    Log.e("ChatViewModel", "sendMessage exception", e)
                    messageList.add(
                        MessageModel(
                            "Ralat: $raw",
                            MessageModel.ROLE_MODEL
                        )
                    )
                }
        }
    }

        private suspend fun trySendWithModels(history: List<com.google.ai.client.generativeai.type.Content>, question: String): String? {
            var lastError: String? = null
            for (modelName in modelCandidates) {
                    Log.d("ChatViewModel", "Trying model: $modelName")
                try {
                    val generativeModel = GenerativeModel(
                        modelName = modelName,
                        apiKey = Constants.GEMINI_API_KEY
                    )
                    val chat = generativeModel.startChat(history = history)
                    val response = chat.sendMessage(question)
                    // If succeeded, return the text
                    response.text?.let { return it }
                } catch (e: MissingFieldException) {
                    // Known serialization issue where server responses don't always match expected schema
                    lastError = "Serialization error for model $modelName: ${e.localizedMessage ?: e.toString()}"
                    Log.e("ChatViewModel", lastError, e)
                } catch (e: Exception) {
                    // If model not found or unsupported, show a clearer message and try next model
                    val localized = e.localizedMessage ?: e.toString()
                    // If the error looks like the model is not found for this API version, make it clear
                    val notFound = localized.contains("not found", ignoreCase = true) || localized.contains("NOT_FOUND")
                    lastError = if (notFound) {
                        "Model $modelName tidak ditemui untuk versi API ini: $localized"
                    } else {
                        "Model $modelName gagal: $localized"
                    }
                    Log.w("ChatViewModel", lastError, e)
                    continue
                }
            }
            // Semua model gagal
            if (lastError != null) {
                return "(Tiada respons) $lastError"
            }
            return null
        }
    
    /**
     * Padam semua sejarah perbualan
     */
    fun clearChat() {
        messageList.clear()
    }

    private suspend fun testAvailableModels(): List<String> {
        val available = mutableListOf<String>()
        for (modelName in modelCandidates) {
            try {
                val generativeModel = GenerativeModel(
                    modelName = modelName,
                    apiKey = Constants.GEMINI_API_KEY
                )
                // Create a chat and try a minimal request to confirm availability
                val chat = generativeModel.startChat(history = emptyList())
                // A simple ping message to verify the model works
                val response = chat.sendMessage("Hello")
                if (response.text != null) {
                    available.add(modelName)
                }
            } catch (e: Exception) {
                Log.w("ChatViewModel", "Model $modelName not available: ${e.localizedMessage}")
            }
        }
        return available
    }
}
