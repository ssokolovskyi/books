package com.ssokolovskyi.books.view.details

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.ssokolovskyi.books.R
import com.ssokolovskyi.books.view.details.ViewData.NoNetwork
import com.ssokolovskyi.books.view.details.ViewData.Ready

class VolumesDetailsActivity : AppCompatActivity() {

    companion object {
        const val VOLUME_ID_ARG = "VOLUME_ID_ARG"
    }

    private lateinit var viewModel: VolumeViewModel
    private lateinit var icon: ImageView
    private lateinit var title: TextView
    private lateinit var authors: TextView
    private lateinit var publisher: TextView
    private lateinit var publishedDate: TextView
    private lateinit var pages: TextView
    private lateinit var language: TextView
    private lateinit var link: TextView
    private lateinit var detailsContainer: View
    private lateinit var errorView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volumes_details)
        icon = findViewById(R.id.icon)
        title = findViewById(R.id.title)
        authors = findViewById(R.id.authors)
        publisher = findViewById(R.id.publisher)
        publishedDate = findViewById(R.id.published_date)
        pages = findViewById(R.id.pages)
        language = findViewById(R.id.language)
        link = findViewById(R.id.link)
        detailsContainer = findViewById(R.id.details_container)
        errorView = findViewById(R.id.errorView)
        viewModel = getVolumeViewModel(intent.getStringExtra(VOLUME_ID_ARG))
        initActionBar()
        observeData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getVolumeViewModel(volumeId: String): VolumeViewModel {
        return ViewModelProviders
            .of(this, VolumeViewModelFactory(volumeId, application))
            .get(VolumeViewModel::class.java)
    }

    private fun observeData() {
        viewModel.getViewData().observe(this, Observer { viewData ->
            when (viewData) {
                NoNetwork -> {
                    setErrorVisibility(true)
                }
                is Ready -> {
                    setErrorVisibility(false)
                    val volume = viewData.volumeData
                    title.text = volume.title
                    authors.text = volume.authors
                    volume.iconUrl?.let { setIcon(it) }
                    publisher.text = volume.publisher
                    publishedDate.text = volume.publishedDate
                    pages.text = volume.pageCount
                    language.text = volume.language
                    link.text = volume.previewLink
                }
            }
        })
    }

    private fun setErrorVisibility(visible: Boolean) {
        if (visible) {
            errorView.visibility = View.VISIBLE
            detailsContainer.visibility = View.GONE
        } else {
            errorView.visibility = View.GONE
            detailsContainer.visibility = View.VISIBLE
        }
    }

    private fun setIcon(iconUrl: String) {
        Picasso
            .get()
            .load(iconUrl)
            .resize(icon.layoutParams.width, icon.layoutParams.height)
            .centerCrop()
            .into(icon)
    }
}
