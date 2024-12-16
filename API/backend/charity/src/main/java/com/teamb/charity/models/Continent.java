package com.teamb.charity.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("continents")
public class Continent {
    @Id
    private String id;

    @NotNull(message = "This field is required")
    private String country;
    @NotNull(message = "This field is required")
    private String continent;
}
