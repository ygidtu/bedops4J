package basic;
/**
 * This is a basic bed class for one line of bed format message
 * For example, chr1, 100, 1000, name, score/other label, strand/other message
 * @author Zhang Yiming
 * @since 2018.2.9
 *
 */

import java.lang.Math;
import java.util.*;


/**
 * Combined all the input strings into a whole file path
 * @author Zhang Yiming
 * @since 2018.2.10
 *
 */

public class BasicBed implements Comparable<BasicBed>, Cloneable {

    private int id;
    private static int nextId = 0;
    private String chromosome;
    private int startSite;
    private int endSite;
    private String name;
    private String score;
    private String strand;
    private String otherMessage;

    /**
     * A simple comparator, for AttayList comparation
     */
    public final static Comparator compatator = new Comparator<BasicBed>() {
        @Override
        public int compare(BasicBed thisOne, BasicBed other) {
            int comparesionOfChromosome = thisOne.chromosome.compareToIgnoreCase(other.chromosome);
            if (comparesionOfChromosome != 0) {
                return comparesionOfChromosome;
            } else {
                if (thisOne.startSite < other.startSite) {
                    return -1;
                }else if (thisOne.startSite > other.startSite) {
                    return 1;
                } else {
                    if (thisOne.endSite < other.endSite) {
                        return -1;
                    } else if (thisOne.endSite > other.endSite) {
                        return 1;
                    }else {
                        return 0;
                    }
                }
            }
        }
    };


    // set default value to all parameters;
    {
        this.chromosome = "";
        this.startSite = 0;
        this.endSite = 0;
        this.name = "";
        this.score = "";
        this.strand = "";
        this.otherMessage = "";
    }


    /**
     * constructors for BasicBed
     * @param chromosome: chromosome of a genomic position
     * @param startSite: first base of a genomic position
     * @param endSite: last base of a genomic position
     * @throws BedFormatError
     */
    public BasicBed (String chromosome, int startSite, int endSite) throws BedFormatError {
        setId();
        setChrom(chromosome);
        setStart(startSite);
        setEnd(endSite);
    }

