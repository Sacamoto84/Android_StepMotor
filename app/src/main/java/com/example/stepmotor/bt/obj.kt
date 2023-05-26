package com.example.stepmotor.bt

import kotlinx.coroutines.channels.Channel

data class NetCommand(var cmd : String, var newString : Boolean =  false)


//Канал передачи из STM32
val channelNetworkIn = Channel<String>(Channel.UNLIMITED)

//Канал передачи в STM32
val channelNetworkOut = Channel<String>(Channel.UNLIMITED)


val decoder = NetCommandDecoder(channelNetworkIn)
