package jmplib.sourcecode;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SourceFromSrcZipExtractor {
    ZipFile zipFile;
    private String javaHome;
    private String srcPath;
    private Dictionary<String, ZipEntry> javaEntries = new Hashtable<String, ZipEntry>();

    public SourceFromSrcZipExtractor() throws IOException {
        javaHome = System.getProperty("java.home");
        if (javaHome.endsWith("\\jre")) {
            javaHome = javaHome.replace("\\jre", "");
        }
        String srcPath = javaHome + "/src.zip";
        //System.out.println(srcPath);

        zipFile = new ZipFile(srcPath);

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().endsWith(".java")) {
                String fullPath = getFullPath(entry.getName());
                javaEntries.put(fullPath, entry);
            }
        }
    }

    private String getFullPath(String strEntry) {
        if (strEntry.endsWith(".java"))
            strEntry = strEntry.substring(0, strEntry.length() - ".java".length());

        strEntry = strEntry.replaceAll("/", ".");
        return strEntry;
    }

    public String getSourceCode(String className) throws IOException {
        ZipEntry ze = javaEntries.get(className);
        if (ze == null) return null;

        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(zipFile.getInputStream(ze), "UTF-8");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}