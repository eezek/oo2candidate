package br.edu.ulbra.election.candidate.repository;

import br.edu.ulbra.election.candidate.model.Candidate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends CrudRepository<Candidate, Long> {
}
