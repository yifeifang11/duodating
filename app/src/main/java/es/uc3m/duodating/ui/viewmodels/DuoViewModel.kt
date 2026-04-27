package es.uc3m.duodating.ui.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uc3m.duodating.data.DuoRepository
import es.uc3m.duodating.data.models.DuoInvite
import es.uc3m.duodating.data.models.User
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DuoViewModel(
    private val duoRepository: DuoRepository = DuoRepository()
) : ViewModel() {

    var currentUser by mutableStateOf<User?>(null)
        private set

    var incomingInvite by mutableStateOf<DuoInvite?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        observeUserStatus()
    }

    private fun observeUserStatus() {
        viewModelScope.launch {
            duoRepository.listenToUserStatus().collectLatest { user ->
                currentUser = user
                if (user != null && user.status != "LINKED") {
                    checkForInvites(user.phoneNumber)
                }
            }
        }
    }

    private fun checkForInvites(phone: String) {
        viewModelScope.launch {
            duoRepository.listenToIncomingInvites(phone).collectLatest { invite ->
                incomingInvite = invite
            }
        }
    }

    fun sendInvite(targetPhone: String) {
        viewModelScope.launch {
            isLoading = true
            val result = duoRepository.sendInvite(targetPhone)
            if (result.isFailure) {
                errorMessage = result.exceptionOrNull()?.message
            }
            isLoading = false
        }
    }

    fun cancelInvite() {
        viewModelScope.launch {
            isLoading = true
            val result = duoRepository.cancelInvite()
            if (result.isFailure) {
                errorMessage = result.exceptionOrNull()?.message
            }
            isLoading = false
        }
    }

    fun acceptInvite() {
        val invite = incomingInvite ?: return
        viewModelScope.launch {
            isLoading = true
            val result = duoRepository.acceptInvite(invite)
            if (result.isFailure) {
                errorMessage = result.exceptionOrNull()?.message
            }
            isLoading = false
        }
    }

    fun declineInvite() {
        val invite = incomingInvite ?: return
        viewModelScope.launch {
            isLoading = true
            val result = duoRepository.declineInvite(invite.inviteId, invite.senderUid)
            if (result.isFailure) {
                errorMessage = result.exceptionOrNull()?.message
            } else {
                incomingInvite = null
            }
            isLoading = false
        }
    }

    fun saveDuoProfile(
        duoId: String,
        questionChoice: String,
        questionAnswer: String,
        imageUri: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = duoRepository.saveDuoProfile(duoId, questionChoice, questionAnswer, imageUri)
            if (result.isSuccess) {
                onSuccess()
            } else {
                errorMessage = result.exceptionOrNull()?.message
            }
            isLoading = false
        }
    }
}
