package func;

/**
 * @author zhang
 * @since 2018.3.20
 * @version 0.1
 * A class to perform element-of and non-element-of function
 */

import basic.BasicBed;
import basic.BedFormatError;
import java.io.*;
import java.util.*;


/**                       CoverageBeds
 * original bedops element-of and not-element-of options
 * @author Zhang Yiming
 * @since 2018.3.4
 */

public class CoverageBed {
    private final SetBasicBed setter = new SetBasicBed();

    // elementOf by bp (integer), file
    public void elementOf(String first, String[] inputFiles, String output, int coverage) throws IOException, BedFormatError {
        ArrayList inputList = new ArrayList<>(Arrays.asList(inputFiles));
        this.elementOf(first, inputList, output, coverage);
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
    public void elementOf(String first, ArrayList inputFiles, String output, int coverage) throws IOException, BedFormatError {
        File temporaryFile;

        // if there are more than one additional files, merge it together before elementOf
        if ( inputFiles.size() > 1 ) {
            MergeBeds merger = new MergeBeds();
            File inputDirectory = new File(first).getAbsoluteFile().getParentFile();

            temporaryFile = new File(inputDirectory, String.valueOf(inputFiles.hashCode()));

            merger.merge(inputFiles, temporaryFile.toString());
        } else {
            temporaryFile = new File(inputFiles.remove(0).toString());
        }

        try{
            this.elementOf(first, temporaryFile.toString(), output, coverage);
        } catch ( IOException | BedFormatError e ) {
            throw e;
        } finally {
            if ( !temporaryFile.delete() ) {
                System.err.println("Temporary File: " + temporaryFile.toString() + " deletion failed");
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
    public void elementOf(String first, String second, String output, int coverage) throws IOException, BedFormatError {
        String firstLine = null;
        String secondLine = null;
        long pointer = 0;       // use this to log the first match location
        boolean logged = false;  // use this to check if this is the first match

        BasicBed firstBed;
        BasicBed secondBed;
        PrintWriter writer;

        if (output == null) {
            writer = new PrintWriter(System.out, true);
        } else {
            writer = new PrintWriter(output);
        }

        RandomAccessFile firstReader = new RandomAccessFile(first, "r");
        RandomAccessFile secondReader = new RandomAccessFile(second, "r");


        firstLine = firstReader.readLine();
        secondLine = secondReader.readLine();

        firstBed = setter.setBasicBed(firstLine);
        secondBed = setter.setBasicBed(secondLine);

        while ( true ) {
            int comparision = firstBed.isOverlapped(secondBed);
            int coverageSize = firstBed.coverageTo(secondBed);

            if ( comparision < 0 ) {
                firstLine = firstReader.readLine();

                if(firstLine == null) {
                    break;
                }

                firstBed = setter.setBasicBed(firstLine);

                // if first file read next line, switch second pointer back
                if (logged) {
                    logged = false;
                    secondReader.seek(pointer);
                    secondLine = secondReader.readLine();
                    secondBed = setter.setBasicBed(secondLine);
                }
            } else if ( coverageSize == -1 ) {
                if(!logged) {
                    pointer = secondReader.getFilePointer();
                }

                secondLine = secondReader.readLine();

                if(secondLine == null) {
                    break;
                }
                secondBed = setter.setBasicBed(secondLine);
            } else {
                // if this is the first time of match, log it
                if (!logged) {
                    logged = true;
                }

                if ( coverageSize >= coverage ) {
                    writer.println(firstBed.getBed());
                }

                firstLine = firstReader.readLine();

                if(firstLine == null) {
                    break;
                }
                firstBed = setter.setBasicBed(firstLine);
            }

        }


        writer.close();
        secondReader.close();
        secondReader.close();
        firstReader.close();
        firstReader.close();
    }


    // elementOf by percent (float), file
    public void elementOf(String first, String[] inputFiles, String output, float coverage) throws IOException, BedFormatError {
        ArrayList inputList = new ArrayList<>(Arrays.asList(inputFiles));
        this.elementOf(first, inputList, output, coverage);
    }

    /**
     * test if two bed regions have coverage above specific threshold (bp)
     * @param first: path to first bed file
     * @param output: path to output file
     * @param coverage: how many bp
     * @throws IOException
     * @throws BedFormatError
     */
    public void elementOf(String first, ArrayList inputFiles, String output, float coverage) throws IOException, BedFormatError {
        MergeBeds merger = new MergeBeds();
        File temporaryFile;

        // if there are more than one additional files, merge it together before elementOf
        if ( inputFiles.size() > 1 ) {
            File inputDirectory = new File(first).getAbsoluteFile().getParentFile();

            temporaryFile = new File(inputDirectory, String.valueOf(inputFiles.hashCode()));

            merger.merge(inputFiles, temporaryFile.toString());
        } else {
            temporaryFile = new File(inputFiles.remove(0).toString());
        }

        try{
            this.elementOf(first, temporaryFile.toString(), output, coverage);
        } catch ( IOException | BedFormatError e ) {
            throw e;
        } finally {
            if ( !temporaryFile.delete() ) {
                System.err.println("Temporary File: " + temporaryFile.toString() + " deletion failed");
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
    public void elementOf(String first, String second, String output, float coverage) throws IOException, BedFormatError {
        String firstLine = null;
        String secondLine = null;
        long pointer = 0;       // use this to log the first match location
        boolean logged = false;  // use this to check if this is the first match

        BasicBed firstBed;
        BasicBed secondBed;
        PrintWriter writer;

        if (output == null) {
            writer = new PrintWriter(System.out, true);
        } else {
            writer = new PrintWriter(output);
        }

        RandomAccessFile firstReader = new RandomAccessFile(first, "r");
        RandomAccessFile secondReader = new RandomAccessFile(second, "r");

        firstLine = firstReader.readLine();
        secondLine = secondReader.readLine();

        firstBed = setter.setBasicBed(firstLine);
        secondBed = setter.setBasicBed(secondLine);


        while ( true ) {
            int comparision = firstBed.isOverlapped(secondBed);
            float coverageSize = firstBed.coverageToPercent(secondBed);

            if ( comparision < 0 ) {
                firstLine = firstReader.readLine();

                if(firstLine == null) {
                    break;
                }
                firstBed = setter.setBasicBed(firstLine);

                // if first file read next line, switch second pointer back
                if (logged) {
                    logged = false;
                    secondReader.seek(pointer);
                    secondLine = secondReader.readLine();
                    secondBed = setter.setBasicBed(secondLine);
                }

            } else if ( coverageSize == -1 ) {
                if(!logged) {
                    pointer = secondReader.getFilePointer();
                }
                secondLine = secondReader.readLine();

                if(secondLine == null) {
                    break;
                }
                secondBed = setter.setBasicBed(secondLine);
            } else {
                // if this is the first time of match, log it
                if (!logged) {
                    logged = true;
                }

                if (  coverageSize >= coverage ) {
                    writer.println(firstBed.getBed());
                }

                firstLine = firstReader.readLine();

                if(firstLine == null) {
                    break;
                }
                firstBed = setter.setBasicBed(firstLine);
            }

        }


        writer.close();
        firstReader.close();
        secondReader.close();
    }


    // elementOf by bp (integer), stdout
    public void elementOf(String first, String[] inputFiles, int coverage) throws IOException, BedFormatError {
        ArrayList<String> inputList = new ArrayList<>(Arrays.asList(inputFiles));
        this.elementOf(first, inputList, coverage);
    }

    /**
     *
     * @param first: path to first file
     * @param inputFiles: a collection of input files
     * @param coverage: bp, like 1
     * @throws IOException
     * @throws BedFormatError
     */
    public void elementOf(String first, ArrayList inputFiles, int coverage) throws IOException, BedFormatError {
        this.elementOf(first, inputFiles, null, coverage);
    }

    /**
     * test if two bed regions have coverage above specific threshold (bp)
     * @param first: path to first bed file
     * @param second: path to second bed file
     * @param coverage: how many bp
     * @throws IOException
     * @throws BedFormatError
     */
    public void elementOf(String first, String second, int coverage) throws IOException, BedFormatError {
        this.elementOf(first, second, null, coverage);
    }


    // elementOf by percentage (float), stdout
    public void elementOf(String first, String[] inputFiles, float coverage) throws IOException, BedFormatError {
        ArrayList<String> inputList = new ArrayList<>(Arrays.asList(inputFiles));
        this.elementOf(first, inputList, coverage);
    }

    /**
     *
     * @param first: path to first bed file
     * @param inputFiles: a collection of input files
     * @param coverage: percentage, like: 100%
     * @throws IOException
     * @throws BedFormatError
     */
    public void elementOf(String first, ArrayList inputFiles, float coverage) throws IOException, BedFormatError {
        this.elementOf(first, inputFiles, null, coverage);
    }

    /**
     * test if two bed regions have coverage above specific threshold (percentage of first one)
     * @param first: path to first bed file
     * @param second: path to second bed file
     * @param coverage: percentage, like: 100%
     * @throws IOException
     * @throws BedFormatError
     */
    public void elementOf(String first, String second, float coverage) throws IOException, BedFormatError {
        this.elementOf(first, second, null, coverage);
    }


    // non-element-of
    public void nonElementOf(String first, String second, float coverage) throws IOException, BedFormatError {
        this.nonElementOf(first, second, null, coverage);
    }

    public void nonElementOf(String first, String second, int coverage) throws IOException, BedFormatError {
        this.nonElementOf(first, second, null,coverage);
    }

    public void nonElementOf(String first, String second, String output, float coverage) throws IOException, BedFormatError {
        String firstLine = null;
        String secondLine = null;

        BasicBed firstBed;
        BasicBed secondBed;
        PrintWriter writer;

        if (output == null) {
            writer = new PrintWriter(System.out, true);
        } else {
            writer = new PrintWriter(output);
        }

        RandomAccessFile firstReader = new RandomAccessFile(first, "r");
        RandomAccessFile secondReader = new RandomAccessFile(second, "r");

        firstLine = firstReader.readLine();
        secondLine = secondReader.readLine();

        firstBed = setter.setBasicBed(firstLine);
        secondBed = setter.setBasicBed(secondLine);


        while ( true ) {
            int comparision = firstBed.isOverlapped(secondBed);

            if ( comparision == 0) {
                firstLine = firstReader.readLine();

                if(firstLine == null) {
                    break;
                }
                firstBed = setter.setBasicBed(firstLine);
            } else if ( comparision > 0 ) {
                secondLine = secondReader.readLine();

                if(secondLine == null) {
                    break;
                }

                secondBed = setter.setBasicBed(secondLine);
            } else {

                if ( firstBed.coverageToPercent(secondBed) <= coverage ) {
                    writer.println(firstBed.getBed());
                }

                firstLine = firstReader.readLine();

                if(firstLine == null) {
                    break;
                }
                firstBed = setter.setBasicBed(firstLine);
            }
        }

        while ((firstLine = firstReader.readLine()) != null) {
            writer.println(firstLine);
        }

        writer.close();
        secondReader.close();
        firstReader.close();

    }

    public void nonElementOf(String first, String second, String output, int coverage) throws IOException, BedFormatError {
        String firstLine = null;
        String secondLine = null;

        BasicBed firstBed;
        BasicBed secondBed;
        PrintWriter writer;

        if (output == null) {
            writer = new PrintWriter(System.out, true);
        } else {
            writer = new PrintWriter(output);
        }

        RandomAccessFile firstReader = new RandomAccessFile(first, "r");
        RandomAccessFile secondReader = new RandomAccessFile(second, "r");

        firstLine = firstReader.readLine();
        secondLine = secondReader.readLine();

        firstBed = setter.setBasicBed(firstLine);
        secondBed = setter.setBasicBed(secondLine);


        while ( true ) {
            int comparision = firstBed.isOverlapped(secondBed);

            if ( comparision == 0) {
                firstLine = firstReader.readLine();

                if(firstLine == null) {
                    break;
                }
                firstBed = setter.setBasicBed(firstLine);
            } else if ( comparision > 0 ) {
                secondLine = secondReader.readLine();

                if(secondLine == null) {
                    break;
                }
                secondBed = setter.setBasicBed(secondLine);
            } else {

                if ( firstBed.coverageTo(secondBed) <= coverage ) {
                    writer.println(firstBed.getBed());
                }

                firstLine = firstReader.readLine();

                if(firstLine == null) {
                    break;
                }
                firstBed = setter.setBasicBed(firstLine);
            }
        }

        while ((firstLine = firstReader.readLine()) != null) {
            writer.println(firstLine);
        }

        if( firstLine != null ) {
            // if second file is finished, but first file still have content, just print it all
            do {
                writer.println(setter.setBasicBed(firstLine).getBed());
            } while ( (firstLine=firstReader.readLine()) != null );
        } else {
            // if the first file is finished, but second still have content, just test it all
            do {
                if ( firstBed.coverageTo(secondBed) <= coverage ) {
                    writer.println(firstBed.getBed());
                }
                secondBed = setter.setBasicBed(secondLine);
            } while ( (secondLine = secondReader.readLine()) != null);
        }


        writer.close();
        secondReader.close();
        firstReader.close();

    }
}

