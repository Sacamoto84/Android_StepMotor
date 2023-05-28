package com.example.stepmotor.bt

import kotlinx.coroutines.channels.Channel

//Канал передачи из STM32
val channelNetworkIn = Channel<String>(Channel.UNLIMITED)

//Канал передачи в STM32, просто записываем команды
val channelNetworkOut = Channel<String>(Channel.UNLIMITED)

val decoder = NetCommandDecoder(channelNetworkIn)

var bt = BT("Generator", channelNetworkIn, channelNetworkOut)

