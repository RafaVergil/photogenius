package fragments

import abstractions.DatabaseConnection
import adapters.InstagramMediaAdapter
import adapters.InstagramTagAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import br.com.rafaelverginelli.photogenius.HelpActivity
import br.com.rafaelverginelli.photogenius.MainFragmentActivity
import br.com.rafaelverginelli.photogenius.R
import br.com.rafaelverginelli.photogenius.SplashActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_staggered_grid.*
import models.EnvelopeMediaModel
import models.EnvelopeTagModel
import models.MediaModel
import models.TagModel
import persist.MediaPersist
import requests.IMediaRequest
import requests.ITagRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.*
import views.LoadingDialog
import java.net.URLEncoder
import java.util.*

class StaggeredGridFragment : Fragment() {

    val TAG = this::class.java.simpleName

    private var mediaAdapter: InstagramMediaAdapter? = null
    private var tagAdapter: InstagramTagAdapter? = null
    private var searchRequestTimer: Timer? = null

    //requests
    private var getMediaCall: Call<EnvelopeMediaModel>? = null
    private var searchTagsCall: Call<EnvelopeTagModel>? = null

    lateinit var loadingDialog: LoadingDialog
    lateinit var gridMediaClickCallback: InstagramMediaAdapter.IMediaCallback
    lateinit var dataChangedCallback: MainFragmentActivity.IDataChanged

    private var rootView: View? = null

    private fun getConn(): DatabaseConnection {
        return DatabaseConnection.getInstance(activity!!)
    }

