package basic

class BedFormatError : Throwable {

    constructor() {}
    constructor(msg: String) : super(msg) {}

    companion object {
        /**
         * a simple error for BasicBed class
         */
        private val serialVersionUID = 1L
    }

}
