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

    // left, right
    var additions = Pair(0, 0)
    sequences.reversed().forEachIndexed { index, ints ->
        additions = Pair(
            if (index == 0) 0 else ints.last() + additions.second,
            if (index == 0) 0 else ints.first() - additions.first,
        )
    }
    return additions
}

fun solve(lines: List<String>): Pair<Int, Int> {
    val histories = lines.map { it.split(" ").map { it.toInt() } }
    val newValues = histories.map { findNewValues(it) }
    return Pair(newValues.sumOf { it.first }, newValues.sumOf { it.second })
}

println("--- test input")
solve(testInput).let { (part2, part1) -> println("Part1: $part1 \nPart2: $part2") }

println("--- real input")
solve(realInput).let { (part2, part1) -> println("Part1: $part1 \nPart2: $part2") }
