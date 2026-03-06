package org.helmo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * La classe User représente un utilisateur du système. Chaque User a un nom, un HashPassword qui contient le mot de passe hashé et les méthodes pour vérifier les challenges, et une liste de Subscribers qui sont abonnés à ce User.
 * Un User peut être abonné à d'autres Users, et d'autres Users ou Tags peuvent être abonnés à lui. La classe fournit des méthodes pour vérifier les réponses aux challenges, gérer les abonnements, et vérifier si un Subscriber est déjà abonné.
 */
public class User implements ISubscriber {
    private final String name;
    private final HashPassword hashPassword;
    private final List<ISubscriber> subscribers = new ArrayList<>();


    /**
     * Constructeur de l'objet User.
     * @param name Le nom du User.
     * @param hashPassword Le HashPassword du User, qui contient le mot de passe hashé et les méthodes pour vérifier les challenges.
     * @param subscribers Une liste de Subscribers qui sont déjà abonnés à ce User. Si null, la liste des subscribers sera initialisée comme vide.
     */
    public User(String name, HashPassword hashPassword, List<ISubscriber> subscribers) {
        this.name = name;
        this.hashPassword = hashPassword;
        if(subscribers != null) {
            this.subscribers.addAll(subscribers);
        }
    }


   @Override
    public String getName() {
        return name.split("@")[0];
    }

    @Override
    public String getFullName() {
        return name;
    }


    HashPassword getHashPassword() {
        return hashPassword;
    }

    /**
     * Vérifie si la réponse du client correspond au challenge généré par le serveur.
     * @param clientResponse Le hexadécimal que le client a envoyé en réponse au challenge.
     * @param challenge Le challenge de 22 caractères envoyé au client.
     * @return vrai si la réponse du client est correcte, faux sinon.
     */
    public boolean verifyChallengeResponse(String clientResponse, String challenge) {
        return getHashPassword().checkChallenge(clientResponse, challenge);
    }

    /**
     * Vérifie si un Subscriber est déjà abonné à ce User.
     * @param subscriber Le Subscriber à vérifier.
     * @return vrai si le Subscriber est déjà abonné, faux sinon.
     */
    public boolean isSubscribed(ISubscriber subscriber) {
        Objects.requireNonNull(subscriber);
        return subscribers.contains(subscriber);
    }
    @Override
    public void subscribe(ISubscriber subscriber) {
        Objects.requireNonNull(subscriber);
        subscribers.add(subscriber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return getFullName().equals(user.getFullName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public List<ISubscriber> getSubscribers() {
        return List.copyOf(subscribers);
    }

    /**
     * Généré par ia
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User {")
                .append("name='").append(name).append('\'')
                .append(", fullname='").append(getFullName()).append('\'');

        if (subscribers.isEmpty()) {
            sb.append(", subscribers=[]");
        } else {
            sb.append(", subscribers=[");
            for (int i = 0; i < subscribers.size(); i++) {
                sb.append(subscribers.get(i).getName());
                if (i < subscribers.size() - 1) sb.append(", ");
            }
            sb.append("]");
        }

        sb.append('}');
        return sb.toString();
    }
}
