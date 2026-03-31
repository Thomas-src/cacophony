package org.helmo.Exception;

/**
 * Exception levée lors d'une erreur durant le processus d'onboarding.
 */
public class OnboardingException extends Exception {
    /**
     * Construit une nouvelle exception avec le message détaillé spécifié.
     *
     * @param message le message détaillé.
     */
    public OnboardingException(String message) {
        super(message);
    }
}
