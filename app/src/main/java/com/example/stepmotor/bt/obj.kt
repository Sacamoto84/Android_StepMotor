package com.example.stepmotor.bt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.channels.Channel


//Канал передачи из STM32
val channelNetworkIn = Channel<String>(Channel.UNLIMITED)

//Канал передачи в STM32, просто записываем команды
val channelNetworkOut = Channel<String>(Channel.UNLIMITED)

val decoder = NetCommandDecoder(channelNetworkIn)


val listV = mutableListOf<V>()

class V {

    var name = "V"

    var value by mutableStateOf(0)

    suspend fun send()
    {
        channelNetworkOut.send("$name/$value")
    }

}
