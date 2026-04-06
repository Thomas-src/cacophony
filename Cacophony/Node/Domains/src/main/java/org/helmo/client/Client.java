package org.helmo.client;

import org.helmo.subscriber.IUserSubscriber;
import org.helmo.tag.ITag;

import java.util.ArrayList;
import java.util.List;


/**
 * La classe User représente un utilisateur du système. Chaque User a un nom, un HashPassword qui contient le mot de passe hashé et les méthodes pour vérifier les challenges, et une liste de Subscribers qui sont abonnés à ce User.
 * Un User peut être abonné à d'autres Users, et d'autres Users ou Tags peuvent être abonnés à lui. La classe fournit des méthodes pour vérifier les réponses aux challenges, gérer les abonnements, et vérifier si un Subscriber est déjà abonné.
 */
public class Client {
    private final String name;
    private final HashPassword hashPassword;
    private final List<IUserSubscriber> subscribers = new ArrayList<>();
    private final List<ITag> internalTags = new ArrayList<>();
    private final String fullName;
    private final List<String> messages = new ArrayList<>();
    private final List<String> subscriptions = new ArrayList<>();
    /**
     * Constructeur complet de l'objet Client.
     *
     * @param name          Le nom de l'utilisateur.
     * @param hashPassword  L'objet contenant le mot de passe hashé.
     * @param subscribers   La liste des abonnés à ce client.
     * @param domain        Le domaine associé au client.
     * @param internalTags  La liste des tags internes.
     * @param messages      L'historique des messages.
     * @param subscriptions La liste des abonnements du client.
     */
    public Client(String name, HashPassword hashPassword, List<IUserSubscriber> subscribers, String domain, List<ITag> internalTags, List<String> messages, List<String> subscriptions) {
        this.name = name;
        this.hashPassword = hashPassword;
        this.fullName = name + '@' + domain;
        if (subscribers != null) {
            this.subscribers.addAll(subscribers);
        }
        if (internalTags != null) {
            this.internalTags.addAll(internalTags);
        }
        if (messages != null) {
            this.messages.addAll(messages);
        }
        if (subscriptions != null) {
            this.messages.addAll(subscriptions);
        }
    }

    /**
     * Constructeur simplifié de l'objet Client.
     *
     * @param name         Le nom de l'utilisateur.
     * @param hashPassword L'objet contenant le mot de passe hashé.
     * @param domain       Le domaine associé au client.
     */
    public Client(String name, HashPassword hashPassword, String domain) {
        this.name = name;
        this.hashPassword = hashPassword;
        this.fullName = name + '@' + domain;
    }

    /**
     * Récupère la liste des messages du client.
     *
     * @return Une liste de chaînes de caractères représentant les messages.
     */
    public List<String> getMessages() {
        return messages;
    }

    public String getName() {
        return name;
    }

    /**
     * Récupère le nom complet (nom@domaine) du client.
     *
     * @return Le nom complet.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Récupère l'objet de hachage du mot de passe.
     *
     * @return Le HashPassword du client.
     */
    public HashPassword getHashPassword() {
        return hashPassword;
    }

    /**
     * Vérifie si la réponse du client correspond au challenge généré par le serveur.
     *
     * @param clientResponse Le hexadécimal que le client a envoyé en réponse au challenge.
     * @param challenge      Le challenge de 22 caractères envoyé au client.
     * @return vrai si la réponse du client est correcte, faux sinon.
     */
    public boolean verifyChallengeResponse(String clientResponse, String challenge) {
        return hashPassword.checkChallenge(clientResponse, challenge);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return getFullName().equals(client.getFullName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Récupère la liste des abonnés.
     *
     * @return Une liste d'objets IUserSubscriber.
     */
    public List<IUserSubscriber> getSubscribers() {
        return subscribers;
    }

    /**
     * Récupère la liste des tags associés.
     *
     * @return Une liste d'objets ITag.
     */
    public List<ITag> getTags() {
        return internalTags;
    }

    /**
     * Récupère la liste des abonnements du client.
     *
     * @return Une liste de chaînes de caractères représentant les abonnements.
     */
    public List<String> getSubscribtions() {
        return  subscriptions;
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
                sb.append(subscribers.get(i).getFullName());
                if (i < subscribers.size() - 1) sb.append(", ");
            }
            sb.append("]");
        }
        if (internalTags.isEmpty()) {
            sb.append(", tags=[]");
        } else {
            sb.append(", tags=[");
            for (int i = 0; i < internalTags.size(); i++) {
                sb.append(internalTags.get(i).getFullName());
                if (i < internalTags.size() - 1) sb.append(", ");
            }
            sb.append("]");
        }

        sb.append('}');
        return sb.toString();
    }
}
