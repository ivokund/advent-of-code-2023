import java.io.File
import kotlin.math.pow

val testInput = """
Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
""".trimIndent().lines()

val realInput = File("day04/input.txt").readLines()

data class Card(val id: Int, val myNumbers: List<Int>, val winningNumbers: List<Int>) {
    var copies: Int = 1
    fun getMatchCount(): Int {
        return myNumbers.filter { it in winningNumbers }.size
    }

    fun getScore(): Double {
        val count = getMatchCount()
        if (count == 0) return 0.0
        return 2.0.pow(count.toDouble() - 1)
    }

    fun addCopies(num: Int) {
        this.copies += num
    }
}

fun lineToCard(line: String): Card {
    return Regex("^Card\\s+(\\d+):\\s(.*)$").matchEntire(line)!!.let { match ->
        val (id, numbers) = match.destructured
        val (myNumbers, winningNumbers) = numbers.split(" | ").map {
            Regex("\\d++").findAll(it).map { match -> match.value.toInt() }.toList()
        }
        Card(id.toInt(), myNumbers, winningNumbers)
    }
}

fun part1(lines: List<String>): Int {
    return lines.sumOf { lineToCard(it).getScore() }.toInt()
}

fun part2(lines: List<String>): Int {
    val cardsById = lines.map { lineToCard(it) }.associateBy { it.id }

    cardsById.forEach { (_, card) ->
        for (number in 1..card.getMatchCount()) {
            cardsById[card.id + number]?.addCopies(card.copies)
        }
    }

    return cardsById.values.sumOf { it.copies }
}

println("--- test input")
println(part1(testInput))
println(part2(testInput))

println("--- real input")
println(part1(realInput))
println(part2(realInput))
