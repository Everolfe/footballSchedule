package com.github.everolfe.footballmatches.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "arenas")
@Schema(description = "Arena")
public class Arena {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "City cannot be null")
    @Size(min = 1, max = 100, message = "City name must be between 1 and 100 characters")
    @Column(name = "city")
    private String city;

    @NotNull(message = "Capacity cannot be null")
    @Min(value = 1, message = "Capacity must be greater than 0")
    @Column(name = "capacity")
    private Integer capacity;

    @JsonIgnoreProperties("arena")
    @OneToMany(mappedBy = "arena", fetch = FetchType.LAZY,
                cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @Schema(description = "List of matches in arena")
    private  List<Match> matchList;

}
