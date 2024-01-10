import java.io.File

val testInput =
    """
    jqt: rhn xhk nvd
    rsh: frs pzl lsr
    xhk: hfx
    cmg: qnr nvd lhk bvb
    rhn: xhk bvb hfx
    bvb: xhk hfx
    pzl: lsr hfx nvd
    qnr: nvd
    ntq: jqt hfx bvb xhk
    nvd: lhk
    lsr: lhk
    rzs: qnr cmg lsr rsh
    frs: qnr lhk lsr
    """.trimIndent().lines()

val realInput = File("day25/input.txt").readLines()

fun parseInput(lines: List<String>): Diagram {
    val pairs = mutableSetOf<Pair<String, String>>()
    lines.forEach {
        val (from, to) = it.split(": ")
        to.split(" ").forEach { to ->
            pairs.add(Pair(from, to))
            pairs.add(Pair(to, from))
        }
    }
    return Diagram(pairs)
}

val connectionMemory = mutableMapOf<String, Set<String>>()

data class Diagram(val pairs: Set<Pair<String, String>>) {
    fun remove(pair: Pair<String, String>): Diagram {
        val newPairs = pairs.toMutableSet()
        newPairs.remove(Pair(pair.first, pair.second))
        newPairs.remove(Pair(pair.second, pair.first))
        return Diagram(newPairs)
    }

    fun getAllConnectedItems(
        item: String,
        existing: Set<String> = emptySet(),
    ): Set<String> {
        val childItems =
            pairs.filter { it.first == item }
                .map { it.second }.toSet()
        val remaining = childItems - existing
        val result =
            childItems + existing +
                remaining.flatMap {
                    getAllConnectedItems(it, existing + childItems)
                }
        return result
    }

    fun findPartitions(): List<Set<String>> {
        val partitions = mutableListOf<Set<String>>()
        val queue = pairs.map { it.first }.toMutableSet()

        do {
            val item = queue.first()

//            if (!connectionMemory.contains(item)) {
            val partitionItems = setOf(item) + getAllConnectedItems(item)
//                connectionMemory.set(item, partitionItems)
//            }
//
//            val partitionItems = connectionMemory.get(item)!!
            queue.removeAll(partitionItems)

            partitions.add(partitionItems)
        } while (queue.isNotEmpty())

        return partitions
    }

    fun disconnect3ThatProduceTwoPartitions(): List<Set<String>>? {
        val total = pairs.indices.count().toLong() * pairs.indices.count() * pairs.indices.count()
        var done = 0L
        for (i1 in pairs.indices) {
            for (i2 in pairs.indices) {
                for (i3 in pairs.indices) {
                    done++
                    println("$done / $total::: ${done / total * 100}%")
                    if (i1 != i2 && i2 != i3 && i1 != i3) {
                        val first = pairs.elementAt(i1)
                        val second = pairs.elementAt(i2)
                        val third = pairs.elementAt(i3)
                        val newDiagram =
                            remove(first)
                                .remove(second)
                                .remove(third)
                        val partitions = newDiagram.findPartitions()
                        if (partitions.size == 2) {
                            println("found 3 that produce 2 partitions")
                            println(first)
                            println(second)
                            println(third)
                            return partitions
                        }
                    }
                }
            }
        }
        return null
    }
}

fun part1(lines: List<String>): Int {
    val diagram =
        parseInput(lines)

    val partitions = diagram.disconnect3ThatProduceTwoPartitions()

    println(partitions)
    return partitions!!.fold(1) { acc, partition ->
        acc * partition.size
    }
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
// println(part1(realInput))
// println(part2(realInput))
