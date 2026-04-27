package es.uc3m.duodating.data.models

data class User(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val dob: String = "",
    val phoneNumber: String = "",
    val questionChoice: String = "",
    val questionAnswer: String = "",
    val photoUrl: String = "",
    val duoId: String? = null
)
