import java.io.File

val testInput = """
???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1
""".trimIndent().lines()

val realInput = File("day12/input.txt").readLines()

data class Row(val springs: List<Char>, val counts: List<Int>)

fun getCombinationsForRow(row: Row): Int {

    // simply move cursor right until there's a valid position for a group that size
    fun moveRight(springs: List<Char>, groupSize: Int, startingPosition: Int): Int? {
        var pos = startingPosition
        while (pos + groupSize <= springs.size) {
            if (springs.subList(pos, pos + groupSize).all { it == '#' || it == '?' }) {
                return pos
            }
            pos++
        }
        return null
    }

    fun generateCombinations(
        parsedGroupStarts: List<Int>,
        remainingPositions: List<List<Int>>,
        groupSizes: List<Int>
    ): Set<List<Int>> {
        if (remainingPositions.isEmpty()) {
            return setOf(parsedGroupStarts)
        }
        val nextGroup = remainingPositions.first()

        val newCombinations = mutableSetOf<List<Int>>()
        nextGroup
            .filter { parsedGroupStarts.isEmpty() || it > parsedGroupStarts.last() + groupSizes[parsedGroupStarts.size - 1] }
            .forEach { pos ->
                newCombinations.addAll(
                    generateCombinations(parsedGroupStarts + pos, remainingPositions.drop(1), groupSizes)
                )
            }
        return newCombinations
    }

    val possiblePositions = row.counts.map {
        val positions = mutableListOf<Int>()
        var pos: Int? = 0
        do {
            pos = moveRight(row.springs, it, pos!!)
            if (pos != null) {
                positions.add(pos)
                pos++
            } else {
                break
            }
        } while (true)
        positions
    }

    val combinations = generateCombinations(emptyList(), possiblePositions, row.counts)

    // filter using regex and leave only valid patterns
    val validCombinations = combinations.filter { startPositions ->
        var springs = row.springs.joinToString("")
        startPositions.forEachIndexed { groupId, start ->
            springs = springs.replaceRange(start, start + row.counts[groupId], "#".repeat(row.counts[groupId]))
        }
        springs = springs.replace('?', '.')

        val matches = Regex("#+").findAll(springs).map { it.range.count() }.toList()
        matches == row.counts
    }

    return validCombinations.size
}


fun part1(lines: List<String>): Int {
    val rows = lines.map { line ->
        val (springs, counts) = line.split(" ")
        val springsGroups = springs
            .toList()
        Row(springsGroups, counts.split(",").map { it.toInt() })
    }

    return rows.sumOf { getCombinationsForRow(it) }
}


println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
println(part1(realInput))
// println(part2(realInput))
