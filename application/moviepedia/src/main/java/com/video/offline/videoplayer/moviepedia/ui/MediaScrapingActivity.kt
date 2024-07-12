package com.video.offline.videoplayer.moviepedia.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import com.video.offline.videoplayer.moviepedia.R
import com.video.offline.videoplayer.moviepedia.databinding.MoviepediaActivityBinding
import com.video.offline.videoplayer.moviepedia.models.resolver.ResolverMedia
import com.video.offline.videoplayer.moviepedia.viewmodel.MediaScrapingModel
import org.videolan.resources.MOVIEPEDIA_MEDIA
import org.videolan.resources.util.parcelable
import com.video.offline.videoplayer.gui.BaseActivity
import com.video.offline.videoplayer.gui.helpers.UiTools
import com.video.offline.videoplayer.gui.helpers.applyTheme

open class MediaScrapingActivity : BaseActivity(), TextWatcher, TextView.OnEditorActionListener {

    private lateinit var mediaScrapingResultAdapter: MediaScrapingResultAdapter

    private lateinit var viewModel: MediaScrapingModel
    private lateinit var media: MediaWrapper
    private lateinit var binding: MoviepediaActivityBinding
    private val clickHandler = ClickHandler()
    override fun getSnackAnchorView(overAudioPlayer:Boolean): View? = findViewById(android.R.id.content)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTheme()
        val intent = intent
        binding = DataBindingUtil.setContentView(this, R.layout.moviepedia_activity)
        binding.handler = clickHandler

        mediaScrapingResultAdapter = MediaScrapingResultAdapter(layoutInflater)
        mediaScrapingResultAdapter.clickHandler = clickHandler
        binding.nextResults.adapter = mediaScrapingResultAdapter
        binding.nextResults.layoutManager = GridLayoutManager(this, 2)

        intent.parcelable<MediaWrapper>(MOVIEPEDIA_MEDIA)?.let {
            media = it
        }
        if (!::media.isInitialized) {
            finish()
            return
        }

        binding.searchEditText.addTextChangedListener(this)
        binding.searchEditText.setOnEditorActionListener(this)
        viewModel = ViewModelProvider(this)[media.uri.path ?: "", MediaScrapingModel::class.java]
        viewModel.apiResult.observe(this) {
            mediaScrapingResultAdapter.setItems(it.getAllResults())
        }
        viewModel.search(media.uri)
        binding.searchEditText.setText(media.title)
    }

    private fun performSearh(query: String) {
        viewModel.search(query)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {}

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            UiTools.setKeyboardVisibility(binding.root, false)
            performSearh(v.text.toString())
            return true
        }
        return false
    }

    inner class ClickHandler {

        fun onBack(@Suppress("UNUSED_PARAMETER") v: View) {
            finish()
        }

        fun onItemClick(@Suppress("UNUSED_PARAMETER") item: ResolverMedia) {
            //todo
            finish()
        }
    }
}
