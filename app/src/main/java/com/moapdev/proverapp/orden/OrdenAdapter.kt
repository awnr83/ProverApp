package com.moapdev.proverapp.orden

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moapdev.proverapp.R
import com.moapdev.proverapp.databinding.ItemOrdenBinding

import com.moapdev.proverapp.entities.Orden

class OrdenAdapter(private val orderList:MutableList<Orden>, private val listener: OnOrdenListener):RecyclerView.Adapter<OrdenAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val aValores: Array<String> by lazy {
        context.resources.getStringArray(R.array.estado_valor)
    }
    private val aKey: Array<Int> by lazy {
        context.resources.getIntArray(R.array.estado_key).toTypedArray()
    }

    inner class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val binding= ItemOrdenBinding.bind(view)

        fun setListener(orden: Orden){
            binding.actvEstado.setOnItemClickListener { adapterView, view, i, l ->  // i->posicion  l->id
                orden.status=aKey[i]
                listener.onStatusChange(orden)
            }
            binding.btnChat.setOnClickListener {
                listener.onStartChat(orden)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        val view=LayoutInflater.from(context).inflate(R.layout.item_orden, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orden= orderList[position]
        var nombres=""
        orden.productList.forEach{
            nombres+="${it.value.name}, "
        }
        holder.setListener(orden)
        holder.binding.tvId.text= context.getString(R.string.fo_id,orden.id)
        holder.binding.tvProductoName.text= nombres.dropLast(2)
        holder.binding.tvTotal.text= context.getString(R.string.fo_total, orden.total)

        val index= aKey.indexOf(orden.status)
        val estadoAdapter= ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, aValores)
        holder.binding.actvEstado.setAdapter(estadoAdapter)
        if(index!=-1)
            holder.binding.actvEstado.setText(aValores[index], false)
        else
            holder.binding.actvEstado.setText(context.getText(R.string.fo_estado_desconocido), false)
    }

    override fun getItemCount()=orderList.size

    fun add(orden: Orden){
        orderList.add(orden)
        notifyItemInserted(orderList.size-1)
    }
    
}