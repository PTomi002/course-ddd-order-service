package hu.paulintamas.foodorderingsystem.service.domain;

import java.time.Clock;
import java.time.ZonedDateTime;

public class TimeProviderServiceImpl implements TimeProviderService{

    private final static Clock clock = Clock.systemUTC();

    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now(clock);
    }
}
