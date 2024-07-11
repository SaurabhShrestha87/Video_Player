package org.videolan.television.ui

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import org.videolan.moviepedia.database.models.getYear
import org.videolan.moviepedia.viewmodel.MediaMetadataFull

class TvShowDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: ViewHolder, itemData: Any) {
        val details = itemData as MediaMetadataFull
        // In a production app, the itemData object contains the information
        // needed to display details for the media item:
        // viewHolder.getTitle().setText(details.getShortTitle());

        // Here we provide static data for testing purposes:
        val body = details.metadata?.metadata?.summary
        viewHolder.title.text = details.metadata?.metadata?.title
        viewHolder.subtitle.text = details.metadata?.metadata?.getYear()
        viewHolder.body.text = body
    }
}
