package basic

/**
 * This is a basic bed class for one line of bed format message
 * For example, chr1, 100, 1000, name, score/other label, strand/other message
 * @author Zhang Yiming
 * @since 2018.2.9
 */

import java.lang.Math
import java.util.*


/**
 * Combined all the input strings into a whole file path
 * @author Zhang Yiming
 * @since 2018.2.10
 */

class BasicBed
/**
 * constructors for BasicBed
 * @param chromosome: chromosome of a genomic position
 * @param startSite: first base of a genomic position
 * @param endSite: last base of a genomic position
 * @throws BedFormatError
 */
@Throws(BedFormatError::class)
constructor(chromosome: String, startSite: Int, endSite: Int) : Comparable<BasicBed>, Cloneable {

    private var id: Int = 0
    private var chromosome: String? = null
    private var startSite: Int = 0
    private var endSite: Int = 0
    private var name: String? = null
    private var score: String? = null
    private var strand: String? = null
    private var otherMessage: String? = null

    // getters
    var chrom: String
        get() = String(this.chromosome)
        set(chromosome) {
            this.chromosome = chromosome
        }

    var start: Int
        get() = Integer.valueOf(this.startSite)
        set(startSite) {
            if (startSite > 0) {
                this.startSite = startSite
            }
        }

    var end: Int
        get() = Integer.valueOf(this.endSite)
        @Throws(BedFormatError::class)
        set(endSite) = if (endSite >= this.startSite) {
            this.endSite = endSite
        } else {
            val msg = String.format(
                    "The id is %s, the endSite < startSite: %s\t%d\t%d\n",
                    this.id, this.chromosome, this.startSite, endSite
            )
            throw BedFormatError(msg)
        }

    var other: String
        get() = String(this.otherMessage)
        set(otherMessage) {
            this.otherMessage = otherMessage
        }

    val length: Int
        get() = Integer.valueOf(this.endSite) - Integer.valueOf(this.startSite)

    val bed: String
        get() {
            var newString = String.format("%s\t%d\t%d", this.chromosome, this.startSite, this.endSite)
            if (this.name !== "") {
                newString += "\t" + this.name!!
            }
            if (this.score !== "") {
                newString += "\t" + this.score!!
            }
            if (this.strand !== "") {
                newString += "\t" + this.strand!!
            }
            if (this.otherMessage !== "") {
                newString += "\t" + this.otherMessage!!
            }
            return newString
        }

    val basicBed: String
        get() = String.format("%s\t%d\t%d", this.chromosome, this.startSite, this.endSite)


    // set default value to all parameters;
    init {
        this.chromosome = ""
        this.startSite = 0
        this.endSite = 0
        this.name = ""
        this.score = ""
        this.strand = ""
        this.otherMessage = ""
    }


    init {
        setId()
        chrom = chromosome
        start = startSite
        end = endSite
    }

    /**
     * constructors for BasicBed
     * @param chromosome: chromosome of a genomic position
     * @param startSite: first base of a genomic position
     * @param endSite: last base of a genomic position
     * @param name: name of this genomic posiiton
     * @throws BedFormatError
     */
    @Throws(BedFormatError::class)
    constructor(chromosome: String, startSite: Int, endSite: Int, name: String) : this(chromosome, startSite, endSite) {
        setName(name)
    }

    /**
     * constructors for BasicBed
     * @param chromosome: chromosome of a genomic position
     * @param startSite: first base of a genomic position
     * @param endSite: last base of a genomic position
     * @param name: name of this genomic posiiton
     * @param score: score in the official bed format or other customized label, both in string format
     * @throws BedFormatError
     */
    @Throws(BedFormatError::class)
    constructor(chromosome: String, startSite: Int, endSite: Int, name: String, score: String) : this(chromosome, startSite, endSite, name) {
        setScore(score)
    }

    /**
     * constructors for BasicBed
     * @param chromosome: chromosome of a genomic position
     * @param startSite: first base of a genomic position
     * @param endSite: last base of a genomic position
     * @param name: name of this genomic posiiton
     * @param score: score in the official bed format or other customized label, both in string format
     * @param strand: strand of this genomic position
     * @throws BedFormatError
     */
    @Throws(BedFormatError::class)
    constructor(chromosome: String, startSite: Int, endSite: Int, name: String, score: String, strand: String) : this(chromosome, startSite, endSite, name, score) {
        setStrand(strand)
    }

    /**
     * constructors for BasicBed
     * @param chromosome: chromosome of a genomic position
     * @param startSite: first base of a genomic position
     * @param endSite: last base of a genomic position
     * @param name: name of this genomic posiiton
     * @param score: score in the official bed format or other customized label, both in string format
     * @param strand: strand of this genomic position
     * @param otherMessage: other appended message
     * @throws BedFormatError
     */
    @Throws(BedFormatError::class)
    constructor(chromosome: String, startSite: Int, endSite: Int, name: String, score: String, strand: String, otherMessage: String) : this(chromosome, startSite, endSite, name, score, strand) {
        other = otherMessage
    }

    // setters
    private fun setId() {
        this.id = nextId
        nextId++
    }

    fun setName(name: String) {
        this.name = name
    }

    // override setScore for different possibilities
    fun setScore(score: String) {
        this.score = score
    }

    fun setScore(score: Int) {
        this.score = String.valueOf(score)
    }

    fun setScore(score: Float) {
        this.score = String.valueOf(score)
    }

    @Throws(BedFormatError::class)
    fun setStrand(strand: String) {
        if (strand.equals("-") || strand.equals("+") || strand.equals(".")) {
            this.strand = strand
        } else {
            val msg = String.format("Input strand is %s, not identical with +, -, .", strand)
            throw BedFormatError(msg)
        }
    }


    fun getName(): String {
        // protect the internal values with clone
        return String(this.name)
    }

    fun getScore(): String {
        return String(this.score)
    }

    fun getStrand(): String {
        return String(this.strand)
    }

    fun getId(): Int {
        return Integer.valueOf(this.id)
    }


    /**
     * @override hashCode, equals and toString
     * hashCode() and equals() just determine if the chromosome, startSite, endSite and strands are identical
     * toString return all of the values
     */
    @Override
    fun hashCode(): Int {
        return if (this.strand === "") {
            Objects.hash(this.chromosome, this.startSite, this.endSite)
        } else Objects.hash(this.chromosome, this.startSite, this.endSite, this.strand)
    }

    /**
     * 基础判断
     * @param otherObject
     * @return
     */
    private fun basicEquals(otherObject: Object?): Boolean? {
        /*
         * This function perform the basic tests between two objects, and finish the initial tests
         */

        // first test if object are identical
        if (this === otherObject) return true

        // must return false if otherObject is null
        if (otherObject == null) return false

        // if classes don't match return false
        return if (this.getClass() !== otherObject!!.getClass()) false else null

        // if all tests finished, and nothing returned,
        // return a null, to tell the next function to do the next tests

    }


    /**
     * 判断两个位点是否一致，其他不管
     * @param otherObject
     * @return
     */
    @Override
    fun equals(otherObject: Object): Boolean {
        val basicTest = basicEquals(otherObject)

        if (basicTest == null) {
            // after everything matches, generate a new item with identical class, and test
            val other = otherObject as BasicBed
            val bIsEqual = (Objects.equals(this.chromosome, other.chromosome)
                    && this.startSite == other.startSite
                    && this.endSite == other.endSite)
            return if (this.strand === "") {
                bIsEqual
            } else {
                bIsEqual && this.strand === other.strand
            }

        } else {
            return basicTest
        }

    }

    @Override
    fun toString(): String {
        // format the final string
        return String.format(
                "%s[chromosome=%s, startSite=%d, endSite=%d, name=%s, score=%s, strand=%s, otherMessage=%s]",
                this.getClass().getName(), chromosome, startSite, endSite, name, score,
                strand, otherMessage
        )
    }


    /**
     * 判断两个位点是否完全一致
     * isSame() determine if two BasicBed are totally identical
     * means everything in it is identical
     * @param otherObject: another BasicBed class object
     * @return boolean: true for identical, false for not
     */
    fun isSame(otherObject: Object): Boolean {
        val basicTest = basicEquals(otherObject)

        if (basicTest == null) {
            // after everything matches, generate a new item with identical class, and test
            val other = otherObject as BasicBed

            return (Objects.equals(this.chromosome, other.chromosome)
                    && this.startSite == other.startSite
                    && this.endSite == other.endSite
                    && this.name === other.name
                    && this.score === other.score
                    && this.strand === other.strand
                    && this.otherMessage === other.otherMessage)
        } else {
            return basicTest
        }
    }


    /**
     * Collection用来比较大小排序的部分
     * @override Compare to only determine the position of two genomic locations
     * @param other
     * @return
     */
    @Override
    fun compareTo(other: BasicBed): Int {
        // the comparison of chromosome != 0, just return it, otherwise do more comparison
        val comparesionOfChromosome = this.chromosome!!.compareToIgnoreCase(other.chromosome)
        return if (comparesionOfChromosome != 0) {
            comparesionOfChromosome
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

    /**
     * 克隆，但是貌似没啥用
     * @return
     */
    @Override
    fun clone(): BasicBed {
        val clone: BasicBed
        try {
            clone = super.clone() as BasicBed
        } catch (ex: CloneNotSupportedException) {
            throw RuntimeException("BasicBed clone error", ex)
        }

        clone.chromosome = chrom
        clone.startSite = start
        clone.endSite = end
        clone.name = getName()
        clone.score = getScore()
        clone.id = getId()
        clone.strand = getStrand()
        clone.otherMessage = other
        return clone
    }


    /**
     * 测试这个bed位点是否在另一个位点的上游
     * test if this region is totally upstream of other
     * eg: chr1:100-200 is upstream of chr1:201-300
     * @return: true means it is; false means no
     */
    fun isUpstream(other: BasicBed): Boolean {
        val comparesion = this.isOverlapped(other).toInt()
        return if (comparesion == -1) true else false
    }


    /**
     * 测试这个位点是否在另一个位点的下游
     * test if this region is totally downstream of other
     * eg: chr1:100-200 is downstream of chr1:1-99
     * @param other: another BasicBed
     * @return: true means it is; false means no
     */
    fun isDownstream(other: BasicBed): Boolean {
        val comparesion = this.isOverlapped(other).toInt()
        return if (comparesion == 1) true else false
    }


    /**
     * 测试两个位点之间是否存在重合位点，0代表有，1代表这个在另一个的下游，-1代表上游
     * test if two locations have overlaps, if does, return distance between two locations, if not return 0
     * @param other
     * @return 0: two region have overlap; 1: first one is downstream of second; -1 first one is upstream of second
     */
    fun isOverlapped(other: BasicBed): Integer {
        // the comparison of chromosome != 0, just return it, otherwise do more comparison
        val comparesionOfChromosome = this.chromosome!!.compareToIgnoreCase(other.chromosome)
        return if (comparesionOfChromosome != 0) {
            comparesionOfChromosome
        } else if (this.startSite > other.endSite) {
            1
        } else if (this.endSite < other.startSite) {
            -1
        } else {
            0
        }
    }


    /** 返回两个位点间的距离，按照bedops的规则进行计算，多加减了个1
     * return the distance between two bed locations
     * @param other: another BasicBed class object
     * @return: integer number, the distance between the edges of two locations
     */
    fun distanceTo(other: BasicBed): Integer? {
        return if (this.chromosome!!.equals(other.chromosome)) {
            if (this.endSite <= other.startSite) {
                other.startSite - this.endSite + 1
            } else if (this.startSite >= other.endSite) {
                other.endSite - this.startSite - 1
            } else {
                0
            }
        } else null
    }


    /**
     * 返回两个位点中心之间的距离
     * return the distance between the center of two bed locations
     * @param other: another BasicBed class object
     * @return: float number, the distance between the center of two bed locations
     * -1 represents these locations don't in same chromosome;
     */
    fun distanceToCenter(other: BasicBed): Float {
        return if (!this.chromosome!!.equals(other.chromosome)) -1f else ((this.startSite + this.endSite - (other.startSite + other.endSite)) / 2).toFloat()
    }


    /**
     * 比较两个位点间的重合程度，-1没重合，其他为重合的bp数
     * compare the coverage between two bed locations
     * @param other: another BasicBed class object
     * @return: integer number, the coverage, 0 represent these two locations don't have any overlap
     */
    fun coverageTo(other: BasicBed): Int {
        // the comparison of chromosome != 0, just return it, otherwise do more comparison
        val comparesionOfChromosome = this.chromosome!!.compareToIgnoreCase(other.chromosome)
        return if (comparesionOfChromosome != 0) {
            -1
        } else {
            if (this.endSite >= other.startSite && this.startSite <= other.endSite) {
                Math.min(this.endSite, other.endSite) - Math.max(this.startSite, other.startSite)
            } else {
                -1
            }
        }
    }


    /**
     * 比较两个位点间重合的百分比，以第一个的位点的大小为基准
     * compare two genomic locations, and return the coverage by percent
     * @param other: another BasicBed object
     * @return: -1 - because of the original BasicBed do not have any length, endSite - startSite == 0
     * otherwise, return a float number, the coverage / total length of this BasicBed
     */
    fun coverageToPercent(other: BasicBed): Float {
        val coverage = this.coverageTo(other)
        if (coverage == -1) {
            return -1f
        }
        try {
            return Math.abs(coverage) as Float / (this.endSite - this.startSite)
        } catch (e: java.lang.ArithmeticException) {
            return -1f
        }

    }


    /**
     * 融合两个不同的位点
     * merge two basic bed region into one, if they have overlaps
     * @param other: another BasicBed class object
     * @return: Optional object, null to none overlaps, or a brand new BasicBed
     */
    fun merge(other: BasicBed): Optional<BasicBed> {
        var results = Optional.empty()
        if (coverageTo(other) != 0) {
            try {
                results = Optional.of(BasicBed(this.chromosome, Math.min(this.startSite, other.startSite), Math.max(this.endSite, other.endSite)))
            } catch (e: BedFormatError) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }

        return results
    }


    /**
     * 找出这个位点比另一个位点差异的部分，返回列表
     * return the different part of this bed that compare to another
     * @param other
     * @return
     */
    fun difference(other: BasicBed): ArrayList<BasicBed> {
        val results = ArrayList<BasicBed>()

        if (isOverlapped(other) == 0) {
            try {

                if (this.startSite < other.startSite) {
                    if (this.endSite < other.endSite) {
                        results.add(BasicBed(this.chromosome, this.startSite, other.startSite))
                    } else if (this.endSite > other.endSite) {
                        results.add(BasicBed(this.chromosome, other.endSite, this.endSite))
                    }
                } else if (this.startSite > other.startSite) {
                    results.add(BasicBed(this.chromosome, other.endSite, this.endSite))
                }

            } catch (e: RuntimeException) {
                e.printStackTrace()
            } catch (e: BedFormatError) {
                e.printStackTrace()
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
    fun symmetricDifference(other: BasicBed): ArrayList<BasicBed> {
        val results = ArrayList<BasicBed>()

        if (isOverlapped(other) == 0) {
            try {
                results.add(BasicBed(
                        this.chromosome,
                        Math.min(this.startSite, other.startSite),
                        Math.max(this.startSite, other.startSite)
                ))

                results.add(BasicBed(
                        this.chromosome,
                        Math.min(this.endSite, other.endSite),
                        Math.max(this.endSite, other.endSite)
                ))
            } catch (e: BedFormatError) {
                e.printStackTrace()
            }

        }
        return results
    }


    /**
     * 找出两个位点间重合的部分
     * get common part of two genomic locations
     */
    fun intersect(other: BasicBed): BasicBed? {
        try {
            if (isOverlapped(other) == 0) {
                return BasicBed(this.chromosome, Math.max(this.startSite, other.startSite), Math.min(this.endSite, other.endSite))
            }
        } catch (e: BedFormatError) {
            e.printStackTrace()
        }

        return null
    }


    /**
     * 找出将两个位点分块
     * return patitions by symmetric difference, equals to the sum of symmetriDifference and intersect
     */
    fun partition(other: BasicBed): ArrayList<BasicBed> {
        val results = ArrayList<BasicBed>()
        if (isOverlapped(other) == 0) {

            val site = ArrayList(Arrays.asList(
                    this.startSite, this.endSite,
                    other.startSite, other.endSite
            ))

            Collections.sort(site)

            try {
                for (i in 1 until site.size()) {
                    results.add(BasicBed(
                            this.chromosome,
                            site.get(i - 1),
                            site.get(i)
                    ))

                }
            } catch (e: BedFormatError) {
                e.printStackTrace()
            }

        }

        return results
    }


    /**
     * 将一个位点切割成小块
     * chop whole region into pieces by size of input, default 1
     */
    fun chop(): ArrayList<BasicBed> {
        return this.chop(1)
    }

    /**
     * @重载：添加支持chop的大小
     * @param chop
     * @return
     */
    fun chop(chop: Int): ArrayList<BasicBed> {
        val results = ArrayList<BasicBed>()

        var i = this.startSite
        while (i < this.endSite) {
            var j = i + chop
            try {
                if (j > this.endSite) {
                    j = this.endSite
                }
                results.add(BasicBed(this.chromosome, i, j))
            } catch (e: BedFormatError) {
                e.printStackTrace()
            }

            i = j
        }
        return results
    }

    /**
     * @重载：添加间隔大小
     * @param chop
     * @param stagger
     * @return
     */
    fun chop(chop: Int, stagger: Int): ArrayList<BasicBed> {
        val results = ArrayList<BasicBed>()
        var i = this.startSite
        while (i < this.endSite) {
            var j = i + chop
            try {
                if (j > this.endSite) {
                    j = this.endSite
                }
                results.add(BasicBed(this.chromosome, i, j))
            } catch (e: BedFormatError) {
                e.printStackTrace()
            }

            i += stagger
        }
        return results
    }


    /**
     * 找出两个位点间互补的部分
     * find complement part between bed regions
     * @param other: another BasicBed
     * @return: brand new BasicBed in bed3 format
     */
    fun complement(other: BasicBed): BasicBed? {
        val comparision = this.chromosome!!.compareToIgnoreCase(other.chromosome)
        if (comparision == 0) {
            try {
                return BasicBed(
                        this.chromosome,
                        Math.min(this.endSite, other.endSite),
                        Math.max(this.startSite, other.startSite)
                )
            } catch (e: BedFormatError) {
                e.printStackTrace()
            }

        }
        return null
    }

    companion object {
        private var nextId = 0

        /**
         * A simple comparator, for AttayList comparation
         */
        val compatator: Comparator = object : Comparator<BasicBed>() {
            @Override
            fun compare(thisOne: BasicBed, other: BasicBed): Int {
                val comparesionOfChromosome = thisOne.chromosome!!.compareToIgnoreCase(other.chromosome)
                return if (comparesionOfChromosome != 0) {
                    comparesionOfChromosome
                } else {
                    if (thisOne.startSite < other.startSite) {
                        -1
                    } else if (thisOne.startSite > other.startSite) {
                        1
                    } else {
                        if (thisOne.endSite < other.endSite) {
                            -1
                        } else if (thisOne.endSite > other.endSite) {
                            1
                        } else {
                            0
                        }
                    }
                }
            }
        }
    }
}


