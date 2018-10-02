package models

/*
    This Model is responsible for holding the data returned by Instagram's API when we are trying
    to get an Authorization token.
*/
data class AuthModel(
        val access_token: String,
        val user: UserModel
)

data class UserModel(
        val id: String,
        val username: String,
        val full_name: String,
        val profile_picture: String
)