package com.moapdev.proverapp.orden

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.moapdev.proverapp.Constants
import com.moapdev.proverapp.R
import com.moapdev.proverapp.databinding.ActivityOrdenBinding
import com.moapdev.proverapp.entities.Orden

class OrdenActivity : AppCompatActivity(),OnOrdenListener,OrdenAux {

    private lateinit var mBinding: ActivityOrdenBinding
    private lateinit var mAdapter: OrdenAdapter
    private lateinit var ordenSelect: Orden

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding= ActivityOrdenBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupRecyclerView()
        setupFirestore()
    }

    private fun setupRecyclerView() {
        mAdapter= OrdenAdapter(mutableListOf(),this)
        mBinding.rvOrdenes.adapter=mAdapter
    }

    private fun setupFirestore(){
        val userId= FirebaseAuth.getInstance().currentUser!!.uid

        val db= FirebaseFirestore.getInstance()
        db.collection(Constants.FR_REQUESTS).get().addOnSuccessListener {
            for (document in it){
                val orden= document.toObject(Orden::class.java)
                if(userId == orden.clienteId) {
                    orden.id = document.id
                    mAdapter.add(orden)
                }
            }
        }
            .addOnFailureListener {
                Toast.makeText(this, "Error al consultar los datos", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStartChat(orden: Orden) {
        TODO("Not yet implemented")
    }

    override fun onStatusChange(orden: Orden) {
        val db= FirebaseFirestore.getInstance()
        db.collection(Constants.FR_REQUESTS)
            .document(orden.id)
            .update(Constants.PROP_ESTADO, orden.status)
            .addOnSuccessListener {
                Toast.makeText(this, "Orden actualizad", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo actualizar la orden", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getOrdeSelect()= ordenSelect
}