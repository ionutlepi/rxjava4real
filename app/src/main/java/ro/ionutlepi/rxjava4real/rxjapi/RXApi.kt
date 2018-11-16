package ro.ionutlepi.rxjava4real.rxjapi

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers
import ro.ionutlepi.rxjava4real.api.ApiInstance
import ro.ionutlepi.rxjava4real.api.CallResult
import ro.ionutlepi.rxjava4real.api.InvalidWifiState
import ro.ionutlepi.rxjava4real.api.IotDevice
import ro.ionutlepi.rxjava4real.api.WifiState
import ro.ionutlepi.rxjava4real.api.WifiStateForDevice
import ro.ionutlepi.rxjava4real.api.calls.ApiAuth
import ro.ionutlepi.rxjava4real.api.calls.ApiCaller
import ro.ionutlepi.rxjava4real.api.calls.SearchForDevice
import java.util.concurrent.TimeUnit

const val POLL_TIME = 5L

class RXApi(
    private val apiInstance: ApiInstance = ApiInstance()
) {
    private fun createSingleForCall(apiCaller: ApiCaller): Single<CallResult> = ApiCallSingle.create(apiCaller)

    fun auth(): Completable = createSingleForCall(ApiAuth(apiInstance)).ignoreElement()

    fun search(deviceRef: String): Single<IotDevice> = auth()
        .andThen(createSingleForCall(SearchForDevice(apiInstance, deviceRef)))
        .map { it.iotDevice }

    fun getWifiState(deviceRef: String): Single<WifiState> {
        return search(deviceRef)
            .flatMap { createSingleForCall(WifiStateForDevice(apiInstance, it)) }
            .compose(parseWifiState())
    }

    fun pollWifiState(deviceRef: String): Observable<WifiState> =  Observable.interval(
        POLL_TIME,
        TimeUnit.SECONDS,
        Schedulers.io()
    ).flatMap {
        getWifiState(deviceRef).toObservable()
    }

    fun parseWifiState(): SingleTransformer<CallResult, WifiState> {
        return SingleTransformer { singleWifi ->
            singleWifi.flatMap {
                val state: WifiState = when (it.data[0]) {
                    1.toByte() -> WifiState.CONNECTED
                    2.toByte() -> WifiState.DISCONNECTED
                    3.toByte() -> WifiState.NO_INTERNET
                    else -> null
                } ?: return@flatMap Single.error<WifiState>(
                    InvalidWifiState(
                        it.data[0]
                    )
                )
                return@flatMap Single.just(state)
            }
        }
    }
}