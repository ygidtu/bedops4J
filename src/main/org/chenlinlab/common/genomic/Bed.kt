package org.chenlinlab.common.genomic

import java.util.*
import kotlin.collections.ArrayList


/**
 * constructors for BasicBed
 * @param chromosome: chromosome of a genomic position
 * @param startSite: first base of a genomic position
 * @param endSite: last base of a genomic position
 */
class Bed(
        chromosome: String,
        startSite: Int,
        endSite: Int,
        name: String? = null,
        score: String? = null,
        strand: Char = '*',
        other: String? = null
): Region(chromosome, startSite, endSite, strand), Cloneable  {

    private var name: String? = null
    private var score: String? = null
    private var otherMessage: String? = null


    init {
        this.name = name
        this.score = score
        this.otherMessage = other
    }


    override fun obj2String(): String {
        var newString = String.format("%s\t%d\t%d", this.chromosome, this.startSite, this.endSite)
        if (!this.name.isNullOrBlank()) {
            newString += "\t" + this.name!!
        }


        if (!this.score.isNullOrBlank()) {
            newString += "\t" + this.score
        }
        if ( this.strand != '*') {
            newString += "\t" + this.strand
        }

        if (this.otherMessage?.isNotEmpty() == true) {
            newString += "\t" + this.otherMessage!!
        }
        return newString
    }



    /**
     * 判断两个位点是否完全一致
     * isSame() determine if two BasicBed are totally identical
     * means everything in it is identical
     * @param other: another BasicBed class object
     * @return boolean: true for identical, false for not
     */
    fun isIdentical(other: Bed): Boolean {
        return this.chromosome == other.chromosome
                && this.startSite == other.startSite
                && this.endSite == other.endSite
                && this.name == other.name
                && this.score == other.score
                && this.strand == other.strand
                && this.otherMessage == other.otherMessage

    }


    /**
     * 测试这个bed位点是否在另一个位点的上游
     * test if this region is totally upstream of other
     * eg: chr1:100-200 is upstream of chr1:201-300
     * @return: true means it is; false means no
     */
    fun isUpstream(other: Bed): Boolean {
        return when(this.chromosome == other.chromosome) {
            true -> this.endSite < other.startSite
            else -> this.chromosome < other.chromosome
        }
    }


    /**
     * 测试这个位点是否在另一个位点的下游
     * test if this region is totally downstream of other
     * eg: chr1:100-200 is downstream of chr1:1-99
     * @param other: another BasicBed
     * @return: true means it is; false means no
     */
    fun isDownstream(other: Bed): Boolean {
        return when(this.chromosome == other.chromosome) {
            true -> this.startSite > other.endSite
            else -> this.chromosome > other.chromosome
        }
    }


    /**
     * 测试两个位点之间是否存在重合位点，0代表有，1代表这个在另一个的下游，-1代表上游
     * test if two locations have overlaps, if does, return distance between two locations, if not return 0
     * @param other
     * @return 0: two region have overlap; 1: first one is downstream of second; -1 first one is upstream of second
     */
    fun isOverlap(other: Bed): Boolean {
        return this.chromosome == other.chromosome &&
                this.startSite <= other.endSite &&
                this.endSite >= other.startSite
    }


    /** 返回两个位点间的距离，按照bedops的规则进行计算，多加减了个1
     * return the distance between two bed locations
     * @param other: another BasicBed class object
     * @return: integer number, the distance between the edges of two locations
     */
    fun distanceTo(other: Bed): Int? {
        return when(this.chromosome == other.chromosome) {
            true -> when {
                this.isUpstream(other) -> other.startSite - this.endSite
                this.isDownstream(other) -> this.startSite - other.endSite
                else -> 0
            }
            else -> null
        }
    }


    /**
     * 返回两个位点中心之间的距离
     * return the distance between the center of two bed locations
     * @param other: another BasicBed class object
     * @return: float number, the distance between the center of two bed locations
     * -1 represents these locations don't in same chromosome;
     */
    fun distanceToCenter(other: Bed): Float? {
        return when (this.chromosome != other.chromosome) {
            true -> null
            else -> (this.startSite + this.endSite).toFloat() - (other.startSite + other.endSite).toFloat() / 2
        }
    }


    /**
     * 比较两个位点间的重合程度，-1没重合，其他为重合的bp数
     * compare the coverage between two bed locations
     * @param other: another BasicBed class object
     * @return: integer number, the coverage, 0 represent these two locations don't have any overlap
     */
    fun coverage(other: Bed): Int? {

        return when {
            this.chromosome != other.chromosome -> null
            this.isOverlap(other) -> minOf(this.endSite, other.endSite) - maxOf(this.startSite, other.startSite)
            else -> -1
        }

    }


    /**
     * 比较两个位点间重合的百分比，以第一个的位点的大小为基准
     * compare two genomic locations, and return the coverage by percent
     * @param other: another BasicBed object
     * @return: -1 - because of the original BasicBed do not have any length, endSite - startSite == 0
     * otherwise, return a float number, the coverage / total length of this BasicBed
     */
    fun coverageToPercent(other: Bed): Float? {
        val coverage = this.coverage(other)

        return when(coverage) {
            null -> null
            -1 -> -1f
            else -> coverage.toFloat() / this.length()
        }
    }


    /**
     * 融合两个不同的位点
     * merge two basic bed region into one, if they have overlaps
     * @param other: another BasicBed class object
     * @return: Optional object, null to none overlaps, or a brand new BasicBed
     */
    fun merge(other: Bed): Bed? {
        if (this.isOverlap(other)) {
            return(Bed(this.chromosome, minOf(this.startSite, other.startSite), maxOf(this.endSite, other.endSite)))
        }
        return null
    }


    /**
     * 找出这个位点比另一个位点差异的部分，返回列表
     * return the different part of this bed that compare to another
     * @param other
     * @return
     */
    fun difference(other: Bed): ArrayList<Bed> {
        val results = ArrayList<Bed>()

        if (this.isOverlap(other)) {
            if (this.startSite < other.startSite) {
                if (this.endSite < other.endSite) {
                    results.add(Bed(this.chromosome, this.startSite, other.startSite))
                } else if (this.endSite > other.endSite) {
                    results.add(Bed(this.chromosome, other.endSite, this.endSite))
                }
            } else if (this.startSite > other.startSite) {
                results.add(Bed(this.chromosome, other.endSite, this.endSite))
            }
        }
        return results
    }


    /**
     * 找出两个位点间互为不同的部分
     * return symmetric difference part of two genomic locations
     * @param other
     * @return
     */
    fun symmetricDifference(other: Bed): ArrayList<Bed> {
        val results = ArrayList<Bed>()

        if (this.isOverlap(other)) {

            results.add(
                Bed(
                    this.chromosome,
                    minOf(this.startSite, other.startSite),
                    maxOf(this.startSite, other.startSite)
                )
            )

            results.add(
                Bed(
                    chromosome = this.chromosome,
                    startSite = minOf(this.endSite, other.endSite),
                    endSite = maxOf(this.endSite, other.endSite)
                )
            )


        }
        return results
    }


    /**
     * 找出两个位点间重合的部分
     * get common part of two genomic locations
     */
    fun intersect(other: Bed): Bed? {

        if (this.isOverlap(other)) {
            return Bed(this.chromosome, Math.max(this.startSite, other.startSite), Math.min(this.endSite, other.endSite))
        }

        return null
    }


    /**
     * 找出将两个位点分块
     * return partitions by symmetric difference, equals to the sum of symmetriDifference and intersect
     */
    fun partition(other: Bed): ArrayList<Bed> {
        val results = ArrayList<Bed>()
        if (this.isOverlap(other)) {

            val site = ArrayList(Arrays.asList(
                    this.startSite, this.endSite,
                    other.startSite, other.endSite
            ))

            site.sort()

            for (i in 1 until site.size step 2) {
                results.add(Bed(
                        this.chromosome,
                        site[i - 1],
                        site[i]
                ))

            }
        }

        return results
    }


    /**
     * @重载：添加支持chop的大小
     * @param chop
     * @return
     */
    fun chop(chop: Int = 1, stagger: Int?=null): ArrayList<Bed> {
        val results = ArrayList<Bed>()

        var i = this.startSite
        while (i < this.endSite) {
            var j = i + chop

            if (j > this.endSite) {
                j = this.endSite
            }
            results.add(Bed(this.chromosome, i, j))

            if (stagger != null) {
                i += stagger
            } else {
                i = j
            }

        }
        return results
    }


    /**
     * 找出两个位点间互补的部分
     * find complement part between bed regions
     * @param other: another BasicBed
     * @return: brand new BasicBed in bed3 format
     */
    fun complement(other: Bed): Bed? {

        return when {
            this.chromosome == other.chromosome -> Bed(
                    this.chromosome,
                    minOf(this.endSite, other.endSite),
                    maxOf(this.startSite, other.startSite)
            )
            else -> null
        }
    }
}


