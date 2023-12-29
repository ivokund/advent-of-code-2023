import java.io.File

val testInput = """
1,0,1~1,2,1~A
0,0,2~2,0,2~B
0,2,3~2,2,3~C
0,0,4~0,2,4~D
2,0,5~2,2,5~E
0,1,6~2,1,6~F
1,1,8~1,1,9~G
""".trimIndent().lines()

data class Coord(val x: Int, val y: Int, val z: Int) {
    override fun toString(): String {
        return "$x,$y,$z"
    }
}

data class Brick(val from: Coord, val to: Coord, val name: String?) {

    override fun toString(): String {
        return name ?: "${from}~${to}"
    }

    fun moveOneLower(): Brick {
        return Brick(Coord(from.x, from.y, from.z - 1), Coord(to.x, to.y, to.z - 1), name)
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
    val brick1 = Brick(Coord(0, 0, 1), Coord(0, 0, 1), null)
    val brick2 = Brick(Coord(0, 0, 2), Coord(0, 0, 2), null)
    val brick3 = Brick(Coord(0, 0, 4), Coord(0, 0, 4), null)

    val snap = Snapshot(setOf(brick1, brick2, brick3))

    assert(brick2.moveOneLower() == brick1)
    assert(!snap.canBrickExist(brick1.moveOneLower()))
    assert(!snap.canBrickExist(brick2.moveOneLower()))
    assert(snap.canBrickExist(brick3.moveOneLower()))
}
test()


data class Snapshot(val bricks: Set<Brick>) {

    fun createWithout(brick: Brick): Snapshot {
        return Snapshot(bricks.minusElement(brick))
    }

    fun getFallingBricksIfIWouldRemoveBrick(brickToRemove: Brick): Set<Brick> {
        val snapWithout = createWithout(brickToRemove)

        val looseBricks = snapWithout.bricks.filter {
            if (it.from.z == brickToRemove.to.z + 1) {
                snapWithout.canBrickMoveLower(it)
            } else {
                false
            }
        }.toSet()
        return looseBricks
    }


    fun settle(): Snapshot {
        var snapshot = this

        val unsettledStack = bricks.toMutableSet()
        while (unsettledStack.size > 0) {
            val lowestBrick = unsettledStack.minBy { it.from.z }
            unsettledStack.remove(lowestBrick)
            snapshot = snapshot.applyGravity(lowestBrick)
        }
        return snapshot
    }

    fun canBrickMoveLower(brick: Brick): Boolean {
        val newSnap = createWithout(brick)
        return newSnap.canBrickExist(brick.moveOneLower())
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

        val snapWithoutBrick = createWithout(brick)

        var lowestBrick = brick

        while (snapWithoutBrick.canBrickExist(lowestBrick.moveOneLower())) {
            lowestBrick = lowestBrick.moveOneLower()
        }

        val newSnap = Snapshot(snapWithoutBrick.bricks.plusElement(lowestBrick))

        return newSnap
    }

    fun brickIsFree(brick: Brick): Boolean {
        return bricks.none { it.intersects(brick) }
    }

    fun getLooseBricks(): Set<Brick> {
        return bricks.filter {
            val snapWithout = createWithout(it)
            snapWithout.canBrickExist(it.moveOneLower())
        }.toSet()
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
        val (coord1, coord2) = linePart.split("~").take(2)
            .map { it.split(",").map { it.toInt() } }
        val name = linePart.split("~").getOrNull(2)
        Brick(
            Coord(coord1[0], coord1[1], coord1[2]),
            Coord(coord2[0], coord2[1], coord2[2]),
            name
        )
    }.toSet()
    return Snapshot(bricks)
}

fun part1(lines: List<String>): Int {
    val snapshot = parse(lines)

    val settledSnapshot = snapshot.settle()
    return settledSnapshot.bricks.filter {
        val toFall = settledSnapshot.getFallingBricksIfIWouldRemoveBrick(it)
        toFall.isEmpty()
    }.size
}


fun part2(lines: List<String>): Int {
    var snapshot = parse(lines)

    val settled = snapshot.settle()
    val topBricksByLocation = mutableMapOf<String, MutableList<String>>()


    val chainReactionBricks = mutableSetOf<Brick>()

//
//    settled.bricks.forEach {
//        topBricksByLocation.set(it.toString(), mutableListOf())
//        println("checking brick $it")
//        val snapWithoutBrick = Snapshot(settled.bricks.minusElement(it)).recalcSettled()
//        val unsettledCount = snapWithoutBrick.bricks.count { !it.settled }
//
//        if (unsettledCount > 0) {
//            chainReactionBricks.add(it)
//        }
//        snapWithoutBrick.bricks.filter { brick -> !brick.settled }.forEach { brick ->
//            topBricksByLocation[it.toString()]!!.add(brick.toString())
//        }
//    }

    fun getCount(brickId: String): Int {
        println(" $brickId would cause itself to fall plus sum of:")
        return 1 + topBricksByLocation[brickId]!!.sumOf { getCount(it) }
    }

    chainReactionBricks.forEach {
        println("=== GETTING COUNT FOR $it")
        println(">> " + getCount(it.toString()))
    }

    println(topBricksByLocation)
    return 0
}

println("--- test input")
println(part1(testInput))
// println(part2(testInput))

println("--- real input")
println(part1(realInput)) // 522
// println(part2(realInput))

// 516