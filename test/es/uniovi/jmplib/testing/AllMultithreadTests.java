package es.uniovi.jmplib.testing;

import es.uniovi.jmplib.testing.thread_safety.ThreadSafetyDynamicCodeTest;
import es.uniovi.jmplib.testing.thread_safety.ThreadSafetyFieldTest;
import es.uniovi.jmplib.testing.thread_safety.ThreadSafetyMethodTest;
import jmplib.annotations.ExcludeFromJMPLib;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import java.io.Serializable;

@ExcludeFromJMPLib
@RunWith(Suite.class)
@SuiteClasses({ThreadSafetyMethodTest.class, ThreadSafetyFieldTest.class, ThreadSafetyDynamicCodeTest.class})
public class AllMultithreadTests implements Serializable {

    private static final long serialVersionUID = 1L;

}
