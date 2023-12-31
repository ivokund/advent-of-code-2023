import java.io.File
import kotlin.math.abs
import kotlin.math.absoluteValue

val testInput = """
...........
.....###.#.
.###.##..#.
..#.#...#..
....#.#....
.##..S####.
.##..#...#.
.......##..
.##.#.####.
.##..##.##.
...........
""".trimIndent().lines()

val realInput = File("day21/input.txt").readLines()

data class Coords(val x: Int, val y: Int) {
    fun distanceTo(other: Coords): Int {
        return abs(x - other.x) + abs(y - other.y)
    }

    fun getAdjacencies(): List<Coords> {
        return listOf(
            Coords(x - 1, y),
            Coords(x + 1, y),
            Coords(x, y - 1),
            Coords(x, y + 1)
        )
    }

    fun isAdjacentTo(other: Coords): Boolean {
        val deltaX = (x - other.x).absoluteValue
        val deltaY = (y - other.y).absoluteValue
        return deltaX <= 1 && deltaY <= 1
    }
}

data class Diagram(val rocks: Set<Coords>, val positions: Set<Coords>, val xMax: Int, val yMax: Int) {

    val xMin = 0
    val yMin = 0
    fun fits (coord: Coords): Boolean {
        return coord.x in xMin..xMax && coord.y in yMin..yMax
    }

    fun hasRock(coord: Coords): Boolean {
        val localX = (coord.x % xMin)
        val localY = coord.y % yMin
        return rocks.contains(Coords(localX, localY))
    }


    fun getRows(): List<String> {
        val out = mutableListOf<String>()
        for (y in yMin..yMax) {
            var line = ""
            for (x in xMin..xMax) {
                line += getTileLabel(Coords(x, y))
            }
            out.add(line)
        }
        return out
    }

    fun getTileLabel(coord: Coords): Char {
        return if (rocks.contains(coord)) '#'
        else if (positions.contains(coord))
            'O' else '.'
    }

    fun getColumns(): List<String> {
        val out = mutableListOf<String>()
        for (x in xMin..xMax) {
            var line = ""
            for (y in yMin..yMax) {
                line += getTileLabel(Coords(x, y))
            }
            out.add(line)
        }
        return out
    }

    fun draw() {
        println(getRows().joinToString("\n"))
    }

    fun advance(infiniteMap: Boolean): Diagram {
        val newOpenPositions = positions.flatMap { it.getAdjacencies() }.toMutableSet()

        if (!infiniteMap) {
            newOpenPositions.removeIf { !fits(it)}
        }

        newOpenPositions.removeIf { rocks.contains(it) }

        return Diagram(rocks, newOpenPositions.toSet(), xMax, yMax)
    }
}

fun parseInput(lines: List<String>): Diagram {
    var start: Coords? = null
    val tiles = lines.mapIndexed { y, line ->
        line.foldIndexed(mutableSetOf<Coords>()) { x, acc, c ->
            if (c == '#') acc.add(Coords(x, y))
            if (c == 'S') start = Coords(x, y)
            acc
        }
    }.flatten().toSet()
    return Diagram(tiles, setOf(start!!), lines[0].length, lines.size)
}

fun part1(lines: List<String>, steps: Int): Int {
    var diagram = parseInput(lines)
    for (i in 1..steps) {
//        println("after $i iterations:")
        diagram = diagram.advance(false)
//        diagram.draw()
//        println("count: ${diagram.positions.size}")
//        println("")
    }

    return diagram.positions.size
}

fun part2(lines: List<String>, steps: Int): Int {
    var diagram = parseInput(lines)
    for (i in 1..steps) {
        println("after $i iterations:")
        diagram = diagram.advance(true)
//        diagram.draw()
        println("count: ${diagram.positions.size}")
        println("")
    }

    return diagram.positions.size
}

println("--- test input")
println(part1(testInput, 6)) // 16
// println(part2(testInput, 10))

println("--- real input")
println(part1(realInput, 64)) // 3751
// println(part2(realInput))
