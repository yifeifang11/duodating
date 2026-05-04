package es.uc3m.duodating.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uc3m.duodating.data.DuoRepository
import es.uc3m.duodating.data.models.Message
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatViewModel(
    private val duoRepository: DuoRepository = DuoRepository()
) : ViewModel() {

    var messages by mutableStateOf<List<Message>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var myDuoId: String? = null
    private var otherDuoId: String? = null
    private var messagesJob: Job? = null

    /**
     * Starts listening to the chat between my duo and the other duo.
     * Safe to call repeatedly — calling with the same pair is a no-op,
     * calling with a new pair cancels the previous listener.
     */
    fun startListening(myDuoId: String, otherDuoId: String) {
        if (this.myDuoId == myDuoId && this.otherDuoId == otherDuoId && messagesJob?.isActive == true) {
            return
        }

        this.myDuoId = myDuoId
        this.otherDuoId = otherDuoId
        messagesJob?.cancel()

        messagesJob = viewModelScope.launch {
            isLoading = true
            duoRepository.listenToMessages(myDuoId, otherDuoId).collectLatest { newMessages ->
                messages = newMessages
                isLoading = false
            }
        }
    }

    fun sendMessage(text: String) {
        val my = myDuoId ?: return
        val other = otherDuoId ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            errorMessage = null
            val result = duoRepository.sendMessage(my, other, text)
            if (result.isFailure) {
                errorMessage = result.exceptionOrNull()?.message
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        messagesJob?.cancel()
    }
}