package m.fasion.core.model

data class LoginModel(
    val avatar: String,
    val nickname: String,
    val status: String,
    val token: String,
    val uid: String
)