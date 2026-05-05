package es.uc3m.duodating.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uc3m.duodating.data.DuoRepository
import es.uc3m.duodating.data.models.Duo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class DuoWithNames(
    val duo: Duo,
    val user1Name: String,
    val user2Name: String
)

sealed class LikesUiState {
    object Loading : LikesUiState()
    data class Success(
        val likes: List<DuoWithNames>
    ) : LikesUiState()
    object Empty : LikesUiState()
    data class Error(val message: String) : LikesUiState()
}

class LikesViewModel : ViewModel() {
    private val likesUiState = MutableStateFlow<LikesUiState>(LikesUiState.Loading)
    val uiState: StateFlow<LikesUiState> = likesUiState

    private val repository = DuoRepository()

    init {
        observeLikes()
    }
    fun removeDuo(duoId: String) {
        val current = likesUiState.value
        if (current is LikesUiState.Success) {
            val updated = current.likes.filter { it.duo.duoId != duoId }
            likesUiState.value = if (updated.isEmpty()) LikesUiState.Empty else LikesUiState.Success(updated)
        }
    }
    private fun observeLikes() {
        viewModelScope.launch {
            repository.getLikes()
                .onStart {
                    android.util.Log.d("LikesVM", "Starting getLikes...")
                    likesUiState.value = LikesUiState.Loading }
                .catch { e ->
                    android.util.Log.e("LikesVM", "Error in getLikes: ${e.message}")
                    likesUiState.value = LikesUiState.Error(e.message ?: "Error")
                }
                .collect { likes ->
                    likes.forEach { duo ->
                        android.util.Log.d("LikesVM", "duo=${duo.duoId} user1Id='${duo.user1Id}' user2Id='${duo.user2Id}'")
                    }
                    if (likes.isEmpty()) {
                        likesUiState.value = LikesUiState.Empty
                    } else {
                        val duosWithNames = likes.map { duo ->
                            val user1Name = try {
                                repository.getUserById(duo.user1Id)?.firstName ?: duo.user1Id
                            } catch (e: Exception) {
                                duo.user1Id
                            }
                            val user2Name = try {
                                repository.getUserById(duo.user2Id)?.firstName ?: duo.user2Id
                            } catch (e: Exception) {
                                duo.user2Id
                            }
                            DuoWithNames(duo, user1Name, user2Name)
                        }
                        likesUiState.value = LikesUiState.Success(duosWithNames)
                    }
                }
        }
    }
}