package ro.ionutlepi.rxjava4real.api.calls

import ro.ionutlepi.rxjava4real.api.ApiInstance
import ro.ionutlepi.rxjava4real.api.Callback

class ExceptionCall(private val apiInstance: ApiInstance, private val iotDeviceRef: String) :
    ApiCaller() {

    override fun execute(cb: Callback) {
        super.execute(cb)
        throw Exception("Boooo")
    }
}