package app.utils;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

@EnableAsync
public class MyThreadPool<T> {
    private final int size;

    private final List<WorkerThread> workers;

    private final LinkedBlockingQueue<FutureTask<T>> queue;

    public MyThreadPool(int poolSize)
    {
        this.size = poolSize;
        queue = new LinkedBlockingQueue<>();
        workers = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            workers.add(new WorkerThread());
            workers.get(i).start();
        }
    }

    @Async
    public Future<T> execute(Callable<T> task) {
        //monitor
        FutureTask<T> result = new FutureTask<>(task);
        synchronized (queue) {
            //adds the task into the queue
            queue.add(result);
            //notifies all the waiting threads
            queue.notify();
        }
        return result;
    }

    private class WorkerThread extends Thread {
        public void run() {
            FutureTask<T> task;

            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            System.out.println("An error occurred while queue is waiting: " + e.getMessage());
                        }
                    }
                    task = queue.poll();
                }

                try {
                    task.run();
                } catch (Exception e) {
                    System.out.println("Thread pool is interrupted due to an issue: " + e.getMessage());
                }
            }
        }
    }

    public void shutdown() {
        System.out.println("Shutting down thread pool, bye");
        for (int i = 0; i < size; i++) {

            workers.set(i, null);
        }
    }
}
