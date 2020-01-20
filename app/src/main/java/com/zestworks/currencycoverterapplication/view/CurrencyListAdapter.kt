package com.zestworks.currencycoverterapplication.view

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.widget.textChanges
import com.zestworks.currencycoverterapplication.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class CurrencyListAdapter(var currencyList: List<Currency>, private val adapterCallback: AdapterCallback) : RecyclerView.Adapter<CurrencyListAdapter.CurrencyItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyItemHolder =
            CurrencyItemHolder(LayoutInflater.from(parent.context!!).inflate(R.layout.curreny_list_item, parent, false))

    override fun getItemCount(): Int = currencyList.size

    override fun onBindViewHolder(holder: CurrencyItemHolder, position: Int) {
        val currency = currencyList[position]
        holder.currencyName.text = currency.name
        holder.currencyValue.setText(currency.value.roundOffDecimal().toString())
    }

    private fun Double.roundOffDecimal(): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(this).toDouble()
    }

    inner class CurrencyItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencyName: TextView = itemView.findViewById(R.id.curreny_name_textview)
        val currencyValue: EditText = itemView.findViewById(R.id.currency_value_edittext)

        init {

            currencyValue.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    adapterCallback.onUIEvent(UIEvent.StartEditUIEvent(this@CurrencyListAdapter.currencyList[adapterPosition].name))
                    true
                }
                false
            }

            currencyValue.textChanges().skipInitialValue().subscribe {
                if (currencyValue.isFocused) {
                    adapterCallback.onUIEvent(UIEvent.TextChangeUIEvent(it.toString().toDouble()))
                }
            }
        }
    }


    fun setList(currencyList: List<Currency>) {
        val diffCallback = DiffUtil(this.currencyList, currencyList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.currencyList = currencyList

        diffResult.dispatchUpdatesTo(this)
    }
}

class DiffUtil(private val oldList: List<Currency>, private val newList: List<Currency>) :
        DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldCurrency = oldList[oldItemPosition]
        val newCurrency = newList[newItemPosition]

        return oldCurrency.name == newCurrency.name
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldCurrency = oldList[oldItemPosition]
        val newCurrency = newList[newItemPosition]

        return oldCurrency == newCurrency
    }
}
