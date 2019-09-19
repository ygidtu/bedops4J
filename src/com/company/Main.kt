package com.company

import java.io.IOException
import basic.*
import org.apache.commons.cli.*
import java.io.File
import java.security.InvalidParameterException
import java.util.ArrayList
import func.*

/**
 * @author Zhang Yiming
 * @since 2018.2.20
 * Java implementation of BEDOPS
 * Error codes: 101: bed format error; 102: IO exceptions
 */

internal class ParameterParser(cmd: CommandLine) {
    /**
     * get output file path
     */
    var outputFile: String? = null
    var cmd: CommandLine? = null

    /**
     * get coverage percentage, in -e or -n parameter
     * @return: String 0, 0%
     */
    val cov: String
        get() {
            val cov = cmd!!.getArgList().get(0)

            try {
                Integer.valueOf(cov)
                Float.valueOf(cov.replace("%", ""))
                return cov
            } catch (e: NumberFormatException) {
                return "100%"
            }

        }

    /**
     * get list of input files
     * @return
     */
    // modify parameters
    // do not do anything about output file here
    val files: ArrayList<String>
        get() {
            val fileList = ArrayList()
            for (s in cmd!!.getArgs()) {
                val tmpFile = File(s)

                if (s.equals(outputFile)) {
                    continue
                } else if (tmpFile.isFile()) {
                    fileList.add(s)
                }
            }
            return fileList
        }

    init {
        this.cmd = cmd
        this.outputFile = cmd.getOptionValue("output")
    }


    fun getFile(label: String): String {
        return cmd!!.getOptionValue(label)
    }

    fun hasOption(label: String): Boolean {
        return cmd!!.hasOption(label)
    }

    fun hasOption(label: List<String>): Boolean {
        for (s in label) {
            if (cmd!!.hasOption(s)) {
                return true
            }
        }
        return false
    }

    fun getOptionValue(label: String): String {
        return cmd!!.getOptionValue(label)
    }
}


internal class ParameterConstructer(args: Array<String>) {
    var options = Options()
    var parser: CommandLineParser
    var formatter: HelpFormatter
    var cmdParser: ParameterParser

    init {

        val output = Option(
                "o", "output", true,
                "output file path"
        )
        options.addOption(output)


        val merge = Option(
                "m", "merge", false,
                "file paths that needs to be merged"
        )
        options.addOption(merge)


        val preSort = Option(
                "S", "pre-sort", false,
                "Whether files need to be sorted in advance"
        )
        options.addOption(preSort)


        val sort = Option(
                "s", "sort", true,
                "file path that needs to be sorted"
        )
        options.addOption(sort)

        val everything = Option(
                "u", "everything", false,
                "file paths that needs to concatenating"
        )
        options.addOption(everything)

        val coverage = Option(
                "e", "element-of", false,
                "[bp | percentage] ReferenceFile File2 [File]*\n" + "                 by default, -e 100% is used.  'bedops -e 1' is also popular."
        )
        options.addOption(coverage)

        val nonCoverage = Option(
                "n", "non-element-of", false,
                "[bp | percentage] ReferenceFile File2 [File]*\n" + "                 by default, -n 100% is used.  'bedops -n 1' is also popular."
        )
        options.addOption(nonCoverage)

        val complement = Option(
                "c", "complement", false,
                "File1 [File]*"
        )
        options.addOption(complement)

        val chop = Option(
                "w", "chop", true,
                "[bp] [--stagger <nt>] File1 [File]*\n" + "                 by default, -w 1 is used with no staggering*"
        )
        options.addOption(chop)

        val stagger = Option(null, "stagger", true,
                "[nt] only worked for chop"
        )
        options.addOption(stagger)

        val intersect = Option(
                "i", "intersect", false,
                "File1 File2 [File]*"
        )
        options.addOption(intersect)

        val closestFeature = Option(
                "cf", "closest-features", false,
                "File1 File2"
        )
        options.addOption(closestFeature)

        val closest = Option(null, "closest", false,
                "Only work with closest-features, only print the closest element"
        )
        options.addOption(closest)

        val closestCenter = Option(null, "center", false,
                "Only work with closest-features, print the distance between center of two elements"
        )
        options.addOption(closestCenter)

        val closestDist = Option(null, "dist", false,
                "Only work with closest-features, print the distance betweeen two elements"
        )
        options.addOption(closestDist)

        val difference = Option(
                "d", "difference", false,
                "ReferenceFile File2 [File]*"
        )
        options.addOption(difference)

        val symmdiff = Option(
                "sd", "symmdiff", false,
                "File1 File2 [File]*"
        )
        options.addOption(symmdiff)

        val partition = Option(
                "p", "partition", false,
                "File1 [File]*"
        )
        options.addOption(partition)

        // retrieve all the parameters
        this.parser = DefaultParser()
        this.formatter = HelpFormatter()

        if (args.size == 0) {
            formatter.printHelp("Bedops for Java", options)
            System.exit(1)
        }


        try {
            val cmd = parser.parse(options, args)
            this.cmdParser = ParameterParser(cmd)
        } catch (e: ParseException) {
            System.out.println(e.getMessage())
            formatter.printHelp("Bedops for Java", options)

            System.exit(1)
        }

    }

