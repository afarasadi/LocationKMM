import com.afarasadi.location_kmm.KmmLocationProvider
import com.afarasadi.location_kmm.LocationProviderContract
import com.afarasadi.location_kmm.model.Location
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue


private class FakeLocationProvider : LocationProviderContract {
    private val location = MutableSharedFlow<Location?>()
    override fun getLocation(): Flow<Location?> = location
    suspend fun emit(location: Location?) = this.location.emit(location)
}

@OptIn(ExperimentalCoroutinesApi::class)
class KmmLocationProviderTest {
    private lateinit var SUT : KmmLocationProvider

    @BeforeTest
    fun setup() {
        SUT = KmmLocationProvider()
    }

    @Test
    fun givenFakeLocationProvider_whenEmitLocation_locationIsNotEmpty() = runTest(UnconfinedTestDispatcher()) {
        // given
        val fakeLocationProvider = FakeLocationProvider()
        SUT = KmmLocationProvider(fakeLocationProvider)

        // when
        val locationValues = mutableListOf<Location?>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            SUT.getLocation().toList(locationValues)
        }
        val location1 = Location(0.0, 0.0)
        val location2 = Location(1.0, 2.0)

        fakeLocationProvider.emit(location1)
        fakeLocationProvider.emit(location2)

        // then
        assertTrue { locationValues[1] == location2 }
        assertTrue { locationValues[0] == location1 }

        collectJob.cancel()
    }
}
