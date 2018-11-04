package ro.ionutlepi.rxjava4real

import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import ro.ionutlepi.rxjava4real.api.ApiInstance
import ro.ionutlepi.rxjava4real.api.CallResult
import ro.ionutlepi.rxjava4real.api.Callback
import ro.ionutlepi.rxjava4real.api.IotDevice
import ro.ionutlepi.rxjava4real.api.WifiState
import ro.ionutlepi.rxjava4real.api.WifiStateForDevice
import ro.ionutlepi.rxjava4real.api.calls.AuthCall
import ro.ionutlepi.rxjava4real.api.calls.SearchForDevice
import ro.ionutlepi.rxjava4real.api.calls.StartSearchCall
import ro.ionutlepi.rxjava4real.api.calls.WifiStateCall
import ro.ionutlepi.rxjava4real.rxjapi.RXApi

class TestWifiState {

    private val apimockk = mockk<ApiInstance>()
    private val rxApi = RXApi(apimockk)
    private val mockkDevice = mockk<IotDevice>()

    @Test
    fun testWifiGet() {
        val deviceRef = "TestDevice"
        val byte: Byte = 1
        val calResult = CallResult(true, mockkDevice, byteArrayOf(byte))

        every { apimockk.executeCall(ofType(AuthCall::class), any()) } answers {
            val cb = this.args[1] as Callback
            cb.onCallFinished(calResult)
        }

        every { apimockk.registerCaller(ofType(SearchForDevice::class)) } answers { nothing }

        every { apimockk.executeCall(ofType(StartSearchCall::class), any()) } answers {
            val cb = this.args[1] as Callback
            cb.onCallFinished(calResult)
        }

        every { apimockk.registerCaller(ofType(WifiStateForDevice::class)) } answers { nothing }

        every { apimockk.executeCall(ofType(WifiStateCall::class), any()) } answers {
            val cb = this.args[1] as Callback
            cb.onCallFinished(calResult)
        }

        val test = rxApi.getWifiState(deviceRef).test()
        test.assertValue(WifiState.CONNECTED)
            .await()

    }

}