    fun printHelp() {
        formatter.printHelp("Bedops for Java", options)
    }
}


internal class PerformFuntion(private val cmdParser: ParameterParser) {
    private val inputFiles: ArrayList<String>
    private val outputFile: String?
    private val rootDir = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile()
    // MyLogger logger = new MyLogger();
    // check if need for merge
    private val merger = MergeBeds()
    private val chopper = ChopBed()
    private val sorter = SortBed()

    // get inputFile number
    val inputFilesNumber: Int
        get() = inputFiles.size()

    init {
        this.inputFiles = cmdParser.files
        this.outputFile = cmdParser.outputFile
    }

    fun checkInputNumber() {
        if (this.inputFilesNumber < 2) {
            System.err.println("Less than 2 files")
            System.exit(0)
        }
    }

    // run complement function
    @Throws(IOException::class, BedFormatError::class)
    fun complement() {
        this.checkInputNumber()
        val complement = ComplementBed()
        complement.complement(inputFiles, outputFile)
    }

    // run chop
    @Throws(IOException::class, BedFormatError::class)
    fun chop() {
        var tmp: String?
        val chop: Int
        val stagger: Int

        tmp = cmdParser.getOptionValue("chop")

        if (tmp == null) {
            tmp = "1"
        }
        chop = Integer.valueOf(tmp)

        tmp = cmdParser.getOptionValue("stagger")

        if (tmp == null) {
            tmp = "0"
        }
        stagger = Integer.valueOf(tmp)

        for (s in cmdParser.cmd!!.getArgs()) {
            System.out.println(s)
        }
        chopper.chop(inputFiles, outputFile, chop, stagger)

    }

    // run merge
    @Throws(IOException::class, BedFormatError::class)
    fun merge() {
        this.checkInputNumber()
        merger.merge(inputFiles, outputFile)
    }

    // run everything
    @Throws(IOException::class, BedFormatError::class)
    fun everything() {
        this.checkInputNumber()
        merger.merge(inputFiles, outputFile)
    }

    // run sort
    @Throws(IOException::class, BedFormatError::class)
    fun preSort() {
        for (s in inputFiles) {
            sorter.sortBigFile(s, s)
        }
    }

    @Throws(IOException::class, BedFormatError::class)
    fun sort() {
        sorter.sortBigFile(cmdParser.getOptionValue("sort"), outputFile)
    }

    // run element-of or non-element-of
    @Throws(IOException::class, BedFormatError::class)
    fun coverage() {
        this.checkInputNumber()

        val first: String
        val second: String
        first = inputFiles.get(0)
        val cov = cmdParser.cov

        val runCoverage = CoverageBed()


        if (inputFiles.size() === 2) {
            second = inputFiles.get(1)
        } else {
            second = File(FilePath.combine(rootDir, "tmp.bed")).toString()
            merger.merge(inputFiles.subList(1, inputFiles.size() - 1), second)
        }

        // run functions
        if (cmdParser.hasOption("element-of")) {
            if (cov.endsWith("%")) {
                runCoverage.elementOf(
                        first, second, outputFile,
                        Float.valueOf(cov.replace("%", ""))
                )
            } else {
                runCoverage.elementOf(
                        first, second, outputFile,
                        Integer.valueOf(cov.replace("%", ""))
                )
            }
        } else {
            if (cov.endsWith("%")) {
                runCoverage.nonElementOf(
                        first, second, outputFile,
                        Float.valueOf(cov.replace("%", ""))
                )
            } else {
                runCoverage.nonElementOf(
                        first, second, outputFile,
                        Integer.valueOf(cov.replace("%", ""))
                )
            }
        }

        // if this temporary file are used, delete if
        if (inputFiles.size() > 2) {
            File(second).delete()
        }
    }

