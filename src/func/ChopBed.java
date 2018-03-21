package func;

/**
 * @author Zhang
 * @since 2018.3.20
 * @version 0.1
 * A class to preform chop function
 */

import basic.BasicBed;
import basic.BedFormatError;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class ChopBed {
    public void chop (ArrayList inputFiles, int chop) throws IOException, BedFormatError {
        this.chop(inputFiles, null, chop, 0);
    }

    public void chop (ArrayList inputFiles, int chop, int stagger) throws IOException, BedFormatError {
        this.chop(inputFiles, null, chop, stagger);
    }

    public void chop (ArrayList inputFiles, String output, int chop) throws IOException, BedFormatError {
        this.chop(inputFiles, output, chop, 0);
    }

    public void chop (ArrayList<String> inputFiles, String output, int chop, int stagger) throws IOException, BedFormatError {
        String line = null;
        SetBasicBed setter = new SetBasicBed();

        PrintWriter writer;
        if(output == null) {
            writer = new PrintWriter(System.out, true);
        } else {
            writer = new PrintWriter(output);
        }
        File tmpFile;
        if(inputFiles.isEmpty()) {
            System.exit(0);
            return;
        } else if (inputFiles.size() > 1) {
            File tmpDir = new File(inputFiles.get(0).toString()).getParentFile();
            tmpFile = new File(tmpDir, new Date().toString());

            // merge all files into temporary file
            MergeBeds merger = new MergeBeds();
            merger.merge(inputFiles, tmpFile.toString());
        } else {
            tmpFile = new File(inputFiles.get(0).toString());
        }

        // read and find complement
        Scanner reader = new Scanner(tmpFile);

        while (reader.hasNext()) {
            line = reader.nextLine();

            BasicBed bed = setter.setBasicBed(line);

            for (BasicBed b: bed.chop(chop, stagger)) {
                writer.println(b.getBed());
            }
        }

        writer.close();


        if(inputFiles.size() > 1) {
            tmpFile.delete();
        }

    }
}
