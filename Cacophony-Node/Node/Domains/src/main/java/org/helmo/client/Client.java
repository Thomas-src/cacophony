package org.helmo.client;

import org.helmo.subscriber.IUserSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * La classe User représente un utilisateur du système. Chaque User a un nom, un HashPassword qui contient le mot de passe hashé et les méthodes pour vérifier les challenges, et une liste de Subscribers qui sont abonnés à ce User.
 * Un User peut être abonné à d'autres Users, et d'autres Users ou Tags peuvent être abonnés à lui. La classe fournit des méthodes pour vérifier les réponses aux challenges, gérer les abonnements, et vérifier si un Subscriber est déjà abonné.
 */
public class Client  {
    private String name;
    private HashPassword hashPassword;
    private List<IUserSubscriber> subscribers = new ArrayList<>();
    private String fullName;

    public Client() {
    }

    public static Client newClientFromOnboarding(List<String> args) {


        Client actual = new Client();
        actual.name = args.getFirst();
        actual.subscribers = new ArrayList<>(); // Un utilisateur qui vient de créer son compte n'est abonné a rien


        var rotation = Integer.parseInt(args.get(1));
        var saltLength = 22;
        var salt = args.get(2).substring(0, saltLength);
        var hashpassword =args.get(2).substring(saltLength);

        actual.hashPassword = new HashPassword(hashpassword, rotation, salt);

        return actual;
    }

    /**
     * Constructeur de l'objet User.
     *
     * @param name         Le nom du User.
     * @param hashPassword Le HashPassword du User, qui contient le mot de passe hashé et les méthodes pour vérifier les challenges.
     * @param subscribers  Une liste de Subscribers qui sont déjà abonnés à ce User. Si null, la liste des subscribers sera initialisée comme vide.
     */
    public Client(String name, HashPassword hashPassword, List<IUserSubscriber> subscribers, String domain) {
        this.name = name;
        this.hashPassword = hashPassword;
        this.fullName = name+'@'+domain;
        if (subscribers != null) {
            this.subscribers.addAll(subscribers);
        }
    }

    public String getName() {
        return name.split("@")[0];
    }

    public String getFullName() {
        return fullName;
    }


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
        return getHashPassword().checkChallenge(clientResponse, challenge);
    }

    public void subscribe(IUserSubscriber subscriber) {
        Objects.requireNonNull(subscriber);
        subscribers.add(subscriber);
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

    public List<IUserSubscriber> getSubscribers() {
        return subscribers;
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

        sb.append('}');
        return sb.toString();
    }
}
