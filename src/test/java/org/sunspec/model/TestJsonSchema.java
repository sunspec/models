package org.sunspec.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static com.networknt.schema.SpecVersion.VersionFlag.V7;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.sunspec.model.Utils.findAllResources;

class TestJsonSchema {

    private final ObjectMapper mapper = new ObjectMapper();

    private JsonSchema jsonSchema = null;
    private JsonSchema getSchema() {
        if (jsonSchema == null) {
            Resource schema = findAllResources("file:json/schema.json", false).get("schema.json");
            assertNotNull(schema, "Missing schema.json");

            InputStream schemaStream = null;
            try {
                schemaStream = schema.getInputStream();
            } catch (IOException e) {
                fail(e.getMessage(), e);
            }
            assertNotNull(schemaStream, "Unable to read schema.json file");

            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(V7);
            jsonSchema = factory.getSchema(schemaStream);
            assertNotNull(jsonSchema, "Unable to parse schema.json");
        }
        return jsonSchema;
    }

    @Test
    void testModelsAgainstSchema() throws IOException {
        Map<String, Resource> models = findAllResources("file:json/model_*.json", false);

        for (Map.Entry<String, Resource> modelEntry : models.entrySet()) {
            // Load the schema
            JsonSchema jsonSchema = getSchema();

            // Read the file
            JsonNode jsonNode = mapper.readTree(modelEntry.getValue().getInputStream());

            // Verify schema compliance
            Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);

            // Expect no errors
            assertEquals(0, errors.size(),
                String.format("Model in %s failed schema compliance with : %s",
                    modelEntry.getKey(), errors));
        }
    }

}
