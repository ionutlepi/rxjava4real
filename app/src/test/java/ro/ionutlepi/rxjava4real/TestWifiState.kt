package ro.ionutlepi.rxjava4real

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.reactivex.Single
import org.junit.Test
import ro.ionutlepi.rxjava4real.api.ApiInstance
import ro.ionutlepi.rxjava4real.api.CallResult
import ro.ionutlepi.rxjava4real.api.Callback
import ro.ionutlepi.rxjava4real.api.IotDevice
import ro.ionutlepi.rxjava4real.api.WifiState
import ro.ionutlepi.rxjava4real.api.WifiStateForDevice
import ro.ionutlepi.rxjava4real.api.calls.WifiStateCall
import ro.ionutlepi.rxjava4real.rxjapi.RXApi

class TestWifiState {

    private val apimockk = mockk<ApiInstance>()
    private val rxApi = RXApi(apimockk)
    private val mockkDevice = mockk<IotDevice>()

    @Test
    fun testWifiGet() {
        val deviceRef = "TestDevice"
        val spy = spyk(rxApi)
        every { spy.search(deviceRef) } returns Single.just(mockkDevice)

        val byte: Byte = 1
        val calResult = CallResult(true, mockkDevice, byteArrayOf(byte))

        every { apimockk.registerCaller(any<WifiStateForDevice>()) } answers { nothing }

        every { apimockk.executeCall(any<WifiStateCall>(), any()) } answers {
            val cb = this.args[1] as Callback
            cb.onCallFinished(calResult)
        }

        val test = spy.getWifiState(deviceRef).test()
        test.assertValue(WifiState.CONNECTED)
            .await()

    }

}