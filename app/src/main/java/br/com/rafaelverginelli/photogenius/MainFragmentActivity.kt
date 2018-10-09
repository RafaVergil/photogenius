package br.com.rafaelverginelli.photogenius

import abstractions.CustomAppCompatActivity
import adapters.InstagramMediaAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.widget.ImageView
import fragments.MediaPagerFragment
import fragments.StaggeredGridFragment
import kotlinx.android.synthetic.main.activity_main_fragment.*
import kotlinx.android.synthetic.main.fragment_staggered_grid.*
import models.MediaModel
import models.TagModel
import utils.CurrentUserInstance
import utils.UTILS

class MainFragmentActivity : CustomAppCompatActivity() {

    val FRAGMENT_TAG_GRID = "frag_grid"
    val FRAGMENT_TAG_PAGER = "frag_pager"

    var media: List<MediaModel> = ArrayList()
    var tags: List<TagModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_fragment)
        showGrid()
    }

    override fun onBackPressed() {
        val currentFrag = supportFragmentManager
                .findFragmentByTag(FRAGMENT_TAG_PAGER)

        // If we are seeing a media in fullscreen, go back. Otherwise, ask for sign out.
        if (currentFrag != null && currentFrag.isVisible) {
            supportFragmentManager.beginTransaction().remove(currentFrag).commit()
        }
        else {
            signOut()
        }
    }

    override fun onResume() {
        super.onResume()
        val currentFrag = supportFragmentManager
                .findFragmentByTag(FRAGMENT_TAG_GRID) as StaggeredGridFragment?

        // onResume puts focus in the search bar again, triggering the keyboard. Not anymore... ;)
        if (currentFrag != null) {
            currentFrag.etxtSearch?.clearFocus()
            currentFrag.updateHud()
        }
        UTILS.hideKeyboardFrom(this, container)


    }

    private fun signOut(){
        val alertDialog = AlertDialog.Builder(this@MainFragmentActivity).create()
        alertDialog.setTitle(R.string.sign_out_title)
        alertDialog.setMessage(this@MainFragmentActivity
                .getString(R.string.sign_out_message))
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, this@MainFragmentActivity
                .getString(android.R.string.yes)) { dialog, _ ->

            CurrentUserInstance.clearSession(this@MainFragmentActivity)
            startActivity(Intent(this@MainFragmentActivity,
                    SplashActivity::class.java))

            dialog.dismiss()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, this@MainFragmentActivity
                .getString(android.R.string.no)) { dialog, _ -> dialog.dismiss()
        }
        alertDialog.show()
    }
    
    private fun showGrid(){
        supportFragmentManager
                .beginTransaction()
                .add(R.id.container,
                        StaggeredGridFragment
                                .newInstance(onGridMediaClick, dataChangedCallback),
                        FRAGMENT_TAG_GRID)
                .commit()
    }

    private val onGridMediaClick = object: InstagramMediaAdapter.IMediaCallback{

        override fun onMediaClick(index: Int, imageView: ImageView) {
            val fragTransaction = supportFragmentManager
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.container,
                                    MediaPagerFragment.newInstance(index, media),
                                    FRAGMENT_TAG_PAGER)
                            .addToBackStack(null)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fragTransaction.addSharedElement(imageView,
                        ViewCompat.getTransitionName(imageView) ?: "")
            }

            fragTransaction.commit()
        }
    }

    private val dataChangedCallback = object: IDataChanged {
        override fun onMediaFetched(media: List<MediaModel>) {
            this@MainFragmentActivity.media = media
        }

        override fun onTagsFetched(tags: List<TagModel>) {
            this@MainFragmentActivity.tags = tags
        }

    }

    interface IDataChanged {
        fun onMediaFetched(media: List<MediaModel>)
        fun onTagsFetched(tags: List<TagModel>)
    }
}
