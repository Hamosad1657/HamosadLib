package com.hamosad1657.lib

/** Represents a pipe on a reef. Use the companion object. */
class Pipe private constructor(val letter: Char) {
	companion object {
		val A = Pipe('A')
		val B = Pipe('B')
		val C = Pipe('C')
		val D = Pipe('D')
		val E = Pipe('E')
		val F = Pipe('F')
		val G = Pipe('G')
		val H = Pipe('H')
		val I = Pipe('I')
		val J = Pipe('J')
		val K = Pipe('K')
		val L = Pipe('L')
	}
}

/** Represents one of the levels of a pipe (1->4). Use the companion object. */
class PipeLevel private constructor(val level: Int) {
	companion object {
		val L1 = PipeLevel(1)
		val L2 = PipeLevel(2)
		val L3 = PipeLevel(3)
		val L4 = PipeLevel(4)
	}

	operator fun compareTo(other: PipeLevel): Int = this.level - other.level
}

/** Represents one branch on a reef. */
data class Branch(val pipe: Pipe, val level: PipeLevel)