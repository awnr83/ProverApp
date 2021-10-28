package com.moapdev.proverapp.home


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.moapdev.proverapp.databinding.ItemProductoBinding
import com.moapdev.proverapp.entities.Producto

class HomeAdapter(val clickListener: ProductoListener, val clickLongListener: ProductoLongListener, val imgListener: ImageListener)
    :ListAdapter<Producto,HomeAdapter.Viewholder>(ProductoCallback()) {

    class Viewholder private constructor(val binding: ItemProductoBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(item: Producto, clickListener: ProductoListener, clickLongListener: ProductoLongListener, imgListener: ImageListener) {
            //binding.imgProducto -> glide
            binding.producto=item
            binding.tvNombre.text= item.name
            binding.tvPrecio.text= item.precio.toString()
            binding.tvCantidad.text= item.cantidad.toString()

            binding.clickListener= clickListener
            binding.clickLongListener= clickLongListener
            binding.imgListener=imgListener

            Glide.with(itemView)
                .load(item.imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL) //guarda toda la imagen
                .centerCrop()
                .into(binding.imgProducto)
            
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup):Viewholder{
                val layoutInflater= LayoutInflater.from(parent.context)
                var binding= ItemProductoBinding.inflate(layoutInflater,parent,false)
                return Viewholder(binding)
            }
        }
    }

    class ProductoCallback:DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem===newItem
        }
        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem==newItem
        }
    }
    class ProductoListener(val clickListener: (producto: Producto)->Unit) {
        fun onClick(producto: Producto)= clickListener(producto)
    }
    class ProductoLongListener(val clickListener: (producto: Producto)->Unit) {
        fun onClick(producto: Producto):Boolean{
            clickListener(producto)
            return true
        }
    }
    class ImageListener(val imgListener: (producto:Producto)->Unit){
        fun onClick(producto: Producto)= imgListener(producto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        return Viewholder.from(parent)
    }
    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.bind(getItem(position), clickListener, clickLongListener, imgListener)
    }
}