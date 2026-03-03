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
        return "";
    }
}
