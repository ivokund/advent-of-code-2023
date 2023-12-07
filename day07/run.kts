import java.io.File

val testInput = """
32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483
""".trimIndent().lines()

enum class Rating(val rating: Int) {
    FIVE_OF_KIND(7),
    FOUR_OF_KIND(6),
    FULL_HOUSE(5),
    THREE_OF_KIND(4),
    TWO_PAIRS(3),
    ONE_PAIR(2),
    HIGH_CARD(1),
}

val realInput = File("day07/input.txt").readLines()

fun parseHand(line: String): Hand {
    val (cards, bid) = line.split(" ")
    return Hand(cards.toList(), bid.toInt())
}

data class Hand(val cards: List<Char>, val bid: Int) {
    fun rating(): Rating {
        val groups = cards.groupBy { it }
            .map { (k, v) -> k to v.size }
            .associate { (k, v) -> k to v }
        return if (groups.values.contains(5)) {
            Rating.FIVE_OF_KIND
        } else if (groups.values.contains(4)) {
            Rating.FOUR_OF_KIND
        } else if (groups.values.contains(3) && groups.values.contains(2)) {
            Rating.FULL_HOUSE
        } else if (groups.values.contains(3)) {
            Rating.THREE_OF_KIND
        } else if (groups.values.filter { it == 2 }.size == 2) {
            Rating.TWO_PAIRS
        } else if (groups.values.contains(2)) {
            Rating.ONE_PAIR
        } else {
            Rating.HIGH_CARD
        }
    }
}

fun sortHands(hands: List<Hand>, cardRatings: List<Char>): List<Hand> {
    return hands
        .sortedWith(
            compareBy<Hand> {
                it.rating().rating
            }.thenByDescending {
                cardRatings.indexOf(it.cards[0])
            }.thenByDescending {
                cardRatings.indexOf(it.cards[1])
            }.thenByDescending {
                cardRatings.indexOf(it.cards[2])
            }.thenByDescending {
                cardRatings.indexOf(it.cards[3])
            }.thenByDescending {
                cardRatings.indexOf(it.cards[4])
            },
        )
}

fun part1(lines: List<String>): Int {
    val cardRatings = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')

    val hands = lines.map { parseHand(it) }
    val sortedHands = sortHands(hands, cardRatings)

    return sortedHands.foldIndexed(0) { index, acc, hand -> (index + 1) * hand.bid + acc }
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
println(part1(realInput))
// println(part2(realInput))
