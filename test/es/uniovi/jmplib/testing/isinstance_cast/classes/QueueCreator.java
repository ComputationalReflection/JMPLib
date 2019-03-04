package es.uniovi.jmplib.testing.isinstance_cast.classes;


import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class QueueCreator {
    public static Queue newQueue(Object e) {
        if (e instanceof Comparable) return new PriorityQueue();
        return new LinkedList();
    }

    public static Queue newQueueCast(Object e) {
        try {
            Comparable c = (Comparable) e;
            return new PriorityQueue();
        } catch (Exception ex) {
            //ex.printStackTrace();
            return new LinkedList();
        }
    }
}
