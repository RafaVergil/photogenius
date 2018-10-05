package utils

import models.AuthModel

class CurrentUserInstance {

    companion object{
        private var currentUser: AuthModel? = null

        var currenUserInstance: AuthModel?
            get() {
                return currentUser
            }
            set (user: AuthModel?) {
                currentUser = user
            }
    }
}