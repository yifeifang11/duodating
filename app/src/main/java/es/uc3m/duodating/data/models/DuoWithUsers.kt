package es.uc3m.duodating.data.models

data class DuoWithUsers(
    val duo: Duo = Duo(),
    val user1: User = User(),
    val user2: User = User()
)
