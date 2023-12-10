import java.io.File
import kotlin.math.absoluteValue

val testInput = """
..F7.
.FJ|.
SJ.L7
|F--J
LJ...
""".trimIndent().lines()

val realInput = File("day10/input.txt").readLines()

data class Coords(val x: Int, val y: Int) {}

fun getDeltasByTile(tile: Char): List<Coords> = when (tile) {
    '|' -> listOf(Coords(0, -1), Coords(0, 1))
    '-' -> listOf(Coords(1, 0), Coords(-1, 0))
    'L' -> listOf(Coords(0, -1), Coords(1, 0))
    'J' -> listOf(Coords(0, -1), Coords(-1, 0))
    '7' -> listOf(Coords(0, 1), Coords(-1, 0))
    'F' -> listOf(Coords(1, 0), Coords(0, 1))
    'S' -> listOf(Coords(1, 0), Coords(0, 1), Coords(0, -1), Coords(-1, 0))
    else -> throw Error("No direction for $tile")
}

data class Diagram(val map: Map<Coords, Char>) {
    fun pointsToCoords(coord: Coords): List<Coords> {
        val deltas = getDeltasByTile(map[coord]!!)

        return deltas.map { Coords(coord.x + it.x, coord.y + it.y) }.filter { map[it] != null && map[it] != '.' }
            .filter {
                val possibleDeltasFromTarget =
                    getDeltasByTile(map[it]!!).map { target -> Coords(target.x + it.x, target.y + it.y) }
                possibleDeltasFromTarget.contains(coord)
            }
    }
}

fun parseInput(lines: List<String>) = Diagram(lines.flatMapIndexed { y, line ->
    line.mapIndexed { x, value -> Coords(x, y) to value }
}.toMap())

fun part1(lines: List<String>): Int {
    val diagram = parseInput(lines)

    val startCoords = diagram.map.filterValues { it == 'S' }.keys.first()

    val visited = mutableSetOf(startCoords)
    var steps = 1

    var current: Coords? = diagram.pointsToCoords(startCoords).last()
    do {
        visited.add(current!!)
        steps++
        current = diagram.pointsToCoords(current).firstOrNull { !visited.contains(it) }
    } while (current != null)

    return steps / 2
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
println(part1(realInput))
// println(part2(realInput))
