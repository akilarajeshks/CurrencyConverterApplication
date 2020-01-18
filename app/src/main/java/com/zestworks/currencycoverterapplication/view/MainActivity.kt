package com.zestworks.currencycoverterapplication.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.zestworks.currencycoverterapplication.CurrencyViewModel
import com.zestworks.currencycoverterapplication.R
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onStart() {
        super.onStart()
//        val currencyViewModel: CurrencyViewModel = getViewModel()

//        currencyViewModel.rates.observe(this, Observer {
//            when (it) {
//                CurrencyViewModel.State.Loading -> {
//                }
//                is CurrencyViewModel.State.Success -> {
//                    textView.text = it.rates.toString()
//                }
//                is CurrencyViewModel.State.Error -> {
//                    textView.text = it.reason
//                }
//            }
//        })

//        currencyViewModel.fetchCurrencyData()

    }
}

