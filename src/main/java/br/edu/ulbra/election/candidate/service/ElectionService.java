package br.edu.ulbra.election.candidate.service;

import br.edu.ulbra.election.candidate.output.v1.ElectionOutput;
import lombok.AllArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@AllArgsConstructor
public class ElectionService {

    private ElectionClient electionClient;

    public ElectionOutput getById(Long id) {
        return this.electionClient.getById(id);
    }

    public List<Object> findByElectionAndCandidate(Long candidateId, Long electionId) {
        return this.electionClient.findByElectionAndCandidate(candidateId, electionId);
    }

    public List<Object> findByCandidate(Long candidateId) {
        return this.electionClient.findByCandidate(candidateId);
    }

    @FeignClient(value = "election-service", url = "${url.election-service}")
    private interface ElectionClient {

        @GetMapping("/v1/election/{electionId}")
        ElectionOutput getById(@PathVariable(name = "electionId") Long electionId);

        @GetMapping("/v1/vote/candidate/{candidateId}/election/{electionId}")
        List<Object> findByElectionAndCandidate(@PathVariable(name = "candidateId") Long candidateId, @PathVariable(name = "electionId") Long electionId);

        @GetMapping("/v1/vote/candidate/{candidateId}")
        List<Object> findByCandidate(@PathVariable(name = "candidateId") Long candidateId);
    }
}
