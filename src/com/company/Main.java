package com.company;
import java.io.FileNotFoundException;
import java.io.IOException;

import basic.BedFormatError;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import func.*;
import org.apache.commons.cli.*;
import java.io.File;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Zhang Yiming
 * @since 2018.2.20
 * Java implementation of BEDOPS
 * Error codes: 101: bed format error; 102: IO exceptions
 */

class ParameterParser {
    String outputFile = null;
    CommandLine cmd = null;

    ParameterParser (CommandLine cmd) {
        this.cmd = cmd;
        this.outputFile = cmd.getOptionValue("output");
    }

    /**
     * get output file path
     */
    public String getOutputFile() {return this.outputFile; }

    /**
     * get coverage percentage, in -e or -n parameter
     * @return: String 0, 0%
     */
    public String getCov() {
        String cov = cmd.getArgList().get(0);

        try{
            Integer.valueOf(cov);
            Float.valueOf(cov.replace("%", ""));
            return cov;
        } catch (NumberFormatException e) {
            return "100%";
        }

    }

    /**
     * get list of input files
     * @return
     */
    public ArrayList<String> getFiles() {
        ArrayList<String> fileList = new ArrayList<>();

        // modify parameters
        for(String s: cmd.getArgs()) {
            File tmpFile = new File(s);

            if ( s.equals(outputFile) ) { // do not do anything about output file here
                continue;
            } else  if ( tmpFile.isFile() ) {
                fileList.add(s);
            }
        }
        return fileList;
    }


    public String getFile(String label) {
        return cmd.getOptionValue(label);
    }

    public boolean hasOption(String label) {return cmd.hasOption(label);}

    public boolean hasOption(List<String> label) {
        for(String s: label) {
            if(cmd.hasOption(s)) {
                return true;
            }
        }
        return false;
    }

    public String getOptionValue(String label) { return cmd.getOptionValue(label);}
}


class ParameterConstructer {
    Options options = new Options();
    CommandLineParser parser;
    HelpFormatter formatter;
    ParameterParser cmdParser;

    public ParameterConstructer (String[] args) {

        Option output = new Option(
                "o", "output", true,
                "output file path"
        );
        options.addOption(output);


        Option merge = new Option(
                "m", "merge", false,
                "file paths that needs to be merged"
        );
        options.addOption(merge);


        Option preSort = new Option(
                "S", "pre-sort", false,
                "Whether files need to be sorted in advance"
        );
        options.addOption(preSort);


        Option sort = new Option(
                "s", "sort", true,
                "file path that needs to be sorted"
        );
        options.addOption(sort);

        Option everything = new Option(
                "u", "everything", false,
                "file paths that needs to concatenating"
        );
        options.addOption(everything);

        Option coverage = new Option(
                "e", "element-of", false,
                "[bp | percentage] ReferenceFile File2 [File]*\n" +
                        "                 by default, -e 100% is used.  'bedops -e 1' is also popular."
        );
        options.addOption(coverage);

        Option nonCoverage = new Option(
                "n", "non-element-of", false,
                "[bp | percentage] ReferenceFile File2 [File]*\n" +
                        "                 by default, -n 100% is used.  'bedops -n 1' is also popular."
        );
        options.addOption(nonCoverage);

        Option complement = new Option(
                "c", "complement", false,
                "File1 [File]*"
        );
        options.addOption(complement);

        Option chop = new Option(
                "w", "chop", true,
                "[bp] [--stagger <nt>] File1 [File]*\n" +
                        "                 by default, -w 1 is used with no staggering*"
        );
        options.addOption(chop);

        Option stagger = new Option(
                null, "stagger", true,
                "[nt] only worked for chop"
        );
        options.addOption(stagger);

        Option intersect = new Option(
                "i", "intersect", false,
                "File1 File2 [File]*"
        );
        options.addOption(intersect);

        Option closestFeature = new Option(
                "cf", "closest-features", false,
                "File1 File2"
        );
        options.addOption(closestFeature);

        Option closest = new Option(
                null, "closest", false,
                "Only work with closest-features, only print the closest element"
        );
        options.addOption(closest);

        Option closestCenter = new Option(
                null, "center", false,
                "Only work with closest-features, print the distance between center of two elements"
        );
        options.addOption(closestCenter);

        Option closestDist = new Option(
                null, "dist", false,
                "Only work with closest-features, print the distance betweeen two elements"
        );
        options.addOption(closestDist);

        // retrieve all the parameters
        this.parser = new DefaultParser();
        this.formatter = new HelpFormatter();

        if ( args.length == 0 ) {
            formatter.printHelp("Bedops for Java", options);
            System.exit(1);
        }


        try {
            CommandLine cmd = parser.parse(options, args);
            this.cmdParser = new ParameterParser(cmd);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Bedops for Java", options);

            System.exit(1);
        }
    }

