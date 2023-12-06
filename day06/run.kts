
val testInput = """
Time:      7  15   30
Distance:  9  40  200
""".trimIndent().lines()

val realInput = """
Time:        46     82     84     79
Distance:   347   1522   1406   1471
""".trimIndent().lines()

data class Race(val time: Int, val distance: Int) {
    fun getWinningWaitTimes(): List<Int> {
        val winningWaitTimes = mutableListOf<Int>()
        for (ms in 0..time) {
            val distance = (time - ms) * ms
            if (distance > this.distance) {
                winningWaitTimes.add(ms)
            }
        }
        return winningWaitTimes
    }
}

fun parseRaces(lines: List<String>): List<Race> {
    fun parseLine(line: String) = Regex("\\d+")
        .findAll(line.split(":").last())
        .map { it.value.toInt() }
        .toList()

    val times = parseLine(lines[0])
    val distances = parseLine(lines[1])

    return times.mapIndexed { k, time -> Race(time, distances[k]) }
}

fun part1(lines: List<String>): Int {
    val races = parseRaces(lines)

    return races.map { it.getWinningWaitTimes().size }.reduce { acc, i -> acc * i }
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
println(part1(realInput))
// println(part2(realInput))
