package basic;

public class BedFormatError extends Throwable {
    /**
     * a simple error for BasicBed class
     */
    private static final long serialVersionUID = 1L;

    public BedFormatError() {};
    public BedFormatError(String msg) {
        super(msg);
    }

}
