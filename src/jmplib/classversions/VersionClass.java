package jmplib.classversions;

/**
 * Interface that identifies one class as version
 *
 * @author Ignacio Lagartos
 */
public interface VersionClass {
    java.util.concurrent.locks.ReadWriteLock monitor = new java.util.concurrent.locks.ReentrantReadWriteLock();

    Object get_OldVersion();

    void set_OldVersion(Object newValue);

}
