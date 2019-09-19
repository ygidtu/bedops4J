package func

/**
 * @author Zhang
 * @since 2018.3.20
 * @version 0.1
 * A class to preform chop function
 */

import basic.BasicBed
import basic.BedFormatError

import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.util.ArrayList
import java.util.Date
import java.util.Scanner

class ChopBed {
    @Throws(IOException::class, BedFormatError::class)
    fun chop(inputFiles: ArrayList, chop: Int) {
        this.chop(inputFiles, null, chop, 0)
    }

    @Throws(IOException::class, BedFormatError::class)
    fun chop(inputFiles: ArrayList, chop: Int, stagger: Int) {
        this.chop(inputFiles, null, chop, stagger)
    }

    @Throws(IOException::class, BedFormatError::class)
    fun chop(inputFiles: ArrayList, output: String, chop: Int) {
        this.chop(inputFiles, output, chop, 0)
    }

    @Throws(IOException::class, BedFormatError::class)
    fun chop(inputFiles: ArrayList<String>, output: String?, chop: Int, stagger: Int) {
        var line: String? = null
        val setter = SetBasicBed()

        val writer: PrintWriter
        if (output == null) {
            writer = PrintWriter(System.out, true)
        } else {
            writer = PrintWriter(output)
        }
        val tmpFile: File
        if (inputFiles.isEmpty()) {
            System.exit(0)
            return
        } else if (inputFiles.size() > 1) {
            val tmpDir = File(inputFiles.get(0).toString()).getParentFile()
            tmpFile = File(tmpDir, Date().toString())

            // merge all files into temporary file
            val merger = MergeBeds()
            merger.merge(inputFiles, tmpFile.toString())
        } else {
            tmpFile = File(inputFiles.get(0).toString())
        }

        // read and find complement
        val reader = Scanner(tmpFile)

        while (reader.hasNext()) {
            line = reader.nextLine()

            val bed = setter.setBasicBed(line)

            for (b in bed.chop(chop, stagger)) {
                writer.println(b.getBed())
            }
        }

        writer.close()


        if (inputFiles.size() > 1) {
            tmpFile.delete()
        }

    }
}
