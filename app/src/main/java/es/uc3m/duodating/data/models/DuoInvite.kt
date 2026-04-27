package es.uc3m.duodating.data.models

data class DuoInvite(
    val inviteId: String = "",
    val senderUid: String = "",
    val senderName: String = "",
    val receiverPhone: String = "",
    val status: String = "pending" // pending, accepted, declined
)
