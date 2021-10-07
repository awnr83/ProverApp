package com.moapdev.proverapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import com.google.firebase.firestore.Exclude

@Parcelize
data class Producto (
    @get:Exclude var id: String?=null,  //con esto no se toma encuenta el id en firesbase,
                                        // recordar q el id q se usa es de firestore
    var name: String?=null,
    var descripcion: String?=null,
    var imgUrl: String?=null,
    var cantidad: Int=0,
    var precio: Double=0.0
):Parcelable
//  ya no se necesita mas xq se tiene diffutil
// {
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as Producto
//
//        if (id != other.id) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return id?.hashCode() ?: 0
//    }
//}