    /**
     * constructors for BasicBed
     * @param chromosome: chromosome of a genomic position
     * @param startSite: first base of a genomic position
     * @param endSite: last base of a genomic position
     * @param name: name of this genomic posiiton
     * @throws BedFormatError
     */
    public BasicBed (String chromosome, int startSite, int endSite, String name) throws BedFormatError {
        this(chromosome, startSite, endSite);
        setName(name);
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
    public BasicBed (String chromosome, int startSite, int endSite, String name, String score) throws BedFormatError  {
        this(chromosome, startSite, endSite, name);
        setScore(score);
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
    public BasicBed (String chromosome, int startSite, int endSite, String name, String score, String strand) throws BedFormatError  {
        this(chromosome, startSite, endSite, name, score);
        setStrand(strand);
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
    public BasicBed (String chromosome, int startSite, int endSite, String name, String score, String strand, String otherMessage) throws BedFormatError  {
        this(chromosome, startSite, endSite, name, score, strand);
        setOther(otherMessage);
    }

    // setters
    private void setId() {
        this.id = nextId;
        nextId++;
    }

    public void setChrom (String chromosome) {
        this.chromosome = chromosome;
    }

    public void setStart (int startSite) {
        if (startSite > 0) {
            this.startSite = startSite;
        }
    }

    public void setEnd (int endSite) throws BedFormatError {
        if (endSite >= this.startSite) {
            this.endSite = endSite;
        } else {
            String msg = String.format(
                    "The id is %s, the endSite < startSite: %s\t%d\t%d\n",
                    this.id, this.chromosome, this.startSite, endSite
            );
            throw new BedFormatError(msg);
        }
    }

    public void setName (String name) {
        this.name = name;
    }

    // override setScore for different possibilities
    public void setScore (String score) {
        this.score = score;
    }

    public void setScore (int score) {
        this.score = String.valueOf(score);
    }

    public void setScore (float score) {
        this.score = String.valueOf(score);
    }

    public void setStrand (String strand) throws BedFormatError {
        if (strand.equals("-") || strand.equals("+") || strand.equals(".")) {
            this.strand = strand;
        } else {
            String msg = String.format("Input strand is %s, not identical with +, -, .", strand);
            throw new BedFormatError(msg);
        }
    }

    public void setOther (String otherMessage) {
        this.otherMessage = otherMessage;
    }

    // getters
    public String getChrom(){
        return new String(this.chromosome);
    }


    public String getName() {
        // protect the internal values with clone
        return new String(this.name);
    }

    public int getStart() {
        return Integer.valueOf(this.startSite);
    }

    public int getEnd() {
        return Integer.valueOf(this.endSite);
    }

    public String getScore() {
        return new String(this.score);
    }

    public String getStrand() {
        return new String(this.strand);
    }

    public String getOther() {
        return new String(this.otherMessage);
    }

    public int getId() {
        return Integer.valueOf(this.id);
    }

    public int getLength() { return Integer.valueOf(this.endSite) - Integer.valueOf(this.startSite); };

    public String getBed() {
        String newString = String.format("%s\t%d\t%d", this.chromosome, this.startSite, this.endSite);
        if(this.name != "") {
            newString += "\t" + this.name;
        }
        if(this.score != "") {
            newString += "\t" + this.score;
        }
        if(this.strand != "") {
            newString += "\t" + this.strand;
        }
        if(this.otherMessage != "") {
            newString += "\t" + this.otherMessage;
        }
        return newString;
    }

    public String getBasicBed() {
        return String.format("%s\t%d\t%d", this.chromosome, this.startSite, this.endSite);
    }


    /**
     * @override hashCode, equals and toString
     * hashCode() and equals() just determine if the chromosome, startSite, endSite and strands are identical
     * toString return all of the values
     */
    @Override
    public int hashCode() {
        if (this.strand == "") {
            return Objects.hash(this.chromosome, this.startSite, this.endSite);
        }
        return Objects.hash(this.chromosome, this.startSite, this.endSite, this.strand);
    }

    /**
     * 基础判断
     * @param otherObject
     * @return
     */
    private Boolean basicEquals(Object otherObject) {
        /*
         * This function perform the basic tests between two objects, and finish the initial tests
         */

        // first test if object are identical
        if (this == otherObject) return true;

        // must return false if otherObject is null
        if (otherObject == null) return false;

        // if classes don't match return false
        if (this.getClass() != otherObject.getClass()) return false;

        // if all tests finished, and nothing returned,
        // return a null, to tell the next function to do the next tests
        return null;

    }


    /**
     * 判断两个位点是否一致，其他不管
     * @param otherObject
     * @return
     */
    @Override
    public boolean equals(Object otherObject) {
        Boolean basicTest = basicEquals(otherObject);

        if (basicTest == null) {
            // after everything matches, generate a new item with identical class, and test
            BasicBed other = (BasicBed) otherObject;
            boolean bIsEqual = Objects.equals(this.chromosome, other.chromosome)
                    && this.startSite == other.startSite
                    && this.endSite == other.endSite;
            if (this.strand == "") {
                return bIsEqual;
            } else {
                return bIsEqual && this.strand == other.strand;
            }

        } else {
            return basicTest;
        }

    }

    @Override
    public String toString() {
        // format the final string
        String newString = String.format(
                "%s[chromosome=%s, startSite=%d, endSite=%d, name=%s, score=%s, strand=%s, otherMessage=%s]",
                this.getClass().getName(), this.chromosome, this.startSite, this.endSite, this.name, this.score,
                this.strand, this.otherMessage
        );
        return newString;
    }


    /**
     * 判断两个位点是否完全一致
     * isSame() determine if two BasicBed are totally identical
     * means everything in it is identical
     * @param otherObject: another BasicBed class object
     * @return boolean: true for identical, false for not
     */
    public boolean isSame(Object otherObject) {
        Boolean basicTest = basicEquals(otherObject);

        if (basicTest == null) {
            // after everything matches, generate a new item with identical class, and test
            BasicBed other = (BasicBed) otherObject;

            return Objects.equals(this.chromosome, other.chromosome)
                    && this.startSite == other.startSite
                    && this.endSite == other.endSite
                    && this.name == other.name
                    && this.score == other.score
                    && this.strand == other.strand
                    && this.otherMessage == other.otherMessage;
        } else {
            return basicTest;
        }
    }


    /**
     * Collection用来比较大小排序的部分
     * @override Compare to only determine the position of two genomic locations
     * @param other
     * @return
     */
    @Override
    public int compareTo(BasicBed other) {
        // the comparison of chromosome != 0, just return it, otherwise do more comparison
        int comparesionOfChromosome = this.chromosome.compareToIgnoreCase(other.chromosome);
        if (comparesionOfChromosome != 0) {
            return comparesionOfChromosome;
        } else {
            if ( this.startSite < other.startSite ) {
                return -1;
            } else if ( this.startSite > other.startSite ) {
                return 1;
            } else if ( this.endSite < other.endSite ) {
                return -1;
            } else if ( this.endSite > other.endSite ) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 克隆，但是貌似没啥用
     * @return
     */
    @Override
    public BasicBed clone() {
        final BasicBed clone;
        try {
            clone = (BasicBed) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("BasicBed clone error", ex);
        }
        clone.chromosome = getChrom();
        clone.startSite = getStart();
        clone.endSite = getEnd();
        clone.name = getName();
        clone.score = getScore();
        clone.id = getId();
        clone.strand = getStrand();
        clone.otherMessage = getOther();
        return clone;
    }


    /**
     * 测试这个bed位点是否在另一个位点的上游
     * test if this region is totally upstream of other
     * eg: chr1:100-200 is upstream of chr1:201-300
     * @return: true means it is; false means no
     */
    public Boolean isUpstream(BasicBed other) {
        int comparesion = this.isOverlapped(other);
        return (comparesion == -1) ? true: false;
    }


    /**
     * 测试这个位点是否在另一个位点的下游
     * test if this region is totally downstream of other
     * eg: chr1:100-200 is downstream of chr1:1-99
     * @param other: another BasicBed
     * @return: true means it is; false means no
     */
    public boolean isDownstream(BasicBed other) {
        int comparesion = this.isOverlapped(other);
        return (comparesion == 1) ? true: false;
    }


    /**
     * 测试两个位点之间是否存在重合位点，0代表有，1代表这个在另一个的下游，-1代表上游
     * test if two locations have overlaps, if does, return distance between two locations, if not return 0
     * @param other
     * @return 0: two region have overlap; 1: first one is downstream of second; -1 first one is upstream of second
     */
    public Integer isOverlapped(BasicBed other) {
        // the comparison of chromosome != 0, just return it, otherwise do more comparison
        int comparesionOfChromosome = this.chromosome.compareToIgnoreCase(other.chromosome);
        if (comparesionOfChromosome != 0) {
            return comparesionOfChromosome;
        } else if ( this.startSite > other.endSite ) {
            return 1;
        } else if ( this.endSite < other.startSite ) {
            return -1;
        } else {
            return 0;
        }
    }


    /** 返回两个位点间的距离，按照bedops的规则进行计算，多加减了个1
     * return the distance between two bed locations
     * @param other: another BasicBed class object
     * @return: integer number, the distance between the edges of two locations
     */
    public Integer distanceTo(BasicBed other) {
        if (this.chromosome.equals(other.chromosome)) {
            if ( this.endSite <= other.startSite ) {
                return other.startSite - this.endSite + 1;
            } else if ( this.startSite >= other.endSite ) {
                return other.endSite - this.startSite - 1;
            } else {
                return 0;
            }
        }
        return null;
    }


    /**
     * 返回两个位点中心之间的距离
     * return the distance between the center of two bed locations
     * @param other: another BasicBed class object
     * @return: float number, the distance between the center of two bed locations
     * 			-1 represents these locations don't in same chromosome;
     */
    public float distanceToCenter(BasicBed other) {
        if (!this.chromosome.equals(other.chromosome)) return -1;
        return ((this.startSite + this.endSite) - (other.startSite + other.endSite)) / 2;
    }


    /**
     * 比较两个位点间的重合程度，-1没重合，其他为重合的bp数
     * compare the coverage between two bed locations
     * @param other: another BasicBed class object
     * @return: integer number, the coverage, 0 represent these two locations don't have any overlap
     */
    public int coverageTo(BasicBed other) {
        // the comparison of chromosome != 0, just return it, otherwise do more comparison
        int comparesionOfChromosome = this.chromosome.compareToIgnoreCase(other.chromosome);
        if (comparesionOfChromosome != 0) {
            return -1;
        } else {
            if (this.endSite >= other.startSite && this.startSite <= other.endSite) {
                return Math.min(this.endSite, other.endSite) - Math.max(this.startSite, other.startSite);
            } else {
                return -1;
            }
        }
    }


    /**
     * 比较两个位点间重合的百分比，以第一个的位点的大小为基准
     * compare two genomic locations, and return the coverage by percent
     * @param other: another BasicBed object
     * @return: -1 - because of the original BasicBed do not have any length, endSite - startSite == 0
     * 			otherwise, return a float number, the coverage / total length of this BasicBed
     */
    public float coverageToPercent(BasicBed other) {
        int coverage = this.coverageTo(other);
        if ( coverage == -1 ) {
            return -1;
        }
        try {
            return (float) Math.abs(coverage) / (this.endSite - this.startSite);
        } catch (java.lang.ArithmeticException e) {
            return -1;
        }
    }


    /**
     * 融合两个不同的位点
     * merge two basic bed region into one, if they have overlaps
     * @param other: another BasicBed class object
     * @return: Optional object, null to none overlaps, or a brand new BasicBed
     */
    public Optional<BasicBed> merge(BasicBed other) {
        Optional<BasicBed> results = Optional.empty();
        if ( coverageTo(other) != 0 ) {
            try {
                results = Optional.of(new BasicBed(this.chromosome, Math.min(this.startSite, other.startSite), Math.max(this.endSite, other.endSite)));
            } catch ( BedFormatError e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return results;
    }


    /**
     * 找出这个位点比另一个位点差异的部分，返回列表
     * return the different part of this bed that compare to another
     * @param other
     * @return
     */
    public ArrayList<BasicBed> difference(BasicBed other){
        ArrayList<BasicBed> results = new ArrayList<BasicBed>();

        if (isOverlapped(other) == 0) {
            try {

                if (this.startSite < other.startSite) {
                    if (this.endSite < other.endSite) {
                        results.add(new BasicBed(this.chromosome, this.startSite, other.startSite));
                    } else if (this.endSite > other.endSite) {
                        results.add(new BasicBed(this.chromosome, other.endSite, this.endSite));
                    }
                } else if (this.startSite > other.startSite) {
                    results.add(new BasicBed(this.chromosome, other.endSite, this.endSite));
                }

            }catch (RuntimeException | BedFormatError e) {
                e.printStackTrace();
            }
        }
        return results;
    }


    /**
     * 找出两个位点间互为不同的部分
     * return symmetric difference part of two genomic locations
     * @param other
     * @return
     */
    public ArrayList<BasicBed> symmetricDifference(BasicBed other){
        ArrayList<BasicBed> results = new ArrayList<BasicBed>();

        if (isOverlapped(other) == 0) {
            try{
                results.add(new BasicBed(
                        this.chromosome,
                        Math.min(this.startSite, other.startSite),
                        Math.max(this.startSite, other.startSite)
                ));

                results.add(new BasicBed(
                        this.chromosome,
                        Math.min(this.endSite, other.endSite),
                        Math.max(this.endSite, other.endSite)
                ));
            } catch (BedFormatError e) {
                e.printStackTrace();
            }
        }
        return results;
    }


    /**
     * 找出两个位点间重合的部分
     * get common part of two genomic locations
     */
    public BasicBed intersect(BasicBed other){
        try {
            if (isOverlapped(other) == 0) {
                return new BasicBed(this.chromosome, Math.max(this.startSite, other.startSite), Math.min(this.endSite, other.endSite));
            }
        } catch (BedFormatError e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 找出将两个位点分块
     * return patitions by symmetric difference, equals to the sum of symmetriDifference and intersect
     */
    public ArrayList<BasicBed> partition(BasicBed other){
        ArrayList<BasicBed> results = new ArrayList<BasicBed>();
        if (isOverlapped(other) == 0) {

            ArrayList<Integer> site = new ArrayList<>(Arrays.asList(
                    this.startSite, this.endSite,
                    other.startSite, other.endSite
            ));

            Collections.sort(site);

            try {
                for(int i = 1; i < site.size(); i++) {
                    results.add(new BasicBed(
                            this.chromosome,
                            site.get(i - 1),
                            site.get(i)
                    ));

                }
            } catch (BedFormatError e) { e.printStackTrace(); }
        }

        return results;
    }


    /**
     * 将一个位点切割成小块
     * chop whole region into pieces by size of input, default 1
     */
    public ArrayList<BasicBed> chop() {
        return this.chop(1);
    }

    /**
     * @重载：添加支持chop的大小
     * @param chop
     * @return
     */
    public ArrayList<BasicBed> chop(int chop) {
        ArrayList<BasicBed> results = new ArrayList<BasicBed>();

        int i = this.startSite;
        while (i < this.endSite) {
            int j = i + chop;
            try {
                if(j > this.endSite) {
                    j = this.endSite;
                }
                results.add(new BasicBed(this.chromosome, i, j));
            } catch (BedFormatError e) {
                e.printStackTrace();
            }
            i = j;
        }
        return results;
    }

    /**
     * @重载：添加间隔大小
     * @param chop
     * @param stagger
     * @return
     */
    public ArrayList<BasicBed> chop(int chop, int stagger) {
        ArrayList<BasicBed> results = new ArrayList<BasicBed>();
        int i = this.startSite;
        while (i < this.endSite) {
            int j = i + chop;
            try {
                if(j > this.endSite) {
                    j = this.endSite;
                }
                results.add(new BasicBed(this.chromosome, i, j));
            } catch (BedFormatError e) {
                e.printStackTrace();
            }
            i += stagger;
        }
        return results;
    }


    /**
     * 找出两个位点间互补的部分
     * find complement part between bed regions
     * @param other: another BasicBed
     * @return: brand new BasicBed in bed3 format
     */
    public BasicBed complement(BasicBed other) {
        int comparision = this.chromosome.compareToIgnoreCase(other.chromosome);
        if (comparision == 0) {
            try{
                return new BasicBed(
                        this.chromosome,
                        Math.min(this.endSite, other.endSite),
                        Math.max(this.startSite, other.startSite)
                );
            } catch (BedFormatError e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}


