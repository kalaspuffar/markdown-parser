package org.ea;

import org.junit.runners.Parameterized;
import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Parallelized extends Parameterized {
    private static class ThreadPoolScheduler implements RunnerScheduler {
        private ExecutorService executor;

        public ThreadPoolScheduler() {
            String threads = System.getProperty("junit.parallel.threads", "4");
            int numThreads = Integer.parseInt(threads);
            this.executor = Executors.newFixedThreadPool(numThreads);
        }

        @Override
        public void finished() {
            this.executor.shutdown();
            try {
                this.executor.awaitTermination(12, TimeUnit.HOURS);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }

        @Override
        public void schedule(Runnable childStatement) {
            this.executor.submit(childStatement);
        }
    }

    public Parallelized(Class<?> klass) throws Throwable {
        super(klass);
        setScheduler(new ThreadPoolScheduler());
    }
}