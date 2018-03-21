package func;

import basic.BasicBed;
import basic.BedFormatError;
import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * A class to sort bed format files
 * @author Zhang
 * @since 2018.2.25
 * @version 0.1
 */

public class SortBed {
    private final MergeBeds mergeBeds = new MergeBeds();
    private final SetBasicBed setter = new SetBasicBed();


    /**
     * get percentage of system memory usage
     * @return: int
     */
    private static int getMemory(){
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // retrive the total memory
        long totalvirtualMemory = osmxb.getTotalSwapSpaceSize();
        // retrive the free memory
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
        Double compare=(Double)(1-freePhysicalMemorySize*1.0/totalvirtualMemory)*100;
        return compare.intValue();
    }

    /*
     * last two functions not been used.
     */
    /**
     * get maximum memory that we could use
     * @return
     */
    private static long getAvailableMemory() {
        // http://stackoverflow.com/questions/12807797/java-get-available-memory
        Runtime r = Runtime.getRuntime();
        long allocatedMemory = r.totalMemory() - r.freeMemory();
        long presFreeMemory = r.maxMemory() - allocatedMemory;
        return presFreeMemory;
    }

    /**
     * exstmite best block size for temporary files
     * @param fileSize: the size of input file bytes
     * @param maxTmpFiles: the maximum number of temporary files
     * @return: long block size
     */
    private static long getBestBlockSize(long fileSize, int maxTmpFiles) {
        // we don't want to open up much more than maxtmpfiles temporary
        // files, better run
        // out of memory first.
        long maxMemory = getAvailableMemory();
        long blocksize = fileSize / maxTmpFiles
                + (fileSize % maxTmpFiles == 0 ? 0 : 1);

        // on the other hand, we don't want to create many temporary
        // files
        // for naught. If blocksize is smaller than half the free
        // memory, grow it.
        if (blocksize < maxMemory / 2) {
            blocksize = maxMemory / 2;
        }
        return blocksize;
    }

    /*
     * delete files and directory
     */
    private void deleteFile(File input) {
        if(input.exists() && input.isFile()){
            input.delete();
        }
    }

    private  void deleteDir(File input) {
        if(input.exists() && input.isDirectory()){

            for(File f: input.listFiles()){
                deleteFile(f);
            }
            input.delete();
        }
    }


    /**
     * A private function, split big file into small temporary files or just read into memory
     * @param input: the path of big file
     * @param tmpDir: the path of temporary directory
     * @return: Optional, if file size is bigger than available memory, return null, else, return a list of BasicBed
     */
    private ArrayList<BasicBed> splitBigFile(String input, File tmpDir) throws BedFormatError, IOException {
        String line = null;
        BasicBed temporaryBed = null;
        File inputFile = new File(input);

        if(!inputFile.exists() || !inputFile.isFile()) {
            throw new FileNotFoundException("Sorting: " + input + " is not file");
        }

        // using ram usage to split big files
        int baseRamUsage = getMemory();

        FileReader inputFileReader = new FileReader(inputFile);
        BufferedReader inputBufferedReader = new BufferedReader(inputFileReader);


        ArrayList<BasicBed> temporaryList = new ArrayList<BasicBed>();

        if(!tmpDir.exists()) {
            tmpDir.mkdirs();
        }


        while((line = inputBufferedReader.readLine()) != null) {
            // get temporary BasicBed and put it into list
            temporaryBed = this.setter.setBasicBed(line);
            temporaryList.add(temporaryBed);


            int currentRamUsage = getMemory();
            if( currentRamUsage > 90 || (currentRamUsage - baseRamUsage) > 20  ) {
                if(!tmpDir.exists()) {
                    tmpDir.mkdirs();
                }

                Collections.sort(temporaryList);
                File tem = new File(tmpDir, String.valueOf(temporaryList.hashCode()));

                // print a message about the temporary file
                System.out.println("Create temporary file: " + tem.toString());

                PrintWriter writer = new PrintWriter(tem);
                // write to temporary files
                for(BasicBed b: temporaryList) { writer.println(b.getBed()); };
                writer.close();
                temporaryList.clear();

            }
        }

        inputBufferedReader.close();
        inputFileReader.close();

        if(tmpDir.exists()) {
            // if there are still part of files not write into temporary file
            if (temporaryList.size() > 0) {
                File tem = new File(tmpDir, String.valueOf(temporaryList.hashCode()));
                // sort again
                Collections.sort(temporaryList);

                PrintWriter writer = new PrintWriter(tem);
                // write to temporary files
                for(BasicBed b: temporaryList) { writer.println(b.getBed()); };
                writer.close();

                temporaryList.clear();
            }
        }
        return temporaryList;
    }


    public void sortBigFile(String input) throws IOException, BedFormatError  {
        this.sortBigFile(input, null);
    }

