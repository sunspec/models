
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
    "value",
    "count",
    "size",
    "sf",
    "units",
    "access",
    "mandatory",
    "static",
    "label",
    "desc",
    "detail",
    "notes",
    "comments",
    "symbols"
})
@ToString
@EqualsAndHashCode
@Getter
public class Point {

    @JsonProperty(value = "name", required = true)
    public String name;
    @JsonProperty(value = "type", required = true)
    public Point.Type type;
    @JsonProperty("value")
    public Integer value;
    @JsonProperty("count")
    public Integer count;
    @JsonProperty(value = "size", required = true)
    public Integer size;
    @JsonProperty("sf")
    public String scalingFactor; // The NAME of the scaling factor point OR a [-10;10] integer
    @JsonProperty("units")
    public String units;
    @JsonProperty("access")
    public Point.Access access = Access.READONLY;
    @JsonProperty("mandatory")
    public Point.Mandatory mandatory = Mandatory.OPTIONAL;
    @JsonProperty("static")
    public Point.Static sTatic = Static.DYNAMIC;
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
    @JsonProperty("symbols")
    public List<Symbol> symbols = new ArrayList<>();

    public enum Access {
        READONLY("R"),
        READWRITE("RW");
        private final String value;
        private static final Map<String, Point.Access> CONSTANTS = new HashMap<>();

        static {
            for (Point.Access c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Access(String value) {
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
        public static Point.Access fromValue(String value) {
            Point.Access constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum Mandatory {
        MANDATORY("M"),
        OPTIONAL("O");
        private final String value;
        private static final Map<String, Point.Mandatory> CONSTANTS = new HashMap<>();

        static {
            for (Point.Mandatory c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Mandatory(String value) {
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
        public static Point.Mandatory fromValue(String value) {
            Point.Mandatory constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }


    public enum Static {
        DYNAMIC("D"),
        STATIC("S");
        private final String value;
        private static final Map<String, Point.Static> CONSTANTS = new HashMap<>();

        static {
            for (Point.Static c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Static(String value) {
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
        public static Point.Static fromValue(String value) {
            Point.Static constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }


    public enum Type {
        INT_16("int16", 1),
        INT_32("int32", 2),
        INT_64("int64", 4),
        RAW_16("raw16", 1),
        UINT_16("uint16", 1),
        UINT_32("uint32", 2),
        UINT_64("uint64", 4),
        ACC_16("acc16", 1),
        ACC_32("acc32", 2),
        ACC_64("acc64", 4),
        BITFIELD_16("bitfield16", 1),
        BITFIELD_32("bitfield32", 2),
        BITFIELD_64("bitfield64", 4),
        ENUM_16("enum16", 1),
        ENUM_32("enum32", 2),
        FLOAT_32("float32", 2),
        FLOAT_64("float64", 4),
        STRING("string", null),
        PAD("pad", 1),
        IPADDR("ipaddr", 2),
        IPV_6_ADDR("ipv6addr", 8),
        EUI_48("eui48", 4),
        SUNSSF("sunssf", 1),
        COUNT("count", 1);
        private final String value;
        private final Integer registerCount;
        private static final Map<String, Point.Type> CONSTANTS = new HashMap<>();

        static {
            for (Point.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Type(String value, Integer registerCount) {
            this.value = value;
            this.registerCount = registerCount;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        /**
         * @return the number of 16 bit modbus registers for this value or
         *         NULL if it must be explicitly specified in the model (i.e. it is a string).
         */
        public Integer getRegisterCount() {
            return registerCount;
        }

        @JsonCreator
        public static Point.Type fromValue(String value) {
            Point.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
