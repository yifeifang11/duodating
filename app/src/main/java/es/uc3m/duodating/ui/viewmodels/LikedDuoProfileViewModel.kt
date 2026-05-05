package es.uc3m.duodating.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uc3m.duodating.data.DuoRepository
import es.uc3m.duodating.data.models.DuoWithUsers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LikedDuoProfileViewModel : ViewModel() {

    private val repository = DuoRepository()
    private val _duo = MutableStateFlow<DuoWithUsers?>(null)
    val duo: StateFlow<DuoWithUsers?> = _duo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadDuo(duoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                _duo.value = repository.getDuoWithUsersById(duoId)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }

            _isLoading.value = false
        }
    }

    fun likeBack(targetDuoId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.sendLike(targetDuoId)
            onComplete()
        }
    }

    fun pass(targetDuoId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.passDuo(targetDuoId)
            onComplete()
        }
    }
}