    // run difference
    @Throws(IOException::class, BedFormatError::class)
    fun difference() {
        val differ = DifferentBed()

        differ.difference(inputFiles, outputFile)
    }

    // run symdiff
    @Throws(IOException::class, BedFormatError::class)
    fun symmdiff() {
        val differ = DifferentBed()

        differ.symmetricDifference(inputFiles, outputFile)
    }

    // run partition
    @Throws(IOException::class, BedFormatError::class)
    fun pattition() {
        val parter = Partition()

        this.checkInputNumber()
        if (inputFiles.size() === 2) {
            parter.partition(inputFiles.get(0), inputFiles.get(1), outputFile)
        } else if (inputFiles.size() > 2) {
            parter.partition(inputFiles, outputFile)
        }
    }

    // run intersect
    @Throws(IOException::class, BedFormatError::class)
    fun intersect() {
        val intersect = IntersectBed()

        this.checkInputNumber()
        if (inputFilesNumber == 2) {
            intersect.intersect(inputFiles.get(0), inputFiles.get(1), outputFile)
        } else {
            intersect.intersect(inputFiles, outputFile)
        }
    }

    // run closest-feature
    @Throws(IOException::class, BedFormatError::class)
    fun closestFeature() {
        var dist = false
        var center = false
        var closest = false

        if (cmdParser.hasOption("dist")) {
            dist = true
        }

        if (cmdParser.hasOption("center")) {
            center = true
        }

        if (cmdParser.hasOption("closest")) {
            closest = true
        }

        val closestFeature = ClosestFeatures()

        if (closest) {
            closestFeature.closestFeatures(inputFiles.get(0), inputFiles.get(1), outputFile, dist, center)
        } else {
            closestFeature.closestFeaturesAll(inputFiles.get(0), inputFiles.get(1), outputFile, dist, center)
        }

    }
}


object Main {

    fun main(args: Array<String>) {
        // write your code here
        // default parameters
        // MyLogger logger = new MyLogger();
        // Construct parameters
        val constParser = ParameterConstructer(args)
        val cmdParser = constParser.cmdParser
        // construct runner
        val runner = PerformFuntion(cmdParser)

        // run
        val sorter = SortBed()

        if (args.size == 0 || cmdParser.hasOption("help")) {
            constParser.printHelp()
            System.exit(0)
        }


        try {
            // check if its need to be sort first
            if (cmdParser.hasOption("pre-sort")) {
                runner.preSort()
            }

            // run other functions
            if (cmdParser.hasOption("complement")) {
                runner.complement()
            } else if (cmdParser.hasOption("chop")) {
                runner.chop()
            } else if (cmdParser.hasOption("merge")) {
                runner.merge()
            } else if (cmdParser.hasOption("everything")) {
                runner.everything()
            } else if (cmdParser.hasOption("element-of") || cmdParser.hasOption("non-element-of")) {
                runner.coverage()
            } else if (cmdParser.hasOption("intersect")) {
                runner.intersect()
            } else if (cmdParser.hasOption("sort")) {
                runner.sort()
            } else if (cmdParser.hasOption("closest-features")) {
                runner.closestFeature()
            } else if (cmdParser.hasOption("partition")) {
                runner.pattition()
            } else if (cmdParser.hasOption("difference")) {
                runner.difference()
            } else if (cmdParser.hasOption("symmdiff")) {
                runner.symmdiff()
            }
        } catch (e: NumberFormatException) {
            System.err.println(e.getMessage())
        } catch (e: BedFormatError) {
            System.err.println(e.getMessage())
            System.err.println(e.getStackTrace())
            System.exit(101)
        } catch (e: IOException) {
            System.err.println(e.getMessage())
            System.err.println(e.getStackTrace())
            System.exit(102)
        } catch (e: InvalidParameterException) {
            System.err.println(e.getMessage())
            System.exit(100)
        } catch (e: Exception) {
            System.err.println(e.getMessage())
            System.err.println(e.getStackTrace())
            System.exit(100)
        }

    }
}
