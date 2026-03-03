package org.helmo.services;

import org.helmo.views.IViewServer;

public class OnboardingService implements IOnboarding{

    private final IViewServer view;

    public OnboardingService(IViewServer viewServer) {
        this.view = viewServer;
    }

    @Override
    public String onNewOnboarding(String message) {
        view.showMessage("Nouvelle tentative d'inscription : " + message);
        // TODO -> Lire les utilisateurs deja inscrit
        // TODO -> Comparer avec celui-ci
        // TODO -> Si pas deja dedans,
        // TODO -> Inscrire dans un JSON le nouvel utilisateur + return "ok"
        // TODO -> Sinon return "err" (cf machine à états)

        return "";
    }
}
