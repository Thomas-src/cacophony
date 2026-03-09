package org.helmo.services;

import org.helmo.Exception.OnboardingException;

public interface IOnboarding {
    public String onNewOnboarding(String message) throws OnboardingException;
}
