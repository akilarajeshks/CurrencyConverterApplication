package com.zestworks.currencycoverterapplication.repository

import com.zestworks.currencycoverterapplication.repository.network.NetworkResult
import com.zestworks.currencycoverterapplication.repository.network.NetworkService
import java.net.UnknownHostException

class CurrencyRepositoryImpl(private val networkService: NetworkService) : Repository {
    override suspend fun getCurrencyData(baseCurrency:String): NetworkResult<CurrencyData> {
        try {
            val currencyRateResponse = networkService.getCurrencyRatesFromNetwork(baseCurrency)

            if (currencyRateResponse.isSuccessful) {
                if (currencyRateResponse.body() != null) {
                    return NetworkResult.Success(currencyRateResponse.body()!!)
                }
            }

            return NetworkResult.Error(currencyRateResponse.toString())

        } catch (ex: UnknownHostException) {
            return NetworkResult.Error(ex.toString())
        }
    }
}