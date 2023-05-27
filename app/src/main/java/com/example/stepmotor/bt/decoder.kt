package com.example.stepmotor.bt

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import timber.log.Timber

class NetCommandDecoder(
    private val channelIn: Channel<String>,               //Входной канал от bt и wifi
) {

    /**
     * # Добавить команду
     */
    fun addCmd(name: String, cb: (List<String>) -> Unit = { }) = cmdList.add(CliCommand(name, cb))


    private var lastString: String = "" //Прошлая строка
    private val channelRoute = Channel<String>(1000000)

    @OptIn(DelicateCoroutinesApi::class)
    fun run() {
        Timber.i("Запуск декодировщика")
        GlobalScope.launch(Dispatchers.IO) { decodeScope() }
        GlobalScope.launch(Dispatchers.IO) { commandDecoder() }
        GlobalScope.launch(Dispatchers.IO) { cliDecoder() }
    }

    private suspend fun decodeScope() {

        val bigStr: StringBuilder =
            StringBuilder()//Большая строка в которую и складируются данные с канала

        while (true) {

            var string =
                channelIn.receive() //Получить строку с канала, может содежать несколько строк

            string = string.replace('\r', '▒')

            //Timber.e( "in>>>${string.length} "+string )

            if (string.isEmpty()) continue

            bigStr.append(string) //Захерячиваем в большую строку

            //MARK: Будем сами делить на строки
            while (true) {
                //Индекс \n
                val indexN = bigStr.indexOf('\n')

                if (indexN != -1) {
                    //Область полюбому имеет конец строки
                    //MARK: Чета есть, копируем в подстроку
                    val stringDoN = bigStr.substring(0, indexN)
                    bigStr.delete(0, bigStr.indexOf('\n') + 1)

                    lastString += stringDoN
                    channelRoute.send(lastString)

                    //Timber.i( "out>>>${lastString.length} "+lastString )
                    lastString = ""

                } else {
                    //Конец строки не найден
                    //MARK: Тут для дополнения прошлой строки
                    //Получить полную запись посленней строки
                    lastString += bigStr
                    bigStr.clear() //Он отжил свое)
                    break
                }

            }


        }


    }

    private suspend fun commandDecoder() {

        while (true) {

            //val raw = "qqq¹xzassd¡¡¡¡¡²45³qw" //'¹' '²' '³' 179 '¡' 161    ¡ A1   §A7 ¿DF ¬AC
            val raw = channelRoute.receive()

            val posStart = raw.indexOf("!")
            val posCRC = raw.indexOf(";")
            val posEnd = raw.indexOf("$")

            if ((posStart == -1) || (posEnd == -1) || (posCRC == -1) || (posCRC !in (posStart + 1) until posEnd)) {
                //Timber.e("Ошибка позиции пакета S:$posStart C:$posCRC E:$posEnd")
                continue
            }

            if (((posEnd - posCRC) > 4) || ((posEnd - posCRC) == 1)) {
                Timber.e("S:$posStart C:$posCRC E:$posEnd")
                Timber.e("L0 > Error > (PosE - PosCRC) > 4 or == 1")
                continue
            }

            val crcStr = raw.substring(posCRC + 1 until posEnd)
            var crc = 0
            try {
                crc = crcStr.toInt()
            } catch (e: Exception) {
                Timber.e("Ошибка преобразования CRC $crcStr")
                continue
            }

            val s = raw.substring(posStart + 1 until posCRC)
            if (s == "") {
                Timber.e("Нет тела команды $raw")
                continue
            }
            val crc8 = CRC8(s)

            if (crc.toUByte() != crc8) {
                Timber.e("Ошибка CRC $crc != CRC8 $crc8 $raw")
                continue
            }
            //Прошли все проверкu
            channelOutCommand.send(s)

        }

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private val channelOutCommand = Channel<String>(1000000) //Готовые команды из пакета

    data class CliCommand(var name: String, var cb: (List<String>) -> Unit)

    //Перевод на сет
    private val cmdList = mutableListOf<CliCommand>() //Список команд


    private suspend fun cliDecoder() {
        while (true) {
            val s = channelOutCommand.receive()
            parse(s)
        }
    }

    private fun parse(str: String) {
        if (str.isEmpty()) return
        val l = str.split(' ').toMutableList()
        val name = l.first()
        l.removeFirst()
        val arg: List<String> = l.filter { it.isNotEmpty() }
        try {
            val command: CliCommand = cmdList.first { it.name == name }
            command.cb.invoke(arg)
        } catch (e: Exception) {
            Timber.e("CLI отсутствует команда $name")
        }

    }


}