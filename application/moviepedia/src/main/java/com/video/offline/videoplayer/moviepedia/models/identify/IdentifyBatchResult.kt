package com.video.offline.videoplayer.moviepedia.models.identify

import com.squareup.moshi.Json
import com.video.offline.videoplayer.moviepedia.models.resolver.ResolverBatchResult

data class IdentifyBatchResult(
        @Json(name = "id")
        val id: String,
        @Json(name = "lucky")
        val lucky: MoviepediaMedia?
) : ResolverBatchResult() {
    override fun getId(): Long = id.toLong()

    override fun getMedia() = lucky
}

