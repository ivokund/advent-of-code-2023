import java.io.File

val testInput = """
Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
""".trimIndent().lines()

val realInput = File("day02/input.txt").readLines()

data class Colors(val blue: Int, val green: Int, val red: Int) {
    fun isUnder(other: Colors) = blue <= other.blue && green <= other.green && red <= other.red
    fun power() = listOf(blue, green, red).filter { it > 0 }.fold(1) { acc, i -> acc * i }
}

data class Game(val id: Int, val colors: List<Colors>) {
    fun minCombination() = Colors(
        red = colors.filter { it.red > 0 }.maxOf { it.red },
        green = colors.filter { it.green > 0 }.maxOf { it.green },
        blue = colors.filter { it.blue > 0 }.maxOf { it.blue },
    )
}

fun inputToGames(line: String): Game {
    val result = Regex("Game (\\d+): (.*)").find(line)
    val (_, id, allCards) = result?.groupValues ?: throw Error("no match")

    val cards = allCards.split(";").map {
        Regex("(\\d+) (red|green|blue)").findAll(it).associate {
            val (num, color) = it.destructured
            color to num.toInt()
        }
    }.map { Colors(blue = it["blue"] ?: 0, green = it["green"] ?: 0, red = it["red"] ?: 0) }
    return Game(id.toInt(), cards)
}

fun part1(lines: List<String>): Int {
    val games = lines.map { inputToGames(it) }
    val maxCards = Colors(red = 12, green = 13, blue = 14)

    val invalidGames = games.filter { game ->
        game.colors.all { it.isUnder(maxCards) }
    }

    return invalidGames.sumOf { it.id }
}

fun part2(lines: List<String>): Int {
    val games = lines.map { inputToGames(it) }

    return games.map {
        it.minCombination().power()
    }.sum()
}

println("--- test input")
println(part1(testInput))
println(part2(testInput))

println("--- real input")
println(part1(realInput))
println(part2(realInput))
