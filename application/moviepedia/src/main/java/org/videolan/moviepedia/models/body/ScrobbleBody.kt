package org.videolan.moviepedia.models.body

data class ScrobbleBody(
        val osdbhash: String? = null,
        val infohash: String? = null,
        val imdbId: String? = null,
        val dvdId: String? = null,
        val title: String? = null,
        val alternativeTitles: String? = null,
        val filename: String? = null,
        val show: String? = null,
        val year: String? = null,
        val season: String? = null,
        val episode: String? = null,
        val duration: String? = null
)

data class ScrobbleBodyBatch(
        val id: String,
        val metadata: ScrobbleBody
)

