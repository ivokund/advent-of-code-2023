import java.io.File
import kotlin.math.abs

val testInput = """
O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#....
""".trimIndent().lines()

val realInput = File("day14/input.txt").readLines()
data class Coords(val x: Int, val y: Int) {
    fun distanceTo(other: Coords): Int {
        return abs(x - other.x) + abs(y - other.y)
    }
}

data class Diagram(val rocks: Map<Coords, Char>) {
    private val xMin = 0
    private val xMax = rocks.keys.maxOf { it.x }.toInt()
    private val yMin = 0
    private val yMax = rocks.keys.maxOf { it.y }.toInt()

    fun getRows(): List<String> {
        val out = mutableListOf<String>()
        for (y in yMin..yMax) {
            var line = ""
            for (x in xMin..xMax) {
                line += rocks[Coords(x, y)] ?: '.'
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
                line += rocks[Coords(x, y)] ?: '.'
            }
            out.add(line)
        }
        return out
    }

    fun getTiltedTop(): Diagram {
        var out = rocks

        doLoop@
        do {
            var moved = false

            for (y in yMin..yMax) {
                for ((coords, c) in out) {
                    if (coords.y != y || c == '#') continue
                    val newCoords = Coords(coords.x, coords.y - 1)

                    if (newCoords.y >= yMin && out[newCoords] == null) {
                        out = out.minus(coords) + (newCoords to c)
                        moved = true
                        continue@doLoop
                    }
                }
            }
        } while (moved)

        return Diagram(out)
    }

    fun getTotalLoad(): Int {
        return rocks.filter { it.value == 'O' }
            .map { yMax - it.key.y + 1 }
            .sum()
    }

    fun draw() {
        println(getRows().joinToString("\n"))
    }
}

fun parseInput(lines: List<String>) = Diagram(
    lines.foldIndexed(mutableMapOf()) { y, acc, line ->
        line.forEachIndexed { x, c ->
            if (c != '.') acc[Coords(x, y)] = c
        }
        acc
    }
)


fun part1(lines: List<String>): Int {

    val diagram = parseInput(lines).getTiltedTop()
    diagram.draw()
    return diagram.getTotalLoad()
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
 println(part1(realInput))
// println(part2(realInput))
