import java.io.File
import kotlin.math.abs

val testInput = """
...#...####...#..
.....##.##.##....
##....######....#
..#.##.#..#.##...
##.###.####.###.#
..###...##...###.
#####.##..##.####
#######....######
###...#.##.#...##
....###.##.###...
##.####.##.####.#
..###...##...###.
##.#.##....##.#.#
##..#.#....#.#..#
##.###.#..#.###.#
###.#...##...#.##
..####.####.####.
""".trimIndent()

val realInput = File("day13/input.txt").readText()

data class Coords(val x: Int, val y: Int) {
    fun distanceTo(other: Coords): Int {
        return abs(x - other.x) + abs(y - other.y)
    }
}

data class Diagram(val rocks: Set<Coords>) {
    private val xMin = 0
    private val xMax = rocks.maxOf { it.x }.toInt()
    private val yMin = 0
    private val yMax = rocks.maxOf { it.y }.toInt()

    fun getLinesLeftToReflection(): Int? {
        val columns = getColumns()

        fun colsAreMirroring(cols: List<String>, x: Int): Boolean {
//            println("checking $x, size: ${cols.size}")
            if (cols[x] == cols[x + 1]) {
//                println(" .. match at $x: ${cols[x]} matches ${cols[x + 1]}")
                val newCols = cols
                    .filterIndexed { index, _ -> index !in x..x+1 }

                if (x < 1 || x > newCols.size - 1) {
                    return true
                }
                return colsAreMirroring(newCols, x - 1)
            }
            return false
        }

        for (x in 0..<xMax) {
            if (colsAreMirroring(columns, x)) {
                return x + 1
            }
        }
        return null
    }
    fun getLinesAboveReflection(): Int? {
        val rows = getRows()

        fun rowsAreMirroring(rows: List<String>, y: Int): Boolean {
//            println("checking $y, size: ${rows.size}")
            if (rows[y] == rows[y + 1]) {
                val newRows = rows
                    .filterIndexed { index, _ -> index !in y..y+1 }

                if (y < 1 || y > newRows.size - 1) {
                    return true
                }
                return rowsAreMirroring(newRows, y - 1)
            }
            return false
        }

        for (y in 0..<yMax) {
            if (rowsAreMirroring(rows, y)) {
                return y + 1
            }
        }
        return null
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

    val results = diagrams.mapIndexed { index, diagram ->
//        println("diagram $index")

        val cols = diagram.getLinesLeftToReflection()
        val rows = diagram.getLinesAboveReflection()

        Pair(cols, rows)
    }

    results.forEach { println(it) }

    return results.sumOf { it.first ?: 0 } + 100 * results.sumOf { it.second ?: 0 }
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
 println(part1(realInput))
// println(part2(realInput))
// 42361