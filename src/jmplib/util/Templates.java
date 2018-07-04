package jmplib.util;

import jmplib.annotations.ExcludeFromJMPLib;

/**
 * This class have String templates that can be filled to create code fragments
 * to add into the classes.
 *
 * @author Ignacio Lagartos
 */
@ExcludeFromJMPLib
public class Templates {
    public static final String GET_OLD_VERSION_TEMPLATE = "{ return _oldVersion; }";
    public static final String SET_OLD_VERSION_TEMPLATE = "{ _oldVersion = (%s)newValue; }";
    public static final String JMPLIB_MONITOR_NAME = "_jmplibMonitor";

    // New Class in cache template
    // %1$s: NewClass
    // %2$s: OriginalClass
    /**
     * This template generates the code to generate the creator of a cached
     * class
     */
    public static final String CREATOR_TEMPLATE = "{"
            + "  %1$s ov = null;"
            + "  try{"
            + "   ov = (%1$s) o._createInstance();"
            + "  }catch (Exception e) {e.printStackTrace();}"
            + "  Object oldVersion = o.get_NewVersion() == null? o: o.get_NewVersion();"
            + "  " + TransferState.class.getName() + ".transferState(oldVersion, ov);"
            + "  ov.set_OldVersion(o);\n"
            + "  o.set_NewVersion(ov);"
            + "  o.set_CurrentInstanceVersion(o._currentClassVersion);"
            + "}";

    // New Class in cache template
    // %1$s: NewClass
    // %2$s: OriginalClass
    /**
     * This template generates the code to generate the creator of a cached
     * class
     */
    public static final String THREAD_SAFE_CREATOR_TEMPLATE = "{"
            //  + "System.out.println(\"o.monitor = \" + o."  + Templates.JMPLIB_MONITOR_NAME + ");"
            + "  o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().unlock();"
            + "  o." + Templates.JMPLIB_MONITOR_NAME + ".writeLock().lock();"
            + "  if(o.get_CurrentInstanceVersion() < o._currentClassVersion) {"
            + "      %1$s ov = null;"
            + "      try{"
            + "         ov = (%1$s) o._createInstance();"
            + "      }catch (Exception e) {e.printStackTrace();}"
            + "      Object oldVersion = o.get_NewVersion() == null? o: o.get_NewVersion();"
            + "      " + TransferState.class.getName() + ".transferState(oldVersion, ov);"
            + "      ov.set_OldVersion(o);\n"
            + "      o.set_NewVersion(ov);"
            + "      o.set_CurrentInstanceVersion(o._currentClassVersion);"
            + "  }"
            + "  o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().lock();"
            + "  o." + Templates.JMPLIB_MONITOR_NAME + ".writeLock().unlock();"

            + "}";

    // New Class in cache template
    // %1$s: OriginalClass
    // %2$s: MethodName
    // %3$s: NewClass
    // %4$s: paramsNames
    // %5$s: if return?"return ":""
    /**
     * This template generates the code to generate the invoker of each method
     * of a cached class
     */
    public static final String INVOKER_BODY_TEMPLATE = "{"
            + " if(o.get_CurrentInstanceVersion() != %1$s._currentClassVersion) {"
            + "   _creator(o);"
            + " }"
            + " %5$s((%3$s)o.get_NewVersion()).%2$s(%4$s);"
            + "}";

    // New Class in cache template
    // %1$s: OriginalClass
    // %2$s: MethodName
    // %3$s: NewClass
    // %4$s: paramsNames
    // %5$s: if return?"return ":""
    /**
     * This template generates the code to generate the invoker of each method
     * of a cached class
     */
    public static final String THREAD_SAFE_INVOKER_BODY_TEMPLATE = "{"
            + "synchronized(jmplib.primitives.ThreadSafePrimitiveExecutor.class) {"
           // + "jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().lock();" //*
            + " o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().lock();"
            + " Class<?> cl = jmplib.classversions.VersionTables.getNewVersion(%1$s.class);"
            + "if (%3$s.class != cl){"
            + "       "
            + "       try {"
            + "        o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().unlock();  "
         //   + "         jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().unlock();" //*
            + "        %5$s %7$s cl.getMethod(\"_%2$s_invoker\", %1$s.class%9$s).invoke(cl, o%8$s);"
            + "         %6$s"
            + "       } catch (Exception ex) {ex.printStackTrace();}"
            + "  }"
            + " if(o.get_CurrentInstanceVersion() < %1$s._currentClassVersion) {"
            + "    _creator(o);"
            + " }"
            + " %5$s((%3$s)o.get_NewVersion()).%2$s(%4$s);"
            + " o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().unlock();"
          //  + " jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().unlock();" //*
            + " %6$s"
             + "}"
            + "}";

    // New Class in cache template
    // %1$s: FieldName
    // %2$s: NewClass
    // %3$s: OriginalClass
    /**
     * This template generates the code to generate the field getter of each
     * field of a cached class
     */
    public static final String FIELD_GETTER_TEMPLATE = "{"
            + "if(o.get_CurrentInstanceVersion() != %3$s._currentClassVersion)"
            + " _creator(o);"
            + "return ((%2$s)o.get_NewVersion()).%1$s;"
            + "}";

