package org.helmo.server.threads;

import org.helmo.Exception.HandlerException;
import org.helmo.Task;
import org.helmo.client.ClientSession;
import org.helmo.handler.CommandMenu;

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
                String response = menu.execute(task.getRawLine(), task.getSession());
                if (response != null && task.getWestThread() != null) {
                    task.getWestThread().send(response);
                }
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            } catch (HandlerException e) {
                throw new RuntimeException(e);
            }

        }
    }


    public void addTask(Task task) {
        queue.add(task);
    }

}
