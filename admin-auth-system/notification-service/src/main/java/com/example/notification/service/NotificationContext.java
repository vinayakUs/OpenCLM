package com.example.notification.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.notification.enums.NotificationChannelType;
import com.example.notification.service.strategy.NotificationStrategy;

@Service
public class NotificationContext {

    private final Map<NotificationChannelType, NotificationStrategy> strategyMap;

    public NotificationContext(List<NotificationStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(NotificationStrategy::channel, Function.identity()));
    }

    public NotificationStrategy getStrategy(NotificationChannelType channel) {
        return strategyMap.get(channel);
    }
}



