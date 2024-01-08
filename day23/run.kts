import java.io.File
import kotlin.math.abs
import kotlin.math.absoluteValue

val testInput =
    """
    #.#####################
    #.......#########...###
    #######.#########.#.###
    ###.....#.>.>.###.#.###
    ###v#####.#v#.###.#.###
    ###.>...#.#.#.....#...#
    ###v###.#.#.#########.#
    ###...#.#.#.......#...#
    #####.#.#.#######.#.###
    #.....#.#.#.......#...#
    #.#####.#.#.#########v#
    #.#...#...#...###...>.#
    #.#.#v#######v###.###v#
    #...#.>.#...>.>.#.###.#
    #####v#.#.###v#.#.###.#
    #.....#...#...#.#.#...#
    #.#########.###.#.#.###
    #...###...#...#...#.###
    ###.###.#.###v#####v###
    #...#...#.#.>.>.#.>.###
    #.###.###.#.###.#.#v###
    #.....###...###...#...#
    #####################.#
    """.trimIndent().lines()

val realInput = File("day23/input.txt").readLines()

data class Coords(val x: Int, val y: Int) {
    fun distanceTo(other: Coords): Int {
        return abs(x - other.x) + abs(y - other.y)
    }

    fun getAdjacentCoords(): List<Coords> {
        return listOf(
            Coords(x - 1, y),
            Coords(x + 1, y),
            Coords(x, y - 1),
            Coords(x, y + 1),
        )
    }

    fun isAdjacentTo(other: Coords): Boolean {
        val deltaX = (x - other.x).absoluteValue
        val deltaY = (y - other.y).absoluteValue
        return deltaX <= 1 && deltaY <= 1
    }
}

data class Diagram(val tiles: Map<Coords, Char>) {
    val xMin = 0
    val yMin = 0
    var xMax = 0
    var yMax = 0

    init {
        xMax = tiles.keys.maxOf { it.x }
        yMax = tiles.keys.maxOf { it.y }
    }

    fun fits(coord: Coords): Boolean {
        return coord.x in xMin..xMax && coord.y in yMin..yMax
    }

    fun getPossibleMovesFrom(pos: Coords): List<Coords> {
        val moves = mutableListOf<Coords>()
        val tile = tiles[pos]!!
        if (tile == '>') {
            moves.add(Coords(pos.x + 1, pos.y))
        } else if (tile == '<') {
            moves.add(Coords(pos.x - 1, pos.y))
        } else if (tile == '^') {
            moves.add(Coords(pos.x, pos.y - 1))
        } else if (tile == 'v') {
            moves.add(Coords(pos.x, pos.y + 1))
        } else if (tile == '.') {
            moves.addAll(pos.getAdjacentCoords())
        }

        return moves.filter { fits(it) && tiles[it] != '#' }
    }

    fun getRows(): List<String> {
        val out = mutableListOf<String>()
        for (y in yMin..yMax) {
            var line = ""
            for (x in xMin..xMax) {
                line += tiles[Coords(x, y)]
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
                line += tiles[Coords(x, y)]
            }
            out.add(line)
        }
        return out
    }

    fun getMaxMoves(): Set<Coords> {
        val frames = mutableListOf(Frame(Coords(1, 0), setOf()))

        var maxCoords = setOf<Coords>()
        while (frames.isNotEmpty()) {
            val frame = frames.removeAt(0)

            if (frame.visited.size > maxCoords.size) {
                maxCoords = frame.visited
            }

            val nextMoves =
                getPossibleMovesFrom(frame.pos)
                    .filter { !frame.visited.contains(it) }
                    .map { Frame(it, frame.visited + it) }
            frames.addAll(nextMoves)
        }
        return maxCoords
    }

    fun draw() {
        println(getRows().joinToString("\n"))
    }
}

fun parseInput(lines: List<String>): Diagram {
    val tiles = mutableMapOf<Coords, Char>()
    for ((y, line) in lines.withIndex()) {
        for ((x, char) in line.withIndex()) {
            tiles[Coords(x, y)] = char
        }
    }
    return Diagram(tiles)
}

data class Frame(val pos: Coords, val visited: Set<Coords>)

fun part1(lines: List<String>): Int {
    val diagram = parseInput(lines)
    diagram.draw()
    return diagram.getMaxMoves().size
}

fun part2(lines: List<String>): Int {
    val newLines = lines.map { it.replace(Regex("([<>^v])"), ".") }
    val diagram = parseInput(newLines)
//    diagram.draw()

    val moves = diagram.getMaxMoves()

    val nd =
        Diagram(
            diagram.tiles.mapValues { if (moves.contains(it.key)) '✅' else it.value },
        )
    nd.draw()

    return diagram.getMaxMoves().size
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
println(part1(realInput))
// println(part2(realInput))
