import java.io.File

val testInput = """
1,0,1~1,2,1
0,0,2~2,0,2
0,2,3~2,2,3
0,0,4~0,2,4
2,0,5~2,2,5
0,1,6~2,1,6
1,1,8~1,1,9
""".trimIndent().lines()

data class Cube(val x: Int, val y: Int, val z: Int) {
}
data class Brick(val cubes: Set<Cube>) {
    override fun toString(): String {
        return "[${cubes.minBy { it.x }.x},${cubes.minBy { it.y }.y},${cubes.minBy { it.z }.z}~${cubes.maxBy { it.x }.x},${cubes.maxBy { it.y }.y},${cubes.maxBy { it.z }.z}]"
    }
    fun isDirectlyUnder(upperBrick: Brick): Boolean {
        return cubes.any { cube ->
            upperBrick.cubes.any {
                it.x == cube.x && it.y == cube.y && cube.z == it.z - 1
            }
        }
    }

    fun moveOneLower(): Brick {
        val newBrick = Brick(cubes.map { Cube(it.x, it.y, it.z - 1) }.toSet())
        return newBrick
    }

    fun intersects(other: Brick): Boolean {
        return cubes.any { cube ->
            other.cubes.any {
                it.x == cube.x && it.y == cube.y && cube.z == it.z
            }
        }
    }

    fun takesPosition(cube: Cube): Boolean {
        return cubes.contains(cube)
    }
}

fun test() {
    val brick1 = Brick(setOf(Cube(0, 0, 1)))
    val brick2 = Brick(setOf(Cube(0, 0, 2)))
    val brick3 = Brick(setOf(Cube(0, 0, 4)))

    val snap = Snapshot(setOf(brick1, brick2, brick3))

    assert(brick1.isDirectlyUnder(brick2))
    assert(brick2.moveOneLower() == brick1)
    assert(!snap.canBrickExist(brick1.moveOneLower()))
    assert(!snap.canBrickExist(brick2.moveOneLower()))
    assert(snap.canBrickExist(brick3.moveOneLower()))

    // todo: vertical slices test case

}
test()


data class Snapshot(val bricks: Set<Brick>) {

    fun settle(): Snapshot {
        var settled = false
        var snapshot = this

        while (!settled) {
            val brick = snapshot.getFirstMovableBrick()
            if (brick == null) {
                settled = true
            } else {
                println("- moving brick $brick")
                snapshot = snapshot.applyGravity(brick)
//                snapshot.draw()
            }
        }
        return snapshot
    }
    fun canBrickExist(brick: Brick): Boolean {
        if (brick.cubes.any { it.z < 1}) {
            return false
        }
        if (!brickIsFree(brick)) {
            return false
        }
        return true
    }

    fun applyGravity(brick: Brick): Snapshot {
        return Snapshot(bricks.map {
            if (it == brick) {
                it.moveOneLower()
            } else {
                it
            }
        }.toSet())
    }

    fun brickIsFree(brick: Brick): Boolean {
        return bricks.none { it.intersects(brick) }
    }

    fun getFirstMovableBrick(): Brick? {
        return bricks.firstOrNull {
            Snapshot(bricks.minusElement(it))
                .canBrickExist(it.moveOneLower())
        }
    }

    fun draw() {
        val out: MutableList<String> = mutableListOf()
        val xMin = bricks.minOf { it.cubes.map { it.x }.min() }
        val yMin = bricks.minOf { it.cubes.map { it.y }.min() }
        val zMin = 0
        val yMax = bricks.maxOf { it.cubes.map { it.y }.max() }
        val xMax = bricks.maxOf { it.cubes.map { it.x }.max() }
        val zMax = bricks.maxOf { it.cubes.map { it.z }.max() }

        println("x: $xMin..$xMax, y: $yMin..$yMax, z: $zMin..$zMax")

        for (z in zMax downTo zMin) {
            var line = ""
            for (x in xMin..xMax) {

                val matchingBricks = mutableSetOf<Brick>()
                for (y in yMin..yMax) {
                    val matching = bricks.filter { it.takesPosition(Cube(x, y, z)) }
//                    println("x: $x, y: $y, z: $z, matching: ${matching.size}")
                    matchingBricks.addAll(matching)
                }
//                println("matchingBricks: ${matchingBricks.size}")

                val brickCount = matchingBricks.size
                line += if (brickCount > 0) brickCount else if (z == 0) "-" else "."
            }
            out.add(line + " z: $z")
        }
        println(out.joinToString("\n"))
    }
}

val realInput = File("day22/input.txt").readLines()

fun parse(line: List<String>): Snapshot {
    val bricks = line.map { linePart ->
        val cubes = mutableSetOf<Cube>()
        val (coord1, coord2) = linePart.split("~")
            .map { it.split(",").map { it.toInt() } }
        for (x in coord1[0]..coord2[0]) {
            for (y in coord1[1]..coord2[1]) {
                for (z in coord1[2]..coord2[2]) {
                    cubes.add(Cube(x, y, z))
                }
            }
        }
        Brick(cubes)
    }.toSet()
    return Snapshot(bricks)
}

fun part1(lines: List<String>): Int {
    var snapshot = parse(lines)

    val settled = snapshot.settle()
    println("settled")

    return settled.bricks.filter {
        println("checking brick $it")
        val snapWithoutBrick = Snapshot(settled.bricks.minusElement(it))
        snapWithoutBrick.getFirstMovableBrick() == null
    }.size
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
 println(part1(realInput))
// println(part2(realInput))
