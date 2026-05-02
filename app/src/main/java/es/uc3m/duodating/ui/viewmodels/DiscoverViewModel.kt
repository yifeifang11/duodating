package es.uc3m.duodating.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import es.uc3m.duodating.data.DuoRepository
import es.uc3m.duodating.data.models.DuoWithUsers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DiscoverViewModel(
    private val duoRepository: DuoRepository = DuoRepository()
) : ViewModel() {

    var duos by mutableStateOf<List<DuoWithUsers>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadDuos()
    }

    private fun loadDuos() {
        viewModelScope.launch {
            isLoading = true
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            
            // We'll use a flow to get all active duos
            duoRepository.getAllActiveDuos().collectLatest { activeDuos ->
                val resultList = mutableListOf<DuoWithUsers>()
                
                for (duo in activeDuos) {
                    // Filter out the current user's own duo
                    if (currentUid != null && duo.userIds.contains(currentUid)) continue
                    
                    val user1 = duoRepository.getUserById(duo.user1Id)
                    val user2 = duoRepository.getUserById(duo.user2Id)
                    
                    if (user1 != null && user2 != null) {
                        resultList.add(DuoWithUsers(duo, user1, user2))
                    }
                }
                
                duos = resultList
                isLoading = false
            }
        }
    }

    fun likeDuo(targetDuoId: String) {
        viewModelScope.launch {
            duoRepository.sendLike(targetDuoId)
        }
    }

    fun passDuo(targetDuoId: String) {
        // Implementation for passing if needed (e.g., local filtering or DB update)
    }
}
