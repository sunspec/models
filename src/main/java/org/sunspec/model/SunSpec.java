package org.sunspec.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.sunspec.model.entities.SunSpecModel;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.sunspec.model.Utils.findAllResources;

public final class SunSpec {

    private SunSpec() {
        // Utility class
    }

    private static final SortedMap<Integer, SunSpecModel> models;

    static {
        Map<String, Resource> modelResources = findAllResources("file:json/model_*.json", false);
        ObjectMapper mapper = new ObjectMapper();

        Map<Integer, SunSpecModel> sunSpecModels = modelResources
            .values()
            .stream()
            .map(resource -> {
                try {
                    return mapper.readValue(resource.getInputStream(), SunSpecModel.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toMap(SunSpecModel::getId, Function.identity()));

        models = java.util.Collections.unmodifiableSortedMap(new TreeMap<>(sunSpecModels));
    }

    public static SortedMap<Integer, SunSpecModel> getModels() {
        return models;
    }

}
