package org.videolan.moviepedia.repository

import android.net.Uri
import org.videolan.moviepedia.models.resolver.ResolverBatchResult
import org.videolan.moviepedia.models.resolver.ResolverCasting
import org.videolan.moviepedia.models.resolver.ResolverMedia
import org.videolan.moviepedia.models.resolver.ResolverResult
import java.util.*

abstract class MediaResolverApi {
    abstract suspend fun searchMediaBatch(filesToIndex: HashMap<Long, Uri>): List<ResolverBatchResult>
    abstract suspend fun getMedia(showId: String): ResolverMedia
    abstract suspend fun searchTitle(query: String): ResolverResult
    abstract suspend fun getMediaCast(resolverId: String): ResolverCasting
    abstract suspend fun searchMedia(uri: Uri): ResolverResult
}