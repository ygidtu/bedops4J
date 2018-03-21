package func;

/**
 * @author zhang
 * @since 2018.3.20
 * @version 0.1
 * A class to perform intersect function
 */

import basic.BasicBed;
import basic.BedFormatError;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.PrintWriter;
import java.util.ArrayList;



public class IntersectBed {

    SetBasicBed setter = new SetBasicBed();


    public void intersect(String first, String second) throws IOException, BedFormatError {
        this.intersect(first, second, null);
    }

    /**
     * find all intersect between two bed files
     * @param first: path to first file
     * @param second: path to second file
     * @param output: path to output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    public void intersect(String first, String second, String output) throws IOException, BedFormatError {
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

        /*
         test if these two have overlaps
         And check make sure the pointer does not exceed the limit
          */
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

                BasicBed intersected = firstBed.intersect(secondBed);
                if(intersected != null) {
                    writer.println(intersected.getBed());
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

    public ArrayList<BasicBed> intersectList(String first, String second) throws IOException, BedFormatError {
        ArrayList<BasicBed> results = new ArrayList<>();
        String firstLine = null, secondLine = null;
        long pointer = 0;
        boolean logged = false;
        RandomAccessFile firstReader = new RandomAccessFile(first, "r");
        RandomAccessFile secondReader = new RandomAccessFile(second, "r");

        firstLine = firstReader.readLine();
        secondLine = secondReader.readLine();
        BasicBed firstBed = setter.setBasicBed(firstLine);
        BasicBed secondBed = setter.setBasicBed(secondLine);

        // test if these two have overlaps
        while (true) {
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

                results.add(firstBed.intersect(secondBed));

                secondLine = secondReader.readLine();

                if(secondLine == null) {
                    break;
                }
                secondBed = setter.setBasicBed(secondLine);
            }
        }

        firstReader.close();
        secondReader.close();
        return results;
    }

    /**
     * a private method to compare a list of BasicBed and a bed file
     * @param infile: path to a file
     * @param inputList: a list of BasicBed
     * @return: a list of BasicBed
     * @throws IOException
     * @throws BedFormatError
     */
    private ArrayList<BasicBed> intersect(String infile, ArrayList<BasicBed> inputList) throws IOException, BedFormatError  {
        int index = 0;
        long pointer = 0;
        boolean logged = false;
        String line = null;
        ArrayList<BasicBed> results = new ArrayList<>();
        RandomAccessFile reader = new RandomAccessFile(infile, "r");

        line = reader.readLine();
        BasicBed firstBed = inputList.get(index);
        BasicBed secondBed = setter.setBasicBed(line);

        // 文件读取到null后会break，因此，只需要保证列表不超出即可
        while ( index < inputList.size() ) {
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
                results.add(firstBed.intersect(secondBed));

                line = reader.readLine();

                if(line == null) {
                    break;
                }
                secondBed = setter.setBasicBed(line);
            }
        }
        return results;
    }

    public void intersect(ArrayList<String> inputList)  throws IOException, BedFormatError {
        this.intersect(inputList, null);
    }

    /**
     * a method to find intersect of a list of files
     * @param inputList: a list of files, at least two files
     * @param output: path to output file, if null, redirect to stdout
     * @throws IOException
     * @throws BedFormatError
     */
    public void intersect(ArrayList<String> inputList, String output) throws IOException, BedFormatError {
        ArrayList<BasicBed> results = this.intersectList(inputList.get(0), inputList.get(1));

        for(int i=2; i < inputList.size() - 1; i++) {
            results.addAll(this.intersect(inputList.get(i), results));
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
