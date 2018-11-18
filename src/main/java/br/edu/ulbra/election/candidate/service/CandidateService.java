package br.edu.ulbra.election.candidate.service;

import br.edu.ulbra.election.candidate.client.ElectionClientService;
import br.edu.ulbra.election.candidate.client.PartyClientService;
import br.edu.ulbra.election.candidate.exception.GenericOutputException;
import br.edu.ulbra.election.candidate.input.v1.CandidateInput;
import br.edu.ulbra.election.candidate.model.Candidate;
import br.edu.ulbra.election.candidate.output.v1.*;
import br.edu.ulbra.election.candidate.repository.CandidateRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import feign.FeignException;

@Service
@AllArgsConstructor
public class CandidateService {

    private CandidateRepository candidateRepository;
    private final ElectionClientService electionClientService;
    private final PartyClientService partyClientService;
    private ModelMapper modelMapper;

    private static final String MESSAGE_NOT_FOUND = "Not found";
    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_INVALID_ELECTION_ID = "Invalid Election Id";

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, ModelMapper modelMapper, ElectionClientService electionClientService, PartyClientService partyClientService){
        this.candidateRepository = candidateRepository;
        this.modelMapper = modelMapper;
        this.electionClientService = electionClientService;
        this.partyClientService = partyClientService;
    }

    public List<CandidateOutput> getAll() {
        List<Candidate> candidateList = (List<Candidate>)candidateRepository.findAll();
        return candidateList.stream().map(this::toCandidateOutput).collect(Collectors.toList());
    }

    public CandidateOutput getById(Long candidateId) {
        return modelMapper.map(byId(candidateId), CandidateOutput.class);
    }

    public CandidateOutput create(CandidateInput candidateInput) {
        validateInput(candidateInput);
        Candidate candidate = new Candidate();
        candidate.setName(candidateInput.getName());
        candidate.setPartyId(candidateInput.getPartyId());
        candidate.setElectionId(candidateInput.getElectionId());
        candidate.setNumberElection(candidateInput.getNumberElection());

        candidate = candidateRepository.save(candidate);

        return modelMapper.map(candidate, CandidateOutput.class);
    }

    public CandidateOutput update(Long candidateId, CandidateInput candidateInput) {

        Candidate candidate = byId(candidateId);
        this.validateElectionVotes(candidate.getElectionId());
        candidate.setName(candidateInput.getName());
        candidate.setPartyId(candidateInput.getPartyId());
        candidate.setElectionId(candidateInput.getElectionId());
        candidate.setNumberElection(candidateInput.getNumberElection());
        candidate = candidateRepository.save(candidate);
        return modelMapper.map(candidate, CandidateOutput.class);
    }

    public GenericOutput delete(Long candidateId) {
        Candidate candidate = byId(candidateId);
        this.validateElectionVotes(candidate.getElectionId());
        candidateRepository.delete(byId(candidateId));
        return new GenericOutput("Candidate deleted");
    }

    private Candidate byId(Long candidateId) {
        return candidateRepository.findById(candidateId).orElseThrow(() -> new EntityNotFoundException(MESSAGE_NOT_FOUND));
    }

    private void validateInput(CandidateInput candidateInput){
        if (StringUtils.isBlank(candidateInput.getName()) || candidateInput.getName().trim().length() < 5 || !candidateInput.getName().trim().contains(" ")){
            throw new GenericOutputException("Invalid name");
        }
        if (candidateInput.getNumberElection() == null){
            throw new GenericOutputException("Invalid Number Election");
        }
        if (candidateInput.getPartyId() == null){
            throw new GenericOutputException("Invalid Party");
        }

        try{
            PartyOutput partyOutput = partyClientService.getById(candidateInput.getPartyId());
            if (!candidateInput.getNumberElection().toString().startsWith(partyOutput.getNumber().toString())){
                throw new GenericOutputException("Number doesn't belong to party");
            }
        } catch (FeignException e){
            if (e.status() == 500) {
                throw new GenericOutputException("Invalid Party");
            }
        }

        if (candidateInput.getElectionId() == null){
            throw new GenericOutputException(MESSAGE_INVALID_ELECTION_ID);
        }
        try {
            electionClientService.getById(candidateInput.getElectionId());
        } catch (FeignException e){
            if (e.status() == 500) {
                throw new GenericOutputException(MESSAGE_INVALID_ELECTION_ID);
            }
        }
    }

    private void validateElectionVotes(Long electionId)
    {
        try{
            ResultOutput resultOutput = electionClientService.getVotesByElectionId(electionId);
            if(resultOutput.getTotalVotes() > 0)
                throw new GenericOutputException("Not Authorized");
        } catch (FeignException e){
            throw new GenericOutputException("Election request error");
        }
    }

    private CandidateOutput toCandidateOutput(Candidate candidate){
        CandidateOutput candidateOutput = modelMapper.map(candidate, CandidateOutput.class);
        ElectionOutput electionOutput = electionClientService.getById(candidate.getElectionId());
        candidateOutput.setElectionOutput(electionOutput);
        PartyOutput partyOutput = partyClientService.getById(candidate.getPartyId());
        candidateOutput.setPartyOutput(partyOutput);
        return candidateOutput;
    }

}
