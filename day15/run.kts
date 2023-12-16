import java.io.File

val testInput = """
rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
""".trimIndent()

val realInput = File("day15/input.txt").readText()

fun hash(input: String) = input.fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }
check(hash("HASH") == 52)

data class Lens(val label: String, val focalLength: Int) {
    override fun toString() = "[$label $focalLength]"
}

fun part1(input: String): Int {
    return input.split(',').sumOf { hash(it) }
}

fun part2(input: String): Int {
    val boxes = mutableMapOf<Int, MutableList<Lens>>()
    input.split(',').forEach {
        val (label, operation, foo) = Regex("(\\w+)(=|\\-)(\\w*)").matchEntire(it)!!.destructured
        val boxNo = hash(label)
        if (boxNo !in boxes) boxes[boxNo] = mutableListOf()
        if (operation == "=") {
            val index = boxes[boxNo]!!.indexOfFirst { it.label == label }
            val lens = Lens(label, foo.toInt())
            if (index == -1) {
                boxes[boxNo]!!.add(lens)
            } else {
                boxes[boxNo]!![index] = lens
            }
        } else {
            boxes[boxNo]!!.removeIf { it.label == label }
        }
    }

    val focusingPowers = boxes.flatMap {
        val boxNo = it.key + 1
        it.value.mapIndexed { slotNo, lens ->
            lens.focalLength * (slotNo + 1) * boxNo
        }
    }

    return focusingPowers.sum()
}

println("--- test input")
println(part1(testInput))
println(part2(testInput))

println("--- real input")
println(part1(realInput))
println(part2(realInput))
