package com.example.stepmotor

import android.content.Context
import com.example.stepmotor.bt.decoder

class Initialization(private val context: Context) {

    init {


        bt.init(context)
        bt.getPairedDevices()
        bt.autoconnect(context)

        decoder.run()
    }






}