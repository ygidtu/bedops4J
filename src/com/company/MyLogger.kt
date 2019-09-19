package com.company

/**
 * A simple wrapped logger, based on logging
 * A simple LogRecord Formatter, just for learning, actually it never been used
 * A simple File path joiner based on File
 * @author Zhang Yiming
 * @since 2018.2.10
 */

import java.io.File
import java.io.IOException
import java.util.*
import java.util.logging.ConsoleHandler
import java.util.logging.FileHandler
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter


class MyLogger {

    private val LOGGER: Logger
    private val logName: String

    val rootDir: String
        get() = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile()

    init {
        Locale.setDefault(Locale("en", "EN"))
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "[%4\$s]: [%1\$tF %1\$tT] %5\$s %n"
        )
    }
/**
 * Logger constructor
 * @param name: String just using console handler, print message to console
 * Class using both console and file handler, print message to console and a log file named by the class name,
 * which placed in <programe file dir>/logs/*
</programe> */

init{
this.logName = "Bedops4J"
this.LOGGER = Logger.getLogger(this.logName)
this.LOGGER.setLevel(Level.ALL)
this.LOGGER.setUseParentHandlers(false)

// add console handler
val consoleHandler = ConsoleHandler()
consoleHandler.setLevel(Level.FINEST)
consoleHandler.setFormatter(SimpleFormatter())
this.LOGGER.addHandler(consoleHandler)

val rootDir = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile()

try
{
// just use the root directory
val logDirectory = File(File(rootDir).getParent())

logDirectory.mkdirs()

if (logDirectory.isDirectory())
{
val fileHandler = FileHandler(
FilePath.combine(logDirectory.toString(), this.logName + ".log"),
100000, 1, true
)
fileHandler.setEncoding("utf-8")

// set file handler format
fileHandler.setLevel(Level.ALL)
fileHandler.setFormatter(SimpleFormatter())
this.LOGGER.addHandler(fileHandler)
}

}
catch (e:SecurityException) {
val errors = e.getStackTrace()

for (error in errors)
{
info(error)
}
}
catch (e:IOException) {
val errors = e.getStackTrace()
for (error in errors)
{
info(error)
}
}

}


fun severe(message:String) {
LOGGER.log(Level.SEVERE, message)
}

fun warn(message:String) {
LOGGER.log(Level.WARNING, message)
}

fun info(message:String) {
LOGGER.log(Level.INFO, message)
}

fun config(message:String) {
LOGGER.log(Level.CONFIG, message)
}

fun fine(message:String) {
LOGGER.fine(message)
}

fun finer(message:String) {
LOGGER.log(Level.FINER, message)
}

fun finest(message:String) {
LOGGER.log(Level.FINEST, message)
}

// just use StackTraceElement
fun severe(message:StackTraceElement) {
LOGGER.log(Level.SEVERE, message.toString())
}

fun warn(message:StackTraceElement) {
LOGGER.log(Level.WARNING, message.toString())
}

fun info(message:StackTraceElement) {
LOGGER.log(Level.INFO, message.toString())
}

fun config(message:StackTraceElement) {
LOGGER.log(Level.CONFIG, message.toString())
}

fun fine(message:StackTraceElement) {
LOGGER.log(Level.FINE, message.toString())
}

fun finer(message:StackTraceElement) {
LOGGER.log(Level.FINER, message.toString())
}

fun finest(message:StackTraceElement) {
LOGGER.log(Level.FINEST, message.toString())
}

// just use StackTraceElement
fun severe(message:Array<StackTraceElement>) {
val msgs = HashSet()

for (e in message)
{
msgs.add(e.toString())
}
for (s in msgs)
{
LOGGER.log(Level.SEVERE, s)
}
}

fun warn(message:Array<StackTraceElement>) {
val msgs = HashSet()

for (e in message)
{
msgs.add(e.toString())
}
for (s in msgs)
{
LOGGER.log(Level.WARNING, s)
}
}

fun info(message:Array<StackTraceElement>) {
val msgs = HashSet()

for (e in message)
{
msgs.add(e.toString())
}
for (s in msgs)
{
LOGGER.log(Level.INFO, s)
}
}

fun config(message:Array<StackTraceElement>) {
val msgs = HashSet()

for (e in message)
{
msgs.add(e.toString())
}
for (s in msgs)
{
LOGGER.log(Level.CONFIG, s)
}
}

fun fine(message:Array<StackTraceElement>) {
val msgs = HashSet()

for (e in message)
{
msgs.add(e.toString())
}
for (s in msgs)
{
LOGGER.log(Level.FINE, s)
}
}

fun finer(message:Array<StackTraceElement>) {
val msgs = HashSet()

for (e in message)
{
msgs.add(e.toString())
}
for (s in msgs)
{
LOGGER.log(Level.FINER, s)
}
}

fun finest(message:Array<StackTraceElement>) {
val msgs = HashSet()

for (e in message)
{
msgs.add(e.toString())
}
for (s in msgs)
{
LOGGER.log(Level.FINEST, s)
}
}

}
