package com.example.stepmotor.bt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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



