package func

import basic.BasicBed
import basic.BedFormatError

import java.util.Arrays

/**
 * @author zhang
 * @since 2018.3.19
 * This class is used to set BasicBed from line or String array
 */

class SetBasicBed {

    /**
     * construct single BasicBed
     * @param line: string or array of strings
     * @return: null if is empty
     */
    @Throws(BedFormatError::class)
    fun setBasicBed(line: String): BasicBed {
        val lines = line.split("\\s+")
        return this.setBasicBed(lines)
    }

    companion object {


        @Throws(BedFormatError::class)
        fun setBasicBed(lines: Array<String>): BasicBed {
            val temporaryBed: BasicBed

            if (lines.size < 3) {
                throw BedFormatError("Bed columns less than 3: " + String.join("\t", lines))
            } else if (lines.size == 3) {

                temporaryBed = BasicBed(
                        lines[0], Integer.parseInt(lines[1]),
                        Integer.parseInt(lines[2])
                )

            } else if (lines.size == 4) {

                temporaryBed = BasicBed(
                        lines[0], Integer.parseInt(lines[1]),
                        Integer.parseInt(lines[2]), lines[3]
                )

            } else if (lines.size == 5) {
                temporaryBed = BasicBed(
                        lines[0], Integer.parseInt(lines[1]),
                        Integer.parseInt(lines[2]), lines[3],
                        lines[4]
                )
            } else if (lines.size == 6) {

                temporaryBed = BasicBed(
                        lines[0], Integer.parseInt(lines[1]),
                        Integer.parseInt(lines[2]), lines[3],
                        lines[4], lines[5]
                )

            } else {

                val append = String.join("\t", Arrays.copyOfRange(lines, 6, lines.size))
                temporaryBed = BasicBed(
                        lines[0], Integer.parseInt(lines[1]),
                        Integer.parseInt(lines[2]), lines[3],
                        lines[4], lines[5],
                        append
                )

            }
            return temporaryBed
        }
    }

}
