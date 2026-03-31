package org.helmo.Exception;

/*
* Exception levée lors d'une erreur durant la sauvegarde d'un utilisateur.
 */
public class UserSaveException extends Exception {
    /**
     * Construit une nouvelle exception avec le message détaillé spécifié.
     */
    public UserSaveException(String message) {
        super(message);
    }
}
