package hu.paulintamas.foodorderingsystem.service.domain;

import java.time.ZonedDateTime;

public interface TimeProviderService {
    ZonedDateTime now();
}
