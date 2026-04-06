package org.helmo.repository;

import org.helmo.SpeakerConfig;
import org.helmo.exceptions.ConfigReaderException;

/**
 * Interface définissant le dépôt pour la lecture de la configuration.
 * <br>
 * généré par ia
 */
public interface IConfigReaderRepository {
    /**
     * Récupère la configuration du Speaker.
     *
     * @return L'objet SpeakerConfig contenant les paramètres de configuration.
     * @throws ConfigReaderException Si une erreur survient lors de la lecture de la configuration.
     */
    SpeakerConfig getConfig() throws ConfigReaderException;
}
