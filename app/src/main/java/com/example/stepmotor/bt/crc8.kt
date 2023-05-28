package com.example.stepmotor.bt

/*
Name  : CRC-8
Poly  : 0x31    x^8 + x^5 + x^4 + 1
Init  : 0xFF
Revert: false
XorOut: 0x00
Check : 0xF7 ("123456789")
MaxLen: 15 байт(127 бит) - обнаружение
одинарных, двойных, тройных и всех нечетных ошибок
*/
fun CRC8(str: String): UByte {

    var crc: UByte = 0xFFu.toUByte()
    var i: Int

    for (j in str.indices) {
        crc = crc xor str[j].code.toUByte()

        for (k in 0 until 8) {
            crc = if (crc and 0x80u.toUByte() != 0u.toUByte()) {
                (crc.toUInt() shl 1 xor 0x31u.toUInt()).toUByte()
            } else {
                crc.toUInt().shl(1).toUByte()
            }
        }
    }
    return crc
}