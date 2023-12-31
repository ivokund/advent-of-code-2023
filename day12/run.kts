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
    ): List<List<Int>> {
        if (remainingPositions.isEmpty()) {
            return listOf(parsedGroupStarts)
        }
        val nextGroup = remainingPositions.first()
//        println(parsedGroupStarts)
        return nextGroup
            .filter {
                parsedGroupStarts.isEmpty()
                        || it > parsedGroupStarts.last() + groupSizes[parsedGroupStarts.size - 1]
            }
            .flatMap { pos ->
                generateCombinations(parsedGroupStarts + pos, remainingPositions.drop(1), groupSizes)
            }
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

//    println("combinations pre")
    val combinations = generateCombinations(emptyList(), possiblePositions, row.counts)

//    println("combinations done")
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

fun part2(lines: List<String>): Int {
    val rows = lines.map { line ->
        val (springs, counts) = line.split(" ")
        val springsGroups = List(5) { springs }.joinToString("?").toList()
        val countsMultiplied = List(5) { counts }.joinToString(",")
        Row(springsGroups, countsMultiplied.split(",").map { it.toInt() })
    }
    return rows.sumOf {
        val sum = getCombinationsForRow(it)
        println("sum: $sum")
        sum
    }
}


println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
println(part1(realInput))
// println(part2(realInput))
