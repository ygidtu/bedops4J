package func;

/**
 * @author zhang
 * @since 2018.3.20
 * @version 0.1
 * bedops complement function
 * first merge all file together
 * then, calculate the complement by merged single file
 */
import basic.BasicBed;
import basic.BedFormatError;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class ComplementBed {
    public void complement(List inputFiles) throws IOException, BedFormatError {
        this.complement(inputFiles, null);
    }

    public void complement(List inputFiles, String output) throws IOException, BedFormatError {
        String line = null;
        BasicBed firstBed = null, secondBed = null;
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
            if (firstBed == null) {
                firstBed = setter.setBasicBed(line);
                continue;
            }
            secondBed = setter.setBasicBed(line);
            writer.println(firstBed.complement(secondBed).getBed());
            firstBed = secondBed;
        }

        writer.close();

        if(inputFiles.size() > 1) {
            tmpFile.delete();
        }

    }
}
