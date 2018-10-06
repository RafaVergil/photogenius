package models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Those models are responsible for handling media data from Instagrams API
data class MediaModel(
        @SerializedName("id") val id: String,
        @SerializedName("type") val type: String,
        @SerializedName("comments") val comments: MediaFeedbackModel,
        @SerializedName("likes") val likes: MediaFeedbackModel,
        @SerializedName("user") val user: MediaUserModel,
        @SerializedName("images") val images: ImageModel,
        @SerializedName("tags") val tags: ArrayList<String>
) : Serializable

data class MediaFeedbackModel(
        @SerializedName("count") val count: kotlin.Int
) : Serializable

/*
    Do not mistake this by the UserModel data class. This one is to get info about who posted the
    media, only. The UserModel data class handles Authorization of the user.
 */
data class MediaUserModel(
        @SerializedName("userName") val userName: String
) : Serializable

data class ImageModel(
        @SerializedName("low_resolution") val low_resolution: ImageDetailModel,
        @SerializedName("standard_resolution") val standard_resolution: ImageDetailModel
) : Serializable

data class ImageDetailModel(
        @SerializedName("url") val url: String,
        @SerializedName("width") val width: kotlin.Int,
        @SerializedName("height") val height: kotlin.Int
) : Serializable