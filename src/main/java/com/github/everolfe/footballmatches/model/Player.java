package com.github.everolfe.footballmatches.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "players")
@Data
@Schema(description = "Player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull(message = "Name cannot be null")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Column(name = "name")
    private String name;

    @Min(value = 1, message = "Age must be greater than 0")
    @Column(name = "age")
    private Integer age;

    @NotNull(message = "Country cannot be null")
    @Size(min = 1, max = 100, message = "Country must be between 1 and 100 characters")
    @Column(name = "country")
    private String country;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @Schema(description = "Team where player in")
    private Team team;
}
