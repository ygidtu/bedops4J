package org.chenlinlab.common


import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlin.system.exitProcess
import org.apache.log4j.Logger



/**
 * @author ygidtu
 * @since 20190911
 * @version 0.0.1
 */

const val VERSION = "Version: 1.0-SNAPSHOT"


class Parameters: CliktCommand(invokeWithoutSubcommand = true) {
    private val version by option("-v", "--version", help = "version").flag(default = false)

    override fun run() {
        if ( this.version ) {
            println("Splice4k version: 20180928")
            exitProcess(0)
        }
    }
}


fun main(args: Array<String>) {
    val logger = Logger.getLogger("main")
    val cmd = Parameters()

    // help message
    if (args.size <= 1 || "-h" in args || "--help" in args ) {
        val help = when {
            args.isEmpty() -> cmd.getFormattedHelp()
            args[0].toLowerCase() in arrayOf("-v", "--version") -> {
                println(VERSION)
                exitProcess(0)
            }
            else -> cmd.getFormattedHelp()
        }
        logger.info(help)
        exitProcess(0)
    }
}
