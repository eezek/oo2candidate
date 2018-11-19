package br.edu.ulbra.election.candidate.service;

import br.edu.ulbra.election.candidate.output.v1.PartyOutput;
import lombok.AllArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@AllArgsConstructor
public class PartyService {

    private PartyClient partyClient;

    public PartyOutput getById(Long id) {
        return this.partyClient.getById(id);
    }

    @FeignClient(value = "party-service", url = "${url.party-service}")
    private interface PartyClient {

        @GetMapping("/v1/party/{partyId}")
        PartyOutput getById(@PathVariable(name = "partyId") Long partyId);
    }
}
