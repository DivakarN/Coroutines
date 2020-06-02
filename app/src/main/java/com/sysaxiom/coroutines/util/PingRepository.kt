package com.sysaxiom.coroutines.util

class PingRepository (val networkApis: NetworkApis) : SafeApiRequest() {

    suspend fun ping(): PingResponse {
        return apiRequest { networkApis.ping() }
    }

}