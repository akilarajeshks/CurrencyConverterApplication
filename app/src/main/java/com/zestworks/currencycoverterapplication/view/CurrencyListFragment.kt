package com.zestworks.currencycoverterapplication.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.zestworks.currencycoverterapplication.CurrencyViewModel
import com.zestworks.currencycoverterapplication.R
import kotlinx.android.synthetic.main.fragment_currency_list.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CurrencyListFragment : Fragment() {

    private val currencyViewModel : CurrencyViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_currency_list, container, false)
    }

    override fun onStart() {
        super.onStart()

        currencyViewModel.rates.observe(this, Observer {
            when(it){
                CurrencyViewData.LoadingCurrencyViewData -> {
                    //show loader
                }
                is CurrencyViewData.SuccessCurrencyViewData -> {
                    if (currency_list_recycler.adapter == null){
                        currency_list_recycler.apply {
                            adapter = CurrencyListAdapter(it.currencyList, object : AdapterCallback{
                                override fun onUIEvent(uiEvent: UIEvent) {
                                    currencyViewModel.onEvent(uiEvent)
                                }
                            })
                            layoutManager = LinearLayoutManager(this.context)
                        }
                    }else{
                        (currency_list_recycler.adapter!! as CurrencyListAdapter).setList(it.currencyList)
                        //currency_list_recycler.adapter!!.notifyDataSetChanged()
                    }
                }
                is CurrencyViewData.ErrorCurrencyViewData -> {
                    //show error
                }
            }
        })

        currencyViewModel.onUIStarted()
    }
}

interface AdapterCallback{
    fun onUIEvent(uiEvent : UIEvent)
}