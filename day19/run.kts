import java.io.File

val testInput = """
px{a<2006:qkq,m>2090:A,rfg}
pv{a>1716:R,A}
lnx{m>1548:A,A}
rfg{s<537:gd,x>2440:R,A}
qs{s>3448:A,lnx}
qkq{x<1416:A,crn}
crn{x>2662:A,R}
in{s<1351:px,qqz}
qqz{s>2770:qs,m<1801:hdj,R}
gd{a>3333:R,R}
hdj{m>838:A,pv}

{x=787,m=2655,a=1222,s=2876}
{x=1679,m=44,a=2067,s=496}
{x=2036,m=264,a=79,s=2244}
{x=2461,m=1339,a=466,s=291}
{x=2127,m=1623,a=2188,s=1013}
""".trimIndent()

val realInput = File("day19/input.txt").readText()

data class Part(val params: Map<Char, Int>) {
}

data class StepOperation(val param: Char, val operator: Char, val value: Int) {
    fun runPart(part: Part): Boolean {
        return when (operator) {
            '>' -> part.params[param]!! > value
            '<' -> part.params[param]!! < value
            else -> {
                throw Exception("Unknown operator: $operator")
            }
        }
    }
}

data class Step(val operation: StepOperation?, val target: String) {
    fun runPart(part: Part): String? {
        return if (operation == null || operation.runPart(part)) target else null
    }

}

data class Workflow(val name: String, val steps: List<Step>) {
    fun runPart(part: Part): String {
        for (step in this.steps) {
            val result = step.runPart(part)
            if (result != null) {
                return result
            }
        }
        throw Exception("No step matched for running [$part] in $name")
    }
}


fun parseInput(input: String): Pair<List<Workflow>, List<Part>> {
    val inputs = input.split("\n\n")

    val workflows = inputs[0].split("\n").map {
        val (wfName, rest) = it.split("{")
        val steps = rest.dropLast(1).split(",")
            .map { step ->

                if (step.matches(Regex("^\\w+$"))) {
                    Step(null, step)
                } else {
                    val (op, target) = step.split(":")
                    val (param, operator, value) = Regex("(\\w)([<>])(\\d+)").matchEntire(op)!!.groupValues.drop(1)
                    Step(StepOperation(param[0], operator[0], value.toInt()), target)
                }
            }
        Workflow(wfName, steps)
    }

    val parts = inputs[1].split("\n").map {
        val (x, m, a, s) = Regex("\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)}").matchEntire(it)!!.groupValues.drop(1)
            .map { it.toInt() }
        Part(mapOf('x' to x, 'm' to m, 'a' to a, 's' to s))
    }

    return Pair(workflows, parts)
}


fun part1(lines: String): Int {
    val (workflows, parts) = parseInput(lines)
    val wfs = workflows.associateBy { it.name }

    val partResults = parts.associateWith { part ->
        var workflow = "in"
        do {
            workflow = wfs[workflow]!!.runPart(part)
        } while (workflow != "R" && workflow != "A")
        workflow
    }

    return partResults.filter { it.value == "A" }.keys.sumOf { it.params.values.sum() }
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
println(part1(realInput))
// println(part2(realInput))
