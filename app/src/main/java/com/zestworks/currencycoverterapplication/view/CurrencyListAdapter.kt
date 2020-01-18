package com.zestworks.currencycoverterapplication.view

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zestworks.currencycoverterapplication.R

class CurrencyListAdapter(var currencyList: List<Currency>, private val adapterCallback: AdapterCallback) : RecyclerView.Adapter<CurrencyListAdapter.CurrencyItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyItemHolder =
            CurrencyItemHolder(LayoutInflater.from(parent.context!!).inflate(R.layout.curreny_list_item, parent, false))

    override fun getItemCount(): Int = currencyList.size

    override fun onBindViewHolder(holder: CurrencyItemHolder, position: Int) {
        val currency = currencyList[position]
        holder.currencyName.text = currency.name
        holder.currencyValue.setText(currency.value.toString())
    }

    inner class CurrencyItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencyName: TextView = itemView.findViewById(R.id.curreny_name_textview)
        val currencyValue: EditText = itemView.findViewById(R.id.currency_value_edittext)

        init {
            currencyValue.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    onFocusChanged(adapterPosition)
                    true
                }
                false
            }
            currencyValue.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

            })
        }
    }

    fun onFocusChanged(adapterPosition: Int) {
        adapterCallback.onUIEvent(UIEvent.StartEditUIEvent(currencyList[adapterPosition].name))
    }
}

class DiffUtil(private val oldList: List<String>, private val newList: List<String>) :
        DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList.containsAll(newList)
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val name = oldList[oldItemPosition]
        val name1 = newList[newItemPosition]

        return name == name1
    }
}
