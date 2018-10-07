package models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/*
    This Model is responsible for holding the data returned by Instagram's API when we are trying
    to get an Authorization token.
*/
data class AuthModel(
        @SerializedName("access_token") val access_token: String,
        @SerializedName("user") val user: UserModel
) : Serializable

data class UserModel(
        @SerializedName("id") val id: String,
        @SerializedName("username") val username: String,
        @SerializedName("full_name") val full_name: String,
        @SerializedName("profile_picture") val profile_picture: String
) : Serializable