package com.moapdev.proverapp.entities

import com.google.firebase.database.Exclude

data class Message(
    @get:Exclude
    var id:String?="",
    var message:String="",
    var sender:String="",       //es quien envia el msg cliente o proveedor, es el ID
    @get:Exclude
    var myUid:String=""
){

    fun isSentByMe():Boolean= sender.equals(myUid)      //esto determina si el msg fue enviado por el usuario

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}