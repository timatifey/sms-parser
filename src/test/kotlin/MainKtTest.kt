import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.streams.asSequence
import kotlin.test.assertEquals

class MainKtTest {

    @Test
    fun singleSMS() {
        val parsedSMS = parseStringIntoSMSArray("My name is Timofey. I love android development.", 100)
        assertArrayEquals(arrayOf("My name is Timofey. I love android development."), parsedSMS)
    }

    @Test
    fun simpleSMSTest() {
        val parsedSMS = parseStringIntoSMSArray("My name is Timofey. I love android development.", 20)
        assertArrayEquals(
            arrayOf("My name is 1/4", "Timofey. I love 2/4", "android 3/4", "development. 4/4"),
            parsedSMS
        )
    }

    @Test
    fun simpleSMSTestWithSpaces() {
        val parsedSMS = parseStringIntoSMSArray("                              ", 10)
        assertArrayEquals(arrayOf("       1/5", "       2/5", "       3/5", "       4/5", "   5/5"), parsedSMS)
    }

    @Test
    fun advancedSMSTest() {
        val sb = StringBuilder()
        for (i in 0 until 10_000) {
            val randomLen = Random.nextLong(3, 10)
            sb.append(randomWord(randomLen))
            val randomLenSpaces = Random.nextInt(1, 5)
            for (j in 0 until randomLenSpaces) sb.append(' ')
        }
        val text = sb.toString().trim()
        for (smsLimit in 20..100 step 10) {
            val parsedSms = parseStringIntoSMSArray(text, smsLimit)
            val sbForParsed = StringBuilder()
            for (sms in parsedSms) {
                assert(sms.length <= smsLimit)
                var j = sms.length - 1
                while (sms[j] != ' ') j--
                sbForParsed.append(sms.substring(0, j + 1))
            }
            assertEquals(text, sbForParsed.toString().trim())
        }
    }

    private fun randomWord(wordLength: Long): String {
        val source = "abcdefghijklmnopqrstuvwxyz"
        return java.util.Random().ints(wordLength, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
    }
}