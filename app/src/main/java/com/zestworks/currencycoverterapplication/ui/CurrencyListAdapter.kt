package com.zestworks.currencycoverterapplication.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jakewharton.rxbinding3.widget.textChanges
import com.zestworks.currencycoverterapplication.R
import com.zestworks.currencycoverterapplication.viewmodel.Currency
import com.zestworks.currencycoverterapplication.viewmodel.UIEvent
import io.reactivex.disposables.CompositeDisposable


class CurrencyListAdapter(var currencyList: List<Currency>, private val adapterCallback: AdapterCallback) : RecyclerView.Adapter<CurrencyListAdapter.CurrencyItemHolder>() {
    private val compositeDisposable = CompositeDisposable()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyItemHolder =
            CurrencyItemHolder(LayoutInflater.from(parent.context!!).inflate(R.layout.curreny_list_item, parent, false))

    override fun getItemCount(): Int = currencyList.size

    override fun onBindViewHolder(holder: CurrencyItemHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            if ((payloads.first() as Currency).name == holder.currencyName.text) {
                val floatValue: Float = (payloads.first() as Currency).value.toFloat()
                holder.currencyValue.setText(String.format("%.2f", floatValue))
            }
        }
    }

    override fun onBindViewHolder(holder: CurrencyItemHolder, position: Int) {
        val currency = currencyList[position]

        holder.currencyName.text = currency.name

        if (holder.currencyValue.text.isEmpty()) {
            val floatValue: Float = currency.value.toFloat()
            holder.currencyValue.setText(String.format("%.2f", floatValue))
        } else {
            if (currency.value != holder.currencyValue.text.toString().toDouble()) {
                val floatValue: Float = currency.value.toFloat()
                holder.currencyValue.setText(String.format("%.2f", floatValue))
            }
        }

        holder.currencyFullName.text = currencyFullNames[currency.name]

        Glide
                .with(holder.itemView.context)
                .load(countryFlagImages[currency.name])
                .apply(RequestOptions.circleCropTransform())
                .into(holder.currencyFlag)

    }

    @SuppressLint("ClickableViewAccessibility")
    inner class CurrencyItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencyName: TextView = itemView.findViewById(R.id.curreny_name_textview)
        val currencyFullName: TextView = itemView.findViewById(R.id.curreny_full_name_textview)
        val currencyValue: EditText = itemView.findViewById(R.id.currency_value_edittext)
        val currencyFlag: ImageView = itemView.findViewById(R.id.currency_image_view)

        init {

            currencyValue.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    adapterCallback.onUIEvent(UIEvent.StartEditUIEvent(this@CurrencyListAdapter.currencyList[adapterPosition].name))
                }
                false
            }

            compositeDisposable.add(currencyValue.textChanges().skipInitialValue().subscribe {
                if (currencyValue.isFocused) {
                    if (it.toString().isEmpty() || it.toString() == ".") {
                        adapterCallback.onUIEvent(UIEvent.TextChangeUIEvent(0.0))
                    } else {
                        adapterCallback.onUIEvent(UIEvent.TextChangeUIEvent(it.toString().toDouble()))
                    }
                }
            })
        }
    }

    fun setList(currencyList: List<Currency>) {
        val toMutableList = this.currencyList.toMutableList()
        //To maintain cursor position while editing.
        if (toMutableList.first().name == currencyList.first().name) {
            toMutableList[0] = currencyList[0]
        }

        val diffCallback = DiffUtil(toMutableList, currencyList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.currencyList = currencyList

        diffResult.dispatchUpdatesTo(this)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        compositeDisposable.dispose()
        super.onDetachedFromRecyclerView(recyclerView)
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

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return newList[newItemPosition]
    }
}
