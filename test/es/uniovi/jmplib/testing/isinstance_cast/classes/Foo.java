package es.uniovi.jmplib.testing.isinstance_cast.classes;


import jmplib.reflect.Introspector;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Foo {
    //Dummy
    public int compareTo(Object f) {
        return 0;
    }

    public static Queue newQueue(Object e) {
        if (e instanceof Comparable) return new PriorityQueue();
        return new LinkedList();
    }

    public static Queue newQueueCast(Object e) {
        try {
            Comparable c = (Comparable)e;
            return new PriorityQueue();
        }
        catch(Exception ex) {
            //ex.printStackTrace();
            return new LinkedList();
        }
    }
/*
    public static Queue newQueueCastB(Object e) {
        try {
            Comparable [] c = (Comparable [])e;
            return new PriorityQueue();
        }
        catch(Exception ex) {
            //ex.printStackTrace();
            return new LinkedList();
        }
    }

    public static Queue newQueueCastB2(Object e) {
        try {
            Comparable []c = (Comparable[])Introspector.cast(Comparable[].class, e);
            return new PriorityQueue();
        }
        catch(Exception ex) {
            //ex.printStackTrace();
            return new LinkedList();
        }
    }*/
/*
    public static Queue newQueueCast2(Object e) {
        try {
            //Comparable c = (Comparable)Introspector.decorateClass(Comparable.class).cast(e);
            Comparable c = (Comparable)Introspector.cast(Comparable.class, e);
            return new PriorityQueue();
        }
        catch(Exception ex) {
            //ex.printStackTrace();
            return new LinkedList();
        }
    }
    public static Queue newQueue2(Object e) {
        //if (Introspector.decorateClass(Comparable.class).isAssignableFrom(Introspector.decorateClass(e.getClass())))
        if (Introspector.instanceOf(e, Comparable.class))
            return new PriorityQueue();
        return new LinkedList();
    }*/
}
