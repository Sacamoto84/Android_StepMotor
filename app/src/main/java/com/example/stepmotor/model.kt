package com.example.stepmotor


import com.example.stepmotor.bt.channelNetworkOut
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date

//Блок настроек
var settingMiliAmper = MutableStateFlow(0)    //V0
var settingSteps = MutableStateFlow(0)        //V1
var settingMicrostep = MutableStateFlow(0)    //V2
var settingMotorOnOff = MutableStateFlow(0)   //V3
var settingMaxSpeed = MutableStateFlow(0)     //V4
var settingAcceleration = MutableStateFlow(0) //V5
var settingTarget = MutableStateFlow(0)       //V6
var settingReady = MutableStateFlow(0)        //V7

//Счетчик принятых пакетов
var counterInput = MutableStateFlow(0)



data class shadowRegister(
    var inValue: Int = 0,  //Данные полученные от stm32
    var outValue: Int = 0, //Выходной регистр будет отправлен на stm32
    var newOutputData: Boolean = false, //Запись true
    var timeOutput: Date = Date(),  //Время когда была сделана отправка
    //var newInputData: Boolean = false, //true когда получили новые данные
    //var timeInput: Date = Date(),  //Время когда была сделана отправка
)

@OptIn(DelicateCoroutinesApi::class)
fun syncRun() {

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
        }
    }

}


val shadowList = Array(8) { shadowRegister() }

@OptIn(DelicateCoroutinesApi::class)
private fun send(index: Int, value: Int) {
    GlobalScope.launch(Dispatchers.IO) {
        channelNetworkOut.send("V $index $value")
    }
}




