package org.helmo.repository;


import org.helmo.Config;
import org.helmo.Exception.ConfigReaderException;
import org.helmo.Exception.ConfigSaveException;

/**
 * Interface définissant les méthodes pour la lecture de la configuration.
 */
public interface IConfigRepository {
    /**
     * Récupère la configuration actuelle.
     *
     * @return L'objet Config contenant les paramètres.
     * @throws ConfigReaderException Si une erreur survient lors de la lecture de la configuration.
     */
    Config getConfig() throws ConfigReaderException;

    /**
     * Sauvegarde l'identifiant actuel dans la configuration.
     *
     * @param currentId L'identifiant à sauvegarder.
     * @throws ConfigSaveException Si une erreur survient lors de la sauvegarde de la configuration.
     */
    void saveId(int currentId) throws ConfigSaveException;
}
