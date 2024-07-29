
package com.video.offline.videoplayer.television.ui

import android.net.Uri
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.video.offline.videoplayer.moviepedia.database.models.MediaMetadataWithImages
import com.video.offline.videoplayer.moviepedia.database.models.subtitle

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    var metadata: MediaMetadataWithImages? = null

    override fun onBindDescription(viewHolder: ViewHolder, itemData: Any) {
        val details = itemData as MediaItemDetails
        // In a production app, the itemData object contains the information
        // needed to display details for the media item:
        // viewHolder.getTitle().setText(details.getShortTitle());

        // Here we provide static data for testing purposes:
        val body = when {
            metadata != null -> metadata!!.metadata.summary
            details.body == null -> Uri.decode(details.location)
            else -> details.body + "\n" + Uri.decode(details.location)
        }
        viewHolder.title.text = metadata?.metadata?.title ?: details.title
        viewHolder.subtitle.text = metadata?.subtitle() ?: details.subTitle
        viewHolder.body.text = body
    }

    companion object {
        const val TAG = "DetailsDescriptionPresenter"
    }
}