    public ParameterParser getCmdParser() {
        return this.cmdParser;
    }

    public void printHelp() { formatter.printHelp("Bedops for Java", options);}
}


class PerformFuntion {
    private ParameterParser cmdParser;
    private ArrayList<String> inputFiles;
    private String outputFile;
    private String rootDir = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
    // MyLogger logger = new MyLogger();
    // check if need for merge
    private MergeBeds merger = new MergeBeds();
    private ChopBed chopper = new ChopBed();
    private SortBed sorter = new SortBed();

    public PerformFuntion(ParameterParser cmdParser) {
        this.cmdParser = cmdParser;
        this.inputFiles = cmdParser.getFiles();
        this.outputFile = cmdParser.getOutputFile();
    }

    public void checkInputNumber() {
        if(this.getInputFilesNumber() < 2) {
            System.err.println("Less than 2 files");
            System.exit(0);
        }
    }

    // get inputFile number
    public int getInputFilesNumber() {return inputFiles.size();}

    // run complement function
    public void complement () throws IOException, BedFormatError {
        this.checkInputNumber();
        ComplementBed complement = new ComplementBed();
        complement.complement(inputFiles, outputFile);
    }

    // run chop
    public void chop () throws IOException, BedFormatError {
        String tmp;
        int chop, stagger;

        tmp = cmdParser.getOptionValue("chop");

        if (tmp == null) {
            tmp = "1";
        }
        chop = Integer.valueOf(tmp);

        tmp = cmdParser.getOptionValue("stagger");

        if (tmp == null) {
            tmp = "0";
        }
        stagger = Integer.valueOf(tmp);

        for (String s : cmdParser.cmd.getArgs()) {
            System.out.println(s);
        }
        chopper.chop(inputFiles, outputFile, chop, stagger);

    }

    // run merge
    public void merge () throws IOException, BedFormatError {
        this.checkInputNumber();
        merger.merge(inputFiles, outputFile);
    }

    // run everything
    public void everything () throws IOException, BedFormatError {
        this.checkInputNumber();
        merger.merge(inputFiles, outputFile);
    }

    // run sort
    public void preSort () throws IOException, BedFormatError {
        for(String s: inputFiles) {
            sorter.sortBigFile(s, s);
        }
    }

    public void sort() throws IOException, BedFormatError {
        sorter.sortBigFile(cmdParser.getOptionValue("sort"), outputFile);
    }

    // run element-of or non-element-of
    public void coverage() throws IOException, BedFormatError {
        this.checkInputNumber();

        String first, second;
        first = inputFiles.get(0);
        String cov = cmdParser.getCov();

        CoverageBed runCoverage = new CoverageBed();


        if (inputFiles.size() == 2) {
            second = inputFiles.get(1);
        } else {
            second = new File(FilePath.combine(rootDir, "tmp.bed")).toString();
            merger.merge(inputFiles.subList(1, inputFiles.size() - 1), second);
        }

        // run functions
        if (cmdParser.hasOption("element-of")) {
            if (cov.endsWith("%")) {
                runCoverage.elementOf(
                        first, second, outputFile,
                        Float.valueOf(cov.replace("%", ""))
                );
            } else {
                runCoverage.elementOf(
                        first, second, outputFile,
                        Integer.valueOf(cov.replace("%", ""))
                );
            }
        } else {
            if (cov.endsWith("%")) {
                runCoverage.nonElementOf(
                        first, second, outputFile,
                        Float.valueOf(cov.replace("%", ""))
                );
            } else {
                runCoverage.nonElementOf(
                        first, second, outputFile,
                        Integer.valueOf(cov.replace("%", ""))
                );
            }
        }

        // if this temporary file are used, delete if
        if (inputFiles.size() > 2) { new File(second).delete(); }
    }

