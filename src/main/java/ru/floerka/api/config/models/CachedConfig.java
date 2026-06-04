package ru.floerka.api.config.models;

import dev.dejvokep.boostedyaml.YamlDocument;

public record CachedConfig<T>(String name, YamlDocument document, T instance) {}
