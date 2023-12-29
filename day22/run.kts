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

data class Coord(val x: Int, val y: Int, val z: Int) {
    override fun toString(): String {
        return "$x,$y,$z"
    }
}

data class Brick(val from : Coord, val to: Coord) {

    var settled = false
    override fun toString(): String {
        return "${from}~${to}"
    }

    fun moveOneLower(): Brick {
        return Brick(Coord(from.x, from.y, from.z - 1), Coord(to.x, to.y, to.z - 1))
    }

    fun intersects(other: Brick): Boolean {

        for (x in from.x..to.x) {
            for (y in from.y..to.y) {
                for (z in from.z..to.z) {
                    if (other.occupies(Coord(x, y, z))) {
                        return true
                    }
                }
            }
        }

        return false
    }

    fun occupies(coord: Coord): Boolean {
        return from.x <= coord.x && from.y <= coord.y && from.z <= coord.z &&
                to.x >= coord.x && to.y >= coord.y && to.z >= coord.z
    }
}

fun test() {
    val brick1 = Brick(Coord(0, 0, 1), Coord(0, 0, 1))
    val brick2 = Brick(Coord(0, 0, 2), Coord(0, 0, 2))
    val brick3 = Brick(Coord(0, 0, 4), Coord(0, 0, 4))

    val snap = Snapshot(setOf(brick1, brick2, brick3))

    assert(brick2.moveOneLower() == brick1)
    assert(!snap.canBrickExist(brick1.moveOneLower()))
    assert(!snap.canBrickExist(brick2.moveOneLower()))
    assert(snap.canBrickExist(brick3.moveOneLower()))
}
test()


data class Snapshot(val bricks: Set<Brick>) {

    fun settle(): Snapshot {
        var snapshot = this

        while (snapshot.bricks.count { !it.settled } > 0 ) {
            val brick = snapshot.getLowestUnsettledBrick()
            println("- moving brick $brick")
            snapshot = snapshot.applyGravity(brick!!)
//                snapshot.draw()
        }
        return snapshot
    }
    fun canBrickExist(brick: Brick): Boolean {
        if (brick.from.z < 1) {
            return false
        }
        if (!brickIsFree(brick)) {
            return false
        }
        return true
    }

    fun applyGravity(brick: Brick): Snapshot {

        val snapWithoutBrick = Snapshot(bricks.minusElement(brick))

        var lowestBrick = brick

        while (snapWithoutBrick.canBrickExist(lowestBrick.moveOneLower())) {
            lowestBrick = lowestBrick.moveOneLower()
        }

        lowestBrick.settled = true
        return snapWithoutBrick.bricks.plusElement(lowestBrick).let { Snapshot(it) }
    }

    fun brickIsFree(brick: Brick): Boolean {
        return bricks.none { it.intersects(brick) }
    }

    fun getLowestUnsettledBrick(): Brick? {
        return bricks.filter { !it.settled }.minByOrNull { it.from.z }
    }

    fun recalcSettled(): Snapshot {
        val newBricks = bricks.map {
            it.settled = !Snapshot(bricks.minusElement(it)).canBrickExist(it.moveOneLower())
            it
        }
        return Snapshot(newBricks.toSet())
    }

    fun draw() {
        val out: MutableList<String> = mutableListOf()
        val xMin = bricks.minOf { it.from.x }
        val yMin = bricks.minOf { it.from.y }
        val zMin = 0
        val yMax = bricks.maxOf { it.to.y }
        val xMax = bricks.maxOf { it.to.x }
        val zMax = bricks.maxOf { it.to.z }

        println("x: $xMin..$xMax, y: $yMin..$yMax, z: $zMin..$zMax")

        for (z in zMax downTo zMin) {
            var line = ""
            for (x in xMin..xMax) {

                val matchingBricks = mutableSetOf<Brick>()
                for (y in yMin..yMax) {
                    val matching = bricks.filter { it.occupies(Coord(x, y, z)) }
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
        val (coord1, coord2) = linePart.split("~")
            .map { it.split(",").map { it.toInt() } }
        Brick(
            Coord(coord1[0], coord1[1], coord1[2]),
            Coord(coord2[0], coord2[1], coord2[2])
        )
    }.toSet()
    return Snapshot(bricks)
}

fun part1(lines: List<String>): Int {
    var snapshot = parse(lines)

    val settled = snapshot.settle()


    return settled.bricks.filter {
        println("checking brick $it")
        val snapWithoutBrick = Snapshot(settled.bricks.minusElement(it)).recalcSettled()
        val unsettledCount = snapWithoutBrick.bricks.count { !it.settled }
        unsettledCount== 0
    }.size
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
 println(part1(realInput)) // 522
// println(part2(realInput))

// 516