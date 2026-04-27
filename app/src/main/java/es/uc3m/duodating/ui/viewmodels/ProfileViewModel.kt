package es.uc3m.duodating.ui.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uc3m.duodating.data.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun saveProfile(
        firstName: String,
        lastName: String,
        dob: String,
        phoneNumber: String,
        questionChoice: String,
        questionAnswer: String,
        imageUri: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            val result = userRepository.saveUserProfile(
                firstName = firstName,
                lastName = lastName,
                dob = dob,
                phoneNumber = phoneNumber,
                questionChoice = questionChoice,
                questionAnswer = questionAnswer,
                imageUri = imageUri
            )

            isLoading = false
            if (result.isSuccess) {
                onSuccess()
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
            }
        }
    }
}
