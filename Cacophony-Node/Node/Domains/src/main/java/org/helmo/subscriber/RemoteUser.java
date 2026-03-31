package org.helmo.subscriber;

/**
 * Représente un utilisateur distant dans le système.
 */
public class RemoteUser implements IUserSubscriber {
    private final String fullName;

    /**
     * Construit un nouvel utilisateur distant.
     *
     * @param fullName Le nom complet de l'utilisateur.
     */
    public RemoteUser(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Récupère le nom complet de l'utilisateur.
     *
     * @return Le nom complet.
     */
    @Override
    public String getFullName() {
        return fullName;
    }

    /**
     * Indique si l'utilisateur est local.
     * @return false car il s'agit d'un utilisateur distant.
     */
    @Override
    public boolean isLocal() {
        return false;
    }

}
