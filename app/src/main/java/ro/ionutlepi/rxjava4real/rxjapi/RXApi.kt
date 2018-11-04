package ro.ionutlepi.rxjava4real.rxjapi

import io.reactivex.Completable
import io.reactivex.Single
import ro.ionutlepi.rxjava4real.api.ApiInstance
import ro.ionutlepi.rxjava4real.api.CallResult
import ro.ionutlepi.rxjava4real.api.Callback
import ro.ionutlepi.rxjava4real.api.InvalidWifiState
import ro.ionutlepi.rxjava4real.api.IotDevice
import ro.ionutlepi.rxjava4real.api.WifiState
import ro.ionutlepi.rxjava4real.api.WifiStateForDevice
import ro.ionutlepi.rxjava4real.api.calls.ApiAuth
import ro.ionutlepi.rxjava4real.api.calls.ApiCaller
import ro.ionutlepi.rxjava4real.api.calls.SearchForDevice

class RXApi(
    private val apiInstance: ApiInstance = ApiInstance()
) {
    private fun createSingleForCall(apiCaller: ApiCaller): Single<CallResult> {
        return Single.create<CallResult> { emmiter ->
            apiCaller.execute(object : Callback {
                override fun onCallFinished(result: CallResult) {
                    if (emmiter.isDisposed) {
                        //nothing to do if disposed
                        return
                    }
                    if (result.successful) {
                        emmiter.onSuccess(result)
                    } else {
                        emmiter.onError(CallError())
                    }
                }
            })
        }
    }

    fun auth(): Completable = createSingleForCall(ApiAuth(apiInstance)).ignoreElement()

    fun search(deviceRef: String): Single<IotDevice> = auth()
        .andThen(createSingleForCall(SearchForDevice(apiInstance, deviceRef)))
        .map { it.iotDevice }

    fun getWifiState(deviceRef: String): Single<WifiState> {
        return search(deviceRef)
            .flatMap {
                createSingleForCall(
                    WifiStateForDevice(
                        apiInstance,
                        it
                    )
                )
            }.flatMap { wifiByte ->
                val state: WifiState = when (wifiByte.data[0]) {
                    1.toByte() -> WifiState.CONNECTED
                    2.toByte() -> WifiState.DISCONNECTED
                    3.toByte() -> WifiState.NO_INTERNET
                    else -> null
                } ?: return@flatMap Single.error<WifiState>(
                    InvalidWifiState(
                        wifiByte.data[0]
                    )
                )
                return@flatMap Single.just(state)
            }
    }
}