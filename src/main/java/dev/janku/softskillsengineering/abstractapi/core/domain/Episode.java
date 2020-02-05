package dev.janku.softskillsengineering.abstractapi.core.domain;

import java.time.LocalDate;

public class Episode {
    private final int id;
    private final String name;
    private final LocalDate date;
    private final String description;
    private final String notes;

    private Episode(Builder builder) {
        id = builder.id;
        name = builder.name;
        date = builder.date;
        description = builder.description;
        notes = builder.notes;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "id=" + id  +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }

    public static final class Builder {
        private int id;
        private String name;
        private LocalDate date;
        private String description;
        private String notes;

        public int getId() {
            return id;
        }

        public Builder id(int val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder date(LocalDate val) {
            date = val;
            return this;
        }

        public Builder description(String val) {
            description = val;
            return this;
        }

        public Builder notes(String val) {
            notes = val;
            return this;
        }

        public Episode build() {
            return new Episode(this);
        }
    }
}
