package es.uc3m.duodating.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

    var currentIndex by mutableIntStateOf(0)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadDuos()
    }

    private fun loadDuos() {viewModelScope.launch {
        isLoading = true
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUid == null) {
            errorMessage = "User not logged in"
            isLoading = false
            return@launch
        }

        // Use the new repository function that handles exclusion logic
        duoRepository.getDiscoveryFeed(currentUid).collectLatest { filteredDuos ->
            val resultList = mutableListOf<DuoWithUsers>()

            for (duo in filteredDuos) {
                // Fetch user details for the discovery cards
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
            duoRepository.sendLikeDiscover(targetDuoId)
        }
        currentIndex++
    }

    fun passDuo(targetDuoId: String) {
        currentIndex++
    }
}
