package es.uc3m.duodating.data.models

data class DuoProfile(
    val user1id: String = "",
    val user1name: String = "",
    val user2id: String = "",
    val user2name: String = "",
    val user1prompt: String = "",
    val user1promptresponse: String = "",
    val user2prompt: String = "",
    val user2promptresponse: String = "",
    val user1image: String = "",
    val user2image: String = "",
    val combinedprompt: String = "",
    val combinedpromptresponse: String = "",
    val combinedimage: String = ""
)
