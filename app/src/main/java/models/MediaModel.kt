package models

import java.io.Serializable

// Those models are responsible for handling media data from Instagrams API
data class MediaModel(
        val id: String,
        val type: String,
        val comments: MediaFeedbackModel,
        val likes: MediaFeedbackModel,
        val user: MediaUserModel,
        val images: ImageModel
) : Serializable

data class MediaFeedbackModel(
        val count: Int
)

/*
    Do not mistake this by the UserModel data class. This one is to get info about who posted the
    media, only. The UserModel data class handles Authorization of the user.
 */
data class MediaUserModel(
        val userName: String
)

data class ImageModel(
        val low_resolution: ImageDetailModel,
        val standard_resolution: ImageDetailModel
)

data class ImageDetailModel(
        val url: String,
        val width: Int,
        val height: Int
)