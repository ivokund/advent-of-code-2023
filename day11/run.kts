import java.io.File
import kotlin.math.abs
import kotlin.system.exitProcess


val testInput = """
...#......
.......#..
#.........
..........
......#...
.#........
.........#
..........
.......#..
#...#.....
""".trimIndent().lines()

val realInput = File("day11/input.txt").readLines()

data class Coords(val x: Int, val y: Int) {
    fun distanceTo(other: Coords): Int {
        return abs(x - other.x) + abs(y - other.y)
    }
}

data class Diagram(val map: Set<Coords>) {
    val xMin = 0
    val xMax = map.maxOf { it.x }
    val yMin = 0
    val yMax = map.maxOf { it.y }

    fun draw() {
        val out: MutableList<String> = mutableListOf()
        for (y in yMin..yMax) {
            var line = ""
            for (x in xMin..xMax) {
                val tile = if (map.contains(Coords(x, y))) "#" else "."
                line += tile
            }
            out.add(line)
        }
        println(out.joinToString("\n"))
    }

    fun getExpanded(): Diagram {
        val galaxyCoords = map
        val setX = galaxyCoords.map { it.x }.toSet()
        val setY = galaxyCoords.map { it.y }.toSet()

        val expandY = (yMin..yMax).filterNot { setY.contains(it) }
        val expandX = (xMin..xMax).filterNot { setX.contains(it) }

        var newGalaxies = galaxyCoords

        for (x in expandX.reversed()) {
            newGalaxies = newGalaxies.map {
                Coords(if (it.x > x) it.x + 1 else it.x, it.y)
            }.toSet()

        }
        for (y in expandY.reversed()) {
            newGalaxies = newGalaxies.map {
                Coords(it.x, if (it.y > y) it.y + 1 else it.y)
            }.toSet()
        }

        return Diagram(newGalaxies)
    }
}

fun parseInput(lines: List<String>) = Diagram(
    lines.flatMapIndexed { y, line ->
        line.foldIndexed(mutableSetOf<Coords>()) { x, acc, c ->
            if (c == '#') acc.add(Coords(x, y))
            acc
        }
    }.toSet()
)

fun part1(lines: List<String>): Int {
    val diagram = parseInput(lines).getExpanded()

    diagram.draw()
    val distanceMap = mutableMapOf<Pair<Coords, Coords>, Int>()

    diagram.map.forEach {
        diagram.map.forEach { other ->
            if (!(it.x == other.x && it.y == other.y)
                && !distanceMap.contains(Pair(it, other))
                && !distanceMap.contains(Pair(other, it))
            ) {
                distanceMap[it to other] = it.distanceTo(other)
            }

        }
    }
    return distanceMap.values.sum()
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
 println(part1(realInput))
// println(part2(realInput))
