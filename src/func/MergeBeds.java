package func;

import basic.BasicBed;
import basic.BedFormatError;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


/**                       MergeBeds
 * merge function, if don't use output file, just print to stdout
 * It's absolutely unnecessary to write so many Overrides
 *
 * @author Zhang Yiming
 * @since 2018.2.25
 * @version 0.1
 */

public class MergeBeds {

    private final SetBasicBed setter = new SetBasicBed();


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

    /**
     * merge multiple files, without the output file, redirect the results to stdout
     * @param inputDirectory: {@link String} input directory
     */
    public void merge(String inputDirectory) throws IOException, BedFormatError {
        File[] inputFiles = new File(inputDirectory).listFiles();
        this.merge(inputFiles);
    }

    /**
     * merge multiple files, without the output file, redirect the results to stdout
     * @param inputDirectory: {@link File} input directory
     */
    public void merge(File inputDirectory) throws IOException, BedFormatError {
        File[] inputFiles = inputDirectory.listFiles();
        this.merge(inputFiles);
    }

    /**
     * merge multiple files, without the output file, redirect the results to stdout
     * @param inputFiles: {@link String[]} input files
     */
    public void merge(String[] inputFiles) throws IOException, BedFormatError {
        ArrayList<String> inputList = new ArrayList<String>(Arrays.asList(inputFiles));

        this.merge(inputList);
    }

    /**
     * merge multiple files, without the output file, redirect the results to stdout
     * @param inputFiles: {@link File[]} input files
     */
    public void merge(File[] inputFiles) throws IOException, BedFormatError {
        ArrayList<String> input = new ArrayList<String>(inputFiles.length);

        for(File f: inputFiles){
            input.add(f.toString());
        }

        this.merge(input);
    }

