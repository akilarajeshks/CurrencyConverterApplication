package com.zestworks.currencycoverterapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.zestworks.currencycoverterapplication.network.NetworkResult
import com.zestworks.currencycoverterapplication.repository.Repository
import com.zestworks.currencycoverterapplication.view.CurrencyViewData
import com.zestworks.currencycoverterapplication.view.UIEvent
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CurrencyViewModelUnitTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mockk<Repository>()
    private lateinit var currencyViewModel: CurrencyViewModel
    private val testObserver: Observer<CurrencyViewData> = mockk()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        currencyViewModel = CurrencyViewModel(repository)
        Dispatchers.setMain(Dispatchers.Unconfined)
        currencyViewModel.rates.observeForever(testObserver)

        every {
            testObserver.onChanged(any())
        }.returns(
                Unit
        )
    }

    @Test
    fun `Test Success`() {
        every {
            runBlocking {
                repository.getCurrencyData()
            }
        }.returns(NetworkResult.Success(dummySuccessData))

        currencyViewModel.onUIStarted()

        verifyOrder {
            testObserver.onChanged(CurrencyViewData.LoadingCurrencyViewData)
            testObserver.onChanged(CurrencyViewData.SuccessCurrencyViewData(dummyListOfCurrencyData))
        }

    }

    @Test
    fun `Test Error`() {
        every {
            runBlocking {
                repository.getCurrencyData()
            }
        }.returns(NetworkResult.Error(errorMessage))

        currencyViewModel.onUIStarted()

        verifyOrder {
            testObserver.onChanged(CurrencyViewData.LoadingCurrencyViewData)
            testObserver.onChanged(CurrencyViewData.ErrorCurrencyViewData(errorMessage))
        }
    }

    @Test
    fun `List rearrange on UIEvent`() {
        val baseCurrency = "INR"
        every {
            runBlocking {
                repository.getCurrencyData()
            }
        }.returns(NetworkResult.Success(dummySuccessData))

        currencyViewModel.onUIStarted()
        currencyViewModel.onEvent(UIEvent.StartEditUIEvent(baseCurrency))

        val currentRateList = currencyViewModel.rates.value
        (currentRateList is CurrencyViewData.SuccessCurrencyViewData) shouldBe true
        (currentRateList as CurrencyViewData.SuccessCurrencyViewData).currencyList.first().name shouldBe baseCurrency
    }



    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }
}

