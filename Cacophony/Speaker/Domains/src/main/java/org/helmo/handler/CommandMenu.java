package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.crypto.CryptoProvider;
import org.helmo.domain.Domain;
import org.helmo.exceptions.HandlerException;
import org.helmo.network.SpeakerTcpConnector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Gère un menu de commandes basées sur des expressions régulières.
 * Permet d'associer des motifs (Pattern) à des gestionnaires de commandes (CommandHandler).
 */
 public class CommandMenu {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LinkedHashMap<Pattern, CommandHandler> handlers = new LinkedHashMap<>();


    /**
     * Ajoute une nouvelle commande au menu.
     *
     * @param command Le motif Regex correspondant à la commande.
     * @param handler Le gestionnaire à exécuter si le motif correspond.
     */
    public void add(Pattern command, CommandHandler handler) {
        handlers.put(command, handler);
    }

    /**
     * Tente d'exécuter une commande correspondant au message fourni.
     *
     * @param message Le message brut reçu à analyser.
     * @throws HandlerException Si une erreur survient lors de l'exécution de la commande.
     */
    public void execute(String message) throws HandlerException {
        LOGGER.info("Command received: [{}]", message.replace("\n", "\\n").replace("\r", "\\r"));

        if (tryExecute(message)) {
            return;
        }
        LOGGER.warn("Command {} not found", message);
    }

    /**
     * Parcourt les gestionnaires enregistrés pour trouver une correspondance.
     *
     * @param message Le message à tester par rapport aux motifs enregistrés.
     * @return true si une commande correspondante a été trouvée et exécutée, sinon false.
     * @throws HandlerException Si une erreur survient lors de l'exécution du gestionnaire de commande.
     */
    private boolean tryExecute(String message) throws HandlerException {
        for (var entry : handlers.entrySet()) {
            Pattern pattern = entry.getKey();
            CommandHandler handler = entry.getValue();
            Matcher matcher = pattern.matcher(message);

            if (matcher.matches()) {
                executeCommand(handler, matcher);
                return true;
            }
        }
        return false;
    }

    /**
     * Extrait les arguments des groupes de capture Regex et invoque le gestionnaire.
     *
     * @param handler Le gestionnaire de commande à invoquer.
     * @param matcher L'objet Matcher contenant les groupes de capture.
     * @throws HandlerException Si une erreur survient lors du traitement de la commande.
     */
    private void executeCommand(CommandHandler handler, Matcher matcher) throws HandlerException {
        List<String> args = new ArrayList<>();
        for (int i = 1; i <= matcher.groupCount(); i++) {
            args.add(matcher.group(i));
        }

        handler.handle(args);
    }
}
