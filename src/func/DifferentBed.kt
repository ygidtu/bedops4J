package func

/**
 * @author Zhang
 * @since 2018.3.20
 * @version
 * A class to perform difference and Symmetric difference
 *
 * when input as file list, use the rest files to compare to the first one,
 * therefore, the results may in random order
 */

import basic.BasicBed
import basic.BedFormatError

import java.io.IOException
import java.io.RandomAccessFile
import java.io.PrintWriter
import java.util.ArrayList

class DifferentBed {
    private val setter = SetBasicBed()

    @Throws(IOException::class, BedFormatError::class)
    fun difference(first: String, second: String) {
        this.difference(first, second, null)
    }

    /**
     * find difference between two bed files
     * @param first: path to first file
     * @param second: path to second file
     * @param output: path to output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun symmetricDifference(first: String, second: String, output: String?) {
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

                for (b in firstBed.difference(secondBed)) {
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
        firstReader.close()
        secondReader.close()
    }

    /**
     * compare the difference between first file and the rest
     * @param inputList: a list of files
     * @param output: output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun symmetricDifference(inputList: ArrayList<String>, output: String) {
        for (i in 1 until inputList.size()) {
            this.difference(inputList.get(0), inputList.get(i), output)
        }
    }

    @Throws(IOException::class, BedFormatError::class)
    fun symmetricDifference(first: String, second: String) {
        this.difference(first, second, null)
    }

    /**
     * find difference between two bed files
     * @param first: path to first file
     * @param second: path to second file
     * @param output: path to output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun difference(first: String, second: String, output: String?) {
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

                for (b in firstBed.symmetricDifference(secondBed)) {
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
        firstReader.close()
        secondReader.close()
    }

    /**
     * compare the difference between first file and the rest
     * @param inputList: a list of files
     * @param output: output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun difference(inputList: ArrayList<String>, output: String) {
        for (i in 1 until inputList.size()) {
            this.difference(inputList.get(0), inputList.get(i), output)
        }
    }

}
