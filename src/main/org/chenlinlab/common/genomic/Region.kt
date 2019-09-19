package org.chenlinlab.common.genomic

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


open class Region(val chromosome: String, startSite: Int, endSite: Int, strand: Char): Comparable<Region> {
    var startSite: Int = 0
        set(value) {
            require(value > 0) {"startSite should be positive, instead of ${value}"}
            field = value
        }

    var endSite: Int = 0
    set(value) {
        require(value >= this.startSite) {"StartSite ${this.startSite} > EndSite ${value}"}
        field = value
    }

    var strand: Char = '*'
    set(value) {
        field = when (value in arrayOf('+', '-')) {
            true -> value
            false -> '*'
        }
    }

    init {
        this.startSite = startSite
        this.endSite = endSite
        this.strand = strand
    }


    override fun hashCode(): Int {
        return Objects.hash(listOf(this.chromosome, this.startSite, this.endSite))
    }


    override fun equals(other: Any?): Boolean {
        return this.hashCode() == other.hashCode()
    }


    protected open fun obj2String(): String {
        return when(this.strand) {
            '*' ->  "${this.chromosome}\t${this.startSite}\t${this.endSite}"
            else ->  "${this.chromosome}\t${this.startSite}\t${this.endSite}\t${this.strand}"
        }
    }


    override fun toString(): String {
        return this.obj2String()
    }


    override fun compareTo(other: Region): Int {
        // the comparison of chromosome != 0, just return it, otherwise do more comparison

        return if (this.chromosome == other.chromosome) {
            0
        } else {
            if (this.startSite < other.startSite) {
                -1
            } else if (this.startSite > other.startSite) {
                1
            } else if (this.endSite < other.endSite) {
                -1
            } else if (this.endSite > other.endSite) {
                1
            } else {
                0
            }
        }
    }


    fun length(): Int {
        return this.endSite - this.startSite
    }
}

