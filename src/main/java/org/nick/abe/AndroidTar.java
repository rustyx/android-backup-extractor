package org.nick.abe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarHeader;
import org.kamranzafar.jtar.TarInputStream;
import org.kamranzafar.jtar.TarOutputStream;

/**
 * Android-compatible ustar reader/writer utility.
 * 
 * @author rustyx
 */
public class AndroidTar {

    public static void extractTar(String tarFile, String targetDir, String listFileName) throws IOException {
        File root = new File(targetDir);
        root.mkdirs();
        File listFile = new File(root, listFileName);
        try (TarInputStream in = new TarInputStream(new BufferedInputStream(new FileInputStream(tarFile)));
                PrintWriter list = new PrintWriter(listFile, "UTF-8")) {
            TarEntry entry = null;
            while ((entry = in.getNextEntry()) != null) {
                try {
                    list.println(entry.getName());
                    File f = new File(root, cleanup(entry.getName()));
                    if (entry.isDirectory()) {
                        f.mkdirs();
                    } else {
                        f.getAbsoluteFile().getParentFile().mkdirs();
                        try (FileOutputStream out = new FileOutputStream(f)) {
                            IOUtils.copyLarge(in, out, 0, entry.getSize());
                        }
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public static void createTar(String tarFile, String sourceDir, String listFileName) throws IOException {
        File root = new File(sourceDir);
        File listFile = new File(root, listFileName);
        try (TarOutputStream out = new TarOutputStream(new BufferedOutputStream(new FileOutputStream(tarFile)));
                BufferedReader list = new BufferedReader(new InputStreamReader(new FileInputStream(listFile), "UTF-8"))) {
            String name = null;
            while ((name = list.readLine()) != null) {
                try {
                    File f = new File(root, cleanup(name));
                    TarEntry entry = new TarEntry(TarHeader.createHeader(
                            name, f.length(), f.lastModified(), f.isDirectory(), f.isDirectory() ? 0700 : 0600));
                    entry.setGroupId(1000);
                    entry.setGroupName("");
                    entry.setUserId(1000);
                    entry.setUserName("");
                    entry.setModTime(f.lastModified());
                    out.putNextEntry(entry);
                    if (!f.isDirectory()) {
                        try (FileInputStream in = new FileInputStream(f)) {
                            IOUtils.copyLarge(in, out, 0, entry.getSize());
                        }
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    static String cleanup(String name) {
        return name.replace(':', '~');
    }

}
