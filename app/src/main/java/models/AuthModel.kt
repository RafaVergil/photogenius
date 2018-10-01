package models

/*
    I could have included UserModel in this file. I decided to keep two different files
    for organization purposes, but we can refactor it later, if necessary. It's not a big deal.
*/
data class AuthModel(val access_token: String, val user: UserModel)