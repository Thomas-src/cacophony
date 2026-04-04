package org.helmo.tag;

/**
 * Représente une référence à un tag.
 */
public class TagReference implements ITag{

    private final String fullName;

    /**
     * Initialise une nouvelle instance de TagReference avec le nom complet spécifié.
     *
     * @param fullName Le nom complet du tag.
     */
    public TagReference(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Récupère le nom complet du tag.
     *
     * @return Le nom complet.
     */
    @Override
    public String getFullName() {
        return fullName;
    }

    /**
     * Indique si le tag est interne.
     * @return false car une référence de tag n'est pas considérée comme interne.
     */
    @Override
    public boolean isInternal() {
        return false;
    }

}
