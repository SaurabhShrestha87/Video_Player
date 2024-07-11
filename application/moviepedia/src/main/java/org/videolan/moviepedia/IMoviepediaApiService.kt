package org.videolan.moviepedia

import org.videolan.moviepedia.models.body.ScrobbleBody
import org.videolan.moviepedia.models.body.ScrobbleBodyBatch
import org.videolan.moviepedia.models.identify.IdentifyBatchResult
import org.videolan.moviepedia.models.identify.IdentifyResult
import org.videolan.moviepedia.models.identify.MoviepediaMedia
import org.videolan.moviepedia.models.media.cast.CastResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IMoviepediaApiService {

    @POST("search-media/identify")
    suspend fun searchMedia(@Body body: ScrobbleBody): IdentifyResult

    @POST("search-media/batchidentify")
    suspend fun searchMediaBatch(@Body body: List<ScrobbleBodyBatch>): List<IdentifyBatchResult>

    @GET("media/{media}")
    suspend fun getMedia(@Path("media") mediaId: String): MoviepediaMedia

    @GET("media/{media}/cast")
    suspend fun getMediaCast(@Path("media") mediaId: String): CastResult
}