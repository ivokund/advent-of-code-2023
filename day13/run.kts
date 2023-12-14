import java.io.File
import kotlin.math.abs

val testInput = """
#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.

#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#
""".trimIndent()

val realInput = File("day13/input.txt").readText()

data class Coords(val x: Int, val y: Int) {
    fun distanceTo(other: Coords): Int {
        return abs(x - other.x) + abs(y - other.y)
    }
}

enum class ReflectionType {
    Horizontal,
    Vertical,
}

data class ReflectionLine(val type: ReflectionType, val position: Int)

fun List<ReflectionLine>.score(): Int {
    return this.sumOf {
        it.position * (if (it.type == ReflectionType.Horizontal) 100 else 1)
    }
}

fun isMirroring(rows: List<String>, x: Int): Boolean {
    if (rows[x] == rows[x + 1]) {
        val newRows = rows.filterIndexed { index, _ -> index !in x..x + 1 }

        if (x < 1 || x > newRows.size - 1) {
            return true
        }
        return isMirroring(newRows, x - 1)
    }
    return false
}

data class Diagram(val rocks: Set<Coords>) {
    private val xMin = 0
    private val xMax = rocks.maxOf { it.x }.toInt()
    private val yMin = 0
    private val yMax = rocks.maxOf { it.y }.toInt()

    fun getReflectionLine(not: ReflectionLine? = null): ReflectionLine? {
        return (emptyList<ReflectionLine>() +
                getLinesLeftToReflection().map { ReflectionLine(ReflectionType.Vertical, it) } +
                getLinesAboveReflection().map { ReflectionLine(ReflectionType.Horizontal, it) })
            .firstOrNull { it != not }
    }

    fun getLinesLeftToReflection(): List<Int> {
        val columns = getColumns()
        return (0 until xMax).filter { isMirroring(columns, it) }.map { it + 1 }
    }

    fun getLinesAboveReflection(): List<Int> {
        val rows = getRows()
        return (0 until yMax).filter { isMirroring(rows, it) }.map { it + 1 }
    }

    fun fixSmudge(): ReflectionLine {
        val currentLine = getReflectionLine()!!
        for (y in yMin..yMax) {
            for (x in xMin..xMax) {
                val coords = Coords(x, y)
                val newDiagram = Diagram(
                    if (rocks.contains(coords)) rocks.minusElement(coords) else rocks.plusElement(coords)
                )
                newDiagram.getReflectionLine(currentLine)?.let {
                    return it
                }
            }
        }
        throw Error("Did not find a smudge")
    }

    fun getRows(): List<String> {
        val out = mutableListOf<String>()
        for (y in yMin..yMax) {
            var line = ""
            for (x in xMin..xMax) {
                val tile = if (rocks.contains(Coords(x, y))) "#" else "."
                line += tile
            }
            out.add(line)
        }
        return out
    }

    fun getColumns(): List<String> {
        val out = mutableListOf<String>()
        for (x in xMin..xMax) {
            var line = ""
            for (y in yMin..yMax) {
                val tile = if (rocks.contains(Coords(x, y))) "#" else "."
                line += tile
            }
            out.add(line)
        }
        return out
    }

    fun draw() {
        println(getRows().joinToString("\n"))
    }
}

fun parseInput(lines: List<String>) = Diagram(
    lines.mapIndexed { y, line ->
        line.foldIndexed(mutableSetOf<Coords>()) { x, acc, c ->
            if (c == '#') acc.add(Coords(x, y))
            acc
        }
    }.flatten().toSet()
)

fun part1(lines: String): Int {
    val diagrams = lines.split("\n\n").map { parseInput(it.lines()) }
    return diagrams.map { it.getReflectionLine()!! }.score()
}

fun part2(lines: String): Int {
    val diagrams = lines.split("\n\n").map { parseInput(it.lines()) }
    val smudgeFixedLines = diagrams.map { it.fixSmudge() }
    return smudgeFixedLines.score()

}

println("--- test input")
println(part1(testInput))
println(part2(testInput))

println("--- real input")
println(part1(realInput))
println(part2(realInput))
