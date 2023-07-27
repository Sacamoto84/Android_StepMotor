package com.example.stepmotor.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.example.stepmotor.bt.bt
import com.example.stepmotor.counterInput
import com.example.stepmotor.settingAcceleration
import com.example.stepmotor.settingMaxSpeed
import com.example.stepmotor.settingMicrostep
import com.example.stepmotor.settingMiliAmper
import com.example.stepmotor.settingMotorOnOff
import com.example.stepmotor.settingReady
import com.example.stepmotor.settingSteps
import com.example.stepmotor.settingTarget
import com.example.stepmotor.shadowList

//var settingMiliAmper    by mutableStateOf(0) //V0
//var settingSteps        by mutableStateOf(0) //V1
//var settingMicrostep    by mutableStateOf(0) //V2
//var settingMotorOnOff   by mutableStateOf(0) //V3
//var settingMaxSpeed     by mutableStateOf(0) //V4
//var settingAcceleration by mutableStateOf(0) //V5
//var settingTarget       by mutableStateOf(0) //V6
//var settingReady        by mutableStateOf(0) //V7



@Composable
fun Home()
{


   Column {

       Text(text = "${bt.btStatus.collectAsState().value}")
       Text(text = "${counterInput.collectAsState().value}")


       Text(text = "Шагов на оборот ${settingSteps.collectAsState().value}")

       Text(text = "Микрошаг ${settingMicrostep.collectAsState().value}")

       Text(text = "Максимальная скорость ${settingMaxSpeed.collectAsState().value}")

       Text(text = "Ускорение ${settingAcceleration.collectAsState().value}")

       Text(text = "Позиция ${settingTarget.collectAsState().value}")

       Text(text = "Мотор достиг позиции ${settingReady.collectAsState().value}")


       Text(text = "Ток ${settingMiliAmper.collectAsState().value} mA")
       Slider(
           steps = 19,
           valueRange = 0F..2000F,
           value = settingMiliAmper.collectAsState().value.toFloat(),
           onValueChange = {
               settingMiliAmper.value = it.toInt()
           },
           onValueChangeFinished =
           {
               shadowList[0].outValue = settingMiliAmper.value
               shadowList[0].newOutputData = true
           }
       )



       Button(onClick = {

           if (settingMotorOnOff.value == 1)
               settingMotorOnOff.value = 0
           else
               settingMotorOnOff.value = 1

           shadowList[3].outValue = settingMotorOnOff.value
           shadowList[3].newOutputData = true

       }) {

           Text(text = "Мотор ON OFF ${settingMotorOnOff.collectAsState().value}")

       }



   }





}





@Preview
@Composable
fun HomePreview()
{
    Home()
}