    /**
     * merge multiple files, without the output file, redirect the results to stdout
     * @param inputFiles: {@link ArrayList} input files
     */
    public void merge(ArrayList inputFiles) throws IOException, BedFormatError {
        this.merge(inputFiles, null);
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputDirectory: {@link String} a directory contains several files
     * @param output: {@link String} output file
     */
    public void merge(String inputDirectory, String output) throws IOException, BedFormatError {
        File[] inputFiles = new File(inputDirectory).listFiles();
        ArrayList<String> input = new ArrayList<String>(inputFiles.length);

        for(File f: inputFiles){
            input.add(f.toString());
        }

        this.merge(input, output);
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputDirectory: {@link String} a directory contains several files
     * @param output: {@link File} output file
     */
    public void merge(String inputDirectory, File output) throws IOException, BedFormatError {
        File[] inputFiles = new File(inputDirectory).listFiles();
        ArrayList<String> input = new ArrayList<String>(inputFiles.length);

        for(File f: inputFiles){
            input.add(f.toString());
        }

        this.merge(input, output.toString());
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputDirectory: {@link File} a directory contains several files
     * @param output: {@link File} output file
     */
    public void merge(File inputDirectory, String output) throws IOException, BedFormatError {
        File[] inputFiles = inputDirectory.listFiles();

        this.merge(inputFiles, new File(output));
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputDirectory: {@link File} a directory contains several files
     * @param output: {@link File} output file
     */
    public void merge(File inputDirectory, File output) throws IOException, BedFormatError {
        File[] inputFiles = inputDirectory.listFiles();

        this.merge(inputFiles, output);
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputFiles: {@link String[]} files that need to be merged
     * @param output: {@link String} output file
     */
    public void merge(String[] inputFiles, String output) throws IOException, BedFormatError {
        ArrayList<File> input = new ArrayList<>();

        for (String s: inputFiles) {
            input.add(new File(s));
        }

        this.merge(input, output);
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputFiles: {@link String[]} files that need to be merged
     * @param output: {@link File} output file
     */
    public void merge(String[] inputFiles, File output) throws IOException, BedFormatError {
        ArrayList<File> input = new ArrayList<>();

        for (String s: inputFiles) {
            input.add(new File(s));
        }

        this.merge(input, output.toString());
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputFiles: {@link File[]} files that need to be merged
     * @param output: {@link File} output file
     */
    public void merge(File[] inputFiles, String output) throws IOException, BedFormatError {
        this.merge(Arrays.asList(inputFiles), output);
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputFiles: {@link File[]} files that need to be merged
     * @param output: {@link File} output file
     */
    public void merge(File[] inputFiles, File output) throws IOException, BedFormatError {
        this.merge(new ArrayList<File>(Arrays.asList(inputFiles)), output.toString());
    }

    /**
     * merge different bed files into one, and write to output file
     * @param inputFiles: {@link ArrayList} a directory contains several files
     * @param output: {@link String} output file
     */
    public void merge(List inputFiles, String output) throws IOException, BedFormatError {
        BasicBed smallestBed;
        BasicBed mergedBed = null;
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

            for(int i = 0; i < inputFiles.size(); i++) {

                File tmpFile = new File(inputFiles.get(i).toString());

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
            while( !temporaryList.isEmpty() ) {

                smallestBed = temporaryList.removeFirst();

                if ( mergedBed == null ) {
                    mergedBed = smallestBed.clone();
                }

                Optional<BasicBed> temporary = mergedBed.merge(smallestBed);

                if ( temporary.isPresent() ) {
                    mergedBed = temporary.get();
                } else {
                    writer.println(mergedBed.getBasicBed());
                    mergedBed = smallestBed.clone();
                }

                temporaryBr = getKeyByValue(inputAndBrs, smallestBed);

                // if the BufferedReader still have content, deal with it. Otherwise, close it
                if((line = temporaryBr.readLine()) != null) {
                    smallestBed = this.setter.setBasicBed(line);

                    inputAndBrs.put(temporaryBr, smallestBed);

                    temporaryList.add(smallestBed);
                } else {
                    inputAndBrs.remove(temporaryBr);
                    temporaryBr.close();
                }

                Collections.sort(temporaryList);

            }

            // just output the last one to file or stdout
            if ( temporaryList.isEmpty() ) {
                writer.println(mergedBed.getBasicBed());
            }

            writer.close();

            for(FileReader f: inputReaders){
                f.close();
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
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
    public void everything(List inputFiles, String output) throws IOException {
        if (output == null) {
            this.everything(inputFiles);
        }

        String line = null;
        FileReader temporaryReader;
        BufferedReader temporaryBReader;
        ArrayList<BufferedReader> inputBReaders = new ArrayList<>(inputFiles.size());
        ArrayList<FileReader> inputReaders = new ArrayList<>(inputFiles.size());

        for(int i=0; i < inputFiles.size(); i++) {
            File temporaryFile = new File((String) inputFiles.get(i));
            temporaryReader = new FileReader(temporaryFile);
            inputReaders.add(temporaryReader);

            temporaryBReader = new BufferedReader(temporaryReader);
            inputBReaders.add(temporaryBReader);
        }

        // check output
        PrintWriter writer;
        if (output == null) {
            writer = new PrintWriter(System.out, true);
        } else {
            writer = new PrintWriter(output);
        }

        while (inputBReaders.size() > 0) {
            for(int i=0; i < inputBReaders.size(); i++) {
                if((line = inputBReaders.get(i).readLine()) != null) {
                    writer.println(line);
                } else {
                    temporaryReader = inputReaders.remove(i);
                    temporaryBReader = inputBReaders.remove(i);
                    temporaryBReader.close();
                    temporaryReader.close();
                }
            }
        }

        writer.close();
    }

    public void everything(File[] inputFiles, File output) throws IOException {
        ArrayList<File> temporaryFiles = new ArrayList<>(inputFiles.length);
        Collections.addAll(temporaryFiles, inputFiles);
        this.everything(temporaryFiles, output.toString());
    }

    public void everything(File[] inputFiles, String output) throws IOException {
        ArrayList<File> temporaryFiles = new ArrayList<>(inputFiles.length);
        Collections.addAll(temporaryFiles, inputFiles);
        this.everything(temporaryFiles, output);
    }

    public void everything(String[] inputFiles, String output) throws IOException {
        ArrayList<String> temporaryFiles = new ArrayList<>(inputFiles.length);
        Collections.addAll(temporaryFiles, inputFiles);
        this.everything(temporaryFiles, output);
    }

    public void everything(String[] inputFiles, File output) throws IOException {
        ArrayList<String> temporaryFiles = new ArrayList<>(inputFiles.length);
        Collections.addAll(temporaryFiles, inputFiles);
        this.everything(temporaryFiles, output.toString());
    }

    /**
     * merge everything from different files together by input file order, and redirect to stdout
     * @param inputFiles: input file paths
     * @throws IOException
     */
    public void everything(List inputFiles) throws IOException {
        this.everything(inputFiles, null);
    }

    public void everything(File[] inputFiles) throws IOException {
        ArrayList<File> temporaryFiles = new ArrayList<>(inputFiles.length);
        Collections.addAll(temporaryFiles, inputFiles);
        this.everything(temporaryFiles);
    }


    public void everything(String[] inputFiles) throws IOException {
        ArrayList<String> temporaryFiles = new ArrayList<>(inputFiles.length);
        Collections.addAll(temporaryFiles, inputFiles);
        this.everything(temporaryFiles);
    }

}

