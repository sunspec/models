package org.sunspec.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

final class Utils {
    private Utils() {
        // Utility class
    }

    private static final Logger LOG = LogManager.getLogger();

    public static Map<String, Resource> findAllResources(String resourceString, boolean verbose) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Map<String, Resource> resources = new TreeMap<>();
        try {
            Resource[] resourceArray = resolver.getResources(resourceString);
            if (verbose) {
                LOG.debug("Loading {} files using expression: {}", resourceArray.length, resourceString);
            }
            for (Resource resource : resourceArray) {
                if (verbose) {
                    LOG.debug("- Found file {} ({} bytes)", resource.getFilename(), resource.contentLength());
                }
                resources.put(resource.getFilename(), resource);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading sunspec resources: " + e.getMessage(), e);
        }
        return resources;
    }

}
