package br.com.rafaelverginelli.photogenius

import abstractions.CustomAppCompatActivity
import adapters.InstagramMediaAdapter
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import models.MediaDetailModel
import models.MediaModel
import org.json.JSONObject
import requests.IMediaRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.CONSTANTS
import utils.CurrentUserInstance
import utils.RetrofitClientInstance
import utils.UTILS

class MainActivity : CustomAppCompatActivity() {

    private var mediaAdapter: InstagramMediaAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun loadMediaFeed(){

        loadingDialog.showDialog(getString(R.string.loading), getString(R.string.please_wait))

        val iMediaRequest: IMediaRequest =
                RetrofitClientInstance.retrofitInstance.create(IMediaRequest::class.java)

        val arguments: Map<String, Any> = mapOf()
        arguments.plus(Pair(CONSTANTS.KEY_INSTAGRAM_API_MEDIA_LATITUDE, 0))
        arguments.plus(Pair(CONSTANTS.KEY_INSTAGRAM_API_MEDIA_LONGITUDE, 0))
        arguments.plus(Pair(CONSTANTS.KEY_INSTAGRAM_API_MEDIA_TOKEN,
                CurrentUserInstance.currenUserInstance!!.access_token))

        val call: Call<MediaModel> = iMediaRequest.getMedia(arguments)
        call.enqueue(object : Callback<MediaModel> {

            override fun onResponse(call: Call<MediaModel>?,
                                    response: Response<MediaModel>?) {

                loadingDialog.dismiss()

                if (response != null &&
                        response.isSuccessful && response.body() != null) {

                    //todo: call configure
                    configureMediaGrid(response.body()!!.data)

                } else {
                    //todo: treat errors properly
                    try {
                        val jObjError = JSONObject(response!!.errorBody()!!.string())
                        Toast.makeText(applicationContext,
                                jObjError.getString("message"),
                                Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(applicationContext,
                                e.message,
                                Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<MediaModel>?, t: Throwable?) {
                UTILS.DebugLog(TAG, "onFailure")
            }
        })
    }

    fun configureMediaGrid(data: ArrayList<MediaDetailModel>) {
        recyclerView.layoutManager = StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL)
        mediaAdapter = InstagramMediaAdapter(data, this@MainActivity)
        recyclerView.adapter = mediaAdapter
    }
}
