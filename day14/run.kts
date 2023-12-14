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

fun replacePattern(input: String, regex: Regex, replacement: String): String {
    return if (!regex.containsMatchIn(input)) {
        input
    } else {
        replacePattern(input.replace(regex, replacement), regex, replacement)
    }
}


fun collapseLine(line: String, toStart: Boolean): String {
    fun collapseSegment(segment: String): String {
        val dots = ".".repeat(segment.count { it == '.' })
        val rocks = "O".repeat(segment.count { it == 'O' })
        return if (toStart) rocks + dots else dots + rocks
    }

    val newLine = line.split("#").joinToString("#") {
        collapseSegment(it)
    }
    return newLine
}

fun fromRows(lines: List<String>) = Diagram(
    lines.foldIndexed(mutableMapOf()) { y, acc, line ->
        line.forEachIndexed { x, c -> if (c != '.') acc[Coords(x, y)] = c }
        acc
    }
)

fun fromColumns(cols: List<String>) = Diagram(
    cols.foldIndexed(mutableMapOf()) { x, acc, col ->
        col.forEachIndexed { y, c -> if (c != '.') acc[Coords(x, y)] = c }
        acc
    }
)


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

    fun getTiltedTop() = fromColumns(getColumns().map { collapseLine(it, true) })
    fun getTiltedLeft() = fromRows(getRows().map { collapseLine(it, true) })
    fun getTiltedBottom() = fromColumns(getColumns().map { collapseLine(it, false) })
    fun getTiltedRight() = fromRows(getRows().map { collapseLine(it, false) })

    fun getTiltedOneRound(): Diagram {
        return this.getTiltedTop()
            .getTiltedLeft()
            .getTiltedBottom()
            .getTiltedRight()
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

fun part1(lines: List<String>): Int {
    val diagram = fromRows(lines).getTiltedTop()
//    diagram.draw()
    return diagram.getTotalLoad()
}

fun part2(lines: List<String>): Int {
    var diagram = fromRows(lines)

    val startRounds = 200
    val cycleLength = 42
    val totalRuns = 1000000000 - startRounds
    val neededRuns = startRounds + totalRuns % cycleLength

    repeat(neededRuns) { diagram = diagram.getTiltedOneRound() }
    return diagram.getTotalLoad()
}

println("--- test input")
println(part1(testInput))
println(part2(testInput))

println("--- real input")
println(part1(realInput))
println(part2(realInput))
