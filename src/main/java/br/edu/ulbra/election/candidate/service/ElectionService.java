package br.edu.ulbra.election.candidate.service;

import br.edu.ulbra.election.candidate.output.v1.ElectionOutput;
import lombok.AllArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@AllArgsConstructor
public class ElectionService {

    private ElectionClient electionClient;

    public ElectionOutput getById(Long id) {
        return this.electionClient.getById(id);
    }

    @FeignClient(value = "election-service", url = "${url.election-service}")
    private interface ElectionClient {

        @GetMapping("/v1/election/{electionId}")
        ElectionOutput getById(@PathVariable(name = "electionId") Long electionId);
    }
}
