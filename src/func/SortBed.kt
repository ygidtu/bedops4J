package func

import basic.BasicBed
import basic.BedFormatError
import com.sun.management.OperatingSystemMXBean

import java.io.*
import java.lang.management.ManagementFactory
import java.util.*

/**
 * A class to sort bed format files
 * @author Zhang
 * @since 2018.2.25
 * @version 0.1
 */

class SortBed {
    private val mergeBeds = MergeBeds()
    private val setter = SetBasicBed()


    /**
     * get percentage of system memory usage
     * @return: int
     */
    private// retrive the total memory
    // retrive the free memory
    val memory: Int
        get() {
            val osmxb = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
            val totalvirtualMemory = osmxb.getTotalSwapSpaceSize()
            val freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize()
            val compare = (1 - freePhysicalMemorySize * 1.0 / totalvirtualMemory) as Double * 100
            return compare.intValue()
        }

    /*
     * last two functions not been used.
     */
    /**
     * get maximum memory that we could use
     * @return
     */
    private// http://stackoverflow.com/questions/12807797/java-get-available-memory
    val availableMemory: Long
        get() {
            val r = Runtime.getRuntime()
            val allocatedMemory = r.totalMemory() - r.freeMemory()
            return r.maxMemory() - allocatedMemory
        }

    /**
     * exstmite best block size for temporary files
     * @param fileSize: the size of input file bytes
     * @param maxTmpFiles: the maximum number of temporary files
     * @return: long block size
     */
    private fun getBestBlockSize(fileSize: Long, maxTmpFiles: Int): Long {
        // we don't want to open up much more than maxtmpfiles temporary
        // files, better run
        // out of memory first.
        val maxMemory = availableMemory
        var blocksize = fileSize / maxTmpFiles + if (fileSize % maxTmpFiles == 0L) 0 else 1

        // on the other hand, we don't want to create many temporary
        // files
        // for naught. If blocksize is smaller than half the free
        // memory, grow it.
        if (blocksize < maxMemory / 2) {
            blocksize = maxMemory / 2
        }
        return blocksize
    }

    /*
     * delete files and directory
     */
    private fun deleteFile(input: File) {
        if (input.exists() && input.isFile()) {
            input.delete()
        }
    }

    private fun deleteDir(input: File) {
        if (input.exists() && input.isDirectory()) {

            for (f in input.listFiles()) {
                deleteFile(f)
            }
            input.delete()
        }
    }


    /**
     * A private function, split big file into small temporary files or just read into memory
     * @param input: the path of big file
     * @param tmpDir: the path of temporary directory
     * @return: Optional, if file size is bigger than available memory, return null, else, return a list of BasicBed
     */
    @Throws(BedFormatError::class, IOException::class)
    private fun splitBigFile(input: String, tmpDir: File): ArrayList<BasicBed> {
        var line: String? = null
        var temporaryBed: BasicBed? = null
        val inputFile = File(input)

        if (!inputFile.exists() || !inputFile.isFile()) {
            throw FileNotFoundException("Sorting: $input is not file")
        }

        // using ram usage to split big files
        val baseRamUsage = memory

        val inputFileReader = FileReader(inputFile)
        val inputBufferedReader = BufferedReader(inputFileReader)


        val temporaryList = ArrayList<BasicBed>()

        if (!tmpDir.exists()) {
            tmpDir.mkdirs()
        }


        while ((line = inputBufferedReader.readLine()) != null) {
            // get temporary BasicBed and put it into list
            temporaryBed = this.setter.setBasicBed(line)
            temporaryList.add(temporaryBed)


            val currentRamUsage = memory
            if (currentRamUsage > 90 || currentRamUsage - baseRamUsage > 20) {
                if (!tmpDir.exists()) {
                    tmpDir.mkdirs()
                }

                Collections.sort(temporaryList)
                val tem = File(tmpDir, String.valueOf(temporaryList.hashCode()))

                // print a message about the temporary file
                System.out.println("Create temporary file: " + tem.toString())

                val writer = PrintWriter(tem)
                // write to temporary files
                for (b in temporaryList) {
                    writer.println(b.getBed())
                }
                writer.close()
                temporaryList.clear()

            }
        }

        inputBufferedReader.close()
        inputFileReader.close()

        if (tmpDir.exists()) {
            // if there are still part of files not write into temporary file
            if (temporaryList.size() > 0) {
                val tem = File(tmpDir, String.valueOf(temporaryList.hashCode()))
                // sort again
                Collections.sort(temporaryList)

                val writer = PrintWriter(tem)
                // write to temporary files
                for (b in temporaryList) {
                    writer.println(b.getBed())
                }
                writer.close()

                temporaryList.clear()
            }
        }
        return temporaryList
    }


