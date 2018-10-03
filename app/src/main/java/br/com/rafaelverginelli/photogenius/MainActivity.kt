package br.com.rafaelverginelli.photogenius

import abstractions.CustomAppCompatActivity
import adapters.InstagramMediaAdapter
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import models.EnvelopeMediaModel
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

        loadMediaFeed()
    }

    fun loadMediaFeed(){

        loadingDialog.showDialog(getString(R.string.loading), getString(R.string.please_wait))

        val iMediaRequest: IMediaRequest =
                RetrofitClientInstance.retrofitInstance.create(IMediaRequest::class.java)

        val call: Call<EnvelopeMediaModel> = iMediaRequest.getSelfMedia(
                CurrentUserInstance.currenUserInstance!!.access_token)

        call.enqueue(object : Callback<EnvelopeMediaModel> {

            override fun onResponse(call: Call<EnvelopeMediaModel>?,
                                    response: Response<EnvelopeMediaModel>?) {

                loadingDialog.dismiss()

                if (response != null &&
                        response.isSuccessful && response.body() != null) {
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

            override fun onFailure(call: Call<EnvelopeMediaModel>?, t: Throwable?) {
                UTILS.DebugLog(TAG, "onFailure")
            }
        })
    }

    fun configureMediaGrid(data: List<MediaModel>) {
        recyclerView.layoutManager =
                StaggeredGridLayoutManager(CONSTANTS.STAGGERED_GRID_COLS_SPAN,
                LinearLayout.VERTICAL)
        mediaAdapter = InstagramMediaAdapter(data, this@MainActivity)
        recyclerView.adapter = mediaAdapter
    }
}
