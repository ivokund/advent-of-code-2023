import java.io.File

val testInputPart1 = """
LLR

AAA = (BBB, BBB)
BBB = (AAA, ZZZ)
ZZZ = (ZZZ, ZZZ)
""".trimIndent().lines()

val testInputPart2 = """
LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)
""".trimIndent().lines()

val realInput = File("day08/input.txt").readLines()

fun parse(lines: List<String>): Map<String, Pair<String, String>> {
    return lines.drop(2).map {
        val (id, left, right) = Regex("(\\w{3}) = \\((\\w{3}), (\\w{3})\\)").matchEntire(it)!!.destructured
        Pair(id, Pair(left, right))
    }.associate { it.first to it.second }
}

fun moveStep (instruction: Char, nodes: Map<String, Pair<String, String>>, current: String): String {
    if (instruction == 'L') {
        return nodes[current]!!.first
    } else if (instruction == 'R') {
        return nodes[current]!!.second
    } else {
        throw Error("Invalid instruction")
    }
}

fun part1(lines: List<String>): Int {
    val instructions = lines[0].toList()
    val nodes = parse(lines)

    var index = 0
    var current = "AAA"
    var stepsTaken = 0
    do {
        val instruction = instructions[index++]
        current = moveStep(instruction, nodes, current)

        if (index == instructions.size) {
            index = 0
        }
        stepsTaken++
    } while (current != "ZZZ")

    return stepsTaken
}


fun part2(lines: List<String>): Long {
    val instructions = lines[0].toList()
    val nodes = parse(lines)

    var currentNodes = nodes.keys.filter { it.endsWith("A") }.associateBy { it }
    var instructionCursor = 0
    var stepsTaken = 0L

    do {
        val instruction = instructions[instructionCursor++] // todo: move by index

        if (instructionCursor == instructions.size) {
            instructionCursor = 0
        }
        stepsTaken++
        if (stepsTaken % 10_000_000 == 0L) {
            println(stepsTaken)
            println(currentNodes)
        }

        val newNodePositions = currentNodes.toMutableMap()

        for (node in currentNodes) {
            val newVal = moveStep(instruction, nodes, node.value)
            newNodePositions[node.key] = newVal
        }
        currentNodes = newNodePositions
    } while (!currentNodes.all { it.value.endsWith("Z") })

    return stepsTaken
}

println("--- test input")
println(part1(testInputPart1))
println(part2(testInputPart2))

println("--- real input")
 println(part1(realInput))
// println(part2(realInput))
