package func

/**
 * @author zhang
 * @since 2018.3.20
 * @version 0.1
 * A class to perform element-of and non-element-of function
 */

import basic.BasicBed
import basic.BedFormatError
import java.io.*
import java.util.*


/**                       CoverageBeds
 * original bedops element-of and not-element-of options
 * @author Zhang Yiming
 * @since 2018.3.4
 */

class CoverageBed {
    private val setter = SetBasicBed()

    // elementOf by bp (integer), file
    @Throws(IOException::class, BedFormatError::class)
    fun elementOf(first: String, inputFiles: Array<String>, output: String, coverage: Int) {
        val inputList = ArrayList(Arrays.asList(inputFiles))
        this.elementOf(first, inputList, output, coverage)
    }

    /**
     * test if two bed regions have coverage above specific threshold (bp)
     * @param first: path to first bed file
     * @param inputFiles: a collection of input files
     * @param output: path to output file
     * @param coverage: bp, like: 1
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun elementOf(first: String, inputFiles: ArrayList, output: String?, coverage: Int) {
        val temporaryFile: File

        // if there are more than one additional files, merge it together before elementOf
        if (inputFiles.size() > 1) {
            val merger = MergeBeds()
            val inputDirectory = File(first).getAbsoluteFile().getParentFile()

            temporaryFile = File(inputDirectory, String.valueOf(inputFiles.hashCode()))

            merger.merge(inputFiles, temporaryFile.toString())
        } else {
            temporaryFile = File(inputFiles.remove(0).toString())
        }

        try {
            this.elementOf(first, temporaryFile.toString(), output, coverage)
        } catch (e: IOException) {
            throw e
        } catch (e: BedFormatError) {
            throw e
        } finally {
            if (!temporaryFile.delete()) {
                System.err.println("Temporary File: " + temporaryFile.toString() + " deletion failed")
            }
        }
    }


    /**
     * test if two bed regions have coverage above specific threshold (percentage of first one)
     * @param first: path to first bed file
     * @param second: path to second bed file
     * @param output: path to output file
     * @param coverage: bp, like: 1
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun elementOf(first: String, second: String, output: String?, coverage: Int) {
        var firstLine: String? = null
        var secondLine: String? = null
        var pointer: Long = 0       // use this to log the first match location
        var logged = false  // use this to check if this is the first match

        var firstBed: BasicBed
        var secondBed: BasicBed
        val writer: PrintWriter

        if (output == null) {
            writer = PrintWriter(System.out, true)
        } else {
            writer = PrintWriter(output)
        }

        val firstReader = RandomAccessFile(first, "r")
        val secondReader = RandomAccessFile(second, "r")


        firstLine = firstReader.readLine()
        secondLine = secondReader.readLine()

        firstBed = setter.setBasicBed(firstLine)
        secondBed = setter.setBasicBed(secondLine)

        while (true) {
            val comparision = firstBed.isOverlapped(secondBed)
            val coverageSize = firstBed.coverageTo(secondBed)

            if (comparision < 0) {
                firstLine = firstReader.readLine()

                if (firstLine == null) {
                    break
                }

                firstBed = setter.setBasicBed(firstLine)

                // if first file read next line, switch second pointer back
                if (logged) {
                    logged = false
                    secondReader.seek(pointer)
                    secondLine = secondReader.readLine()
                    secondBed = setter.setBasicBed(secondLine)
                }
            } else if (coverageSize == -1) {
                if (!logged) {
                    pointer = secondReader.getFilePointer()
                }

                secondLine = secondReader.readLine()

                if (secondLine == null) {
                    break
                }
                secondBed = setter.setBasicBed(secondLine)
            } else {
                // if this is the first time of match, log it
                if (!logged) {
                    logged = true
                }

                if (coverageSize >= coverage) {
                    writer.println(firstBed.getBed())
                }

                firstLine = firstReader.readLine()

                if (firstLine == null) {
                    break
                }
                firstBed = setter.setBasicBed(firstLine)
            }

        }


        writer.close()
        secondReader.close()
        secondReader.close()
        firstReader.close()
        firstReader.close()
    }


    // elementOf by percent (float), file
    @Throws(IOException::class, BedFormatError::class)
    fun elementOf(first: String, inputFiles: Array<String>, output: String, coverage: Float) {
        val inputList = ArrayList(Arrays.asList(inputFiles))
        this.elementOf(first, inputList, output, coverage)
    }

    /**
     * test if two bed regions have coverage above specific threshold (bp)
     * @param first: path to first bed file
     * @param output: path to output file
     * @param coverage: how many bp
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun elementOf(first: String, inputFiles: ArrayList, output: String?, coverage: Float) {
        val merger = MergeBeds()
        val temporaryFile: File

        // if there are more than one additional files, merge it together before elementOf
        if (inputFiles.size() > 1) {
            val inputDirectory = File(first).getAbsoluteFile().getParentFile()

            temporaryFile = File(inputDirectory, String.valueOf(inputFiles.hashCode()))

            merger.merge(inputFiles, temporaryFile.toString())
        } else {
            temporaryFile = File(inputFiles.remove(0).toString())
        }

        try {
            this.elementOf(first, temporaryFile.toString(), output, coverage)
        } catch (e: IOException) {
            throw e
        } catch (e: BedFormatError) {
            throw e
        } finally {
            if (!temporaryFile.delete()) {
                System.err.println("Temporary File: " + temporaryFile.toString() + " deletion failed")
            }
        }
    }

    /**
     * test if two bed regions have coverage above specific threshold (percentage of first one)
     * @param first: path to first bed file
     * @param second: path to second bed file
     * @param output: path to output file
     * @param coverage: percentage, like: 100%
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun elementOf(first: String, second: String, output: String?, coverage: Float) {
        var firstLine: String? = null
        var secondLine: String? = null
        var pointer: Long = 0       // use this to log the first match location
        var logged = false  // use this to check if this is the first match

        var firstBed: BasicBed
        var secondBed: BasicBed
        val writer: PrintWriter

        if (output == null) {
            writer = PrintWriter(System.out, true)
        } else {
            writer = PrintWriter(output)
        }

        val firstReader = RandomAccessFile(first, "r")
        val secondReader = RandomAccessFile(second, "r")

        firstLine = firstReader.readLine()
        secondLine = secondReader.readLine()

        firstBed = setter.setBasicBed(firstLine)
        secondBed = setter.setBasicBed(secondLine)


        while (true) {
            val comparision = firstBed.isOverlapped(secondBed)
            val coverageSize = firstBed.coverageToPercent(secondBed)

            if (comparision < 0) {
                firstLine = firstReader.readLine()

                if (firstLine == null) {
                    break
                }
                firstBed = setter.setBasicBed(firstLine)

                // if first file read next line, switch second pointer back
                if (logged) {
                    logged = false
                    secondReader.seek(pointer)
                    secondLine = secondReader.readLine()
                    secondBed = setter.setBasicBed(secondLine)
                }

            } else if (coverageSize == -1f) {
                if (!logged) {
                    pointer = secondReader.getFilePointer()
                }
                secondLine = secondReader.readLine()

                if (secondLine == null) {
                    break
                }
                secondBed = setter.setBasicBed(secondLine)
            } else {
                // if this is the first time of match, log it
                if (!logged) {
                    logged = true
                }

                if (coverageSize >= coverage) {
                    writer.println(firstBed.getBed())
                }

                firstLine = firstReader.readLine()

                if (firstLine == null) {
                    break
                }
                firstBed = setter.setBasicBed(firstLine)
            }

        }


        writer.close()
        firstReader.close()
        secondReader.close()
    }


    // elementOf by bp (integer), stdout
    @Throws(IOException::class, BedFormatError::class)
    fun elementOf(first: String, inputFiles: Array<String>, coverage: Int) {
        val inputList = ArrayList(Arrays.asList(inputFiles))
        this.elementOf(first, inputList, coverage)
    }

    /**
     *
     * @param first: path to first file
     * @param inputFiles: a collection of input files
     * @param coverage: bp, like 1
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun elementOf(first: String, inputFiles: ArrayList, coverage: Int) {
        this.elementOf(first, inputFiles, null, coverage)
    }

    /**
     * test if two bed regions have coverage above specific threshold (bp)
     * @param first: path to first bed file
     * @param second: path to second bed file
     * @param coverage: how many bp
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun elementOf(first: String, second: String, coverage: Int) {
        this.elementOf(first, second, null, coverage)
    }


    // elementOf by percentage (float), stdout
    @Throws(IOException::class, BedFormatError::class)
    fun elementOf(first: String, inputFiles: Array<String>, coverage: Float) {
        val inputList = ArrayList(Arrays.asList(inputFiles))
        this.elementOf(first, inputList, coverage)
    }

    /**
     *
     * @param first: path to first bed file
     * @param inputFiles: a collection of input files
     * @param coverage: percentage, like: 100%
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun elementOf(first: String, inputFiles: ArrayList, coverage: Float) {
        this.elementOf(first, inputFiles, null, coverage)
    }

    /**
     * test if two bed regions have coverage above specific threshold (percentage of first one)
     * @param first: path to first bed file
     * @param second: path to second bed file
     * @param coverage: percentage, like: 100%
     * @throws IOException
     * @throws BedFormatError
     */
    @Throws(IOException::class, BedFormatError::class)
    fun elementOf(first: String, second: String, coverage: Float) {
        this.elementOf(first, second, null, coverage)
    }


