package org.sunspec.model.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON Schema for SunSpec information model definition
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "group",
    "label",
    "desc",
    "detail",
    "notes",
    "comments"
})
@ToString
@EqualsAndHashCode
@Getter
public class SunSpecModel {
    @JsonProperty(value = "id",required = true)
    public Integer id;
    @JsonProperty(value = "group",required = true)
    public Group group;
    @JsonProperty("label")
    public String label;
    @JsonProperty("desc")
    public String description;
    @JsonProperty("detail")
    public String detail;
    @JsonProperty("notes")
    public String notes;
    @JsonProperty("comments")
    public List<String> comments = new ArrayList<>();
}
