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
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "arenas")
@Data
@Schema(description = "Arena")
public class Arena {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "city")
    private String city;

    @Column(name = "capacity")
    private Integer capacity;

    @JsonIgnoreProperties("arena")
    @OneToMany(mappedBy = "arena", fetch = FetchType.LAZY,
                cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @Schema(description = "List of matches in arena")
    private  List<Match> matchList;

}
