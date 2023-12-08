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

fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

val realInput = File("day08/input.txt").readLines()

fun parse(lines: List<String>): Map<String, Pair<String, String>> {
    return lines.drop(2).map {
        val (id, left, right) = Regex("(\\w{3}) = \\((\\w{3}), (\\w{3})\\)").matchEntire(it)!!.destructured
        Pair(id, Pair(left, right))
    }.associate { it.first to it.second }
}

fun moveStep (instruction: Char, nodes: Map<String, Pair<String, String>>, current: String): String {
    return if (instruction == 'L') {
        nodes[current]!!.first
    } else if (instruction == 'R') {
        nodes[current]!!.second
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

    val startNodes = nodes.keys
        .filter { it.endsWith("A") }
        .map {
            var current = it
            var steps = 0L
            do {
                instructions.forEach {
                    current = moveStep(it, nodes, current)
                    steps++
                }
            } while (!current.endsWith("Z"))
            it to steps
        }

    return startNodes.map { it.second }.reduce { acc, i -> findLCM(acc, i)}
}

println("--- test input")
println(part1(testInputPart1))
println(part2(testInputPart2))

println("--- real input")
println(part1(realInput))
println(part2(realInput))
