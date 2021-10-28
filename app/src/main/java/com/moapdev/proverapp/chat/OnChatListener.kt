package com.moapdev.proverapp.chat

import com.moapdev.proverapp.entities.Message

interface OnChatListener {
    fun deleteMessagge(message: Message)
}