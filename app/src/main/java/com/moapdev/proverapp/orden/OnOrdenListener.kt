package com.moapdev.proverapp.orden

import com.moapdev.proverapp.entities.Orden

interface OnOrdenListener {
    fun onStartChat(orden: Orden)
    fun onStatusChange(orden: Orden)
}