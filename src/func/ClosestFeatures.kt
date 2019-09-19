package func

import basic.BasicBed
import basic.BedFormatError
import java.io.*
import java.util.Arrays
import java.util.HashMap


class ClosestFeatures {
    private val setter = SetBasicBed()

    private fun getMessage(firstBed: BasicBed, secondBed: Array<BasicBed>, dist: Boolean, center: Boolean): String {
        var msg = firstBed.getBed()
        for (b in secondBed) {
            if (b == null) {
                if (dist) {
                    msg += "|NA|NA"
                } else {
                    msg += "|NA"
                }
                continue
            }
            if (dist) {
                if (center) {
                    msg += String.format(
                            "|%s|%f",
                            b!!.getBed(),
                            firstBed.distanceToCenter(b)
                    )
                } else {
                    msg += String.format(
                            "|%s|%d",
                            b!!.getBed(),
                            firstBed.distanceTo(b)
                    )
                }
            } else {
                msg += String.format(
                        "|%s",
                        b!!.getBed()
                )
            }
        }

        return msg
    }


    private fun getMessage(matches: HashMap<BasicBed, BasicBed>, matchDist: HashMap<BasicBed, Float>, key: BasicBed, distance: Boolean, center: Boolean): String? {
        var msg: String? = null
        if (matches.containsKey(key)) {
            if (distance) {
                if (center) {
                    msg = String.format("%s|%s|%f", key.getBed(), matches.get(key).getBed(), matchDist.get(key))
                } else {
                    msg = String.format("%s|%s|%d", key.getBed(), matches.get(key).getBed(), matchDist.get(key).intValue())
                }
            } else {
                msg = String.format("%s|%s", key.getBed(), matches.get(key).getBed())
            }
        }
        return msg
    }


    @Throws(IOException::class, BedFormatError::class)
    fun closestFeatures(first: String, second: String, dist: Boolean, center: Boolean) {
        this.closestFeatures(first, second, null, dist, center)
    }


    /**
     * Only print the closest one
     * @param first
     * @param second
     * @param output
     */
    @Throws(IOException::class, BedFormatError::class)
    fun closestFeatures(first: String, second: String, output: String?, dist: Boolean, center: Boolean) {
        val matches = HashMap()       // for the closest matches
        val matchDist = HashMap()
        // create print writer
        val writer: PrintWriter
        if (output == null) {

            writer = PrintWriter(System.out, true)
        } else {
            writer = PrintWriter(output)
        }
        var firstLine: String? = null
        var secondLine: String? = null
        var pointer: Long = 0
        var bIsOverlapped = false  // this one is used to log if there any overlap happened
        var distance = 0.toFloat()

        val firstReader = RandomAccessFile(first, "r")
        val secondReader = RandomAccessFile(second, "r")

        firstLine = firstReader.readLine()
        secondLine = secondReader.readLine()


        var firstBed = setter.setBasicBed(firstLine)
        var secondBed = setter.setBasicBed(secondLine)


        while (true) {

            val overlap = firstBed.isOverlapped(secondBed)

            if (center) {  // calculate the distance here,这个不如all麻烦，直接在这里计算距离就好
                distance = firstBed.distanceToCenter(secondBed)
            } else {
                distance = firstBed.distanceTo(secondBed)
            }

            if (overlap > 0) {
                if (!bIsOverlapped) {
                    pointer = secondReader.getFilePointer()
                }

                // 如果两个位点不可能有临近位点，那么这个就只可能是上游最近的位点了
                val tmp = HashMap()
                tmp.put(secondBed, distance)
                if (matches.containsKey(firstBed)) {
                    if (Math.abs(distance) < Math.abs(matchDist.get(firstBed))) {
                        matches.put(firstBed, secondBed)
                        matchDist.put(firstBed, distance)
                    }
                } else {
                    matches.put(firstBed, secondBed)
                    matchDist.put(firstBed, distance)
                }


                secondLine = secondReader.readLine()
                secondBed = setter.setBasicBed(secondLine)

            } else if (overlap < 0) {

                /* 如果没有重合过，那么目前这个就是最近的下游，跟上游比对一下，谁近，保留谁
                 */
                if (!bIsOverlapped) {
                    val tmp = HashMap()
                    tmp.put(secondBed, distance)
                    if (matches.containsKey(firstBed)) {
                        if (Math.abs(distance) < Math.abs(matchDist.get(firstBed))) {
                            matches.put(firstBed, secondBed)
                            matchDist.put(firstBed, distance)
                        }
                    } else {
                        matches.put(firstBed, secondBed)
                        matchDist.put(firstBed, distance)
                    }
                }

                // 输出结果，清空字典，重置指针
                writer.println(this.getMessage(matches, matchDist, firstBed, dist, center))
                matches.clear()
                matchDist.clear()

                secondReader.seek(pointer)
                secondLine = secondReader.readLine()
                secondBed = setter.setBasicBed(secondLine)

                firstLine = firstReader.readLine()
                firstBed = setter.setBasicBed(firstLine)
            } else {  // two region have overlap

                if (!bIsOverlapped) {
                    bIsOverlapped = true
                }

                // 此处，有重合之后，选择最后一个有重合的
                val tmp = HashMap()
                tmp.put(secondBed, distance)
                if (matches.containsKey(firstBed)) {
                    if (Math.abs(distance) <= Math.abs(matchDist.get(firstBed))) {
                        matches.put(firstBed, secondBed)
                        matchDist.put(firstBed, distance)
                    }
                } else {
                    matches.put(firstBed, secondBed)
                    matchDist.put(firstBed, distance)
                }

                // while循环不通过<=就无法读取完整的内容，但是=后就容易读过了，因此特地加一个判断
                if (secondReader.getFilePointer() < secondReader.length()) {
                    secondLine = secondReader.readLine()
                    secondBed = setter.setBasicBed(secondLine)
                } else {
                    break
                }
            }
        }

        // 额外输出一个结果，并且清空字典
        writer.println(this.getMessage(matches, matchDist, firstBed, dist, center))
        matches.clear()
        matchDist.clear()

        writer.close()
        firstReader.close()
        secondReader.close()

    }


