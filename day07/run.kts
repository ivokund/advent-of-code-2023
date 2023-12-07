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

fun getNormalRating(cards: List<Char>): Rating {
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

fun getJokerRating(cards: List<Char>): Rating {
    val groups = cards.groupBy { it }
        .map { (k, v) -> k to v.size }
        .associate { (k, v) -> k to v }
        .toMutableMap()
    do {
        var adjusted = false
        val jokerCount = groups['J'] ?: 0
        if (jokerCount in 1..4) {
            val maxCard = groups.filter { it.key != 'J' && it.value < 5 }.maxBy { it.value }.key
            groups[maxCard] = groups[maxCard]!! + 1
            groups['J'] = groups['J']!! - 1
            adjusted = true
        }
    } while (adjusted)
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
data class Hand(val cards: List<Char>, val bid: Int, val rating: Rating)

fun sortHands(hands: List<Hand>, cardRatings: List<Char>): List<Hand> {
    return hands
        .sortedWith(
            compareBy<Hand> {
                it.rating.rating
            }.thenBy {
                cardRatings.indexOf(it.cards[0])
            }.thenBy {
                cardRatings.indexOf(it.cards[1])
            }.thenBy {
                cardRatings.indexOf(it.cards[2])
            }.thenBy {
                cardRatings.indexOf(it.cards[3])
            }.thenBy {
                cardRatings.indexOf(it.cards[4])
            },
        )
}

fun part1(lines: List<String>): Int {
    val cardRatings = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')

    val hands = lines.map {
        val (cards, bid) = it.split(" ")
        Hand(cards.toList(), bid.toInt(), getNormalRating(cards.toList()))
    }
    val sortedHands = sortHands(hands, cardRatings)
    return sortedHands.foldIndexed(0) { index, acc, hand -> (index + 1) * hand.bid + acc }
}

fun part2(lines: List<String>): Int {
    val cardRatings = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')

    val hands = lines.map {
        val (cards, bid) = it.split(" ")
        Hand(cards.toList(), bid.toInt(), getJokerRating(cards.toList()))
    }

    val sortedHands = sortHands(hands, cardRatings)
    return sortedHands.foldIndexed(0) { index, acc, hand -> (index + 1) * hand.bid + acc }
}

println("--- test input")
println(part1(testInput))
println(part2(testInput))

println("--- real input")
println(part1(realInput))
println(part2(realInput))

// 254164885
// 254177478