    // non-element-of
    @Throws(IOException::class, BedFormatError::class)
    fun nonElementOf(first: String, second: String, coverage: Float) {
        this.nonElementOf(first, second, null, coverage)
    }

    @Throws(IOException::class, BedFormatError::class)
    fun nonElementOf(first: String, second: String, coverage: Int) {
        this.nonElementOf(first, second, null, coverage)
    }

    @Throws(IOException::class, BedFormatError::class)
    fun nonElementOf(first: String, second: String, output: String?, coverage: Float) {
        var firstLine: String? = null
        var secondLine: String? = null

        var firstBed: BasicBed
        var secondBed: BasicBed
        val writer: PrintWriter

        if (output == null) {
            writer = PrintWriter(System.out, true)
        } else {
            writer = PrintWriter(output)
        }

        val firstReader = RandomAccessFile(first, "r")
        val secondReader = RandomAccessFile(second, "r")

        firstLine = firstReader.readLine()
        secondLine = secondReader.readLine()

        firstBed = setter.setBasicBed(firstLine)
        secondBed = setter.setBasicBed(secondLine)


        while (true) {
            val comparision = firstBed.isOverlapped(secondBed)

            if (comparision == 0) {
                firstLine = firstReader.readLine()

                if (firstLine == null) {
                    break
                }
                firstBed = setter.setBasicBed(firstLine)
            } else if (comparision > 0) {
                secondLine = secondReader.readLine()

                if (secondLine == null) {
                    break
                }

                secondBed = setter.setBasicBed(secondLine)
            } else {

                if (firstBed.coverageToPercent(secondBed) <= coverage) {
                    writer.println(firstBed.getBed())
                }

                firstLine = firstReader.readLine()

                if (firstLine == null) {
                    break
                }
                firstBed = setter.setBasicBed(firstLine)
            }
        }

        while ((firstLine = firstReader.readLine()) != null) {
            writer.println(firstLine)
        }

        writer.close()
        secondReader.close()
        firstReader.close()

    }

