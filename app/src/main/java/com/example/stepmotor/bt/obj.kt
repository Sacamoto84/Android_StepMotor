package com.example.stepmotor.bt

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

//Канал передачи из STM32
val channelNetworkIn = Channel<String>(Channel.UNLIMITED)

//Канал передачи в STM32, просто записываем команды
val channelNetworkOut = Channel<String>(Channel.UNLIMITED)

val decoder = NetCommandDecoder(channelNetworkIn)

var bt = BT("Generator", channelNetworkIn, channelNetworkOut)

@OptIn(DelicateCoroutinesApi::class)
fun send(index: Int, value: Int) {
    GlobalScope.launch(Dispatchers.IO) {
        channelNetworkOut.send("V $index $value")
    }
}