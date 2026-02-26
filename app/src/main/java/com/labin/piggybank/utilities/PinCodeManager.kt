package com.labin.piggybank.utilities

import java.security.MessageDigest

object PinCodeManager {
    fun hashPin(pin: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(pin.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }

    fun verifyPin(inputPin: String, hashedPin: String): Boolean {
        return hashPin(inputPin) == hashedPin
    }
}