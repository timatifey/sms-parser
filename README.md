# SMS-parser
* #### Author: [Timofey Pletnev](https://github.com/TiMaTiFeY)
* #### Date: 24.05.2021
## Задание
Необходимо разбить входную строку на смс-ки. То есть написать метод, который принимает строку и максимальную длину смс,
а возвращает массив строк сформированный по следующим правилам:
1) Разбивать текст можно только по пробелам;
2) Если получилось >1 смс-ки то нужно добавить ко всем суффикс вида _" 12/123"_, где 12 номер текущей смс 123 общее число смс;
3) Длина смс, включая суффикс, должна быть не больше переданного лимита;
4) Так как отправка смс платная, важно получить минимальное число смс.

Дополнительно гарантируется, что переданную строку можно по всем правилам разбить на смс-ки (например нет слов длиннее лимита и прочее)
## Предложенный алгоритм
*  Сравниваем входную строку с максимальной длиной SMS: если длина меньше максимальной, то вернем массив с одним элементом;
*  Найдем минимальную оценку возможного количества SMS:
`ceil((str.length / (smsLimit - 4)).toDouble()).toInt()`
(длина строки / (максимальная длина - 4)). Поскольку минимальная длина суффикса равна 4: " 1/5"
*  Найдем число SMS, при котором придется пересматривать изначальное разбиение из-за увеличения количества символов для записи общего числа SMS:
`10.0.pow(minEstimateOfSMSNum.toString().length).toInt()`
*  Проходим по строке и запоминаем индексы, по которым будем делать срез SMS-к. Изменяя значение индексов текущей SMS `startInd`, `endInd`. Изменяя максимальную длину текущей SMS в зависимости от номера SMS: `curLimit = getLimitGivenSuffix(smsNum, minEstimateOfSMSNum, smsLimit)`
*  На каждой итерации будем запоминать запас символов в `reserveOfCharsList`, для случая, если придется пересматривать SMS заново.
*  Если накопленное число SMS будет равно числу, когда общее число SMS будет на 1 символ длиннее: `if (smsNum == maxNumOfSMS)`
То итерируемся по всем SMS и уменьшаем запас символов на 1, если будет встречена SMS с нулевым запасом, то все последующие SMS будут забыты, поскольку произошел сдвиг. А у последней SMS будет удалено последнее слово.
```
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
```
* После всего имеем `smsIndices`, в котором хранятся пары индексов начала и конца соответствующих SMS. Сохраняем их в массив, предварительно добавив суффикс, и возвращаем результат:
```
val smsList = mutableListOf<String>()
smsIndices.forEachIndexed { index, smsInd ->
        smsList.add("${str.substring(smsInd.first, smsInd.second + 1)} ${index + 1}/${smsIndices.size}")
}

return smsList.toTypedArray()
```
## Тестирование
Были написаны Unit-тесты:
* `@Test fun singleSMS()` для проверки случая с единственным SMS;
* `@Test fun simpleSMSTest()` обычный тест;
* `@Test fun simpleSMSTestWithSpaces()` тест для строки, состоящей только из пробелов;
* `@Test fun advancedSMSTest()` тест для случайного текста длиной 10000 слов со случайным числом пробелов между ними с разной максимальной длиной SMS: проверяется, чтобы не было превышения максимальной длины, а также не было потери слов при разбиении.   
