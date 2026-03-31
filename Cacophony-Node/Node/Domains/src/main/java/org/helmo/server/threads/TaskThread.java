package org.helmo.server.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.handler.CommandMenu;
import org.helmo.task.Task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread responsable de la gestion et de l'exécution séquentielle des tâches.
 * Utilise une file d'attente bloquante pour traiter les tâches entrantes.
 */
public class TaskThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();

    private final BlockingQueue<Task> queue = new LinkedBlockingQueue<>();
    private final CommandMenu menu;

    /**
     * Initialise le thread avec le menu de commandes spécifié.
     *
     * @param menu Le menu utilisé pour exécuter les tâches.
     */
    public TaskThread(CommandMenu menu) {
        this.menu = menu;
    }

    /**
     * Boucle principale du thread qui attend et exécute les tâches de la file.
     */
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
                LOGGER.error(e.getMessage());
            }
        }
    }


    /**
     * Ajoute une nouvelle tâche à la file d'attente pour exécution.
     *
     * @param task La tâche à ajouter.
     */
    public void addTask(Task task) {
        // queue.size() '4 chiffres +1' % 9999
        queue.add(task);
    }

}
