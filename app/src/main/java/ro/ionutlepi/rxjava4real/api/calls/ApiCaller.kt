package ro.ionutlepi.rxjava4real.api.calls

import ro.ionutlepi.rxjava4real.api.Callback

open class ApiCaller {
    val resources = emptyArray<String>()
    lateinit var cb: Callback
    open fun execute(cb: Callback) {
        this.cb = cb
    }
    
    /**
     * Call this to terminate call execution or for clean up afterwards
     */
    open fun finish() {

    }
}
