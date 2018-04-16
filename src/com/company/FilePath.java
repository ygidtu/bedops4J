package com.company;

import java.io.File;
import java.util.Arrays;

public class FilePath {
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

