import java.io.File

val testInput = """
FF7FSF7F7F7F7F7F---7
L|LJ||||||||||||F--J
FL-7LJLJ||||||LJL-77
F--JF--7||LJLJ7F7FJ-
L---JF-JLJ.||-FJLJJ7
|F|F-JF---7F7-L7L|7|
|FFJF7L7F-JF7|JL---7
7-L-JL7||F7|L7F-7F7|
L.L7LFJ|||||FJL7||LJ
L7JLJL-JLJLJL--JLJ.L
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

        return deltas.map { Coords(coord.x + it.x, coord.y + it.y) }
            .filter { map[it] != null && map[it] != '.' }
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

fun findLoop(diagram: Diagram): List<Coords> {
    val startCoords = diagram.map.filterValues { it == 'S' }.keys.first()

    val visited = mutableSetOf(startCoords)

    var current: Coords? = diagram.pointsToCoords(startCoords).last()
    do {
        visited.add(current!!)
        current = diagram.pointsToCoords(current).firstOrNull { !visited.contains(it) }
    } while (current != null)

    return visited.toList()
}

fun part1(lines: List<String>): Int {
    val diagram = parseInput(lines)
    val loop = findLoop(diagram)

    return loop.size / 2
}

fun part2(lines: List<String>): Int {
    val diagram = parseInput(lines)
    val loop = findLoop(diagram)

    val xMin = 0
    val xMax = diagram.map.keys.maxOf { it.x }
    val yMin = 0
    val yMax = diagram.map.keys.maxOf { it.y }

    val insideCoords = mutableListOf<Coords>()

    for (y in yMin..yMax) {
        var line = (xMin..xMax)
            .map { diagram.map[Coords(it, y)] }
            .joinToString("")
            .replace("S", "|")
        var isInside = false

        var cornerStarted: Char? = null

        line.forEachIndexed { index, c ->
            val coords = Coords(index, y)
            val onLoop = coords in loop

            if (onLoop) {
                if (c == 'F' || c == 'L') {
                    cornerStarted = c
                    return@forEachIndexed
                }
                if (cornerStarted != null && c == '-') {
                    return@forEachIndexed
                }
                if (cornerStarted == 'F' && c == '7' || cornerStarted == 'L' && c == 'J') {
                    cornerStarted = null
                    return@forEachIndexed
                }
                if (cornerStarted == 'F' && c == 'J' || cornerStarted == 'L' && c == '7') {
                    cornerStarted = null
                    isInside = !isInside
                    return@forEachIndexed
                }
                if (c == '|') {
                    isInside = !isInside
                    return@forEachIndexed
                }
            } else {
                if (isInside) {
                    insideCoords.add(coords)
                } else {
                    // we haven't reached the thing yet, ignore everything
                    return@forEachIndexed
                }
            }
        }
    }

//    val out: MutableList<String> = mutableListOf()
//    for (y in yMin..yMax) {
//        var line = ""
//        for (x in xMin..xMax) {
//            val tile = diagram.map[Coords(x, y)]
//            line += if (Coords(x, y) in loop) tile
//            else if (Coords(x, y) in insideCoords) "✅"
//            else "O"
//        }
//        out.add(line)
//    }
//    println(out.joinToString("\n"))

    return insideCoords.size
}

// 325
println("--- test input")
println(part1(testInput))
println(part2(testInput))

println("--- real input")
println(part1(realInput))
println(part2(realInput))
