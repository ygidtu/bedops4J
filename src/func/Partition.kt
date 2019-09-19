package func

/**
 * @author zhang
 * @since 2018.3.20
 * @version 0.1
 * A class to perform bedops partition
 */

import basic.BasicBed
import basic.BedFormatError

import java.io.IOException
import java.io.RandomAccessFile
import java.io.PrintWriter
import java.util.ArrayList


class Partition {
    internal var setter = SetBasicBed()

    @Throws(IOException::class, BedFormatError::class)
    fun partition(first: String, second: String) {
        this.partition(first, second, null)
    }

    /**
     *
     * @param first: first file
     * @param second: second file
     * @param output: if null, redirect to stdout
     */
    @Throws(IOException::class, BedFormatError::class)
    fun partition(first: String, second: String, output: String?) {
        var firstLine: String? = null
        var secondLine: String? = null
        var pointer: Long = 0
        var logged = false
        val firstReader = RandomAccessFile(first, "r")
        val secondReader = RandomAccessFile(second, "r")

        val writer: PrintWriter
        if (output == null) {
            writer = PrintWriter(System.out, true)
        } else {
            writer = PrintWriter(output)
        }

        firstLine = firstReader.readLine()
        secondLine = secondReader.readLine()
        var firstBed = setter.setBasicBed(firstLine)
        var secondBed = setter.setBasicBed(secondLine)

        // make sure the pointer does not exceed the limit
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

                for (b in firstBed.partition(secondBed)) {
                    writer.println(b.getBed())
                }

                secondLine = secondReader.readLine()

                if (secondLine == null) {
                    break
                }
                secondBed = setter.setBasicBed(secondLine)

            }
        }
        writer.close()
    }


    /* // 递归
    private ArrayList<BasicBed> partition (BasicBed first, BasicBed second) {
        return first.partition(second);
    }

    private ArrayList<BasicBed> partition (ArrayList<BasicBed> inputList, BasicBed second) {
        ArrayList<BasicBed> results = new ArrayList<>();
        for(BasicBed b: inputList) {

            ArrayList<BasicBed> tmp = this.partition(b, second);
            if (tmp.isEmpty()) { return tmp; };
            results.addAll(this.partition(tmp, second));
        }
        return results;
    }
    */

    @Throws(IOException::class, BedFormatError::class)
    private fun partitionList(first: String, second: String): ArrayList<BasicBed> {
        var firstLine: String? = null
        var secondLine: String? = null
        var pointer: Long = 0
        var logged = false
        val results = ArrayList()
        val firstReader = RandomAccessFile(first, "r")
        val secondReader = RandomAccessFile(second, "r")

        firstLine = firstReader.readLine()
        secondLine = secondReader.readLine()
        var firstBed = setter.setBasicBed(firstLine)
        var secondBed = setter.setBasicBed(secondLine)

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
                results.addAll(firstBed.partition(secondBed))

                secondLine = secondReader.readLine()

                if (secondLine == null) {
                    break
                }
                secondBed = setter.setBasicBed(secondLine)
            }
        }
        return results
    }

    /**
     *
     * @param infile: a single file
     * @param inputList: a list of BasicBed
     * @return: partitioned BasicBed
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    private fun partition(infile: String, inputList: ArrayList<BasicBed>): ArrayList<BasicBed> {
        var line: String? = null
        var index = 0
        var pointer: Long = 0
        var logged = false
        val results = ArrayList()
        val reader = RandomAccessFile(infile, "r")

        line = reader.readLine()
        var firstBed = inputList.get(index)
        var secondBed = setter.setBasicBed(line)

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
                results.addAll(firstBed.partition(secondBed))

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
    fun partition(inputFiles: ArrayList<String>) {
        this.partition(inputFiles, null)

    }

    /**
     *
     * @param inputFiles: input files
     * @param output: output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun partition(inputFiles: ArrayList<String>, output: String?) {
        var results: ArrayList<BasicBed> = this.partitionList(inputFiles.get(0), inputFiles.get(1))
        for (i in 2 until inputFiles.size()) {
            results = this.partition(inputFiles.get(i), results)
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
