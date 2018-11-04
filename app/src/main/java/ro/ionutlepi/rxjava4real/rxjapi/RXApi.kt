package ro.ionutlepi.rxjava4real.rxjapi

import io.reactivex.Completable
import io.reactivex.Single
import ro.ionutlepi.rxjava4real.ResourceManager
import ro.ionutlepi.rxjava4real.api.ApiInstance
import ro.ionutlepi.rxjava4real.api.CallResult
import ro.ionutlepi.rxjava4real.api.Callback
import ro.ionutlepi.rxjava4real.api.IotDevice
import ro.ionutlepi.rxjava4real.api.calls.ApiAuth
import ro.ionutlepi.rxjava4real.api.calls.ApiCaller
import ro.ionutlepi.rxjava4real.api.calls.SearchForDevice

class RXApi(
    private val apiInstance: ApiInstance = ApiInstance()
) {

    private fun createSingleForCall(apiCaller: ApiCaller): Single<CallResult> {
        return Single.create<CallResult> { emitter ->
            apiCaller.execute(object : Callback {
                override fun onCallFinished(result: CallResult) {
                    if (emitter.isDisposed) {
                        //nothing to do if disposed
                        return
                    }
                    if (result.successful) {
                        emitter.onSuccess(result)
                    } else {
                        emitter.onError(CallError())
                    }
                }
            })
        }
    }

    fun auth(): Completable = createSingleForCall(ApiAuth(apiInstance)).ignoreElement()

    fun search(deviceRef: String): Single<IotDevice> = auth()
        .andThen(createSingleForCall(SearchForDevice(apiInstance, deviceRef)))
        .map { it.iotDevice }
}