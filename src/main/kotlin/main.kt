import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.pow

/**
Необходимо разбить входную строку на смс-ки
То есть написать метод, который принимает строку и максимальную длину смс,
а возвращает массив строк сформированный по следующим правилам:

1) разбивать текст можно только по пробелам
2) если получилось >1 смс-ки то нужно добавить ко всем суффикс вида " 12/123"
где 12 номер текущей смс 123 общее число смс
3) длина смс включая суффикс должна быть не больше переданного лимита
4) так как отправка смс платная важно получить минимальное число смс

дополнительно гарантируется что переданную строку можно по всем правилам разбить на смс-ки
(например нет слов длиннее лимита и прочее)
 */

/**
 * @author: Timofey Pletnev
 */
fun main() {
    val text = """
        Seen head favourite numerous position at household indulged knew.
        Of charmed said with made prepare dine week advanced. Prosperous addition admitting own confined inquiry poor. Improve world pleasure feeling exquisite equal sold manor moment. Resources see confined hunted overcame addition tore savings ladies away inquietude. Entreaties applauded enquire has doubt sang polite differed extent mind concern. 
        Enough garrets him deal giving right rendered room provided civilly equal sex law improving forming. Assurance merry ladies begin meant turned saw deficient girl married seemed cousin screened bed smart hill desirous. Brother like whom. Rapturous believe entered mirth furniture numerous new viewing weeks all throwing mistress season several. Though engaged delay too solid feel home discourse otherwise. 
        Noise weddings daughter dispatched one nay merely smallest. Had required noise performed.
    """.trimIndent()
    parseStringIntoSMSArray(text, 50).forEach { println(it) }
}

/**
 * @param str: входная строка для разбиения на SMS
 * @param smsLimit: максимальная длина SMS
 */
fun parseStringIntoSMSArray(str: String, smsLimit: Int): Array<String> {
    if (str.length <= smsLimit) {
        return arrayOf(str)
    }

    var smsIndices = mutableListOf<Pair<Int, Int>>()
    var minEstimateOfSMSNum = ceil((str.length / (smsLimit - 4)).toDouble()).toInt()
    var maxNumOfSMS = 10.0.pow(minEstimateOfSMSNum.toString().length).toInt()
    var reserveOfCharsList = mutableListOf<Int>()

    var startInd: Int
    var endInd = -2
    var smsNum = 0
    var curLimit: Int

    mainLoop@ do {
        smsNum++
        if (smsNum == maxNumOfSMS) {
            minEstimateOfSMSNum = maxNumOfSMS
            maxNumOfSMS *= 10
            for (i in 0 until reserveOfCharsList.size) {
                if (reserveOfCharsList[i] == 0) {
                    var j = smsIndices[i].second
                    var lenOfLastWord = 0
                    while (str[j--] != ' ') lenOfLastWord++

                    startInd = smsIndices[i].first
                    endInd = smsIndices[i].second - lenOfLastWord - 1

                    smsIndices = smsIndices.subList(0, i)
                    smsIndices.add(startInd to endInd)

                    reserveOfCharsList = reserveOfCharsList.subList(0, i)
                    smsNum = i + 1
                    curLimit = getLimitGivenSuffix(smsNum, minEstimateOfSMSNum, smsLimit)
                    reserveOfCharsList.add(curLimit - (endInd - startInd + 1))

                    continue@mainLoop
                } else {
                    reserveOfCharsList[i] = reserveOfCharsList[i] - 1
                }
            }
        }

        startInd = min(endInd + 2, str.length - 1)
        curLimit = getLimitGivenSuffix(smsNum, minEstimateOfSMSNum, smsLimit)
        endInd += curLimit + 1
        endInd = min(endInd, str.length - 1)
        if (endInd < str.length - 1) {
            while (str[endInd + 1] != ' ') endInd--
        }
        smsIndices.add(startInd to endInd)

        reserveOfCharsList.add(curLimit - (endInd - startInd + 1))
    } while (endInd < str.length - 1)

    val smsList = mutableListOf<String>()
    smsIndices.forEachIndexed { index, smsInd ->
        smsList.add("${str.substring(smsInd.first, smsInd.second + 1)} ${index + 1}/${smsIndices.size}")
    }

    return smsList.toTypedArray()
}

private fun getSuffixLength(numOfSms: Int, smsNum: Int): Int =
    numOfSms.toString().length + smsNum.toString().length + 2

private fun getLimitGivenSuffix(numOfSms: Int, smsNum: Int, smsLimit: Int) =
    smsLimit - getSuffixLength(numOfSms, smsNum)