package info.jab.cli.io;

import java.io.File;

/**
 * FileSystemChecker implementation using java.io.File
 */
public class FileSystemChecker {

    public boolean fileExists(String filename) {
        return new File(filename).exists();
    }
}
