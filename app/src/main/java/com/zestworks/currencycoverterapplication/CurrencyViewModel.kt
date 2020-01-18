package com.zestworks.currencycoverterapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zestworks.currencycoverterapplication.network.NetworkResult
import com.zestworks.currencycoverterapplication.repository.CurrencyData
import com.zestworks.currencycoverterapplication.repository.Repository
import com.zestworks.currencycoverterapplication.view.Currency
import com.zestworks.currencycoverterapplication.view.CurrencyViewData
import com.zestworks.currencycoverterapplication.view.UIEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CurrencyViewModel(private val repository: Repository) : ViewModel() {

    private val _rates = MutableLiveData<CurrencyViewData>()
    val rates: LiveData<CurrencyViewData> = _rates

    fun onEvent(event: UIEvent) {
         when (event) {
            is UIEvent.StartEditUIEvent -> {
                val baseCurrency = event.currencyName
                val currencyList = (_rates.value as CurrencyViewData.SuccessCurrencyViewData).currencyList.toMutableList()
                val currency = currencyList.find { it.name == baseCurrency }
                if (currency!=null){
                    currencyList.remove(currency)
                    currencyList.add(0,currency)
                    _rates.postValue(CurrencyViewData.SuccessCurrencyViewData(currencyList))
                }
            }
            is UIEvent.TextChangeUIEvent -> {

            }
        }
    }

    fun onUIStarted() {
        _rates.postValue(CurrencyViewData.LoadingCurrencyViewData)

        viewModelScope.launch {
            while (true) {
                val networkResponse : NetworkResult<CurrencyData> = if (_rates.value!=null && _rates.value is CurrencyViewData.SuccessCurrencyViewData){
                    val base = (_rates.value as CurrencyViewData.SuccessCurrencyViewData).currencyList.first().name
                    repository.getCurrencyData(base)
                }else{
                    repository.getCurrencyData()
                }
                when (networkResponse) {
                    is NetworkResult.Success -> {
                        val currencyList = mutableListOf<Currency>()

                        val baseCurrency = networkResponse.data.base
                        currencyList.add(Currency(name = baseCurrency, value = 1.0))

                        val rates = networkResponse.data.rates
                        rates.keys.forEach {
                            currencyList.add(Currency(name = it, value = rates[it]!!))
                        }

                        _rates.postValue(CurrencyViewData.SuccessCurrencyViewData(currencyList))
                    }
                    is NetworkResult.Error -> {
                        _rates.postValue(CurrencyViewData.ErrorCurrencyViewData(reason = networkResponse.reason))
                    }
                }
                delay(5000)
            }
        }
    }
}