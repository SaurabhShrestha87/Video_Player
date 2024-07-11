package org.videolan.moviepedia.repository

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videolan.moviepedia.IMoviepediaApiService
import org.videolan.moviepedia.MoviepediaApiClient
import org.videolan.moviepedia.models.body.ScrobbleBody
import org.videolan.moviepedia.models.body.ScrobbleBodyBatch
import org.videolan.moviepedia.models.resolver.ResolverBatchResult
import org.videolan.moviepedia.models.resolver.ResolverResult
import org.videolan.tools.FileUtils
import java.io.File

class MoviepediaApiRepository(private val moviepediaApiService: IMoviepediaApiService) : MediaResolverApi() {

    override suspend fun searchMedia(uri: Uri): ResolverResult {
        val hash = withContext(Dispatchers.IO){ FileUtils.computeHash(File(uri.path)) }
        val scrobbleBody = ScrobbleBody(filename = uri.lastPathSegment, osdbhash = hash)
        return moviepediaApiService.searchMedia(scrobbleBody)
    }

    override suspend fun searchMediaBatch(uris: HashMap<Long, Uri>): List<ResolverBatchResult> {
        val body = ArrayList<ScrobbleBodyBatch>()
        uris.forEach { uri ->
            val hash = withContext(Dispatchers.IO) { FileUtils.computeHash(File(uri.value.path)) }
            val scrobbleBody = ScrobbleBody(filename = uri.value.lastPathSegment, osdbhash = hash)

            val scrobbleBodyBatch = ScrobbleBodyBatch(id = uri.key.toString(), metadata = scrobbleBody)
            body.add(scrobbleBodyBatch)
        }

        return moviepediaApiService.searchMediaBatch(body)
    }

    override suspend fun searchTitle(query: String) = moviepediaApiService.searchMedia(ScrobbleBody(title = query, filename = query))

    override suspend fun getMedia(showId: String) = moviepediaApiService.getMedia(showId)

    override suspend fun getMediaCast(resolverId: String) = moviepediaApiService.getMediaCast(resolverId)

    companion object {
        private val instance = MoviepediaApiRepository(MoviepediaApiClient.instance)
        fun getInstance() = instance
    }
}