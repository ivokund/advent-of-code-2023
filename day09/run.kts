import java.io.File

val testInput = """
0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45
""".trimIndent().lines()

val realInput = File("day09/input.txt").readLines()

fun findNextValue (history: List<Int>): Int {

    val sequences = mutableListOf(history)
    var lastSequence = history

    do {
        lastSequence = (lastSequence.mapIndexed { index, value ->
            if (index == 0) null else value - lastSequence[index - 1]
        }.filterNotNull())
        sequences.add(lastSequence)

    } while (lastSequence.any { it != 0 })


    sequences.reverse()
    val newSequences = mutableListOf<List<Int>>()

    sequences.forEachIndexed { index, ints ->
        val intToAdd = (if (index == 0) 0 else ints.last() + newSequences[index - 1].last())
        newSequences.add(ints + intToAdd)
    }

    return newSequences.last().last()
}

fun part1(lines: List<String>): Int {
    val histories = lines.map { it.split(" ").map { it.toInt() } }
    val newValues = histories.map { findNextValue(it) }
    return newValues.sum()
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
 println(part1(realInput))
// println(part2(realInput))
