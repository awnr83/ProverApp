package com.moapdev.proverapp.entities

import com.google.firebase.firestore.Exclude

data class ProductoOrden(
    @get:Exclude var id:String="",
    var name:String="",
    var cantidad:Int=0
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductoOrden

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}