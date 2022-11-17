import com.afarasadi.location_kmm.Greeting
import kotlin.test.Test
import kotlin.test.assertTrue

class GreetingTest {

    private val SUT = Greeting()

    @Test
    fun whenGetGreeting_IsNotEmpty() {
        val greeting = SUT.greeting()
        println(greeting)
        assertTrue(greeting.isNotEmpty())
    }
}
