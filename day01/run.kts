import java.io.File

val testInput = """
1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet
""".trimIndent().lines()

val testInputPart2 = """
two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen
""".trimIndent().lines()

val realInput = File("day01/input.txt").readLines()

fun part1(lines: List<String>): Long {
    return lines.sumOf {
        val first = it.find { it.isDigit() }
        val last = it.findLast { it.isDigit() }
        "$first$last".toLong()
    }
}

fun part2(lines: List<String>): Long {
    val replaceMap = mapOf(
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9",
    )
    val validResults = replaceMap.keys + replaceMap.values

    return lines.map {
        val (_, first) = it.findAnyOf(validResults)!!
        val (_, last) = it.findLastAnyOf(validResults)!!

        val firstDigit = replaceMap[first] ?: first
        val lastDigit = replaceMap[last] ?: last

        "$firstDigit$lastDigit".toLong()
    }.sum()
}

println("--- test input")
println(part1(testInput))
println(part2(testInputPart2))

println("--- real input")
println(part1(realInput))
println(part2(realInput))
