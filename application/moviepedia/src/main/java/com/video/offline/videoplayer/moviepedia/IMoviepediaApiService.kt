package com.video.offline.videoplayer.moviepedia

import com.video.offline.videoplayer.moviepedia.models.body.ScrobbleBody
import com.video.offline.videoplayer.moviepedia.models.body.ScrobbleBodyBatch
import com.video.offline.videoplayer.moviepedia.models.identify.IdentifyBatchResult
import com.video.offline.videoplayer.moviepedia.models.identify.IdentifyResult
import com.video.offline.videoplayer.moviepedia.models.identify.MoviepediaMedia
import com.video.offline.videoplayer.moviepedia.models.media.cast.CastResult
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