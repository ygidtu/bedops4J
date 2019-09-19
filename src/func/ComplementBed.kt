package func

/**
 * @author zhang
 * @since 2018.3.20
 * @version 0.1
 * bedops complement function
 * first merge all file together
 * then, calculate the complement by merged single file
 */
import basic.BasicBed
import basic.BedFormatError
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.util.Date
import java.util.Scanner


class ComplementBed {
    @Throws(IOException::class, BedFormatError::class)
    fun complement(inputFiles: List) {
        this.complement(inputFiles, null)
    }

    @Throws(IOException::class, BedFormatError::class)
    fun complement(inputFiles: List, output: String?) {
        var line: String? = null
        var firstBed: BasicBed? = null
        var secondBed: BasicBed? = null
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
            if (firstBed == null) {
                firstBed = setter.setBasicBed(line)
                continue
            }
            secondBed = setter.setBasicBed(line)
            writer.println(firstBed!!.complement(secondBed).getBed())
            firstBed = secondBed
        }

        writer.close()

        if (inputFiles.size() > 1) {
            tmpFile.delete()
        }

    }
}
