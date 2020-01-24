package com.zestworks.currencycoverterapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zestworks.currencycoverterapplication.repository.CurrencyData
import com.zestworks.currencycoverterapplication.repository.Repository
import com.zestworks.currencycoverterapplication.repository.network.NetworkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CurrencyViewModel(private val repository: Repository) : ViewModel() {

    private val _rates = MutableLiveData<CurrencyUiModel>()
    val rates: LiveData<CurrencyUiModel> = _rates

    private lateinit var launch: Job

    fun onEvent(event: UIEvent) {
        when (event) {
            is UIEvent.StartEditUIEvent -> {
                launch.cancel()

                val baseCurrency = event.currencyName
                val currencyList = (_rates.value as CurrencyUiModel.SuccessCurrencyUiModel).currencyList.toMutableList()
                if (currencyList.first().name != baseCurrency) {
                    val currency = currencyList.find { it.name == baseCurrency }
                    if (currency != null) {
                        currencyList.remove(currency)
                        currencyList.add(0, currency)
                        _rates.value = (CurrencyUiModel.SuccessCurrencyUiModel(currencyList))
                    }
                }

                onUIStarted()
            }
            is UIEvent.TextChangeUIEvent -> {
                launch.cancel()
                val editedCurrencyValue = event.value
                val currencyList = (_rates.value as CurrencyUiModel.SuccessCurrencyUiModel).currencyList
                val currentCurrencyValue = currencyList.first().value
                val currentBase = currencyList.first().name

                var diff = editedCurrencyValue / currentCurrencyValue
                if (diff.isNaN() || diff.isInfinite()) {
                    diff = 0.0
                }
                val updatedCurrencyList = mutableListOf<Currency>()
                updatedCurrencyList.add(Currency(currentBase, editedCurrencyValue))
                currencyList.forEach {
                    if (it.name != currentBase) {
                        updatedCurrencyList.add(Currency(it.name, it.value * diff))
                    }
                }
                _rates.value = (CurrencyUiModel.SuccessCurrencyUiModel(updatedCurrencyList))
                onUIStarted()
            }
        }
    }

    fun onUIStarted() {
        if (!(::launch.isInitialized) || !launch.isActive) {
            launch = viewModelScope.launch {
                while (true) {
                    var base: String? = null
                    var value: Double? = null
                    if (_rates.value != null) {
                        if (_rates.value is CurrencyUiModel.SuccessCurrencyUiModel) {
                            base = (_rates.value as CurrencyUiModel.SuccessCurrencyUiModel).currencyList.first().name
                            value = (_rates.value as CurrencyUiModel.SuccessCurrencyUiModel).currencyList.first().value
                        }
                    } else {
                        _rates.postValue(CurrencyUiModel.LoadingCurrencyUiModel)
                    }
                    val networkResponse: NetworkResult<CurrencyData> = if (base != null) {
                        repository.getCurrencyData(base)
                    } else {
                        repository.getCurrencyData("EUR")
                    }
                    when (networkResponse) {
                        is NetworkResult.Success -> {
                            val currencyList = mutableListOf<Currency>()

                            val baseCurrency = networkResponse.data.base
                            currencyList.add(Currency(name = baseCurrency, value = 1.0))

                            val rates = networkResponse.data.rates
                            if (_rates.value != null && _rates.value is CurrencyUiModel.SuccessCurrencyUiModel) {
                                val prevCurrencyList = (_rates.value as CurrencyUiModel.SuccessCurrencyUiModel).currencyList
                                currencyList.addAll(
                                        prevCurrencyList.filter { it.name != baseCurrency }.map { Currency(it.name, rates[it.name]!!) }
                                )
                            } else {
                                rates.keys.forEach {
                                    currencyList.add(Currency(name = it, value = rates[it]!!))
                                }
                            }

                            if (base != null && value != null) {
                                if (base == currencyList.first().name && value != currencyList.first().value) {
                                    val updatedList = mutableListOf<Currency>().apply { addAll(currencyList.map { Currency(it.name, (it.value * value)) }) }
                                    _rates.postValue(CurrencyUiModel.SuccessCurrencyUiModel(updatedList))
                                } else {
                                    _rates.postValue(CurrencyUiModel.SuccessCurrencyUiModel(currencyList))
                                }
                            } else {
                                _rates.postValue(CurrencyUiModel.SuccessCurrencyUiModel(currencyList))
                            }
                        }
                        is NetworkResult.Error -> {
                            _rates.postValue(CurrencyUiModel.ErrorCurrencyUiModel(reason = networkResponse.reason))
                        }
                    }
                    delay(1000)
                }
            }
        }
    }
}