    @Throws(IOException::class, BedFormatError::class)
    fun sortBigFile(input: String) {
        this.sortBigFile(input, null)
    }

    /**
     * split sort and merge big files
     * @param input: input file name with path
     * @param output: output file name with path
     */
    @Throws(IOException::class, BedFormatError::class)
    fun sortBigFile(input: String, output: String?) {
        // construct output directory and temporary directory
        val writer: PrintWriter
        val outputFile: File
        val directory: String
        if (output == null) {
            writer = PrintWriter(System.out, true)
            outputFile = File(input)
            directory = File(input).getAbsoluteFile().getParent()
        } else {
            writer = PrintWriter(output)
            outputFile = File(output)
            directory = File(output).getAbsoluteFile().getParent()
        }

        val outputDirectory = File(directory)
        val temporaryOutput = File(outputDirectory, "temporarySortedFile")

        // first remove temporary directory
        deleteDir(temporaryOutput)


        val temporaryList = splitBigFile(input, temporaryOutput)

        if (!temporaryList.isEmpty()) {
            // miss sort process
            Collections.sort(temporaryList)

            // then output all sorted beds
            for (b in temporaryList) {
                writer.println(b.getBed())
            }
        } else {
            merge(temporaryOutput.toString(), output)
            deleteDir(temporaryOutput)
        }
        writer.close()
    }


    @Throws(IOException::class, BedFormatError::class)
    fun sortBigFiles(input: Array<String>, output: String) {
        val inputFiles = arrayOfNulls<File>(input.size)
        for (i in input.indices) {
            this.sortBigFile(input[i], input[i])
            inputFiles[i] = File(input[i])
        }

        this.merge(inputFiles, output)
    }


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


    @Throws(IOException::class, BedFormatError::class)
    private fun merge(inputDirectory: String) {
        val inputFiles = File(inputDirectory).listFiles()
        this.merge(inputFiles)
    }

    /**
     * merge different bed files into one, and write to output file or stdout
     * @param inputDirectory: [String] a directory contains several files
     * @param output: [File] output file, null for stdout
     */
    @Throws(IOException::class, BedFormatError::class)
    private fun merge(inputDirectory: String, output: String) {
        val inputFiles = File(inputDirectory).listFiles()
        this.merge(inputFiles, output)

    }

    @Throws(IOException::class, BedFormatError::class)
    private fun merge(inputFiles: Array<File>) {
        this.merge(inputFiles, null)
    }

    /**
     * merge different bed files into one, and write to output file or stdout
     * @param inputFiles: arrays of file
     * @param output: output file path or null, null for stdout
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    private fun merge(inputFiles: Array<File>, output: String?) {
        var smallestBed: BasicBed
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

            for (tmpFile in inputFiles) {
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
            while (inputAndBrs.size() > 0 && temporaryList.size() > 0) {

                smallestBed = temporaryList.removeFirst()

                writer.println(smallestBed.getBed())

                temporaryBr = getKeyByValue(inputAndBrs, smallestBed)

                // if the BufferedReader still have content, deal with it. Otherwise, close it
                if ((line = temporaryBr!!.readLine()) != null) {
                    smallestBed = this.setter.setBasicBed(line)

                    inputAndBrs.put(temporaryBr, smallestBed)

                    temporaryList.add(smallestBed)
                } else {
                    inputAndBrs.remove(temporaryBr)
                    temporaryBr!!.close()

                    /* if there is only last BufferedReader left, just write all the content from it into output file
                     * there are not need to manipulate the Map or something else.
                     * and put the If statement after removal of BufferedReader, to reduce the calculations.
                     */
                    if (inputAndBrs.size() === 1) {
                        // one last BufferedReader, just take it
                        temporaryBr = inputAndBrs.keySet().iterator().next()
                        while ((line = temporaryBr!!.readLine()) != null) {
                            writer.println(line)
                        }
                        break
                    }
                }

                Collections.sort(temporaryList)
            }

            // If there are have no BufferedReaders, write all the content from temporaryList into file
            for (b in temporaryList) {
                writer.println(b.getBed())
            }

            writer.close()

            for (f in inputReaders) {
                f.close()
            }
        } catch (e: IOException) {
            throw IOException(e.getMessage())
        }

    }
}
