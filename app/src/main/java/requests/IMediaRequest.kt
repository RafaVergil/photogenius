package requests

import models.EnvelopeMediaModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.QueryMap
import utils.CONSTANTS

interface IMediaRequest {

    @Headers("Accept: application/json")
    @GET(CONSTANTS.INSTAGRAM_API_GET_MEDIA)
    fun getMedia(@QueryMap params: Map<String, String>): Call<EnvelopeMediaModel>

    @Headers("Accept: application/json")
    @GET(CONSTANTS.INSTAGRAM_API_GET_SELF_MEDIA)
    fun getSelfMedia(
            @Query(CONSTANTS.KEY_INSTAGRAM_API_MEDIA_TOKEN) access_token: String
    ): Call<EnvelopeMediaModel>

}