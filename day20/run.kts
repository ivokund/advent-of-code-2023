import java.io.File

val testInput = """
broadcaster -> a, b, c
%a -> b
%b -> c
%c -> inv
&inv -> a
""".trimIndent().lines()

val testInput2 = """
broadcaster -> a
%a -> inv, con
&inv -> b
%b -> con
&con -> output
""".trimIndent().lines()

val realInput = File("day20/input.txt").readLines()

data class Pulse(val high: Boolean, val to: String, val from: String) {
    fun debug() {
//        println("$from -${if (high) "high" else "low"}-> $to")
    }
}

data class Module(val name: String, val type: Char?, val destinations: List<String>) {
    val destModules: MutableList<Module> = mutableListOf()
    var flipFlopState = false
    val conjunctionFromPulses = mutableMapOf<String, Boolean>()

    fun primeConjunction(sourceModuleName: String) {
        conjunctionFromPulses[sourceModuleName] = false
    }

    fun receivePulse(pulse: Pulse): List<Pulse> {

        if (name == "broadcaster") {
            return destinations.map { Pulse(pulse.high, it, name) }
        } else if (type == '%') {
            if (pulse.high) {
                // ignore
            } else {
                flipFlopState = !flipFlopState
                return destinations.map { Pulse(flipFlopState, it, name) }
            }
        } else if (type == '&') {
            conjunctionFromPulses[pulse.from] = pulse.high
            val pulseToSend = !conjunctionFromPulses.all { it.value }
            return destinations.map { Pulse(pulseToSend, it, name) }
        }

        return emptyList()
    }
}

fun parseConfiguration (line: String): Module {
    val (name, destinations) = line.split(" -> ")
    val type = if (name[0] == '%' || name[0] == '&') name[0] else null
    val moduleName = if (type == null) name else name.substring(1)
    val module = Module(moduleName, type, destinations.split(", "))
    return module
}

fun initModules(lines: List<String>): Map<String, Module> {
    val modules = lines.map { parseConfiguration(it) }.toMutableList()
    val modulesByName = modules.associateBy { it.name }

    modules.forEach {
        if (it.type == '&') {
            val refModules = modules.filter {refModule ->
                refModule.destinations.contains(it.name)
            }
            refModules.forEach { refModule ->
                it.primeConjunction(refModule.name)
            }
        }
    }
    return modulesByName
}

fun part1(lines: List<String>): Int {
    val modulesByName = initModules(lines)

    var pulseCount = mutableMapOf(true to 0, false to 0)
    fun pushButton() {
        val pulses = mutableListOf(Pulse(false, "broadcaster", "button"))
        do {
            val pulse = pulses.removeAt(0)
            pulseCount[pulse.high] = pulseCount[pulse.high]!! + 1
            pulse.debug()
            if (modulesByName[pulse.to] != null) {
                pulses.addAll(modulesByName[pulse.to]!!.receivePulse(pulse))
            }
        } while (pulses.size > 0)
    }

    for (i in 1..1000) {
        pushButton()
    }

    return pulseCount[true]!! * pulseCount[false]!!
}

fun part2(lines: List<String>): Int {
    val modulesByName = initModules(lines)

    var buttonCount = 0
    var criteriaMet = false
    fun pushButton() {
        buttonCount++
        val pulses = mutableListOf(Pulse(false, "broadcaster", "button"))
        do {
            val pulse = pulses.removeAt(0)
//            pulse.debug()
            if (modulesByName[pulse.to] != null) {
                pulses.addAll(modulesByName[pulse.to]!!.receivePulse(pulse))
            }
            criteriaMet = pulse.to == "rx" && !pulse.high
        } while (pulses.size > 0)
    }

    do {
        pushButton()
    } while (!criteriaMet)

    return buttonCount
}

println("--- test input")
//println(part1(testInput))
println(part1(testInput2))
// println(part2(testInput))

println("--- real input")
 println(part1(realInput))
 println(part2(realInput))
