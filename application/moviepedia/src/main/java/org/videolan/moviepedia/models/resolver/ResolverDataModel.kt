package org.videolan.moviepedia.models.resolver

import android.net.Uri
import java.util.*
import kotlin.collections.ArrayList

abstract class ResolverBatchResult {
    abstract fun getId(): Long
    abstract fun getMedia(): ResolverMedia?
}

abstract class ResolverResult {
    abstract fun lucky(): ResolverMedia?
    abstract fun results(): List<ResolverMedia>
    fun getAllResults(): List<ResolverMedia> = ArrayList(results()).apply { add(lucky()) }.toList()
}

abstract class ResolverCasting {
    abstract fun actors(): List<ResolverPerson>
    abstract fun directors(): List<ResolverPerson>
    abstract fun writers(): List<ResolverPerson>
    abstract fun musicians(): List<ResolverPerson>
    abstract fun producers(): List<ResolverPerson>
}

abstract class ResolverPerson {
    abstract fun name(): String
    abstract fun image(): String?
    abstract fun personId(): String
}

abstract class ResolverMedia {
    abstract fun mediaType(): ResolverMediaType
    abstract fun showId(): String
    abstract fun mediaId(): String
    abstract fun title(): String
    abstract fun summary(): String
    abstract fun genres(): String
    abstract fun date(): Date?
    abstract fun countries(): String
    abstract fun season(): Int?
    abstract fun episode(): Int?
    abstract fun year(): String?
    abstract fun imageUri(languages: List<String>): Uri?
    abstract fun backdropUri(languages: List<String>): Uri?
    abstract fun getBackdrops(languages: List<String>): List<ResolverImage>?
    abstract fun getPosters(languages: List<String>): List<ResolverImage>?
    abstract fun getImageUriFromPath(path: String): String
    abstract fun getCardSubtitle(): String?
}

abstract class ResolverImage {
    abstract fun language(): String
    abstract fun path(): String
}

enum class ResolverMediaType {
    TV_SHOW,
    TV_SEASON,
    TV_EPISODE,
    MOVIE
}