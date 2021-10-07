package com.moapdev.proverapp.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.FragmentNavigatorDestinationBuilder
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.moapdev.proverapp.InterfaceHA
import com.moapdev.proverapp.R
import com.moapdev.proverapp.add.AddDialogFragment
import com.moapdev.proverapp.databinding.FragmentHomeBinding
import com.moapdev.proverapp.model.Producto
import java.net.ProxySelector

class HomeFragment : Fragment(), InterfaceHA {

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mAdapter: HomeAdapter
    private lateinit var mFirestoreListener: ListenerRegistration

    private lateinit var mViewModel: HomeViewModel

    private var mProductoSelected: Producto?= null

    private var lista= mutableListOf<Producto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding= FragmentHomeBinding.inflate(inflater)

        mViewModel= ViewModelProvider(this).get(HomeViewModel::class.java)
        mBinding.viewModel=mViewModel
        mBinding.lifecycleOwner=this

        mViewModel.productos.observe(viewLifecycleOwner, Observer {
            mAdapter.submitList(it)
        })
        mViewModel.error.observe(viewLifecycleOwner, Observer {
            if(it) {
                Toast.makeText(context,"Error al realizar la operacion",Toast.LENGTH_SHORT).show()
            }
        })
        mViewModel.exito.observe(viewLifecycleOwner, Observer {
            if(it) {
                Toast.makeText(context,"Operacion exitosa",Toast.LENGTH_SHORT).show()
            }
        })
        configRecyclerView()
        //configFirestoneRealTime()
        configButtons()

        return mBinding.root
    }

    private fun configRecyclerView(){
        mAdapter= HomeAdapter(
            HomeAdapter.ProductoListener {
                Log.i("alfredo","producto: ${it.name}")
                mProductoSelected=it
                AddDialogFragment().show(parentFragmentManager,AddDialogFragment::class.java.simpleName)
            },
            HomeAdapter.ProductoLongListener {
                mViewModel.eliminarProducto(it)
            },
            HomeAdapter.ImageListener{
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetalleFragment(it))
            }
        )
        mBinding.recyclerViewProductos.adapter= mAdapter
    }

   /* private fun configFirestoneRealTime(){
        val db= FirebaseFirestore.getInstance()
        val productoRef= db.collection("productos")

        mFirestoreListener=productoRef.addSnapshotListener { value, error ->
            if(error!=null) {
                Toast.makeText(context, "Error al leer la db", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            for(document in value!!.documentChanges){
                val producto= document.document.toObject(Producto::class.java)
                producto.id= document.document.id
                when(document.type){
                    DocumentChange.Type.ADDED->{ //adapter.add(producto)
                        }
                    DocumentChange.Type.MODIFIED->{ //adapter.update(producto)
                        }
                    DocumentChange.Type.REMOVED->{ //adapter.delete(producto)
                     }
                }
            }
        }
    }*/

    private fun configButtons(){
        mBinding.fabCrear.setOnClickListener {
            mProductoSelected= null
            AddDialogFragment().show(parentFragmentManager,AddDialogFragment::class.java.simpleName)
        }
    }

    override fun getProductSelected(): Producto? = mProductoSelected
}