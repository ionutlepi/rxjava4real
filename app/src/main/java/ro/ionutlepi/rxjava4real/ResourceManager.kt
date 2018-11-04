package ro.ionutlepi.rxjava4real

import io.reactivex.Completable
import ro.ionutlepi.rxjava4real.api.calls.ApiCaller
import java.util.concurrent.TimeUnit

class ResourceManager {
    fun reserve(resources: Array<String>) {

    }

    fun waitForResource(apiCaller: ApiCaller) = Completable.complete().delay(5,
        TimeUnit.SECONDS
    )

    fun release(resources: Array<String>) {

    }
}