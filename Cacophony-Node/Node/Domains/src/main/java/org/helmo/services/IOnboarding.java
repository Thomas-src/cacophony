package org.helmo.services;

import org.helmo.Exception.OnboardingException;
import org.helmo.repository.UserReaderException;
import org.helmo.repository.UserSaveException;

public interface IOnboarding {
    public String onNewOnboarding(String message) throws OnboardingException;
}
