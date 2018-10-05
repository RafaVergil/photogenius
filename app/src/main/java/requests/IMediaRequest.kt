package requests

import models.EnvelopeMediaModel
import retrofit2.Call
import retrofit2.http.*
import utils.CONSTANTS

interface IMediaRequest {

    @Headers("Accept: application/json")
    @GET(CONSTANTS.INSTAGRAM_API_GET_MEDIA_BY_TAG)
    fun getMedia(
            @Path(CONSTANTS.KEY_INSTAGRAM_API_MEDIA_TAG_NAME) tagName: String,
            @Query(CONSTANTS.KEY_INSTAGRAM_API_MEDIA_MAX_TAG_ID) maxTagId: String,
            @Query(CONSTANTS.KEY_INSTAGRAM_API_MEDIA_MIN_TAG_ID) minTagId: String,
            @Query(CONSTANTS.KEY_INSTAGRAM_API_MEDIA_TOKEN) access_token: String
    ): Call<EnvelopeMediaModel>

    @Headers("Accept: application/json")
    @GET(CONSTANTS.INSTAGRAM_API_GET_SELF_MEDIA)
    fun getSelfMedia(
            @Query(CONSTANTS.KEY_INSTAGRAM_API_MEDIA_TOKEN) access_token: String
    ): Call<EnvelopeMediaModel>

}