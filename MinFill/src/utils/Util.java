package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Util {
    public static InputStream getInput(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            return System.in;
        } else {
            return new FileInputStream(new File(args[0]));
        }
    }
}
