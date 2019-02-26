package com.crestron.blackbird.mobile.projectmanagement.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Helper class to perform file I/O operations.
 */
public class FileHelper {
    private static final String TAG = FileHelper.class.getSimpleName();
    private static final Stack<String> NESTED_ZIP_STACK = new Stack<>();

    /**
     * Extract zip file's contents to a new directory.
     *
     * @param zipFileName Path of zip file to be extracted.
     * @param destFolder  Destination dir to extract to.
     * @throws IOException
     */
    public static void extractZipTo(String zipFileName, String destFolder) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipFileName)) {
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();
            java.io.File curDestFile;
            java.io.File nestedZipParentFolder;
            ZipEntry entry;
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                curDestFile = new java.io.File(destFolder, entry.getName());

                if (entry.isDirectory()) {
                    curDestFile.mkdirs();
                } else {
                    saveFile(zipFile.getInputStream(entry), curDestFile);
                }

                if (entry.getName().endsWith(".zip")) {
                    nestedZipParentFolder = curDestFile.getParentFile();
                    NESTED_ZIP_STACK.push(entry.getName());
                    // only go 2 levels deep for nested zips... we have a weird unzip format
                    if (NESTED_ZIP_STACK.size() < 2) {
                        Log.d(TAG, "performing nested extraction on file " + entry.getName());
                        extractZipTo(curDestFile.getAbsolutePath(), nestedZipParentFolder.getAbsolutePath());
                        // remove the zip file after extraction
                        curDestFile.delete();
                    }
                    NESTED_ZIP_STACK.pop();
                }
            }
        }
    }

    /**
     * Save the input stream from extracted zip file to a new file.
     *
     * @param bufferedInputStream InputStream - from extracted zip
     * @param outputFile          File - new file to save to
     * @throws IOException
     */
    private static void saveFile(InputStream bufferedInputStream, java.io.File outputFile) throws IOException {
        int BUFFER_SIZE = 8192;
        byte[] readBuffer = new byte[BUFFER_SIZE];
        // create any missing parent directories
        java.io.File parentFolder = outputFile.getParentFile();
        parentFolder.mkdirs();

        try (
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
                BufferedInputStream bis = new BufferedInputStream(bufferedInputStream, BUFFER_SIZE)
        ) {
            // read the file into the ByteArrayOutputStream
            int bytesRead;
            while ((bytesRead = bis.read(readBuffer)) != -1) {
                bos.write(readBuffer, 0, bytesRead);
            }
        }
    }
}
