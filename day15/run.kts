import java.io.File

val testInput = """
rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
""".trimIndent()

val realInput = File("day15/input.txt").readText()

fun hash(input: String) = input.fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }
check(hash("HASH") == 52)

fun part1(input: String): Int {
    return input.split(',').sumOf { hash(it) }
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
println(part1(realInput))
// println(part2(realInput))
