package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.MessageBuilder;
import org.helmo.task.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Représente un menu de commandes qui associe des motifs Regex à des gestionnaires de commandes.
 */
public class CommandMenu {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LinkedHashMap<Pattern, CommandHandler> handlers = new LinkedHashMap<>();

    /**
     * Ajoute une nouvelle commande au menu.
     *
     * @param command Le motif Regex correspondant à la commande.
     * @param handler Le gestionnaire qui sera exécuté si le motif correspond.
     */
    public void add(Pattern command, CommandHandler handler) {
        handlers.put(command, handler);
    }

    /**
     * Exécute la commande correspondant au message contenu dans la tâche.
     *
     * @param task La tâche contenant le message et l'expéditeur.
     * @return La réponse générée par le gestionnaire de commande.
     * @throws HandlerException Si une erreur survient lors du traitement de la commande.
     */
    public String execute(Task task) throws HandlerException {
        for (var entry : handlers.entrySet()) {
            Pattern pattern = entry.getKey();
            CommandHandler handler = entry.getValue();
            var matcher = pattern.matcher(task.message());

            // Extraitre les arguments
            if (matcher.matches()) {
                List<String> args = new ArrayList<>();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    args.add(matcher.group(i));
                }

                return handler.handle(args, task.sender());
            }
        }

        LOGGER.warn("Command {} not found", task.message());
        return MessageBuilder.buildError();
    }
}
