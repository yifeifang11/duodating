package es.uc3m.duodating.ui.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uc3m.duodating.data.DuoRepository
import es.uc3m.duodating.data.models.Duo
import es.uc3m.duodating.data.models.DuoInvite
import es.uc3m.duodating.data.models.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DuoViewModel(
    private val duoRepository: DuoRepository = DuoRepository()
) : ViewModel() {

    var currentUser by mutableStateOf<User?>(null)
        private set

    var incomingInvite by mutableStateOf<DuoInvite?>(null)
        private set

    var currentDuo by mutableStateOf<Duo?>(null)
        private set

    var partnerUser by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var statusJob: Job? = null
    private var inviteJob: Job? = null
    private var duoJob: Job? = null
    private var partnerJob: Job? = null

    init {
        observeUserStatus()
    }

    private fun observeUserStatus() {
        statusJob?.cancel()
        statusJob = viewModelScope.launch {
            duoRepository.listenToUserStatus().collectLatest { user ->
                currentUser = user
                if (user != null) {
                    // Only check for invites if we are not already in a Duo
                    if (user.status != "LINKED" && user.status != "DUO_ONBOARDING") {
                        checkForInvites(user.phoneNumber)
                    } else {
                        inviteJob?.cancel()
                        incomingInvite = null
                    }
                    
                    if (user.linkedDuoId != null) {
                        observeDuo(user.linkedDuoId)
                    } else {
                        currentDuo = null
                        partnerUser = null
                        duoJob?.cancel()
                        partnerJob?.cancel()
                    }
                } else {
                    // Reset everything if user logs out
                    currentDuo = null
                    partnerUser = null
                    incomingInvite = null
                    inviteJob?.cancel()
                    duoJob?.cancel()
                    partnerJob?.cancel()
                }
            }
        }
    }

    private fun observeDuo(duoId: String) {
        if (duoJob?.isActive == true && currentDuo?.duoId == duoId) return
        
        duoJob?.cancel()
        duoJob = viewModelScope.launch {
            duoRepository.listenToDuo(duoId).collectLatest { duo ->
                currentDuo = duo
                if (duo != null) {
                    val myUid = currentUser?.uid
                    val partnerId = duo.userIds.find { it != myUid }
                    if (partnerId != null) {
                        observePartner(partnerId)
                    }
                } else {
                    partnerUser = null
                    partnerJob?.cancel()
                }
            }
        }
    }

    private fun observePartner(partnerId: String) {
        if (partnerJob?.isActive == true && partnerUser?.uid == partnerId) return
        
        partnerJob?.cancel()
        partnerJob = viewModelScope.launch {
            duoRepository.listenToUser(partnerId).collectLatest { user ->
                partnerUser = user
            }
        }
    }

    private fun checkForInvites(phone: String) {
        if (inviteJob?.isActive == true) return
        
        inviteJob = viewModelScope.launch {
            duoRepository.listenToIncomingInvites(phone).collectLatest { invite ->
                incomingInvite = invite
            }
        }
    }

    fun sendInvite(targetPhone: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
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
            errorMessage = null
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
            errorMessage = null
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
            errorMessage = null
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
