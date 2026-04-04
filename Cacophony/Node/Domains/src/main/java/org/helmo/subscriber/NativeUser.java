package org.helmo.subscriber;

/**
 * Représente un utilisateur natif du système.
 */
public class NativeUser implements IUserSubscriber {
    private final String fullName;

    /**
     * Crée une nouvelle instance de NativeUser.
     *
     * @param fullName Le nom complet de l'utilisateur.
     */
    public NativeUser(String fullName) {
        this.fullName = fullName;
    }

    /**
     * {@inheritDoc}
     * @return Le nom complet de l'utilisateur.
     */
    @Override
    public String getFullName() {
        return fullName;
    }

    /**
     * {@inheritDoc}
     * @return Vrai, car l'utilisateur est local.
     */
    @Override
    public boolean isLocal() {
        return true;
    }
}
