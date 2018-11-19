package br.edu.ulbra.election.candidate.repository;

import br.edu.ulbra.election.candidate.model.Candidate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends CrudRepository<Candidate, Long> {

    Candidate findFirstByNumberElectionAndAndElectionId(Long numberElection, Long electionId);

    Candidate findByElectionId(Long electionId);

    Optional<Candidate> findByNumberElection(Long number);

}