    /**
     * This template generates the code to generate the field getter of each
     * field of a cached class
     */
    public static final String THREAD_SAFE_FIELD_GETTER_TEMPLATE = "{"
            + "synchronized(jmplib.primitives.ThreadSafePrimitiveExecutor.class) {"
          //  + "jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().lock();" //*
            + " o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().lock();"
            + " Class<?> cl = jmplib.classversions.VersionTables.getNewVersion(%3$s.class);"
            + "if (%2$s.class != cl){"
            + "       "
            + "       try {"
            + "        o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().unlock();  "
          //  + "         jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().unlock();" //*
            + "        %4$s retValue = (%4$s) cl.getMethod(\"_%1$s_fieldGetter\", %3$s.class).invoke(cl, o);"
            + "         return retValue;"
            + "       } catch (Exception ex) {ex.printStackTrace();}"
            + "  }"
            + " if(o.get_CurrentInstanceVersion() != %3$s._currentClassVersion)"
            + "      _creator(o);"
            + " %4$s retValue = ((%2$s)o.get_NewVersion()).%1$s;"
            + " o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().unlock();"
          //  + " jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().unlock();"//*
            + " return retValue;"
            + "}"
            + "}";

    // New Class in cache template
    // %1$s: FieldName
    // %2$s: NewClass
    // %3$s: OriginalClass
    /**
     * This template generates the code to generate the field setter of each
     * field of a cached class
     */
    public static final String FIELD_SETTER_TEMPLATE = "{"
            + "if(o.get_CurrentInstanceVersion() != %3$s._currentClassVersion)"
            + " _creator(o);"
            + "((%2$s)o.get_NewVersion()).%1$s = value;"
            + "}";

    /**
     * This template generates the code to generate the field setter of each
     * field of a cached class
     */
    public static final String THREAD_SAFE_FIELD_SETTER_TEMPLATE = "{"
            + "synchronized(jmplib.primitives.ThreadSafePrimitiveExecutor.class) {"
           // + "jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().lock();" //*
            + " o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().lock();"
            + " Class<?> cl = jmplib.classversions.VersionTables.getNewVersion(%3$s.class);"
            + "if (%2$s.class != cl){"
            + "       "
            + "       try {"
            + "        o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().unlock();  "
            //+ "         jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().unlock();"//*
            + "        cl.getMethod(\"_%1$s_fieldSetter\", %3$s.class).invoke(cl, o, %4$s.class);"
            + "         return;"
            + "       } catch (Exception ex) {ex.printStackTrace();}"
            + "  }"
            + "if(o.get_CurrentInstanceVersion() != %3$s._currentClassVersion)"
            + " _creator(o);"
            + "((%2$s)o.get_NewVersion()).%1$s = value;"
            + " o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().unlock();"
            //+ " jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().unlock();"//*
            + "}"
            + "}";

    // Unary method template
    // %1$s: FieldName
    // %2$s: NewClass
    // %3$s: OriginalClass
    public static final String INSTANCE_FIELD_UNARY_TEMPLATE = "{"
            + "if(o.get_CurrentInstanceVersion() != %3$s._currentClassVersion)"
            + " _creator(o);"
            + "switch (type) {"
            + " case 1:"
            + "  return ((%2$s)o.get_NewVersion()).%1$s++;"
            + " case 2:"
            + "  return ++((%2$s)o.get_NewVersion()).%1$s;"
            + " case 3:"
            + "  return ((%2$s)o.get_NewVersion()).%1$s--;"
            + " case 4:"
            + "  return --((%2$s)o.get_NewVersion()).%1$s;"
            + " default:"
            + "  throw new RuntimeException(\"Invalid unary type\");"
            + "}"
            + "}";

    public static final String THREAD_SAFE_INSTANCE_FIELD_UNARY_TEMPLATE = "{"
            + "synchronized(jmplib.primitives.ThreadSafePrimitiveExecutor.class) {"
           // + "jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().lock();"//*
            + " o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().lock();"
            + " Class<?> cl = jmplib.classversions.VersionTables.getNewVersion(%3$s.class);"
            + "if (%2$s.class != cl){"
            + "       "
            + "       try {"
            + "        o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().unlock();  "
         //   + "         jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().unlock();"//*
            + "        %4$s retValue = (%4$s) cl.getMethod(\"_%1$s_unary\", %3$s.class).invoke(cl, o, %4$s.class);"
            + "         return retValue;"
            + "       } catch (Exception ex) {ex.printStackTrace();}"
            + "  }"

            + "if(o.get_CurrentInstanceVersion() != %3$s._currentClassVersion)"
            + " _creator(o);"
            + "%4$s retValue;"
            + "switch (type) {"
            + " case 1:"
            + "  retValue = ((%2$s)o.get_NewVersion()).%1$s++;"
            + "  break;"
            + " case 2:"
            + "  retValue = ++((%2$s)o.get_NewVersion()).%1$s;"
            + "  break;"
            + " case 3:"
            + "  retValue = ((%2$s)o.get_NewVersion()).%1$s--;"
            + "  break;"
            + " case 4:"
            + "  retValue = --((%2$s)o.get_NewVersion()).%1$s;"
            + "  break;"
            + " default:"
            + "  throw new RuntimeException(\"Invalid unary type\");"
            + "}"
            + " o." + Templates.JMPLIB_MONITOR_NAME + ".readLock().unlock();"
          //  + " jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().unlock();"//*
            + "return retValue;"
            + "}"
            + "}";

    // Unary method template
    // %1$s: FieldName
    // %2$s: Class
    public static final String STATIC_FIELD_UNARY_TEMPLATE = "{"
            + "return %2$s._%1$s_unary(type);"
            + "}";

}
