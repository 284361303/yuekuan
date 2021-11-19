package m.fasion.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val avatar: String,
    val nickname: String,
    val status: String,
    val token: String,
    val uid: String,
    val phone: String
) : Parcelable