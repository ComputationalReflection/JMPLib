package es.uniovi.jmplib.testing.times.lu;

import java.time.Duration;
import java.time.Instant;

public class Chronometer
    {
        private Instant ticks1, ticks2;
        private boolean stopped;

        public void start()
        {
            ticks1 = Instant.now();
            stopped = false;
        }
        public void stop()
        {
            ticks2 = Instant.now();
            stopped = true;
        }

        private static int ticksToMicroSeconds(Instant t1, Instant t2)
        {
        	Duration difference = Duration.between(t1, t2);
            return (int) (difference.getSeconds() * 1000000 + difference.getNano() / 1000);
        }

        private static int ticksToMiliSeconds(Instant t1, Instant t2)
        {
        	Duration difference = Duration.between(t1, t2);
            return (int) (difference.getSeconds() * 1000 + difference.getNano() / 1000000);
        }

        private static int ticksToSeconds(Instant t1, Instant t2)
        {
        	Duration difference = Duration.between(t1, t2);
            return (int) (difference.getSeconds() + difference.getNano() / 1000000000);
        }

        public int GetMicroSeconds()
        {
            if (stopped)
                return ticksToMicroSeconds(ticks1, ticks2);
            return ticksToMicroSeconds(ticks1, Instant.now());
        }

        public int GetMiliSeconds()
        {
            if (stopped)
                return ticksToMiliSeconds(ticks1, ticks2);
            return ticksToMiliSeconds(ticks1, Instant.now());
        }

        public int GetSeconds()
        {
            if (stopped)
                return ticksToSeconds(ticks1, ticks2);
            return ticksToSeconds(ticks1, Instant.now());
        }
    }