package func;

/**
 * @author zhang
 * @since 2018.3.20
 * @version 0.1
 * A class to perform bedops partition
 */

import basic.BasicBed;
import basic.BedFormatError;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.PrintWriter;
import java.util.ArrayList;


public class Partition {
    SetBasicBed setter = new SetBasicBed();

    public void partition (String first, String second) throws IOException, BedFormatError {
        this.partition(first, second, null);
    }

    /**
     *
     * @param first: first file
     * @param second: second file
     * @param output: if null, redirect to stdout
     */
    public void partition (String first, String second, String output) throws IOException, BedFormatError {
        String firstLine = null, secondLine = null;
        long pointer = 0;
        boolean logged = false;
        RandomAccessFile firstReader = new RandomAccessFile(first, "r");
        RandomAccessFile secondReader = new RandomAccessFile(second, "r");

        PrintWriter writer;
        if(output == null) {
            writer = new PrintWriter(System.out, true);
        } else {
            writer = new PrintWriter(output);
        }

        firstLine = firstReader.readLine();
        secondLine = secondReader.readLine();
        BasicBed firstBed = setter.setBasicBed(firstLine);
        BasicBed secondBed = setter.setBasicBed(secondLine);

        // make sure the pointer does not exceed the limit
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

                for(BasicBed b: firstBed.partition(secondBed)) {
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
    }


    /* // 递归
    private ArrayList<BasicBed> partition (BasicBed first, BasicBed second) {
        return first.partition(second);
    }

    private ArrayList<BasicBed> partition (ArrayList<BasicBed> inputList, BasicBed second) {
        ArrayList<BasicBed> results = new ArrayList<>();
        for(BasicBed b: inputList) {

            ArrayList<BasicBed> tmp = this.partition(b, second);
            if (tmp.isEmpty()) { return tmp; };
            results.addAll(this.partition(tmp, second));
        }
        return results;
    }
    */

    private ArrayList<BasicBed> partitionList (String first, String second) throws IOException, BedFormatError {
        String firstLine = null, secondLine = null;
        long pointer = 0;
        boolean logged = false;
        ArrayList<BasicBed> results = new ArrayList<>();
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
                results.addAll(firstBed.partition(secondBed));

                secondLine = secondReader.readLine();

                if(secondLine == null) {
                    break;
                }
                secondBed = setter.setBasicBed(secondLine);
            }
        }
        return results;
    }

    /**
     *
     * @param infile: a single file
     * @param inputList: a list of BasicBed
     * @return: partitioned BasicBed
     * @throws IOException
     * @throws BedFormatError
     */
    private ArrayList<BasicBed> partition (String infile, ArrayList<BasicBed> inputList) throws IOException, BedFormatError  {
        String line = null;
        int index = 0;
        long pointer = 0;
        boolean logged = false;
        ArrayList<BasicBed> results = new ArrayList<>();
        RandomAccessFile reader = new RandomAccessFile(infile, "r");

        line = reader.readLine();
        BasicBed firstBed = inputList.get(index);
        BasicBed secondBed = setter.setBasicBed(line);

        while (index < inputList.size() ) {
            int overlap = firstBed.isOverlapped(secondBed);

            if (overlap > 0) {
                if(!logged) {
                    pointer = reader.getFilePointer();
                }
                line = reader.readLine();

                if(line == null) {
                    break;
                }
                secondBed = setter.setBasicBed(line);
            } else if (overlap < 0) {
                index ++;
                firstBed = inputList.get(index);

                if(logged) {
                    logged = false;
                    reader.seek(pointer);
                    line = reader.readLine();
                    secondBed = setter.setBasicBed(line);
                }
            } else {
                if(!logged) {
                    logged = true;
                }
                results.addAll(firstBed.partition(secondBed));

                line = reader.readLine();

                if(line == null) {
                    break;
                }
                secondBed = setter.setBasicBed(line);
            }
        }
        return results;
    }

    public void partition (ArrayList<String> inputFiles) throws IOException, BedFormatError {
        this.partition(inputFiles, null);

    }

    /**
     *
     * @param inputFiles: input files
     * @param output: output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    public void partition (ArrayList<String> inputFiles, String output) throws IOException, BedFormatError {
        ArrayList<BasicBed> results = this.partitionList(inputFiles.get(0), inputFiles.get(1));
        for(int i=2; i < inputFiles.size(); i++) {
            results = this.partition(inputFiles.get(i), results);
        }
        PrintWriter writer;
        if(output == null) {
            writer = new PrintWriter(System.out, true);
        } else {
            writer = new PrintWriter(output);
        }

        for(BasicBed b: results) {
            writer.println(b.getBed());
        }

        writer.close();
    }
}
