package func

/**
 * @author zhang
 * @since 2018.3.20
 * @version 0.1
 * A class to perform intersect function
 */

import basic.BasicBed
import basic.BedFormatError
import java.io.IOException
import java.io.RandomAccessFile
import java.io.PrintWriter
import java.util.ArrayList


class IntersectBed {

    internal var setter = SetBasicBed()


    @Throws(IOException::class, BedFormatError::class)
    fun intersect(first: String, second: String) {
        this.intersect(first, second, null)
    }

    /**
     * find all intersect between two bed files
     * @param first: path to first file
     * @param second: path to second file
     * @param output: path to output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun intersect(first: String, second: String, output: String?) {
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
        var logged = false

        val firstReader = RandomAccessFile(first, "r")
        val secondReader = RandomAccessFile(second, "r")

        firstLine = firstReader.readLine()
        secondLine = secondReader.readLine()

        var firstBed = setter.setBasicBed(firstLine)
        var secondBed = setter.setBasicBed(secondLine)

        /*
         test if these two have overlaps
         And check make sure the pointer does not exceed the limit
          */
        while (true) {

            val overlap = firstBed.isOverlapped(secondBed)

            if (overlap > 0) {
                if (!logged) {
                    pointer = secondReader.getFilePointer()
                }
                secondLine = secondReader.readLine()

                if (secondLine == null) {
                    break
                }
                secondBed = setter.setBasicBed(secondLine)
            } else if (overlap < 0) {
                firstLine = firstReader.readLine()

                if (firstLine == null) {
                    break
                }
                firstBed = setter.setBasicBed(firstLine)

                if (logged) {
                    logged = false
                    secondReader.seek(pointer)
                    secondLine = secondReader.readLine()
                    secondBed = setter.setBasicBed(secondLine)
                }
            } else {
                if (!logged) {
                    logged = true
                }

                val intersected = firstBed.intersect(secondBed)
                if (intersected != null) {
                    writer.println(intersected!!.getBed())
                }

                secondLine = secondReader.readLine()

                if (secondLine == null) {
                    break
                }
                secondBed = setter.setBasicBed(secondLine)
            }
        }

        writer.close()
        firstReader.close()
        secondReader.close()
    }

    @Throws(IOException::class, BedFormatError::class)
    fun intersectList(first: String, second: String): ArrayList<BasicBed> {
        val results = ArrayList()
        var firstLine: String? = null
        var secondLine: String? = null
        var pointer: Long = 0
        var logged = false
        val firstReader = RandomAccessFile(first, "r")
        val secondReader = RandomAccessFile(second, "r")

        firstLine = firstReader.readLine()
        secondLine = secondReader.readLine()
        var firstBed = setter.setBasicBed(firstLine)
        var secondBed = setter.setBasicBed(secondLine)

        // test if these two have overlaps
        while (true) {
            val overlap = firstBed.isOverlapped(secondBed)

            if (overlap > 0) {
                if (!logged) {
                    pointer = secondReader.getFilePointer()
                }
                secondLine = secondReader.readLine()

                if (secondLine == null) {
                    break
                }
                secondBed = setter.setBasicBed(secondLine)
            } else if (overlap < 0) {
                firstLine = firstReader.readLine()

                if (firstLine == null) {
                    break
                }
                firstBed = setter.setBasicBed(firstLine)

                if (logged) {
                    logged = false
                    secondReader.seek(pointer)
                    secondLine = secondReader.readLine()
                    secondBed = setter.setBasicBed(secondLine)
                }
            } else {
                if (!logged) {
                    logged = true
                }

                results.add(firstBed.intersect(secondBed))

                secondLine = secondReader.readLine()

                if (secondLine == null) {
                    break
                }
                secondBed = setter.setBasicBed(secondLine)
            }
        }

        firstReader.close()
        secondReader.close()
        return results
    }

    /**
     * a private method to compare a list of BasicBed and a bed file
     * @param infile: path to a file
     * @param inputList: a list of BasicBed
     * @return: a list of BasicBed
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    private fun intersect(infile: String, inputList: ArrayList<BasicBed>): ArrayList<BasicBed> {
        var index = 0
        var pointer: Long = 0
        var logged = false
        var line: String? = null
        val results = ArrayList()
        val reader = RandomAccessFile(infile, "r")

        line = reader.readLine()
        var firstBed = inputList.get(index)
        var secondBed = setter.setBasicBed(line)

        // 文件读取到null后会break，因此，只需要保证列表不超出即可
        while (index < inputList.size()) {
            val overlap = firstBed.isOverlapped(secondBed)

            if (overlap > 0) {
                if (!logged) {
                    pointer = reader.getFilePointer()
                }
                line = reader.readLine()

                if (line == null) {
                    break
                }
                secondBed = setter.setBasicBed(line)
            } else if (overlap < 0) {
                index++
                firstBed = inputList.get(index)

                if (logged) {
                    logged = false
                    reader.seek(pointer)
                    line = reader.readLine()
                    secondBed = setter.setBasicBed(line)
                }
            } else {
                if (!logged) {
                    logged = true
                }
                results.add(firstBed.intersect(secondBed))

                line = reader.readLine()

                if (line == null) {
                    break
                }
                secondBed = setter.setBasicBed(line)
            }
        }
        return results
    }

    @Throws(IOException::class, BedFormatError::class)
    fun intersect(inputList: ArrayList<String>) {
        this.intersect(inputList, null)
    }

    /**
     * a method to find intersect of a list of files
     * @param inputList: a list of files, at least two files
     * @param output: path to output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun intersect(inputList: ArrayList<String>, output: String?) {
        val results = this.intersectList(inputList.get(0), inputList.get(1))

        for (i in 2 until inputList.size() - 1) {
            results.addAll(this.intersect(inputList.get(i), results))
        }

        val writer: PrintWriter
        if (output == null) {
            writer = PrintWriter(System.out, true)
        } else {
            writer = PrintWriter(output)
        }

        for (b in results) {
            writer.println(b.getBed())
        }
        writer.close()
    }

}
