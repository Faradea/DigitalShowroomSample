package ru.macgavrina.digitalshowroom.api

import com.google.gson.GsonBuilder
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.macgavrina.digitalshowroom.SERVER_API_URL
import ru.macgavrina.digitalshowroom.model.GetCatalogResponse

interface CatalogDataService {

    @GET("---/products/sparkow/{sportSparkowId}")
    fun getAllCatalogItems(@Path("sportSparkowId") sportSparkowId: Int): Single<GetCatalogResponse>

    //https://api.dev.decathlon.ru/tree-catalog/products/sparkow/231423?page=1
    @GET("---/products/sparkow/{sportSparkowId}")
    fun getAllCatalogItemsForPage(@Path("sportSparkowId") sportSparkowId: Int, @Query("page") page: Int): Single<GetCatalogResponse>

    companion object ApiFactory {
        fun create(): CatalogDataService {

            val gson = GsonBuilder().create()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(SERVER_API_URL)
                .build()
            return retrofit.create(CatalogDataService::class.java)
        }

    }
}