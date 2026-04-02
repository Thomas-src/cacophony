package org.helmo.handler;

import org.helmo.exceptions.HandlerException;

import java.util.List;

/**
 * Interface représentant un gestionnaire de commandes.
 */
public interface CommandHandler {
    /**
     * Traite une commande avec les arguments fournis.
     *
     * @param args La liste des arguments de la commande.
     * @throws HandlerException Si une erreur survient lors du traitement de la commande.
     */
    void handle(List<String> args) throws HandlerException;
}
