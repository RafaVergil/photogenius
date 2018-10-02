package requests

import models.MediaModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.QueryMap
import utils.CONSTANTS

interface IMediaRequest {

    @Headers("Accept: application/json")
    @GET(CONSTANTS.INSTAGRAM_API_GET_MEDIA)
    fun getMedia(@QueryMap params: Map<String, Any>): Call<MediaModel>

}