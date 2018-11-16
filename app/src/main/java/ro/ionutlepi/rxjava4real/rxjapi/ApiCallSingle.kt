package ro.ionutlepi.rxjava4real.rxjapi

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.plugins.RxJavaPlugins
import ro.ionutlepi.rxjava4real.api.CallResult
import ro.ionutlepi.rxjava4real.api.Callback
import ro.ionutlepi.rxjava4real.api.calls.ApiCaller

class ApiCallSingle private constructor(private val apiCaller: ApiCaller): Single<CallResult>() {

    companion object {
        fun create(apiCaller: ApiCaller): ApiCallSingle = ApiCallSingle(apiCaller)
    }

    override fun subscribeActual(observer: SingleObserver<in CallResult>) {
        val disposable = CallDisposable(apiCaller)
        observer.onSubscribe(disposable)
       try {
           apiCaller.execute(object : Callback {
               override fun onCallFinished(result: CallResult) {
                   if (result.successful) {
                       if (disposable.isDisposed) {
                           observer.onSuccess(result)
                       }
                   } else {
                       if (disposable.isDisposed) {
                           observer.onError(CallError())
                       }
                   }
               }
           })
       } catch (t: Throwable) {
           Exceptions.throwIfFatal(t)
           if(!disposable.isDisposed) {
               try {
                   observer.onError(t)
               } catch (inner: Throwable) {
                   Exceptions.throwIfFatal(inner)
                   RxJavaPlugins.onError(CompositeException(t, inner))
               }
           }
       }
    }


    private class CallDisposable internal constructor(private val call: ApiCaller) : Disposable {
        @Volatile
        private var disposed: Boolean = false

        override fun dispose() {
            disposed = true
            call.finish()
        }

        override fun isDisposed(): Boolean {
            return disposed
        }
    }

}