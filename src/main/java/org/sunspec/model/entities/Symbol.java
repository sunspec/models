
package org.sunspec.model.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "value",
    "label",
    "desc",
    "detail",
    "notes",
    "comments"
})
@ToString
@EqualsAndHashCode
@Getter
public class Symbol {
    @JsonProperty(value = "name",required = true)
    public String name;
    @JsonProperty(value = "value",required = true)
    public Object value;
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
