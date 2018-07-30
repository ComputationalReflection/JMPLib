package jmplib.util;

import jmplib.annotations.ExcludeFromJMPLib;

import javax.tools.SimpleJavaFileObject;
import java.io.File;
import java.net.URI;

@ExcludeFromJMPLib
public class JavaSourceFromString extends SimpleJavaFileObject {
    /**
     * The source code of this "file".
     */
    final String code;

    final String className;

    final int identifier;

    public File getFileData() {
        return fileData;
    }

    public void setFileData(File fileData) {
        this.fileData = fileData;
    }

    File fileData;

    /**
     * Constructs a new JavaSourceFromString.
     *
     * @param name the name of the compilation unit represented by this file object
     * @param code the source code for the compilation unit represented by this file
     *             object
     */
    public JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
        this.className = name;
        this.identifier = 0;
    }

    /**
     * Constructs a new JavaSourceFromString.
     *
     * @param name    the name of the compilation unit represented by this file object
     * @param code    the source code for the compilation unit represented by this file
     *                object
     * @param identifier the hashcode of the path
     */
    public JavaSourceFromString(String name, String code, int identifier) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
        this.className = name;
        this.identifier = identifier;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }

    public String getCode() {
        return code;
    }

    public String getClassName() {
        return className;
    }

    public int getIdentifier() {
        return identifier;
    }
}