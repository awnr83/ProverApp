package com.moapdev.proverapp.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.moapdev.proverapp.entities.Producto

class HomeViewModel: ViewModel() {

    private var firestore: FirebaseFirestore

    private val _productos = MutableLiveData<ArrayList<Producto>>()
    val productos: LiveData<ArrayList<Producto>>
        get() = _productos

    private val _error= MutableLiveData<Boolean>()
    val error: LiveData<Boolean>
        get()= _error

    private val _exito= MutableLiveData<Boolean>()
    val exito: LiveData<Boolean>
        get()= _exito

    init {
        firestore = FirebaseFirestore.getInstance()
        _error.value=false
        _exito.value=false
        iniciar()
    }

    private fun iniciar() {
        firestore.collection("productos").addSnapshotListener { value, error ->
            if (error != null) {
                Log.i("alfredo", "Listen Failed")
                return@addSnapshotListener
            }
            if (value != null) {
                val allProductos = ArrayList<Producto>()
                val documents = value.documents
                documents.forEach {
                    val producto = it.toObject(Producto::class.java)
                    if (producto != null) {
                        producto.id = it.id
                        allProductos.add(producto)
                    }
                }
                _productos.value = allProductos
            }
        }
    }
    fun eliminarProducto(producto: Producto){
        val productoRef= firestore.collection("productos")
        producto.id?.let {id->
            productoRef.document(id)
                .delete()
                .addOnSuccessListener { _exito.value=true }
                .addOnFailureListener { _error.value=true }
        }
    }
}