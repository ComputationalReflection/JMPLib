package jmplib.classversions;

/**
 * Interface that identifies one class as version
 *
 * @author Ignacio Lagartos
 */
public interface VersionClass {
    Object get_OldVersion();

    void set_OldVersion(Object newValue);

}
