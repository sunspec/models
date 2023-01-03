
package org.sunspec.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "type",
    "count",
    "points",
    "groups",
    "label",
    "desc",
    "detail",
    "notes",
    "comments"
})
@ToString
@EqualsAndHashCode
@Getter
public class Group {

    @JsonProperty(value = "name", required = true)
    public String name;
    @JsonProperty(value = "type",required = true)
    public Group.Type type;
    @JsonProperty("count")
    public String count = "1"; // The Integer OR the NAME of the count point
    @JsonProperty("points")
    public List<Point> points = new ArrayList<>();
    @JsonProperty("groups")
    public List<Group> groups = new ArrayList<>();
    @JsonProperty("label")
    public String label;
    @JsonProperty("desc")
    public String desc;
    @JsonProperty("detail")
    public String detail;
    @JsonProperty("notes")
    public String notes;
    @JsonProperty("comments")
    public List<String> comments = new ArrayList<>();


    public enum Type {
        GROUP("group"),
        SYNC("sync");
        private final String value;
        private static final Map<String, Group.Type> CONSTANTS = new HashMap<>();

        static {
            for (Group.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Group.Type fromValue(String value) {
            Group.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
