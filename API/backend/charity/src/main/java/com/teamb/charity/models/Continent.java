package com.teamb.charity.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    //! Verify if country is a foreign key
    @NotNull(message = "This field is required")
    private String country;
    @NotNull(message = "This field is required")
    @Size(min = 1, max = 20)
    private String continent;
}