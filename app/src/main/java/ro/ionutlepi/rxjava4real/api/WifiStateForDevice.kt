package ro.ionutlepi.rxjava4real.api

import ro.ionutlepi.rxjava4real.api.calls.ApiCaller
import ro.ionutlepi.rxjava4real.api.calls.AuthCall
import ro.ionutlepi.rxjava4real.api.calls.WifiStateCall


//for 2nd iteration
class WifiStateForDevice(private val apiInstance: ApiInstance, val evice: IotDevice) : ApiCaller() {


    override fun execute(cb: Callback) {
        super.execute(cb)
        apiInstance.executeCall(WifiStateCall(), cb)
    }
}