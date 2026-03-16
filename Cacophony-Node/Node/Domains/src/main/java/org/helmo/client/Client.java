package org.helmo.client;

import org.helmo.ISubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * La classe User représente un utilisateur du système. Chaque User a un nom, un HashPassword qui contient le mot de passe hashé et les méthodes pour vérifier les challenges, et une liste de Subscribers qui sont abonnés à ce User.
 * Un User peut être abonné à d'autres Users, et d'autres Users ou Tags peuvent être abonnés à lui. La classe fournit des méthodes pour vérifier les réponses aux challenges, gérer les abonnements, et vérifier si un Subscriber est déjà abonné.
 */
public class Client implements ISubscriber {
    private String name;
    private HashPassword hashPassword;
    private List<ISubscriber> subscribers = new ArrayList<>();


    public Client() {};

    public static Client newClientFromOnboarding(String message) {
        if(message==null || !message.toUpperCase().startsWith("ONBOARDING")) {
            return null;
        }

        String[]  args = message.split(" ");
        Client actual =  new Client();
        actual.name = args[1];
        actual.subscribers = List.of(); // Un utilisateur qui vient de créer son compte n'est abonné a rien

        for (int i = 0; i < args.length; i++) {
            System.out.println("Argument " + args[i]);
        }

        String[] split = message.split("\\$");

        var rotation = Integer.parseInt(split[2]);
        var saltLength = 22;
        var salt = split[3].substring(0, saltLength);
        var hashpassword = split[3].substring(saltLength);

        actual.hashPassword = new HashPassword(hashpassword, rotation, salt);

        return actual;
    }

    /**
     * Constructeur de l'objet User.
     * @param name Le nom du User.
     * @param hashPassword Le HashPassword du User, qui contient le mot de passe hashé et les méthodes pour vérifier les challenges.
     * @param subscribers Une liste de Subscribers qui sont déjà abonnés à ce User. Si null, la liste des subscribers sera initialisée comme vide.
     */
    public Client(String name, HashPassword hashPassword, List<ISubscriber> subscribers) {
        this.name = name;
        this.hashPassword = hashPassword;
        if(subscribers != null) {
            this.subscribers.addAll(subscribers);
        }
    }

    public static Client Of(Client client) {
        Objects.requireNonNull(client);
        return new Client(client.getName(), client.hashPassword, client.subscribers);
    }


   @Override
    public String getName() {
        return name.split("@")[0];
    }

    @Override
    public String getFullName() {
        return name;
    }


    public HashPassword getHashPassword() {
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
        if (!(o instanceof Client client)) return false;
        return getFullName().equals(client.getFullName());
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
