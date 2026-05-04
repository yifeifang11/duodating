package es.uc3m.duodating.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    @ServerTimestamp val timestamp: Timestamp? = null
)