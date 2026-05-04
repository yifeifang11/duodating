package es.uc3m.duodating.data.models

data class Duo(
    val duoId: String = "",
    val user1Id: String = "",
    val user2Id: String = "",
    val userIds: List<String> = emptyList(),
    val status: String = "PENDING", // PENDING, ACTIVE
    val inviteCode: String = "",
    val questionChoice: String = "",
    val questionAnswer: String = "",
    val photoUrl: String = "",
    val likesSent: List<String> = emptyList(),
    val likesReceived: List<String> = emptyList(),
    val matches: List<String> = emptyList(),
    val createdAt: com.google.firebase.Timestamp? = null
)