    // run partition
    public void pattition () throws IOException, BedFormatError {
        Partition parter = new Partition();

        this.checkInputNumber();
        if (inputFiles.size() == 2) {
            parter.partition(inputFiles.get(0), inputFiles.get(1), outputFile);
        } else if( inputFiles.size() > 2 ) {
            parter.partition(inputFiles, outputFile);
        }
    }

    // run intersect
    public void intersect () throws IOException, BedFormatError {
        IntersectBed intersect = new IntersectBed();

        this.checkInputNumber();
        if (getInputFilesNumber() == 2) {
            intersect.intersect(inputFiles.get(0), inputFiles.get(1), outputFile);
        } else {
            intersect.intersect(inputFiles, outputFile);
        }
    }

    // run closest-feature
    public void closestFeature() throws IOException, BedFormatError {
        boolean dist = false, center = false, closest = false;

        if(cmdParser.hasOption("dist")) {
            dist = true;
        }

        if(cmdParser.hasOption("center")) {
            center = true;
        }

        if(cmdParser.hasOption("closest")) {
            closest = true;
        }

        ClosestFeatures closestFeature = new ClosestFeatures();

        if(closest) {
            closestFeature.closestFeatures(inputFiles.get(0), inputFiles.get(1), outputFile, dist, center);
        } else {
            closestFeature.closestFeaturesAll(inputFiles.get(0), inputFiles.get(1), outputFile, dist, center);
        }

    }
}



public class Main {

    public static void main(String[] args) {
	    // write your code here
        // default parameters
        // MyLogger logger = new MyLogger();
        // Construct parameters
        ParameterConstructer constParser = new ParameterConstructer(args);
        ParameterParser cmdParser = constParser.getCmdParser();
        // construct runner
        PerformFuntion runner = new PerformFuntion(cmdParser);

        // run
        SortBed sorter = new SortBed();

        if (args.length == 0 || cmdParser.hasOption("help")) {
            constParser.printHelp();
            System.exit(0);
        }


        try {
            // check if its need to be sort first
            if (cmdParser.hasOption("pre-sort")) {
                runner.preSort();
            }

            // run other functions
            if (cmdParser.hasOption("complement")) {
                runner.complement();
            } else if (cmdParser.hasOption("chop")) {
                runner.chop();
            } else if (cmdParser.hasOption("merge")) {
                runner.merge();
            } else if (cmdParser.hasOption("everything")) {
                runner.everything();
            } else if (cmdParser.hasOption("element-of") || cmdParser.hasOption("non-element-of")) {
                runner.coverage();
            } else if (cmdParser.hasOption("intersect")) {
                runner.intersect();
            } else if (cmdParser.hasOption("sort")) {
                runner.sort();
            } else if (cmdParser.hasOption("closest-features")) {
                runner.closestFeature();
            } else if (cmdParser.hasOption("partition")) {
                runner.pattition();
            }
        }  catch (NumberFormatException e) {
            System.err.println(e.getMessage());
        } catch (BedFormatError e) {
            System.err.println(e.getMessage());
            System.err.println(e.getStackTrace());
            System.exit(101);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println(e.getStackTrace());
            System.exit(102);
        } catch (InvalidParameterException e) {
            System.err.println(e.getMessage());
            System.exit(100);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(e.getStackTrace());
            System.exit(100);
        }

    }
}
