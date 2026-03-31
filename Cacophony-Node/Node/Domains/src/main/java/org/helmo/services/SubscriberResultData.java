package org.helmo.services;


import java.util.Set;

public record SubscriberResultData(
        Set<String> localSubscribers,
        Set<String> remoteSubscribers,
        Set<String> remoteTags
) {}