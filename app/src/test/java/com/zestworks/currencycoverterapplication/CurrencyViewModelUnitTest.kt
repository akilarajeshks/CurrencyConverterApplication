package com.zestworks.currencycoverterapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.zestworks.currencycoverterapplication.network.NetworkResult
import com.zestworks.currencycoverterapplication.repository.Repository
import com.zestworks.currencycoverterapplication.view.Currency
import com.zestworks.currencycoverterapplication.view.CurrencyViewData
import com.zestworks.currencycoverterapplication.view.UIEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CurrencyViewModelUnitTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mockk<Repository>()
    private lateinit var currencyViewModel: CurrencyViewModel
    private val testObserver: Observer<CurrencyViewData> = mockk()
    @ExperimentalCoroutinesApi
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        currencyViewModel = CurrencyViewModel(repository)
        Dispatchers.setMain(testCoroutineDispatcher)
        currencyViewModel.rates.observeForever(testObserver)

        every {
            testObserver.onChanged(any())
        }.returns(
                Unit
        )
    }

    @Test
    fun `Test if UI is updated when network returns success`() {
        every {
            runBlocking {
                repository.getCurrencyData(any())
            }
        }.returns(NetworkResult.Success(dummySuccessDataEURBase))

        currencyViewModel.onUIStarted()

        verifyOrder {
            testObserver.onChanged(CurrencyViewData.LoadingCurrencyViewData)
            testObserver.onChanged(CurrencyViewData.SuccessCurrencyViewData(dummyListOfCurrencyData))
        }

    }

    @Test
    fun `Test if UI is updated when network returns error`() {
        every {
            runBlocking {
                repository.getCurrencyData(any())
            }
        }.returns(NetworkResult.Error(errorMessage))

        currencyViewModel.onUIStarted()

        verifyOrder {
            testObserver.onChanged(CurrencyViewData.LoadingCurrencyViewData)
            testObserver.onChanged(CurrencyViewData.ErrorCurrencyViewData(errorMessage))
        }
    }

    @Test
    fun `Test if a currency is set as base when it is selected`() {
        val baseCurrency = "INR"
        every {
            runBlocking {
                repository.getCurrencyData(any())
            }
        }.returns(NetworkResult.Success(dummySuccessDataEURBase))

        currencyViewModel.onUIStarted()
        currencyViewModel.onEvent(UIEvent.StartEditUIEvent(baseCurrency))

        verifyOrder {
            testObserver.onChanged(CurrencyViewData.SuccessCurrencyViewData(dummyListAfterClickingINR))
        }
    }

    @Test
    fun `Test conversion values are updated when a currency is edited`() {
        every {
            runBlocking {
                repository.getCurrencyData(any())
            }
        }.returns(NetworkResult.Success(dummySuccessDataEURBase))

        currencyViewModel.onUIStarted()
        currencyViewModel.onEvent(UIEvent.TextChangeUIEvent(2.0))

        verifyOrder {
            testObserver.onChanged(CurrencyViewData.SuccessCurrencyViewData(dummyListOfDataAfterChangeBy2))
        }
    }

    @Test
    fun `Test if refresh loop called every one second after app is opened`() {
        every {
            runBlocking {
                repository.getCurrencyData(any())
            }
        }.returns(NetworkResult.Success(dummySuccessDataEURBase))

        currencyViewModel.onUIStarted()
        testCoroutineDispatcher.advanceTimeBy(2000)

        verifyOrder {
            testObserver.onChanged(CurrencyViewData.LoadingCurrencyViewData)
            testObserver.onChanged(CurrencyViewData.SuccessCurrencyViewData(dummyListOfCurrencyData))
            testObserver.onChanged(CurrencyViewData.SuccessCurrencyViewData(dummyListOfCurrencyData))
            testObserver.onChanged(CurrencyViewData.SuccessCurrencyViewData(dummyListOfCurrencyData))
        }
    }

    @Test
    fun `Test if refresh loop called with correct base currency after clicking INR`() {
        val baseCurrency = "INR"
        every {
            runBlocking {
                repository.getCurrencyData("EUR")
            }
        }.returns(NetworkResult.Success(dummySuccessDataEURBase))

        every {
            runBlocking {
                repository.getCurrencyData("INR")
            }
        }.returns(NetworkResult.Success(dummySuccessDataINRBase))

        currencyViewModel.onUIStarted()
        currencyViewModel.onEvent(UIEvent.StartEditUIEvent(baseCurrency))
        testCoroutineDispatcher.advanceTimeBy(2000)

        verifyOrder {
            testObserver.onChanged(CurrencyViewData.LoadingCurrencyViewData)
            testObserver.onChanged(CurrencyViewData.SuccessCurrencyViewData(dummyListOfCurrencyData.map { Currency(it.name, it.value) }))
            testObserver.onChanged(CurrencyViewData.SuccessCurrencyViewData(dummyListAfterClickingINR.map { Currency(it.name, it.value) }))
            testObserver.onChanged(CurrencyViewData.SuccessCurrencyViewData(dummyListAfterClickingINR.map { Currency(it.name, it.value) }))
            testObserver.onChanged(CurrencyViewData.SuccessCurrencyViewData(dummyListAfterClickingINR.map { Currency(it.name, it.value) }))
            testObserver.onChanged(CurrencyViewData.SuccessCurrencyViewData(dummyListAfterClickingINR.map { Currency(it.name, it.value) }))
        }
    }


    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }
}