    /**
     * split sort and merge big files
     * @param input: input file name with path
     * @param output: output file name with path
     */
    public void sortBigFile(String input, String output) throws IOException, BedFormatError {
        // construct output directory and temporary directory
        PrintWriter writer;
        File outputFile;
        String directory;
        if (output == null) {
            writer = new PrintWriter(System.out, true);
            outputFile = new File(input);
            directory = new File(input).getAbsoluteFile().getParent();
        } else {
            writer = new PrintWriter(output);
            outputFile = new File(output);
            directory = new File(output).getAbsoluteFile().getParent();
        }

        File outputDirectory = new File(directory);
        File temporaryOutput = new File(outputDirectory, "temporarySortedFile");

        // first remove temporary directory
        deleteDir(temporaryOutput);


        ArrayList<BasicBed> temporaryList = splitBigFile(input, temporaryOutput);

        if ( !temporaryList.isEmpty() ) {
            // miss sort process
            Collections.sort(temporaryList);

            // then output all sorted beds
            for(BasicBed b: temporaryList) {
                writer.println(b.getBed());
            }
        } else {
            merge(temporaryOutput.toString(), output);
            deleteDir(temporaryOutput);
        }
        writer.close();
    }


    public void sortBigFiles(String[] input, String output) throws IOException, BedFormatError {
        File[] inputFiles = new File[input.length];
        for(int i=0; i < input.length; i++) {
            this.sortBigFile(input[i], input[i]);
            inputFiles[i] = new File(input[i]);
        }

        this.merge(inputFiles, output);
    }



    /*
     * select key by map value, used in merge function
     */
    private BufferedReader getKeyByValue(Map<BufferedReader, BasicBed> map, BasicBed value){
        for(Map.Entry<BufferedReader, BasicBed> entry : map.entrySet()){
            if(value.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        return null;
    }


    private void merge(String inputDirectory) throws IOException, BedFormatError {
        File[] inputFiles = new File(inputDirectory).listFiles();
        this.merge(inputFiles);
    }

    /**
     * merge different bed files into one, and write to output file or stdout
     * @param inputDirectory: {@link String} a directory contains several files
     * @param output: {@link File} output file, null for stdout
     */
    private void merge(String inputDirectory, String output) throws IOException, BedFormatError {
        File[] inputFiles = new File(inputDirectory).listFiles();
        this.merge(inputFiles, output);

    }

    private void merge(File[] inputFiles) throws IOException, BedFormatError {
        this.merge(inputFiles, null);
    }

    /**
     * merge different bed files into one, and write to output file or stdout
     * @param inputFiles: arrays of file
     * @param output: output file path or null, null for stdout
     * @throws IOException
     * @throws BedFormatError
     */
    private void merge(File[] inputFiles, String output) throws IOException, BedFormatError {
        BasicBed smallestBed;
        BufferedReader temporaryBr;
        LinkedList<BasicBed> temporaryList = new LinkedList<>();
        ArrayList<FileReader> inputReaders = new ArrayList<>();
        Map<BufferedReader, BasicBed> inputAndBrs = new HashMap<>();
        String line = null;

        try{
            PrintWriter writer;
            if (output == null) {
                writer = new PrintWriter(System.out, true);
            } else {
                writer = new PrintWriter(output);
            }

            for(File tmpFile: inputFiles) {
                FileReader tmpFr = new FileReader(tmpFile);
                inputReaders.add(tmpFr);

                BufferedReader tmpBr = new BufferedReader(tmpFr);

                if((line = tmpBr.readLine()) != null) {
                    smallestBed = this.setter.setBasicBed(line);
                    if(!inputAndBrs.containsKey(tmpBr)) {
                        inputAndBrs.put(tmpBr, smallestBed);
                    }
                    temporaryList.add(smallestBed);
                }
            }

            Collections.sort(temporaryList);

            // write the smallest BasicBed and replace with a newly read content
            while(inputAndBrs.size() > 0 && temporaryList.size() > 0) {

                smallestBed = temporaryList.removeFirst();

                writer.println(smallestBed.getBed());

                temporaryBr = getKeyByValue(inputAndBrs, smallestBed);

                // if the BufferedReader still have content, deal with it. Otherwise, close it
                if((line = temporaryBr.readLine()) != null) {
                    smallestBed = this.setter.setBasicBed(line);

                    inputAndBrs.put(temporaryBr, smallestBed);

                    temporaryList.add(smallestBed);
                } else {
                    inputAndBrs.remove(temporaryBr);
                    temporaryBr.close();

                    /* if there is only last BufferedReader left, just write all the content from it into output file
                     * there are not need to manipulate the Map or something else.
                     * and put the If statement after removal of BufferedReader, to reduce the calculations.
                     */
                    if(inputAndBrs.size() == 1) {
                        // one last BufferedReader, just take it
                        temporaryBr = inputAndBrs.keySet().iterator().next();
                        while ((line = temporaryBr.readLine()) != null){
                            writer.println(line);
                        }
                        break;
                    }
                }

                Collections.sort(temporaryList);
            }

            // If there are have no BufferedReaders, write all the content from temporaryList into file
            for(BasicBed b: temporaryList) {
                writer.println(b.getBed());
            }

            writer.close();

            for(FileReader f: inputReaders){
                f.close();
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}
