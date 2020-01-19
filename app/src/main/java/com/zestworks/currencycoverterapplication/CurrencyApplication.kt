package com.zestworks.currencycoverterapplication

import android.app.Application
import com.zestworks.currencycoverterapplication.network.NetworkService
import com.zestworks.currencycoverterapplication.repository.CurrencyRepositoryImpl
import com.zestworks.currencycoverterapplication.repository.Repository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CurrencyApplication:Application() {

    override fun onCreate() {
        super.onCreate()

        val module = module {
            single { provideNetworkService() }
            single { CurrencyViewModel(get()) }

            single<Repository> { CurrencyRepositoryImpl(get()) }
        }

        startKoin {
            androidContext(this@CurrencyApplication)
            modules(module)
        }
    }


    private fun provideNetworkService(): NetworkService {
            val baseUrl = "https://revolut.duckdns.org/"
            val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit.create(NetworkService::class.java)

        }

}