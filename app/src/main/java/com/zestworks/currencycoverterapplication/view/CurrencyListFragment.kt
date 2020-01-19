package com.zestworks.currencycoverterapplication.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.zestworks.currencycoverterapplication.CurrencyViewModel
import com.zestworks.currencycoverterapplication.R
import kotlinx.android.synthetic.main.fragment_currency_list.*
import org.koin.android.ext.android.inject

class CurrencyListFragment : Fragment() {

    private val currencyViewModel: CurrencyViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_currency_list, container, false)
    }

    override fun onStart() {
        super.onStart()

        currencyViewModel.rates.observe(this, Observer {
            when (it) {
                is CurrencyViewData.SuccessCurrencyViewData -> {

                    if (currency_list_recycler.adapter == null) {
                        currency_list_recycler.apply {
                            adapter = CurrencyListAdapter(it.currencyList, object : AdapterCallback {
                                override fun onUIEvent(uiEvent: UIEvent) {
                                    currencyViewModel.onEvent(uiEvent)
                                }
                            })
                            layoutManager = LinearLayoutManager(this.context)
                        }
                    } else {
                        (currency_list_recycler.adapter!! as CurrencyListAdapter).setList(it.currencyList)
                    }

                    error_text_view.visibility = View.INVISIBLE
                    currency_list_recycler.visibility = View.VISIBLE
                }
                is CurrencyViewData.ErrorCurrencyViewData -> {
                    currency_list_recycler.visibility = View.GONE
                    error_text_view.visibility = View.VISIBLE
                }
            }
        })

        currencyViewModel.onUIStarted()
    }
}

interface AdapterCallback {
    fun onUIEvent(uiEvent: UIEvent)
}