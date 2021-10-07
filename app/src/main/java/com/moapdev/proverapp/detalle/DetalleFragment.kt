package com.moapdev.proverapp.detalle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.moapdev.proverapp.R
import com.moapdev.proverapp.databinding.FragmentDetalleBinding

class DetalleFragment : Fragment() {

    private lateinit var mBinding: FragmentDetalleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding= FragmentDetalleBinding.inflate(inflater)

        val producto= DetalleFragmentArgs.fromBundle(arguments!!).producto
        mBinding.tvNombre.text=producto.name
        mBinding.tvDescripcion.text=producto.descripcion
        mBinding.tvCantidad.text=producto.cantidad.toString()
        mBinding.tvPrecio.text=producto.precio.toString()

        Glide.with(this)
            .load(producto.imgUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgProducto)
        return mBinding.root
    }
}