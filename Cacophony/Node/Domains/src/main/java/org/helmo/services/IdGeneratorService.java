package org.helmo.services;

import org.helmo.Exception.ConfigSaveException;
import org.helmo.repository.IConfigRepository;

/**
 * Service responsable de la génération d'identifiants numériques uniques et séquentiels.
 */
public class IdGeneratorService {
    private int count;
    private final IConfigRepository configRepository;

    /**
     * Initialise le service avec une valeur de départ et un dépôt pour la persistance.
     *
     * @param count La valeur initiale du compteur.
     * @param configRepository Le dépôt utilisé pour sauvegarder l'état du compteur.
     */
    public IdGeneratorService(int count, IConfigRepository configRepository) {
        this.count = count;
        this.configRepository = configRepository;
    }

    /**
     * Incrémente le compteur interne, tente de persister la nouvelle valeur
     * et retourne l'identifiant formaté.
     *
     * @return Un entier représentant le prochain identifiant (modulo 99999).
     */
    private int getNextCount() {
        count = count++;
        try {
            configRepository.saveId(count);
        } catch (ConfigSaveException ignored) {
        }
        return count++ % 99999; // pas une solution optimale mais meilleure approche avec le respect de la grammaire
    }

    /**
     * Génère le prochain identifiant sous forme de chaîne de caractères formatée sur 5 chiffres.
     *
     * @return Une chaîne de caractères (ex: "00001").
     */
    public String getNextCountString() {
        return String.format("%05d", getNextCount());
    }

}
