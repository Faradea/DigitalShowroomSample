package ru.macgavrina.digitalshowroom.api

import com.google.gson.GsonBuilder
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import ru.macgavrina.digitalshowroom.SERVER_API_URL
import ru.macgavrina.digitalshowroom.model.SportCategory

interface SportCategoriesDataService {
//
    @GET("---/tree/sparkow/{sportSparkowId}")
    fun getSportCategories(@Path("sportSparkowId") sportSparkowId: Int): Single<SportCategory>


    companion object ApiFactory {
        fun create(): SportCategoriesDataService {

            val gson = GsonBuilder().create()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(SERVER_API_URL)
                .build()
            return retrofit.create(SportCategoriesDataService::class.java)
        }

    }
}