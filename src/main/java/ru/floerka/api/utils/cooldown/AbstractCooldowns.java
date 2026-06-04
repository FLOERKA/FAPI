package ru.floerka.api.utils.cooldown;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCooldowns<T> {

    private final Map<T, Instant> times;

    public AbstractCooldowns() {
        this.times = new ConcurrentHashMap<>();
    }

    public void addCooldown(T name, int milliseconds) {
        if(times.containsKey(name)) {
            removeCooldown(name);
        }
        Instant instant = Instant.now();
        instant.plus(milliseconds, ChronoUnit.MILLIS);
        times.put(name, instant);
    }
    public void removeCooldown(T name) {
        times.remove(name);
    }
    public boolean hasCooldown(T name) {
        return times.containsKey(name);
    }
    public Duration getCooldown(T name) {
        Instant now = Instant.now();
        Instant instant = times.getOrDefault(name, now);
        return Duration.between(now, instant);
    }
    public long getCooldownAsMillis(T name) {
        return getCooldown(name).toMillis();
    }
    public boolean isExpired(T name) {
        return getCooldownAsMillis(name) <= 0;
    }
}
