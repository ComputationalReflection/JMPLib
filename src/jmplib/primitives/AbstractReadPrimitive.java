package jmplib.primitives;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.sourcecode.ClassContent;

/**
 * Simple children of the AbstractPrimtive that just reads information from
 * source files instead of changing the state of the source.
 *
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public abstract class AbstractReadPrimitive<T> extends AbstractPrimitive {

    private T readValue;

    public AbstractReadPrimitive(ClassContent classContent) {
        super(classContent);
    }

    /**
     * Gets the information obtained from the source file
     *
     * @return
     */
    public T getReadValue() {
        return readValue;
    }

    /**
     * Sets the information obtained from the source file
     *
     * @param value
     */
    protected void setReadValue(T value) {
        this.readValue = value;
    }
}
