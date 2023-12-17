import java.io.File

val testInput = """
.|...\....
|.-.\.....
.....|-...
........|.
..........
.........\
..../.\\..
.-.-/..|..
.|....-|.\
..//.|....
""".trimIndent().lines()

data class Coords(val x: Int, val y: Int) {
    fun adjustedBy(delta: Coords) = Coords(x + delta.x, y + delta.y)
}

fun fromRows(lines: List<String>) = Diagram(
    lines.foldIndexed(mutableMapOf()) { y, acc, line ->
        line.forEachIndexed { x, c -> if (c != '.') acc[Coords(x, y)] = c }
        acc
    }
)


data class Diagram(val tiles: Map<Coords, Char>) {
    private val xMin = 0
    private val xMax = tiles.keys.maxOf { it.x }.toInt()
    private val yMin = 0
    private val yMax = tiles.keys.maxOf { it.y }.toInt()

    fun getRows(): List<String> {
        val out = mutableListOf<String>()
        for (y in yMin..yMax) {
            var line = ""
            for (x in xMin..xMax) {
                line += tiles[Coords(x, y)] ?: '.'
            }
            out.add(line)
        }
        return out
    }

    fun fits(coords: Coords): Boolean {
        return coords.x in xMin..xMax && coords.y in yMin..yMax
    }

    fun draw() {
        println(getRows().joinToString("\n"))
    }

    fun getEntryPoints(): List<Pair<Char, Coords>> { // direction + position
        val entryPoints = mutableListOf<Pair<Char, Coords>>()
        for (x in xMin..xMax) {
            entryPoints.add(Pair('S', Coords(x, yMin)))
            entryPoints.add(Pair('N', Coords(x, yMax)))
        }
        for (y in yMin..yMax) {
            entryPoints.add(Pair('E', Coords(xMin, y)))
            entryPoints.add(Pair('W', Coords(xMax, y)))
        }
        return entryPoints
    }
}

fun getEnergizedTiles(diagram: Diagram, startPos: Coords, direction: Char): Set<Coords> {
    data class StackItem(val direction: Char, val nextPos: Coords)

    // set of vectors: direction + coordinate
    val visitedSet = mutableSetOf<Pair<Char, Coords>>()
    val energizedSet = mutableSetOf<Coords>()

    val stack = mutableListOf(StackItem(direction, startPos))

    val coordDeltas = mapOf(
        'E' to Coords(1, 0),
        'W' to Coords(-1, 0),
        'N' to Coords(0, -1),
        'S' to Coords(0, 1),
    )
    val turns = mapOf(
        // direction + encountered tile
        "E\\" to 'S',
        "E/" to 'N',
        "W\\" to 'N',
        "W/" to 'S',
        "N\\" to 'W',
        "N/" to 'E',
        "S\\" to 'E',
        "S/" to 'W',
    )

    fun getNewTurn(stackItem: StackItem, tile: Char): StackItem {
        val at = stackItem.nextPos
        val newDirection = turns["${stackItem.direction}${tile}"]!!
        return StackItem(
            newDirection,
            at.adjustedBy(coordDeltas[newDirection]!!),
        )
    }

    fun getPassThrough(stackItem: StackItem): StackItem {
        val at = stackItem.nextPos
        return StackItem(
            stackItem.direction,
            at.adjustedBy(coordDeltas[stackItem.direction]!!),
        )
    }

    do {
        val stackItem = stack.removeLast()
        val at = stackItem.nextPos
        val tile = diagram.tiles.getOrDefault(at, '.')
        val newItems = mutableListOf<StackItem>()

        if (tile == '.') {
            newItems.add(getPassThrough(stackItem))
        } else if (tile == '\\' || tile == '/') {
            newItems.add(getNewTurn(stackItem, tile))
        } else if (tile == '|') {
            if (stackItem.direction in listOf('W', 'E')) {
                newItems.add(getNewTurn(stackItem, '\\'))
                newItems.add(getNewTurn(stackItem, '/'))
            } else {
                newItems.add(getPassThrough(stackItem))
            }
        } else if (tile == '-') {
            if (stackItem.direction in listOf('S', 'N')) {
                newItems.add(getNewTurn(stackItem, '\\'))
                newItems.add(getNewTurn(stackItem, '/'))
            } else {
                newItems.add(getPassThrough(stackItem))
            }
        }
        energizedSet.add(at)
        visitedSet.add(stackItem.direction to at)

        stack.addAll(newItems.filter {
            diagram.fits(it.nextPos) && Pair(it.direction, it.nextPos) !in visitedSet
        })

    } while (stack.size > 0)
    return energizedSet
}


val realInput = File("day16/input.txt").readLines()

fun part1(lines: List<String>): Int {
    val diagram = fromRows(lines)
    val energizedSet = getEnergizedTiles(diagram, Coords(0, 0), 'E')
    return energizedSet.size
}

fun part2(lines: List<String>): Int {
    val diagram = fromRows(lines)
    val entryPoints = diagram.getEntryPoints()
    val sums = entryPoints.map { (direction, startPos) ->
        getEnergizedTiles(diagram, startPos, direction).size
    }
    return sums.max()
}

println("--- test input")
println(part1(testInput))
println(part2(testInput))

println("--- real input")
println(part1(realInput))
println(part2(realInput))
