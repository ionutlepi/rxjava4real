package ro.ionutlepi.rxjava4real.api.calls

import ro.ionutlepi.rxjava4real.api.ApiInstance
import ro.ionutlepi.rxjava4real.api.Callback

class ApiAuth(private val apiInstance: ApiInstance) : ApiCaller() {

    override fun execute(cb: Callback) {
        super.execute(cb)
        apiInstance.executeCall(AuthCall(), cb)
    }

}