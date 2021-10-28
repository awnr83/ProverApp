package com.moapdev.proverapp.chat

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.moapdev.proverapp.R
import com.moapdev.proverapp.databinding.ItemChatBinding
import com.moapdev.proverapp.entities.Message

class ChatAdapter(private val msgList: MutableList<Message>, private val listener:OnChatListener):RecyclerView.Adapter<ChatAdapter.ViewHolder>()  {

    private lateinit var context: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding= ItemChatBinding.bind(view)

        fun setListener(msg: Message){
            binding.tvMsg.setOnClickListener {
                listener.deleteMessagge(msg)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context= parent.context
        val view= LayoutInflater.from(context).inflate(R.layout.item_chat,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg=msgList[position]
        holder.setListener(msg)

        var gravity= Gravity.END
        var background= ContextCompat.getDrawable(context, R.drawable.background_chat_cliente)
        var color= ContextCompat.getColor(context, R.color.design_default_color_on_secondary)

        if(msg.isSentByMe()) {
            gravity = Gravity.START
            background = ContextCompat.getDrawable(context, R.drawable.background_chat_support)
            color = ContextCompat.getColor(context, R.color.design_default_color_on_primary)
        }

        holder.binding.root.gravity=gravity
        holder.binding.tvMsg.setBackgroundColor(background)
        holder.binding.tvMsg.setTextColor(color)

        holder.binding.tvMsg.text = msg.message
    }

    override fun getItemCount()= msgList.size

    fun add(msg: Message){
        if(!msgList.contains(msg)) {
            msgList.add(msg)
            notifyItemInserted(msgList.size - 1)
        }
    }
    fun update(msg: Message){
        val index=msgList.indexOf(msg)
        if(index!=-1) {
            msgList[index]=msg
            notifyItemChanged(index)
        }
    }
    fun delete(msg: Message){
        val index=msgList.indexOf(msg)
        if(index!=-1) {
            msgList.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}