    @Throws(IOException::class, BedFormatError::class)
    fun closestFeaturesAll(first: String, second: String, dist: Boolean, center: Boolean) {
        this.closestFeaturesAll(first, second, null, dist, center)
    }

    /**
     * get the closest site, from upstream and downstream at same time
     * the algorithm is kind of messy
     * @param first: path to first file
     * @param second: path to second file
     * @param dist: true: print the distance between two region
     * @param center: true: print the distance between two region center
     * @param output: path to output file, if null, redirect to stdout
     *
     * Coding: utf-8
     * 讲起来比较麻烦。
     *
     * 需要用一个isOverlapped记录两个位点之间是否 曾经 存在任何重合位点
     * 基础思路：分别读取两个文件，第二个文件读取到哪了，用指针来记录，
     * 基本判断：
     * 第一次有重合位点，那这就是上游最近的，记录第二个的指针
     * 如果在有重合位点的记录之后，第一次没有重合位点了，那么这个就是最近的下游位点，
     * 把第二个文件的指针恢复到第一次有记录的时候，方便进行完整遍历
     *
     * 但是在这个算法里，是不够的
     * 因为，他不止要有重合位点，还要找出非重合最近的那些位点，
     * 那么如果一直没有重合位点，isOverlapped就一致为false，第二个文件的指针就会一直刷新，没法重置会初始状态
     *
     * 所以，加入了第二个变量，numberOfOverlap,来辅助进行判断
     * numberOfOverlap只在刷新指针或者确实有重合位点进行出输出后才会重置为0
     * 光isOverlapped表明没有重合位点是不够进行指针更新的，numberOfOverlap得不为0才行，表明你们之间曾经有过重合位点
     */
    @Throws(IOException::class, BedFormatError::class)
    fun closestFeaturesAll(first: String, second: String, output: String?, dist: Boolean, center: Boolean) {
        var Matched = arrayOfNulls<BasicBed>(2)       // for the closest matches
        // create print writer
        val writer: PrintWriter
        if (output == null) {

            writer = PrintWriter(System.out, true)
        } else {
            writer = PrintWriter(output)
        }
        var firstLine: String? = null
        var secondLine: String? = null
        var pointer: Long = 0
        var bIsOverlapped = false  // this one is used to log if there any overlap happened
        var numberOfOverlap = 0        // log how many times that overlaps happened

        val firstReader = RandomAccessFile(first, "r")
        val secondReader = RandomAccessFile(second, "r")

        firstLine = firstReader.readLine()
        secondLine = secondReader.readLine()

        var firstBed = setter.setBasicBed(firstLine)
        var secondBed = setter.setBasicBed(secondLine)

        var lastMatched = setter.setBasicBed(secondLine)

        while (true) {

            val overlap = firstBed.isOverlapped(secondBed)

            if (overlap > 0) {
                if (!bIsOverlapped && numberOfOverlap != 0) {
                    /*
                      * the IsOverlapped and numberOfOverlap together to determine
                      * whether there are overlapped happened to the region from first file
                      */
                    numberOfOverlap = 0
                    pointer = secondReader.getFilePointer()
                } else {
                    Matched[0] = setter.setBasicBed(secondLine)
                }

                if (secondReader.getFilePointer() < secondReader.getFilePointer()) {
                    secondLine = secondReader.readLine()
                    secondBed = setter.setBasicBed(secondLine)
                } else {
                    break
                }

                lastMatched = setter.setBasicBed(secondLine)
            } else if (overlap < 0) {

                if (bIsOverlapped) {
                    bIsOverlapped = false
                    numberOfOverlap = 0
                    Matched[1] = lastMatched
                } else {
                    Matched[1] = secondBed
                }

                /* if this is the first time of not overlap,
                 * after a overlap situation,
                 * then this is the closest from downstream
                 */
                secondReader.seek(pointer)
                secondLine = secondReader.readLine()
                secondBed = setter.setBasicBed(secondLine)

                if (!Arrays.asList(Matched).subList(0, Matched.size - 1).contains(null)) {
                    writer.println(this.getMessage(firstBed, Matched, dist, center))
                    Matched = arrayOfNulls<BasicBed>(2)
                }

                if (firstReader.getFilePointer() < firstReader.length()) {
                    firstLine = firstReader.readLine()
                    firstBed = setter.setBasicBed(firstLine)
                } else {
                    break
                }

            } else {  // two region have overlap
                numberOfOverlap++
                if (!bIsOverlapped) {
                    bIsOverlapped = true
                    // if this is the first time of overlap, then this is the closest from upstream
                    Matched[0] = setter.setBasicBed(secondLine)
                }

                if (secondReader.getFilePointer() < secondReader.getFilePointer()) {
                    secondLine = secondReader.readLine()
                    secondBed = setter.setBasicBed(secondLine)
                } else {
                    break
                }
            }

        }


        if (!Arrays.asList(Matched).subList(0, Matched.size - 1).contains(null)) {
            writer.println(this.getMessage(firstBed, Matched, dist, center))
            Matched = arrayOfNulls<BasicBed>(2)
        }

        writer.close()
        firstReader.close()
        secondReader.close()

    }
}
