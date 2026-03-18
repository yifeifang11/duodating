package es.uc3m.duodating.data.models

data class Message(
    val senderName: String,
    val text: String,
    val isFromUser: Boolean
)
