package requests

import models.AuthModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import utils.CONSTANTS

interface IAuthRequest {

    @Headers("Accept: application/json")
    @POST(CONSTANTS.INSTAGRAM_API_GET_ACCESS_TOKEN_URL)
    fun getAuthToken(@Body arguments: RequestBody): Call<AuthModel>

}