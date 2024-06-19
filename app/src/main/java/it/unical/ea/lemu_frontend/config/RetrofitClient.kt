package it.unical.ea.lemu_frontend.config

import okhttp3.OkHttpClient
import org.openapitools.client.apis.UtenteControllerApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val client = OkHttpClient.Builder()
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApiService: UtenteControllerApi by lazy {
        retrofit.create(UtenteControllerApi::class.java)
    }
}
