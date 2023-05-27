package com.example.stepmotor


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.stepmotor.bt.channelNetworkOut
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date

//Блок настроек
var settingMiliAmper by mutableStateOf(0)    //V0
var settingSteps by mutableStateOf(0)        //V1
var settingMicrostep by mutableStateOf(0)    //V2
var settingMotorOnOff by mutableStateOf(0)   //V3
var settingMaxSpeed by mutableStateOf(0)     //V4
var settingAcceleration by mutableStateOf(0) //V5
var settingTarget by mutableStateOf(0)       //V6
var settingReady by mutableStateOf(0)        //V7

//Счетчик принятых пакетов
var counterInput = MutableStateFlow(0)



data class shadowRegister(
    var inValue: Int = 0,  //Данные полученные от stm32
    var outValue: Int = 0, //Выходной регистр будет отправлен на stm32
    var newOutputData: Boolean = false, //Запись true
    var timeOutput: Date = Date(),  //Время когда была сделана отправка
    var newInputData: Boolean = false, //true когда получили новые данные
    var timeInput: Date = Date(),  //Время когда была сделана отправка
)

@OptIn(DelicateCoroutinesApi::class)
fun SyncRun() {

    GlobalScope.launch(Dispatchers.IO) {
        //channelNetworkOut.send("V $index $value")

        while (true) {
            shadowList.forEachIndexed { i, value ->

                //Первая отсылка
                if (value.newOutputData) {
                    if (value.outValue != value.inValue) {
                        value.timeOutput = Date()
                        send(i, value.outValue)
                    }
                    value.newOutputData = false
                } else {
                    //Условие того что данные не пришли в первый раз и шлем заново
                    if ((value.outValue != value.inValue) and ((Date().time - value.timeOutput.time) > 500)) {
                        value.newOutputData = true
                    }
                }


            }

//            GlobalScope.launch(Dispatchers.Main) {
//                if (bt.btStatus == BT.BTstatus.DISCONNECT)
//                    initCompose = false
//            }

        }
    }



}


val shadowList = Array(8) { shadowRegister() }

@OptIn(DelicateCoroutinesApi::class)
fun send(index: Int, value: Int) {


    GlobalScope.launch(Dispatchers.Main) {
        channelNetworkOut.send("V $index $value")
    }
}




