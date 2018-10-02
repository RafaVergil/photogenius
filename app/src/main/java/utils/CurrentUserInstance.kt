package utils

import models.AuthModel

object CurrentUserInstance {

    private var currentUser: AuthModel? = null

    var currenUserInstance: AuthModel?
    get() {
        return currentUser
    }
    set (user: AuthModel?) {
        currentUser = user
    }
}