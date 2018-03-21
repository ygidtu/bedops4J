package func;
import basic.BasicBed;
import basic.BedFormatError;

import java.util.Arrays;

/**
 * @author zhang
 * @since 2018.3.19
 * This class is used to set BasicBed from line or String array
 *
 */

public class SetBasicBed {

    /**
     * construct single BasicBed
     * @param line: string or array of strings
     * @return: null if is empty
     */
    public BasicBed setBasicBed(String line) throws BedFormatError {
        String[] lines = line.split("\\s+");
        return this.setBasicBed(lines);
    }


    public static BasicBed setBasicBed(String[] lines) throws BedFormatError {
        BasicBed temporaryBed;

        if(lines.length < 3) {
            throw new BedFormatError("Bed columns less than 3: " + String.join("\t", lines));
        } else if(lines.length == 3) {

            temporaryBed = new BasicBed(
                    lines[0], Integer.parseInt(lines[1]),
                    Integer.parseInt(lines[2])
            );

        } else if(lines.length == 4) {

            temporaryBed = new BasicBed(
                    lines[0], Integer.parseInt(lines[1]),
                    Integer.parseInt(lines[2]), lines[3]
            );

        } else if(lines.length == 5) {
            temporaryBed = new BasicBed(
                    lines[0], Integer.parseInt(lines[1]),
                    Integer.parseInt(lines[2]), lines[3],
                    lines[4]
            );
        } else if(lines.length == 6) {

            temporaryBed = new BasicBed(
                    lines[0], Integer.parseInt(lines[1]),
                    Integer.parseInt(lines[2]), lines[3],
                    lines[4], lines[5]
            );

        } else {

            String append = String.join("\t", Arrays.copyOfRange(lines, 6, lines.length));
            temporaryBed = new BasicBed(
                    lines[0], Integer.parseInt(lines[1]),
                    Integer.parseInt(lines[2]), lines[3],
                    lines[4], lines[5],
                    append
            );

        }
        return temporaryBed;
    }

}
