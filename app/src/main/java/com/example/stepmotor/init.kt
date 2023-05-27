package com.example.stepmotor

import android.content.Context
import com.example.stepmotor.bt.BT
import com.example.stepmotor.bt.decoder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date


/**
 * ## Флаг который говорит что первое чтение от STM32 использовать для инициализации Compos элементов
 */
var initCompose = false


@OptIn(DelicateCoroutinesApi::class)
class Initialization(context: Context) {

    init {

        bt.init(context)
        bt.getPairedDevices()
        bt.autoconnect(context)

        decoder.run()
        decoder.addCmd("V")
        {

            try {

                it.forEachIndexed { i, v ->
                    shadowList[i].inValue = v.toInt()
                    shadowList[i].timeInput = Date()
                    shadowList[i].outValue = v.toInt()
                }

                //Инициализация компос
                if (!initCompose) {
                    GlobalScope.launch(Dispatchers.Main) {
                        settingMiliAmper = it[0].toInt()    //V0
                        settingSteps = it[1].toInt()        //V1
                        settingMicrostep = it[2].toInt()    //V2
                        settingMotorOnOff = it[3].toInt()   //V3
                        settingMaxSpeed = it[4].toInt()     //V4
                        settingAcceleration = it[5].toInt() //V5
                        settingTarget = it[6].toInt()       //V6
                        settingReady = it[7].toInt()        //V7
                    }
                    initCompose = true
                }

                //Счетчик принятых пакетов
                 counterInput.value++


            } catch (e: Exception) {
                Timber.e(e.localizedMessage)
            }

        }





        SyncRun()

        GlobalScope.launch(Dispatchers.IO) {
            bt.btStatus.collect {
                if (it == BT.BTstatus.DISCONNECT)
                {
                    initCompose = false
                }
            }
        }

    }


}