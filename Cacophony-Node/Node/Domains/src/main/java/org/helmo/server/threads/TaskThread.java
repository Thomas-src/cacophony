package org.helmo.server.threads;

import org.helmo.Exception.HandlerException;
import org.helmo.handler.CommandMenu;
import org.helmo.task.Task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskThread extends Thread {
    private final BlockingQueue<Task> queue = new LinkedBlockingQueue<>();
    private final CommandMenu menu;

    public TaskThread(CommandMenu menu) {
        this.menu = menu;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try{
                Task task = queue.take();
                String response = menu.execute(task);
                if (response != null) {
                    task.send(response);
                }
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            } catch (HandlerException e) {
                throw new RuntimeException(e); // todo à modifier
            }

        }
    }


    public void addTask(Task task) {
        // queue.size() '4 chiffres +1' % 9999
        queue.add(task);
    }

}