    companion object {
        fun newInstance(gridMediaClickCallback: InstagramMediaAdapter.IMediaCallback,
                        dataChangedCallback: MainFragmentActivity.IDataChanged)
                : StaggeredGridFragment{

            val frag = StaggeredGridFragment()
            frag.gridMediaClickCallback = gridMediaClickCallback
            frag.dataChangedCallback = dataChangedCallback
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = layoutInflater.inflate(R.layout.fragment_staggered_grid, null)
        loadingDialog = LoadingDialog(activity!!)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupSearchBar()

        fabItemHelp.setOnClickListener{
            startActivity(Intent(context, HelpActivity::class.java))
        }
        fabItemSignOut.setOnClickListener{
            signOut()
        }

        configureNoMediaFeed(true)
    }

    private fun signOut(){
        val alertDialog = AlertDialog.Builder(activity!!).create()
        alertDialog.setTitle(R.string.sign_out_title)
        alertDialog.setMessage(activity!!
                .getString(R.string.sign_out_message))
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity!!
                .getString(android.R.string.yes)) { dialog, _ ->

            CurrentUserInstance.clearSession(activity!!)
            startActivity(Intent(activity, SplashActivity::class.java))

            dialog.dismiss()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, activity!!
                .getString(android.R.string.no)) { dialog, _ -> dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun setupSearchBar(){

        etxtSearch.setText("")
        etxtSearch.clearFocus()

        // This will prevent users from typing "space", as "space" is not a valid tag.
        etxtSearch.filters = arrayOf(InputFilter {
            source, _, _, _, _, _ ->
            source.toString().filterNot { it.isWhitespace() }
        })

        btnErase.setOnClickListener {
            etxtSearch.setText("")
            etxtSearch.requestFocus()
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etxtSearch, InputMethodManager.SHOW_IMPLICIT)
        }

        etxtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchRequestTimer?.cancel()
                getMediaCall?.cancel()
                searchTagsCall?.cancel()
            }

            override fun afterTextChanged(editable: Editable) {
                searchRequestTimer = Timer()
                searchRequestTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        activity!!.runOnUiThread { loadMediaFeed(etxtSearch.text.toString()) }
                    }
                }, CONSTANTS.MEDIA_SEARCH_SCHEDULE_TIME.toLong())
            }
        })
    }

    private fun configureNoMediaFeed(noMedia: Boolean){
        txtNoMedia.visibility = if(noMedia) View.VISIBLE else View.GONE
        recyclerView.visibility = if(noMedia) View.GONE else View.VISIBLE
    }

    private fun loadMediaFeed(query: String){

        if(query.isEmpty()) {
            configureNoMediaFeed(true)
            return
        }

        loadingDialog.showDialog(getString(R.string.loading), getString(R.string.please_wait))

        val encodedQuery = URLEncoder.encode(query,"UTF-8")

        if(!UTILS.checkConnectivity(activity!!)){
            fetchPersistedMediaData(encodedQuery)
            return
        }

        val iMediaRequest: IMediaRequest =
                RetrofitClientInstance.retrofitInstance.create(IMediaRequest::class.java)


        getMediaCall = iMediaRequest.getMedia(
                encodedQuery, "", "",
                CurrentUserInstance.currenUserInstance!!.access_token)

        getMediaCall!!.enqueue(object : Callback<EnvelopeMediaModel> {

            override fun onResponse(call: Call<EnvelopeMediaModel>?,
                                    response: Response<EnvelopeMediaModel>?) {

                loadingDialog.dismiss()

                if(getMediaCall != null && getMediaCall!!.isCanceled) return

                if(response != null) {
                    if(response.errorBody() == null ||!response.isSuccessful) {
                        if (response.body() != null && !response.body()!!.data.isEmpty()) {
                            dataChangedCallback.onMediaFetched(response.body()!!.data)
                            configureMediaGrid(response.body()!!.data)
                        }
                        else {
                            searchForTags(encodedQuery)
                        }
                    }
                    else {
                        if(!ErrorHelper.checkError(activity!!, response.errorBody()!!)){
                            return
                        }
                    }
                }
                else {
                    ErrorHelper.genericError(activity!!)
                }

            }

            override fun onFailure(call: Call<EnvelopeMediaModel>?, t: Throwable?) {

                loadingDialog.dismiss()

                if(getMediaCall != null && getMediaCall!!.isCanceled) return
                configureNoMediaFeed(true)
                UTILS.debugLog(TAG, "onFailure")

            }
        })
    }

    fun searchForTags(query: String) {

        val iTagRequest: ITagRequest =
                RetrofitClientInstance.retrofitInstance.create(ITagRequest::class.java)

        searchTagsCall = iTagRequest.getTags(
                query, CurrentUserInstance.currenUserInstance!!.access_token)

        searchTagsCall!!.enqueue(object : Callback<EnvelopeTagModel> {

            override fun onResponse(call: Call<EnvelopeTagModel>?,
                                    response: Response<EnvelopeTagModel>?) {

                loadingDialog.dismiss()

                if(searchTagsCall != null && searchTagsCall!!.isCanceled) return

                if(response != null) {
                    if(response.errorBody() == null) {

                        if (response.isSuccessful) {
                            if (response.body() != null &&
                                    !response.body()!!.data.isEmpty()) {
                                dataChangedCallback.onTagsFetched(response.body()!!.data)
                                configureTagGrid(response.body()!!.data)
                            }
                        } else {
                            configureNoMediaFeed(true)
                        }
                    }
                    else {
                        ErrorHelper.checkError(activity!!, response.errorBody()!!)
                    }
                }
                else {
                    ErrorHelper.genericError(activity!!)
                }

            }

            override fun onFailure(call: Call<EnvelopeTagModel>?, t: Throwable?) {

                loadingDialog.dismiss()

                if(searchTagsCall != null && searchTagsCall!!.isCanceled) return
                configureNoMediaFeed(true)
                UTILS.debugLog(TAG, "onFailure")

            }
        })
    }

    fun configureMediaGrid(data: List<MediaModel>) {

        etxtSearch.clearFocus()
        if(rootView != null) {
            UTILS.hideKeyboardFrom(activity!!, rootView!!)
        }

        persistMediaData(data)

        configureNoMediaFeed(false)
        txtTagHeader.visibility = View.GONE

        val params: RelativeLayout.LayoutParams =
                recyclerView.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.BELOW, llSearch.id)
        recyclerView.layoutParams = params

        recyclerView.layoutManager =
                StaggeredGridLayoutManager(CONSTANTS.STAGGERED_GRID_COLS_SPAN,
                        LinearLayout.VERTICAL)

        mediaAdapter = InstagramMediaAdapter(data, activity!!, gridMediaClickCallback)

        recyclerView.adapter = mediaAdapter
    }

    // This method will cache the images fetched.
    private fun persistMediaData(data: List<MediaModel>){
        Thread(Runnable {
            getConn().daoMediaModel().insertMany(toMediaPersist(data))
        }) .start()
    }

    private fun toMediaPersist(data: List<MediaModel>) : List<MediaPersist> {
        val persistData: ArrayList<MediaPersist> = ArrayList()
        val gson = Gson()
        for(d in data){
            persistData.add(MediaPersist(d.id, gson.toJson(d).toString(), d.tags.toString()))
        }
        return persistData
    }

    private fun toMediaModel(data: List<MediaPersist>) : List<MediaModel> {
        val modelData: ArrayList<MediaModel> = ArrayList()
        val gson = Gson()
        for(d in data){
            val any: Any? = gson.fromJson(d.data, MediaModel::class.java)
            if(any != null && any is MediaModel) {
                modelData.add(any)
            }
        }
        return modelData
    }

    // This method will load persisted media data
    private fun fetchPersistedMediaData(query: String){
        Thread(Runnable {
            val data: List<MediaPersist> = getConn().daoMediaModel().selectByTag(query)
            if(!data.isEmpty()){
                activity!!.runOnUiThread{
                    configureMediaGrid(toMediaModel(data))
                    loadingDialog.dismiss()
                }
            }
            else {
                activity!!.runOnUiThread{
                    configureNoMediaFeed(true)
                    loadingDialog.dismiss()
                }
            }
        }) .start()
    }

    fun configureTagGrid(data: List<TagModel>) {

        etxtSearch.clearFocus()
        if(rootView != null) {
            UTILS.hideKeyboardFrom(activity!!, rootView!!)
        }

        if(data.isEmpty()) {
            configureNoMediaFeed(true)
            return
        }

        configureNoMediaFeed(false)
        txtTagHeader.visibility = View.VISIBLE

        val params: RelativeLayout.LayoutParams =
                recyclerView.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.BELOW, txtTagHeader.id)
        recyclerView.layoutParams = params

        recyclerView.layoutManager =
                StaggeredGridLayoutManager(CONSTANTS.STAGGERED_GRID_COLS_SPAN,
                        LinearLayout.VERTICAL)

        tagAdapter = InstagramTagAdapter(data, activity!!,
                object: InstagramTagAdapter.ITagCallback{
                    override fun onTagClick(tag: TagModel) {
                        etxtSearch.setText(tag.name)
                    }
                })

        recyclerView.adapter = tagAdapter
    }

}