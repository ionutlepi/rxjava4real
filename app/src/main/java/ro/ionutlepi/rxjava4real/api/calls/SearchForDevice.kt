package ro.ionutlepi.rxjava4real.api.calls

import ro.ionutlepi.rxjava4real.api.ApiInstance
import ro.ionutlepi.rxjava4real.api.CallResult
import ro.ionutlepi.rxjava4real.api.Callback
import ro.ionutlepi.rxjava4real.api.IotDevice

class SearchForDevice(private val apiInstance: ApiInstance, private val iotDeviceRef: String) :
    ApiCaller() {

    override fun execute(cb: Callback) {
        super.execute(cb)
        val transaction = StartSearchCall(iotDeviceRef)
        apiInstance.registerCaller(this)
        apiInstance.executeCall(transaction, object : Callback {
            override fun onCallFinished(result: CallResult) {
                if (!result.successful) {
                    cb.onCallFinished(result)
                }
                //otherwise we wait, we know that the scann just started
            }
        })
    }

    override fun onDeviceFound(device: IotDevice) {
        super.onDeviceFound(device)
        cb.onCallFinished(CallResult(true, device, byteArrayOf()))
    }
}