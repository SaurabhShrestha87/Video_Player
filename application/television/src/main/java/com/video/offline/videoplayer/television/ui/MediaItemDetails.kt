
package com.video.offline.videoplayer.television.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaItemDetails(var title: String? = null, var subTitle: String? = null,
                            var body: String? = null,
                            var location: String? = null,
                            var artworkUrl: String? = null) : Parcelable
