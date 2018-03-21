package func;

/**
 * @author Zhang
 * @since 2018.3.20
 * @version
 * A class to perform difference and Symmetric difference
 *
 * when input as file list, use the rest files to compare to the first one,
 * therefore, the results may in random order
 */

import basic.BasicBed;
import basic.BedFormatError;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DifferentBed {
    private SetBasicBed setter = new SetBasicBed();

    public void difference(String first, String second) throws IOException, BedFormatError {
        this.difference(first, second, null);
    }

    /**
     * find difference between two bed files
     * @param first: path to first file
     * @param second: path to second file
     * @param output: path to output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    public void symmetricDifference(String first, String second, String output) throws IOException, BedFormatError {
        // create print writer
        PrintWriter writer;
        if(output == null) {
            writer = new PrintWriter(System.out, true);
        } else {
            writer = new PrintWriter(output);
        }
        String firstLine = null, secondLine = null;
        long pointer = 0;
        boolean logged = false;

        RandomAccessFile firstReader = new RandomAccessFile(first, "r");
        RandomAccessFile secondReader = new RandomAccessFile(second, "r");

        firstLine = firstReader.readLine();
        secondLine = secondReader.readLine();

        BasicBed firstBed = setter.setBasicBed(firstLine);
        BasicBed secondBed = setter.setBasicBed(secondLine);

        while ( true ) {

            int overlap = firstBed.isOverlapped(secondBed);

            if (overlap > 0) {
                if(!logged) {
                    pointer = secondReader.getFilePointer();
                }
                secondLine = secondReader.readLine();

                if(secondLine == null) {
                    break;
                }
                secondBed = setter.setBasicBed(secondLine);
            } else if (overlap < 0) {
                firstLine = firstReader.readLine();

                if(firstLine == null) {
                    break;
                }
                firstBed = setter.setBasicBed(firstLine);

                if(logged) {
                    logged = false;
                    secondReader.seek(pointer);
                    secondLine = secondReader.readLine();
                    secondBed = setter.setBasicBed(secondLine);
                }
            } else {
                if(!logged) {
                    logged = true;
                }

                for(BasicBed b: firstBed.difference(secondBed)) {
                    writer.println(b.getBed());
                }

                secondLine = secondReader.readLine();

                if(secondLine == null) {
                    break;
                }
                secondBed = setter.setBasicBed(secondLine);
            }
        }

        writer.close();
        firstReader.close();
        secondReader.close();
    }

    /**
     * compare the difference between first file and the rest
     * @param inputList: a list of files
     * @param output: output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    public void symmetricDifference(ArrayList<String> inputList, String output) throws IOException, BedFormatError {
        for(int i = 1; i < inputList.size(); i++) {
            this.difference(inputList.get(0), inputList.get(i), output);
        }
    }

    public void symmetricDifference(String first, String second) throws IOException, BedFormatError {
        this.difference(first, second, null);
    }

    /**
     * find difference between two bed files
     * @param first: path to first file
     * @param second: path to second file
     * @param output: path to output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    public void difference(String first, String second, String output) throws IOException, BedFormatError {
        // create print writer
        PrintWriter writer;
        if(output == null) {
            writer = new PrintWriter(System.out, true);
        } else {
            writer = new PrintWriter(output);
        }
        String firstLine = null, secondLine = null;
        long pointer = 0;
        boolean logged = false;

        RandomAccessFile firstReader = new RandomAccessFile(first, "r");
        RandomAccessFile secondReader = new RandomAccessFile(second, "r");

        firstLine = firstReader.readLine();
        secondLine = secondReader.readLine();

        BasicBed firstBed = setter.setBasicBed(firstLine);
        BasicBed secondBed = setter.setBasicBed(secondLine);

        while ( true ) {

            int overlap = firstBed.isOverlapped(secondBed);

            if (overlap > 0) {
                if(!logged) {
                    pointer = secondReader.getFilePointer();
                }
                secondLine = secondReader.readLine();

                if(secondLine == null) {
                    break;
                }
                secondBed = setter.setBasicBed(secondLine);
            } else if (overlap < 0) {
                firstLine = firstReader.readLine();

                if(firstLine == null) {
                    break;
                }
                firstBed = setter.setBasicBed(firstLine);

                if(logged) {
                    logged = false;
                    secondReader.seek(pointer);
                    secondLine = secondReader.readLine();
                    secondBed = setter.setBasicBed(secondLine);
                }
            } else {
                if(!logged) {
                    logged = true;
                }

                for(BasicBed b: firstBed.symmetricDifference(secondBed)) {
                    writer.println(b.getBed());
                }

                secondLine = secondReader.readLine();

                if(secondLine == null) {
                    break;
                }
                secondBed = setter.setBasicBed(secondLine);
            }
        }

        writer.close();
        firstReader.close();
        secondReader.close();
    }

    /**
     * compare the difference between first file and the rest
     * @param inputList: a list of files
     * @param output: output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    public void difference(ArrayList<String> inputList, String output) throws IOException, BedFormatError {
        for(int i = 1; i < inputList.size(); i++) {
            this.difference(inputList.get(0), inputList.get(i), output);
        }
    }

}