    @Throws(IOException::class, BedFormatError::class)
    fun nonElementOf(first: String, second: String, output: String?, coverage: Int) {
        var firstLine: String? = null
        var secondLine: String? = null

        var firstBed: BasicBed
        var secondBed: BasicBed
        val writer: PrintWriter

        if (output == null) {
            writer = PrintWriter(System.out, true)
        } else {
            writer = PrintWriter(output)
        }

        val firstReader = RandomAccessFile(first, "r")
        val secondReader = RandomAccessFile(second, "r")

        firstLine = firstReader.readLine()
        secondLine = secondReader.readLine()

        firstBed = setter.setBasicBed(firstLine)
        secondBed = setter.setBasicBed(secondLine)


        while (true) {
            val comparision = firstBed.isOverlapped(secondBed)

            if (comparision == 0) {
                firstLine = firstReader.readLine()

                if (firstLine == null) {
                    break
                }
                firstBed = setter.setBasicBed(firstLine)
            } else if (comparision > 0) {
                secondLine = secondReader.readLine()

                if (secondLine == null) {
                    break
                }
                secondBed = setter.setBasicBed(secondLine)
            } else {

                if (firstBed.coverageTo(secondBed) <= coverage) {
                    writer.println(firstBed.getBed())
                }

                firstLine = firstReader.readLine()

                if (firstLine == null) {
                    break
                }
                firstBed = setter.setBasicBed(firstLine)
            }
        }

        while ((firstLine = firstReader.readLine()) != null) {
            writer.println(firstLine)
        }

        if (firstLine != null) {
            // if second file is finished, but first file still have content, just print it all
            do {
                writer.println(setter.setBasicBed(firstLine).getBed())
            } while ((firstLine = firstReader.readLine()) != null)
        } else {
            // if the first file is finished, but second still have content, just test it all
            do {
                if (firstBed.coverageTo(secondBed) <= coverage) {
                    writer.println(firstBed.getBed())
                }
                secondBed = setter.setBasicBed(secondLine)
            } while ((secondLine = secondReader.readLine()) != null)
        }


        writer.close()
        secondReader.close()
        firstReader.close()

    }
}

