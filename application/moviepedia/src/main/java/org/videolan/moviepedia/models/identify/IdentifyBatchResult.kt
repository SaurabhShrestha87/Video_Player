package org.videolan.moviepedia.models.identify

import com.squareup.moshi.Json
import org.videolan.moviepedia.models.resolver.ResolverBatchResult

data class IdentifyBatchResult(
        @Json(name = "id")
        val id: String,
        @Json(name = "lucky")
        val lucky: MoviepediaMedia?
) : ResolverBatchResult() {
    override fun getId(): Long = id.toLong()

    override fun getMedia() = lucky
}

