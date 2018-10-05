package requests

import models.EnvelopeTagModel
import retrofit2.Call
import retrofit2.http.*
import utils.CONSTANTS

interface ITagRequest {

    @Headers("Accept: application/json")
    @GET(CONSTANTS.INSTAGRAM_API_GET_TAGS)
    fun getTags(
            @Query(CONSTANTS.KEY_INSTAGRAM_API_MEDIA_TAG_QUERY) q: String,
            @Query(CONSTANTS.KEY_INSTAGRAM_API_MEDIA_TOKEN) access_token: String
    ): Call<EnvelopeTagModel>

}