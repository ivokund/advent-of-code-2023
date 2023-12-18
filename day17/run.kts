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

data class StackItem(val coords: Coords, val stepsX: Int, val stepsY: Int) {
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

    fun findShortestPath(straightMin: Int, straightMax: Int): Int {
        val deltasByDirection = mapOf(
            'N' to Coords(0, -1),
            'S' to Coords(0, 1),
            'W' to Coords(-1, 0),
            'E' to Coords(1, 0)
        )
        val opposites = mapOf(
            'N' to 'S',
            'S' to 'N',
            'W' to 'E',
            'E' to 'W'
        )

        val entryPoint = Coords(xMin, yMin)
        val target = Coords(xMax, yMax)

        val firstFrame = StackItem(entryPoint, 0, 0)

        val openStack = mutableSetOf(Pair(firstFrame, 'E'))
        val closed = mutableSetOf<StackItem>()

        val shortestPathsFromStart = mutableMapOf(firstFrame to 0)

        val costs = mutableMapOf<StackItem, Int>()
        val bestParentsByNode = mutableMapOf<StackItem, StackItem?>(firstFrame to null)

        do {
//            println("======")

            val stackItem = openStack.minByOrNull { costs[it.first]!! }!!
            val (current, prevDirection) = stackItem
//            println("current: $current")
            if (current.coords == target) {
                println("========== found target ==========")
                break
            }


            openStack.remove(stackItem)
            closed.add(current)

            deltasByDirection
                .filter {
                    fits(current.coords.adjustedBy(it.value))
                }.filter {
                    opposites[it.key] != prevDirection && opposites[prevDirection] != it.key
                }
                .mapValues {
                    val newCoord = current.coords.adjustedBy(it.value)
                    val newStepsX = if (it.value.x != 0) current.stepsX + 1 else 0
                    val newStepsY = if (it.value.y != 0) current.stepsY + 1 else 0
                    StackItem(newCoord, newStepsX, newStepsY)
                }
                .filter { it.value.stepsX <= straightMax && it.value.stepsY <= straightMax }
                .forEach {

                    val (newDirection, newStackItem) = it
//                    println("-- Processing: ${newStackItem.coords.x},${newStackItem.coords.y}  [${cells[newStackItem.coords]}]")

                    val nodeValue = cells[newStackItem.coords]!!
                    val cost = shortestPathsFromStart[current]!! + nodeValue
//                    println("      cost: $cost")

                    if (shortestPathsFromStart[newStackItem] == null || cost < shortestPathsFromStart[newStackItem]!!) {
//                        println("      updating")
                        shortestPathsFromStart[newStackItem] = cost
                        bestParentsByNode[newStackItem] = current

                        // use manhattan distance as the heuristic
                        costs[newStackItem] = cost + newStackItem.coords.distanceTo(target)
                        val finalItem = Pair(newStackItem, newDirection)
                        if (finalItem !in openStack) {
                            openStack.add(finalItem)
                        }
                    }

                }
        } while (openStack.isNotEmpty())

        val pathToTarget = mutableListOf(target)
        var current: StackItem? = bestParentsByNode.keys.first { it.coords == target }
        while (current != null) {
            current = bestParentsByNode[current]
            if (current != null) {
                pathToTarget.add(current.coords)
            }
        }


//        val newMap = cells + pathToTarget.map { it to 0 }.toMap()
//        val d = Diagram(newMap)
//        d.draw()
//
        val cost = pathToTarget
            .dropLast(1)
            .fold(0) { acc, coords -> acc + cells[coords]!! }

        return cost
    }
}

fun part1(lines: List<String>): Int {
    val diagram = fromRows(lines)
    return diagram.findShortestPath(1, 3)
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
 println(part1(realInput))
// println(part2(realInput))
