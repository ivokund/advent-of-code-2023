import java.io.File

val testInput = """
LLR

AAA = (BBB, BBB)
BBB = (AAA, ZZZ)
ZZZ = (ZZZ, ZZZ)
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

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
 println(part1(realInput))
// println(part2(realInput))
