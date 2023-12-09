import java.io.File

val testInput = """
0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45
""".trimIndent().lines()

val realInput = File("day09/input.txt").readLines()

fun findNewValues (history: List<Int>): Pair<Int, Int> {

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
        val intToAddEnd = (if (index == 0) 0 else ints.last() + newSequences[index - 1].last())
        val intToAddStart = (if (index == 0) 0 else ints.first() - newSequences[index - 1].first())
        newSequences.add(listOf(intToAddStart) + ints + intToAddEnd)
    }

    return Pair(newSequences.last().first(), newSequences.last().last())
}

fun solve(lines: List<String>): Pair<Int, Int> {
    val histories = lines.map { it.split(" ").map { it.toInt() } }
    val newValues = histories.map { findNewValues(it) }
    return Pair(newValues.sumOf { it.first }, newValues.sumOf { it.second })
}

println("--- test input")
println(solve(testInput))

println("--- real input")
println(solve(realInput))
