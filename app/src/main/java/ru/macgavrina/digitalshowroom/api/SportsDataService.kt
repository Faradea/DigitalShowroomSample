package ru.macgavrina.digitalshowroom.api

import com.google.gson.GsonBuilder
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import ru.macgavrina.digitalshowroom.SERVER_API_URL
import ru.macgavrina.digitalshowroom.model.Sport

interface SportsDataService {

    @GET("---/sports")
    fun getSportsList(): Single<List<Sport>>

    companion object ApiFactory {
        fun create(): SportsDataService {

            val gson = GsonBuilder().create()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(SERVER_API_URL)
                .build()
            return retrofit.create(SportsDataService::class.java)
        }

    }
}