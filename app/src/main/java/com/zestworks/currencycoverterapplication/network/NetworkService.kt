package com.zestworks.currencycoverterapplication.network

import com.zestworks.currencycoverterapplication.repository.CurrencyData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    @GET("latest")
    suspend fun getCurrencyRatesFromNetwork(@Query("base") base: String): Response<CurrencyData>
}