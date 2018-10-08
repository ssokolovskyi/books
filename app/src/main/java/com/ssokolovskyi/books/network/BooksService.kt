package com.ssokolovskyi.books.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BooksService {
    companion object {
        private const val SERVICE_ENDPOINT = "https://www.googleapis.com/books/v1/"

        fun getService(): BooksService {
            return RetrofitUtils.getRetrofit(SERVICE_ENDPOINT).create(BooksService::class.java)
        }
    }

    @GET("volumes")
    fun getVolumes(@Query("q") search: String,
                   @Query("maxResults") maxResults: Int,
                   @Query("startIndex") startIndex: Int): Single<VolumesResponse>

    @GET("volumes/{id}")
    fun getVolume(@Path("id") id: String): Single<Volume>
}

data class VolumesResponse(val items: List<Volume>?)
data class Volume(val id: String, val volumeInfo: VolumeInfo)
data class VolumeInfo(val title: String,
                      val authors: List<String>?,
                      val imageLinks: ImageLinks?,
                      val publisher: String,
                      val publishedDate: String,
                      val pageCount: Long,
                      val language: String,
                      val previewLink: String)

data class ImageLinks(val thumbnail: String)
