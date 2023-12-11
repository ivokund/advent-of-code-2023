import java.io.File
import kotlin.math.abs

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

data class Coords(val x: Long, val y: Long) {
    fun distanceTo(other: Coords): Long {
        return abs(x - other.x) + abs(y - other.y)
    }
}

data class Diagram(val galaxies: Set<Coords>) {
    private val xMin = 0
    private val xMax = galaxies.maxOf { it.x }
    private val yMin = 0
    private val yMax = galaxies.maxOf { it.y }

    fun getDistances(): Map<Pair<Coords, Coords>, Long> {
        val distanceMap = mutableMapOf<Pair<Coords, Coords>, Long>()
        galaxies.forEach {
            galaxies.forEach { other ->
                if (!(it.x == other.x && it.y == other.y)
                    && !distanceMap.contains(Pair(it, other))
                    && !distanceMap.contains(Pair(other, it))
                ) {
                    distanceMap[it to other] = it.distanceTo(other)
                }
            }
        }
        return distanceMap.toMap()
    }

    fun draw() {
        val out: MutableList<String> = mutableListOf()
        for (y in yMin..yMax) {
            var line = ""
            for (x in xMin..xMax) {
                val tile = if (galaxies.contains(Coords(x, y))) "#" else "."
                line += tile
            }
            out.add(line)
        }
        println(out.joinToString("\n"))
    }

    fun getExpanded(times: Int): Diagram {
        val expandY = (yMin..yMax).filter { y -> galaxies.none { it.y == y } }
        val expandX = (xMin..xMax).filter { x -> galaxies.none { it.x == x } }

        var newGalaxies = galaxies

        for (x in expandX.reversed()) {
            newGalaxies = newGalaxies.map {
                Coords(if (it.x > x) it.x + times - 1 else it.x, it.y)
            }.toSet()
        }
        for (y in expandY.reversed()) {
            newGalaxies = newGalaxies.map {
                Coords(it.x, if (it.y > y) it.y + times - 1 else it.y)
            }.toSet()
        }

        return Diagram(newGalaxies)
    }
}

fun parseInput(lines: List<String>) = Diagram(
    lines.mapIndexed { y, line ->
        line.foldIndexed(mutableSetOf<Coords>()) { x, acc, c ->
            if (c == '#') acc.add(Coords(x.toLong(), y.toLong()))
            acc
        }
    }.flatten().toSet()
)

fun part1(lines: List<String>): Long {
    val diagram = parseInput(lines).getExpanded(2)
    return diagram.getDistances().values.sum()
}

fun part2(lines: List<String>): Long {
    val diagram = parseInput(lines).getExpanded(1_000_000)
    return diagram.getDistances().values.sum()
}



println("--- test input")
println(part1(testInput))
println(part2(testInput))

println("--- real input")
println(part1(realInput))
println(part2(realInput))
