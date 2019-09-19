package func

import basic.BasicBed
import basic.BedFormatError

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.PrintWriter
import java.util.*


/**                       MergeBeds
 * merge function, if don't use output file, just print to stdout
 * It's absolutely unnecessary to write so many Overrides
 *
 * @author Zhang Yiming
 * @since 2018.2.25
 * @version 0.1
 */

class MergeBeds {

    private val setter = SetBasicBed()


    /*
     * select key by map value, used in merge function
     */
    private fun getKeyByValue(map: Map<BufferedReader, BasicBed>, value: BasicBed): BufferedReader? {
        for (entry in map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey()
            }
        }
        return null
    }

    /**
     * merge multiple files, without the output file, redirect the results to stdout
     * @param inputDirectory: [String] input directory
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputDirectory: String) {
        val inputFiles = File(inputDirectory).listFiles()
        this.merge(inputFiles)
    }

    /**
     * merge multiple files, without the output file, redirect the results to stdout
     * @param inputDirectory: [File] input directory
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputDirectory: File) {
        val inputFiles = inputDirectory.listFiles()
        this.merge(inputFiles)
    }

    /**
     * merge multiple files, without the output file, redirect the results to stdout
     * @param inputFiles: [String[]] input files
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputFiles: Array<String>) {
        val inputList = ArrayList<String>(Arrays.asList(inputFiles))

        this.merge(inputList)
    }

    /**
     * merge multiple files, without the output file, redirect the results to stdout
     * @param inputFiles: [File[]] input files
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputFiles: Array<File>) {
        val input = ArrayList<String>(inputFiles.size)

        for (f in inputFiles) {
            input.add(f.toString())
        }

        this.merge(input)
    }

    /**
     * merge multiple files, without the output file, redirect the results to stdout
     * @param inputFiles: [ArrayList] input files
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputFiles: ArrayList) {
        this.merge(inputFiles, null)
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputDirectory: [String] a directory contains several files
     * @param output: [String] output file
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputDirectory: String, output: String) {
        val inputFiles = File(inputDirectory).listFiles()
        val input = ArrayList<String>(inputFiles.size)

        for (f in inputFiles) {
            input.add(f.toString())
        }

        this.merge(input, output)
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputDirectory: [String] a directory contains several files
     * @param output: [File] output file
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputDirectory: String, output: File) {
        val inputFiles = File(inputDirectory).listFiles()
        val input = ArrayList<String>(inputFiles.size)

        for (f in inputFiles) {
            input.add(f.toString())
        }

        this.merge(input, output.toString())
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputDirectory: [File] a directory contains several files
     * @param output: [File] output file
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputDirectory: File, output: String) {
        val inputFiles = inputDirectory.listFiles()

        this.merge(inputFiles, File(output))
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputDirectory: [File] a directory contains several files
     * @param output: [File] output file
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputDirectory: File, output: File) {
        val inputFiles = inputDirectory.listFiles()

        this.merge(inputFiles, output)
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputFiles: [String[]] files that need to be merged
     * @param output: [String] output file
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputFiles: Array<String>, output: String) {
        val input = ArrayList()

        for (s in inputFiles) {
            input.add(File(s))
        }

        this.merge(input, output)
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputFiles: [String[]] files that need to be merged
     * @param output: [File] output file
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputFiles: Array<String>, output: File) {
        val input = ArrayList()

        for (s in inputFiles) {
            input.add(File(s))
        }

        this.merge(input, output.toString())
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputFiles: [File[]] files that need to be merged
     * @param output: [File] output file
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputFiles: Array<File>, output: String) {
        this.merge(Arrays.asList(inputFiles), output)
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputFiles: [File[]] files that need to be merged
     * @param output: [File] output file
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputFiles: Array<File>, output: File) {
        this.merge(ArrayList<File>(Arrays.asList(inputFiles)), output.toString())
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputFiles: [ArrayList] a directory contains several files
     * @param output: [String] output file
     */
    @Throws(IOException::class, BedFormatError::class)
    fun merge(inputFiles: List, output: String?) {
        var smallestBed: BasicBed
        var mergedBed: BasicBed? = null
        var temporaryBr: BufferedReader?
        val temporaryList = LinkedList()
        val inputReaders = ArrayList()
        val inputAndBrs = HashMap()
        var line: String? = null

        try {
            val writer: PrintWriter
            if (output == null) {
                writer = PrintWriter(System.out, true)
            } else {
                writer = PrintWriter(output)
            }

            for (i in 0 until inputFiles.size()) {

                val tmpFile = File(inputFiles.get(i).toString())

                val tmpFr = FileReader(tmpFile)
                inputReaders.add(tmpFr)

                val tmpBr = BufferedReader(tmpFr)

                if ((line = tmpBr.readLine()) != null) {
                    smallestBed = this.setter.setBasicBed(line)
                    if (!inputAndBrs.containsKey(tmpBr)) {
                        inputAndBrs.put(tmpBr, smallestBed)
                    }
                    temporaryList.add(smallestBed)
                }
            }

            Collections.sort(temporaryList)

            // write the smallest BasicBed and replace with a newly read content
            while (!temporaryList.isEmpty()) {

                smallestBed = temporaryList.removeFirst()

                if (mergedBed == null) {
                    mergedBed = smallestBed.clone()
                }

                val temporary = mergedBed!!.merge(smallestBed)

                if (temporary.isPresent()) {
                    mergedBed = temporary.get()
                } else {
                    writer.println(mergedBed!!.getBasicBed())
                    mergedBed = smallestBed.clone()
                }

                temporaryBr = getKeyByValue(inputAndBrs, smallestBed)

                // if the BufferedReader still have content, deal with it. Otherwise, close it
                if ((line = temporaryBr!!.readLine()) != null) {
                    smallestBed = this.setter.setBasicBed(line)

                    inputAndBrs.put(temporaryBr, smallestBed)

                    temporaryList.add(smallestBed)
                } else {
                    inputAndBrs.remove(temporaryBr)
                    temporaryBr!!.close()
                }

                Collections.sort(temporaryList)

            }

            // just output the last one to file or stdout
            if (temporaryList.isEmpty()) {
                writer.println(mergedBed!!.getBasicBed())
            }

            writer.close()

            for (f in inputReaders) {
                f.close()
            }
        } catch (e: IOException) {
            throw IOException(e.getMessage())
        }

    }


    /*
     * bedops everything, merge every thing from files together by file paths order
     */
    /**
     * merge everything from different files together by input file order, and write it into file
     * @param inputFiles: input files
     * @param output: output file path
     * @throws IOException
     */
    @Throws(IOException::class)
    fun everything(inputFiles: List, output: String?) {
        if (output == null) {
            this.everything(inputFiles)
        }

        var line: String? = null
        var temporaryReader: FileReader
        var temporaryBReader: BufferedReader
        val inputBReaders = ArrayList(inputFiles.size())
        val inputReaders = ArrayList(inputFiles.size())

        for (i in 0 until inputFiles.size()) {
            val temporaryFile = File(inputFiles.get(i) as String)
            temporaryReader = FileReader(temporaryFile)
            inputReaders.add(temporaryReader)

            temporaryBReader = BufferedReader(temporaryReader)
            inputBReaders.add(temporaryBReader)
        }

        // check output
        val writer: PrintWriter
        if (output == null) {
            writer = PrintWriter(System.out, true)
        } else {
            writer = PrintWriter(output)
        }

        while (inputBReaders.size() > 0) {
            for (i in 0 until inputBReaders.size()) {
                if ((line = inputBReaders.get(i).readLine()) != null) {
                    writer.println(line)
                } else {
                    temporaryReader = inputReaders.remove(i)
                    temporaryBReader = inputBReaders.remove(i)
                    temporaryBReader.close()
                    temporaryReader.close()
                }
            }
        }

        writer.close()
    }

    @Throws(IOException::class)
    fun everything(inputFiles: Array<File>, output: File) {
        val temporaryFiles = ArrayList(inputFiles.size)
        Collections.addAll(temporaryFiles, inputFiles)
        this.everything(temporaryFiles, output.toString())
    }

    @Throws(IOException::class)
    fun everything(inputFiles: Array<File>, output: String) {
        val temporaryFiles = ArrayList(inputFiles.size)
        Collections.addAll(temporaryFiles, inputFiles)
        this.everything(temporaryFiles, output)
    }

    @Throws(IOException::class)
    fun everything(inputFiles: Array<String>, output: String) {
        val temporaryFiles = ArrayList(inputFiles.size)
        Collections.addAll(temporaryFiles, inputFiles)
        this.everything(temporaryFiles, output)
    }

    @Throws(IOException::class)
    fun everything(inputFiles: Array<String>, output: File) {
        val temporaryFiles = ArrayList(inputFiles.size)
        Collections.addAll(temporaryFiles, inputFiles)
        this.everything(temporaryFiles, output.toString())
    }

    /**
     * merge everything from different files together by input file order, and redirect to stdout
     * @param inputFiles: input file paths
     * @throws IOException
     */
    @Throws(IOException::class)
    fun everything(inputFiles: List) {
        this.everything(inputFiles, null)
    }

    @Throws(IOException::class)
    fun everything(inputFiles: Array<File>) {
        val temporaryFiles = ArrayList(inputFiles.size)
        Collections.addAll(temporaryFiles, inputFiles)
        this.everything(temporaryFiles)
    }


    @Throws(IOException::class)
    fun everything(inputFiles: Array<String>) {
        val temporaryFiles = ArrayList(inputFiles.size)
        Collections.addAll(temporaryFiles, inputFiles)
        this.everything(temporaryFiles)
    }

}

