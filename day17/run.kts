import java.io.File
import kotlin.math.abs

val testInput = """
2413432311323
3215453535623
3255245654254
3446585845452
4546657867536
1438598798454
4457876987766
3637877979653
4654967986887
4564679986453
1224686865563
2546548887735
4322674655533
""".trimIndent().lines()

val realInput = File("day17/input.txt").readLines()

data class Coords(val x: Int, val y: Int) {
    fun adjustedBy(delta: Coords) = Coords(x + delta.x, y + delta.y)
    fun distanceTo(other: Coords): Int {
        return abs(x - other.x) + abs(y - other.y)
    }
}

fun fromRows(lines: List<String>) = Diagram(
    lines.foldIndexed(mutableMapOf()) { y, acc, line ->
        line.forEachIndexed { x, c -> acc[Coords(x, y)] = c.digitToInt() }
        acc
    }
)


data class Diagram(val cells: Map<Coords, Int>) {
    private val xMin = 0
    private val xMax = cells.keys.maxOf { it.x }.toInt()
    private val yMin = 0
    private val yMax = cells.keys.maxOf { it.y }.toInt()

    fun getRows(): List<String> {
        val out = mutableListOf<String>()
        for (y in yMin..yMax) {
            var line = ""
            for (x in xMin..xMax) {
                line += cells[Coords(x, y)] ?: '.'
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

    fun getPaths(): Int {
        val entryPoint = Coords(xMin, yMin)
        val target = Coords(xMax, yMax)

        // use a* to find the shortest path
        val open = mutableSetOf(entryPoint)
        val closed = mutableSetOf<Coords>()

        val shortestPathsFromStart = mutableMapOf(entryPoint to 0)

        val heuristicFn: (Coords) -> Int = {
            it.distanceTo(target)
        }

        val costs = mutableMapOf<Coords, Int>()
        val bestParentsByNode = mutableMapOf<Coords, Coords?>(entryPoint to null)

        do {
//            println("======")
            println("open: $open")
//            println("closed: $closed")
            // choose the node with the lowest cost
            val current = open.minByOrNull { costs[it]!! }!!
//            println("current: $current")
            if (current == target) {
                println("found target")
                break
            }


            open.remove(current)
            closed.add(current)

            listOf(
                current.adjustedBy(Coords(0, -1)),
                current.adjustedBy(Coords(0, 1)),
                current.adjustedBy(Coords(-1, 0)),
                current.adjustedBy(Coords(1, 0))
            ).filter { fits(it) }
                .forEach {
//                    println("   neighbor: $it")
                    val nodeValue = cells[it]!!
                    val cost = shortestPathsFromStart[current]!! + nodeValue
//                    println("      cost: $cost")

                    if (shortestPathsFromStart[it] == null || cost < shortestPathsFromStart[it]!!) {
//                        println("      updating")
                        shortestPathsFromStart[it] = cost
                        bestParentsByNode[it] = current

                        costs[it] = cost + heuristicFn(it)
                        if (it !in open) {
                            open.add(it)
                        }
                    }
                }
        } while (open.isNotEmpty())

        val pathToTarget = mutableListOf(target)
        var current: Coords? = target
        while (current != null) {
            current = bestParentsByNode[current]
            if (current != null) {
                pathToTarget.add(current)
            }
        }

        val d = Diagram(pathToTarget.map { it to 0 }.toMap())
        d.draw()

        val cost1 = pathToTarget.dropLast(1).fold(0) { acc, coords -> acc + cells[coords]!! }

        return cost1
    }
}

fun part1(lines: List<String>): Int {
    val diagram = fromRows(lines)
    return diagram.getPaths()
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
// println(part1(realInput))
// println(part2(realInput))
