package com.company;

/**
 * A simple wrapped logger, based on logging
 * A simple LogRecord Formatter, just for learning, actually it never been used
 * A simple File path joiner based on File
 * @author Zhang Yiming
 * @since 2018.2.10
 *
 */

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;


class FilePath {
    public static String getRuntimeDirectory (Object object) {
        String runtimeDirectory = null;

        //
        String className = object.getClass().getTypeName() + ".class";
        String classNameWithoutPackage = className.replace(object.getClass().getPackage().getName() + ".", "");

        if (!object.getClass().getResource(classNameWithoutPackage).toString().startsWith("jar")) {

            className = className.replace(object.getClass().getPackage().getName() + ".", object.getClass().getPackage().getName() + "/");

            // add file handler, first get the locations
            ClassLoader loader = object.getClass().getClassLoader();

            runtimeDirectory = new File(loader.getResource(className).getPath()).getParent();
        } else {
            runtimeDirectory = new File(System.getProperty("java.class.path")).getParent();
        }

        return runtimeDirectory;
    }

    public static String combine(String... paths) {
        File finalPath = new File(paths[0]);

        for (String p: Arrays.copyOfRange(paths, 1, paths.length)) {
            finalPath = new File(finalPath, p);
        }

        return finalPath.getPath();
    }
}


public class MyLogger{

    private final Logger LOGGER;
    private String logName;

    {
        Locale.setDefault(new Locale("en", "EN"));
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "[%4$s]: [%1$tF %1$tT] %5$s %n"
        );
    }

    /**
     * Logger constructor
     * @param name: String just using console handler, print message to console
     * 				Class<?> using both console and file handler, print message to console and a log file named by the class name,
     * 					which placed in <programe file dir>/logs/*
     */

    public MyLogger () {
        this.logName = "Bedops4J";
        this.LOGGER = Logger.getLogger(this.logName);
        this.LOGGER.setLevel(Level.ALL);
        this.LOGGER.setUseParentHandlers(false);

        // add console handler
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINEST);
        consoleHandler.setFormatter(new SimpleFormatter());
        this.LOGGER.addHandler(consoleHandler);

        String rootDir = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();

        try {
            // just use the root directory
            File logDirectory = new File(new File(rootDir).getParent());

            logDirectory.mkdirs();

            if(logDirectory.isDirectory()) {
                Handler fileHandler = new FileHandler(
                        FilePath.combine(logDirectory.toString(), this.logName + ".log"),
                        100000, 1, true
                );
                fileHandler.setEncoding("utf-8");

                // set file handler format
                fileHandler.setLevel(Level.ALL);
                fileHandler.setFormatter(new SimpleFormatter());
                this.LOGGER.addHandler(fileHandler);
            }

        } catch (SecurityException | IOException e) {
            StackTraceElement[] errors = e.getStackTrace();

            for (StackTraceElement error: errors) {
                info(error);
            }
        }
    }

    public String getRootDir() {return this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();};


    public void severe(String message) {
        LOGGER.log(Level.SEVERE, message);
    }

    public void warn(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    public void info(String message) {
        LOGGER.log(Level.INFO, message);
    }

    public void config(String message) {
        LOGGER.log(Level.CONFIG, message);
    }

    public void fine(String message) {
        LOGGER.fine(message);
    }

    public void finer(String message) {
        LOGGER.log(Level.FINER, message);
    }

    public void finest(String message) {
        LOGGER.log(Level.FINEST, message);
    }

    // just use StackTraceElement
    public void severe(StackTraceElement message) {
        LOGGER.log(Level.SEVERE, message.toString());
    }

    public void warn(StackTraceElement message) {
        LOGGER.log(Level.WARNING, message.toString());
    }

    public void info(StackTraceElement message) {
        LOGGER.log(Level.INFO, message.toString());
    }

    public void config(StackTraceElement message) {
        LOGGER.log(Level.CONFIG, message.toString());
    }

    public void fine(StackTraceElement message) {
        LOGGER.log(Level.FINE, message.toString());
    }

    public void finer(StackTraceElement message) {
        LOGGER.log(Level.FINER, message.toString());
    }

    public void finest(StackTraceElement message) {
        LOGGER.log(Level.FINEST, message.toString());
    }

    // just use StackTraceElement
    public void severe(StackTraceElement[] message) {
        Set<String> msgs = new HashSet<>();

        for(StackTraceElement e: message) {
            msgs.add(e.toString());
        }
        for(String s: msgs){
            LOGGER.log(Level.SEVERE, s);
        }
    }

    public void warn(StackTraceElement[] message) {
        Set<String> msgs = new HashSet<>();

        for(StackTraceElement e: message) {
            msgs.add(e.toString());
        }
        for(String s: msgs){
            LOGGER.log(Level.WARNING, s);
        }
    }

    public void info(StackTraceElement[] message) {
        Set<String> msgs = new HashSet<>();

        for(StackTraceElement e: message) {
            msgs.add(e.toString());
        }
        for(String s: msgs){
            LOGGER.log(Level.INFO, s);
        }
    }

    public void config(StackTraceElement[] message) {
        Set<String> msgs = new HashSet<>();

        for(StackTraceElement e: message) {
            msgs.add(e.toString());
        }
        for(String s: msgs){
            LOGGER.log(Level.CONFIG, s);
        }
    }

    public void fine(StackTraceElement[] message) {
        Set<String> msgs = new HashSet<>();

        for(StackTraceElement e: message) {
            msgs.add(e.toString());
        }
        for(String s: msgs){
            LOGGER.log(Level.FINE, s);
        }
    }

    public void finer(StackTraceElement[] message) {
        Set<String> msgs = new HashSet<>();

        for(StackTraceElement e: message) {
            msgs.add(e.toString());
        }
        for(String s: msgs){
            LOGGER.log(Level.FINER, s);
        }
    }

    public void finest(StackTraceElement[] message) {
        Set<String> msgs = new HashSet<>();

        for(StackTraceElement e: message) {
            msgs.add(e.toString());
        }
        for(String s: msgs){
            LOGGER.log(Level.FINEST, s);
        }
    }

}
