import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.system.exitProcess

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

val testInput2 = """
111111111111
999999999991
999999999991
999999999991
999999999991
""".trimIndent().lines()

val correctPaths = listOf(
    Coords(1, 0),
    Coords(2, 0),
    Coords(3, 0),
    Coords(4, 0),
    Coords(5, 0),
    Coords(6, 0),
    Coords(7, 0),
    Coords(7, 1),
    Coords(7, 2),
    Coords(7, 3),
    Coords(7, 4),
    Coords(8, 4),
    Coords(9, 4),
    Coords(10, 4),
    Coords(11, 4),
)

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

    fun getAreaSum(from: Coords, to: Coords): Int {
        val (startY, endY) = listOf(from.y, to.y).sorted()
        val (startX, endX) = listOf(from.x, to.x).sorted()
        return (startY..endY).sumOf { y ->
            (startX..endX).sumOf { x ->
                cells[Coords(x, y)]!!
            }
        }
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

            val stackItem = openStack.minByOrNull { costs[it.first]!! }!!
            val (current, prevDirection) = stackItem

            val debug: (Any) -> Unit = {
                if (current.coords in correctPaths) {
//                    println(it)
                }
            }
            debug("======")



            debug("current: $current, moving $prevDirection")
            if (current.coords == target ) {
                debug("========== found target ==========")
                debug("moving $prevDirection")
                debug("StepsX: ${current.stepsX}, StepsY: ${current.stepsY}, min: $straightMin")
                break
            }

            openStack.remove(stackItem)
            closed.add(current)


            val options = if (current.stepsX < straightMin && current.stepsY < straightMin) {
                debug("below min: ${current.stepsX}, ${current.stepsY}")
                mapOf(prevDirection to deltasByDirection[prevDirection]!!)
            } else {
                deltasByDirection
            }.filter {
//                println("-- filtering: ${it.key}")
                fits(current.coords.adjustedBy(it.value))
            }

            debug(options)

            options
                .filter {
                    // cannot turn backwards
                    opposites[it.key] != prevDirection && opposites[prevDirection] != it.key
                }
                .mapValues {
                    val newCoord = current.coords.adjustedBy(it.value)
                    val newStepsX = if (it.value.x != 0) current.stepsX + 1 else 0
                    val newStepsY = if (it.value.y != 0) current.stepsY + 1 else 0

                    StackItem(newCoord, newStepsX, newStepsY)
                }
                .filter {
                    // filter out moves that would not complete before border
                    val stepsToBorderX = xMax - it.value.coords.x
                    val stepsToBorderY = yMax - it.value.coords.y

                    val stepsRequiredX = straightMin - it.value.stepsX
                    val stepsRequiredY = straightMin - it.value.stepsY

                    stepsToBorderX >= stepsRequiredX || stepsToBorderY >= stepsRequiredY
                }

                .filter {
                    // only allow moves that don't exceed straightMax
                    val underMax = it.value.stepsX <= straightMax && it.value.stepsY <= straightMax
//                    println("underMax: $underMax. Steps: ${it.value.stepsX}, ${it.value.stepsY} less than $straightMax")
                    underMax
                }
                .forEach {

                    val (newDirection, newStackItem) = it

                    debug("-- Processing: ${newStackItem.coords.x},${newStackItem.coords.y}  [${cells[newStackItem.coords]}]")

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
                        debug("prepping to add")
                        if (finalItem !in openStack) {
                            debug("  adding pair: $finalItem")
                            openStack.add(finalItem)

                            debug("   stack content:")
                            debug(openStack)
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
////
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

fun part2(lines: List<String>): Int {
    val diagram = fromRows(lines)
    return diagram.findShortestPath(4, 10)
}

println("--- test input")
println(part1(testInput)) // 102
println(part2(testInput)) // 94
println(part2(testInput2)) // 71

println("--- real input")
println(part1(realInput)) // 956
println(part2(realInput)) // ???

// wrong so far:
// 1387
// 1109
// 1111