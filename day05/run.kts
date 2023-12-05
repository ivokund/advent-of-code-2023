import java.io.File

val testInput = """
seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4
""".trimIndent()

val realInput = File("day05/input.txt").readText()

data class MapEntry(val source: Long, val target: Long, val range: Long)
data class Section(val name: String, val maps: List<MapEntry>)

fun parseInput(input: String): Pair<List<Long>, List<Section>> {
    val sectionsText = input.split("\n\n")
    val seeds = sectionsText[0].split(": ").last().split(" ").map { it.toLong() }

    val sections = sectionsText.drop(1).map { section ->
        val lines = section.lines()
        val name = lines[0].split(" ").first()
        val maps = lines.drop(1).map { line ->
            val (target, source, range) = line.split(" ").map { it.toLong() }
            MapEntry(source, target, range)
        }
        Section(name, maps)
    }
    return Pair(seeds, sections)
}

fun getSeedLocation(seed: Long, sections: List<Section>): Long {
    var latest = seed
    for (section in sections) {
        for (map in section.maps) {
            if (latest in map.source..(map.source + map.range - 1)) {
                latest = map.target + (latest - map.source)
                break
            }
        }
    }
    return latest
}

fun part1(input: String): Long {
    val (seeds, sections) = parseInput(input)
    return seeds.minOf { getSeedLocation(it, sections) }
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
println(part1(realInput))
// println(part2(realInput))
