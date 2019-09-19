package com.company

import java.io.File
import java.util.Arrays

object FilePath {
    fun getRuntimeDirectory(`object`: Object): String? {
        var runtimeDirectory: String? = null

        //
        var className = `object`.getClass().getTypeName() + ".class"
        val classNameWithoutPackage = className.replace(`object`.getClass().getPackage().getName() + ".", "")

        if (!`object`.getClass().getResource(classNameWithoutPackage).toString().startsWith("jar")) {

            className = className.replace(`object`.getClass().getPackage().getName() + ".", `object`.getClass().getPackage().getName() + "/")

            // add file handler, first get the locations
            val loader = `object`.getClass().getClassLoader()

            runtimeDirectory = File(loader.getResource(className).getPath()).getParent()
        } else {
            runtimeDirectory = File(System.getProperty("java.class.path")).getParent()
        }

        return runtimeDirectory
    }

    fun combine(vararg paths: String): String {
        var finalPath = File(paths[0])

        for (p in Arrays.copyOfRange(paths, 1, paths.size)) {
            finalPath = File(finalPath, p)
        }

        return finalPath.getPath()
    }
}

