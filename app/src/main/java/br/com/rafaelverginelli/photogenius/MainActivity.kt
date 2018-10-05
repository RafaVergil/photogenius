package br.com.rafaelverginelli.photogenius

import abstractions.CustomAppCompatActivity
import adapters.InstagramMediaAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import java.net.URLEncoder
import java.util.*


class MainActivity : CustomAppCompatActivity() {

    private var mediaAdapter: InstagramMediaAdapter? = null
    private var searchRequestTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSearchBar()

        configureNoMediaFeed(true)
    }

    private fun setupSearchBar(){

        etxtSearch.setText("")
        etxtSearch.clearFocus()

        btnErase.setOnClickListener {
            etxtSearch.setText("")
            etxtSearch.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etxtSearch, InputMethodManager.SHOW_IMPLICIT)
        }

        etxtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchRequestTimer?.cancel()
            }

            override fun afterTextChanged(editable: Editable) {
                searchRequestTimer = Timer()
                searchRequestTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        runOnUiThread { loadMediaFeed(etxtSearch.text.toString()) }
                    }
                }, CONSTANTS.MEDIA_SEARCH_SCHEDULE_TIME.toLong())
            }
        })
    }

    private fun configureNoMediaFeed(noMedia: Boolean){
        txtNoMedia.visibility = if(noMedia) View.VISIBLE else View.GONE
        recyclerView.visibility = if(noMedia) View.VISIBLE else View.GONE
    }

    private fun loadMediaFeed(query: String){

        if(query.isEmpty()) return

        loadingDialog.showDialog(getString(R.string.loading), getString(R.string.please_wait))

        val iMediaRequest: IMediaRequest =
                RetrofitClientInstance.retrofitInstance.create(IMediaRequest::class.java)

        val encodedQuery = URLEncoder.encode(query,"UTF-8")

        val call: Call<EnvelopeMediaModel> = iMediaRequest.getMedia(
                encodedQuery, "", "",
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

                loadingDialog.dismiss()

                UTILS.DebugLog(TAG, "onFailure")
                //todo treat error
            }
        })
    }

    fun configureMediaGrid(data: List<MediaModel>) {
        recyclerView.layoutManager =
                StaggeredGridLayoutManager(CONSTANTS.STAGGERED_GRID_COLS_SPAN,
                        LinearLayout.VERTICAL)

        mediaAdapter = InstagramMediaAdapter(data, this,
                object: InstagramMediaAdapter.IMediaCallback{
                    override fun onMediaClick(index: Int) {
                        val intent =
                                Intent(this@MainActivity, MediaActivity::class.java)

//                        val gson = Gson()
//                        intent.putExtra(CONSTANTS.KEY_MEDIA_BUNDLE, gson.toJson(data))
//                        intent.putExtra(CONSTANTS.KEY_MEDIA_INDEX_BUNDLE, index)
                        MediaActivity.setMediaData(data)
                        startActivity(intent)
                    }
                })

        recyclerView.adapter = mediaAdapter
    }

}
