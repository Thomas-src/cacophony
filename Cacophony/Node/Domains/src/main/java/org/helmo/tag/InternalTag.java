package org.helmo.tag;

import org.helmo.subscriber.IUserSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un tag interne au système.
 * Un tag interne possède un nom complet et une liste d'abonnés.
 */
public class InternalTag implements ITag {

    private final String fullName;
    private final List<IUserSubscriber> subscribers = new ArrayList<>();

    /**
     * Construit un nouveau tag interne.
     * @param fullName Le nom complet du tag.
     * @param subscribers La liste initiale des abonnés (peut être nulle).
     */
    public InternalTag(String fullName, List<IUserSubscriber> subscribers) {
        this.fullName = fullName;
        if(subscribers != null){
            this.subscribers.addAll(subscribers);
        }
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    /**
     * Récupère la liste des abonnés à ce tag.
     * @return Une liste d'objets IUserSubscriber.
     */
    public List<IUserSubscriber> getSubscribers() {
        return subscribers;
    }

    /**
     * Vérifie si un utilisateur spécifique est abonné à ce tag.
     * @param username Le nom d'utilisateur à vérifier.
     * @return true si l'utilisateur est abonné, false sinon.
     */
    public boolean hasSubscriber(String username) {
        return subscribers.stream().anyMatch(sub -> sub.getFullName().equals(username));
    }
}