package com.moapdev.proverapp.entities

import com.google.firebase.firestore.Exclude

data class Orden(
    @get: Exclude var id:String="",
    val clienteId: String="",
    val productList:Map<String, ProductoOrden> = hashMapOf(),
    val total:Double=0.0,
    var status:Int=0
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Orden

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}