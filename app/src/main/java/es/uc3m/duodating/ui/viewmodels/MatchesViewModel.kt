package es.uc3m.duodating.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uc3m.duodating.data.DuoRepository
import es.uc3m.duodating.data.models.DuoWithUsers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MatchesViewModel(
    private val duoRepository: DuoRepository = DuoRepository()
) : ViewModel() {

    var matches by mutableStateOf<List<DuoWithUsers>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch {
            duoRepository.listenToMyMatches().collectLatest { newMatches ->
                matches = newMatches
                isLoading = false
            }
        }
    }
}