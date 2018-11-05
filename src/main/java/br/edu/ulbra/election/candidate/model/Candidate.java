package br.edu.ulbra.election.candidate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long partyId;

    @Column(name = "number", nullable = false)
    private Long numberElection;

    @Column(nullable = false)
    private Long electionId;
}
