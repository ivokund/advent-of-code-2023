import java.io.File
import kotlin.math.absoluteValue

val testInput = """
467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...$.*....
.664.598..
""".trimIndent().lines()

val realInput = File("day03/input.txt").readLines()

data class Coords(val x: Int, val y: Int) {
    fun isAdjacentTo(other: Coords): Boolean {
        val deltaX = (x - other.x).absoluteValue
        val deltaY = (y - other.y).absoluteValue
        return deltaX <= 1 && deltaY <= 1
    }
}
data class Number(val value: Int, val coords: List<Coords>) {
    fun isAdjacentTo(other: Coords): Boolean {
        return this.coords.any { it.isAdjacentTo(other) }
    }
}
data class Symbol(val value: Char, val coord: Coords)

data class Adjacency(val symbol: Symbol, val numbers: List<Number>)

data class Gear(val numbers: Pair<Number, Number>) {
    fun getRatio() = numbers.first.value * numbers.second.value
}

fun parseNumbers(lines: List<String>): List<Number> {
    val numbers = mutableListOf<Number>()
    for (y in lines.indices) {
        Regex("\\d+").findAll(lines[y]).forEach {
            numbers.add(
                Number(
                    it.value.toInt(),
                    it.range.map { x -> Coords(x, y) },
                ),
            )
        }
    }
    return numbers
}

fun parseSymbols(lines: List<String>): List<Symbol> {
    val symbols = mutableListOf<Symbol>()
    for (y in lines.indices) {
        for (x in lines[y].indices) {
            val value = lines[y][x]
            if (!value.isDigit() && value != '.') {
                symbols.add(Symbol(value, Coords(x, y)))
            }
        }
    }
    return symbols
}

fun findAdjacencies(lines: List<String>): List<Adjacency> {
    val numbers = parseNumbers(lines)
    val symbols = parseSymbols(lines)
    val adjacencies = mutableListOf<Adjacency>()
    for (symbol in symbols) {
        val adjacentNumbers = numbers.filter {
            it.isAdjacentTo(symbol.coord)
        }
        adjacencies.add(Adjacency(symbol, adjacentNumbers))
    }
    return adjacencies
}

fun part1(lines: List<String>): Int {
    return findAdjacencies(lines).flatMap { it.numbers }.sumOf { it.value }
}

// fun part2(lines: List<String>): Int {
//    val numbers = parseNumbers(lines)
//    val gearSymbols = parseSymbols(lines).filter { it.value == '*' }
//
//    val f = numbers.fold(mutableListOf<Gear>()) { acc, number ->
//        if (gearSymbols.filter { symbol -> number.isAdjacentTo(symbol.coord) }.size == 2) {
//            acc.add(Gear(Pair(number, number))
//        }
//    }
// }

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
println(part1(realInput))
// println(part2(realInput))
