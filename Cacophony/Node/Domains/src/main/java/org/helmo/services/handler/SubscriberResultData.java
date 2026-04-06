package org.helmo.services.handler;


import java.util.Set;

/**
 * Représente les données de résultat pour les abonnés.
 *
 * @param localSubscribers L'ensemble des abonnés locaux.
 * @param remoteSubscribers L'ensemble des abonnés distants.
 * @param remoteTags L'ensemble des tags distants.
 */
public record SubscriberResultData(
        Set<String> localSubscribers,
        Set<String> remoteSubscribers,
        Set<String> remoteTags
) {}