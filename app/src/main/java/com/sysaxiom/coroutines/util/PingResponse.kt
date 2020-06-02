package com.sysaxiom.coroutines.util

data class PingResponse(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data : Data)

class Data()