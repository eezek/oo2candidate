package br.edu.ulbra.election.candidate.service;

import br.edu.ulbra.election.candidate.exception.GenericOutputException;
import br.edu.ulbra.election.candidate.input.v1.CandidateInput;
import br.edu.ulbra.election.candidate.model.Candidate;
import br.edu.ulbra.election.candidate.output.v1.CandidateOutput;
import br.edu.ulbra.election.candidate.output.v1.ElectionOutput;
import br.edu.ulbra.election.candidate.output.v1.GenericOutput;
import br.edu.ulbra.election.candidate.output.v1.PartyOutput;
import br.edu.ulbra.election.candidate.repository.CandidateRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CandidateService {

    private CandidateRepository candidateRepository;

    private ElectionService electionService;

    private PartyService partyService;

    private ModelMapper modelMapper;

    private static final String MESSAGE_NOT_FOUND = "Not found";
    private static final String MESSAGE_INVALID_NAME = "Invalid name";
    private static final String MESSAGE_INVALID_ELECTION_ID = "Invalid Election Id";
    private static final String NAME_REGEX = "(?i)^\\p{L}+ [\\p{L} ]+$";

    public List<CandidateOutput> getAll() {
        List<Candidate> candidates = (List<Candidate>) candidateRepository.findAll();
        return candidates.stream().map(this::toCandidateOutput).collect(Collectors.toList());
    }

    public CandidateOutput getById(Long candidateId) {
        return modelMapper.map(byId(candidateId), CandidateOutput.class);
    }

    public CandidateOutput create(CandidateInput candidateInput) {

        validateInput(candidateInput);
        validateDuplicate(candidateInput);

        Candidate candidate = modelMapper.map(candidateInput, Candidate.class);
        candidate = candidateRepository.save(candidate);

        return toCandidateOutput(candidate);
    }

    public CandidateOutput update(Long candidateId, CandidateInput candidateInput) {

        validateInput(candidateInput);
        validateDuplicate(candidateInput);

        Candidate candidate = byId(candidateId);

        candidate.setName(candidateInput.getName());
        candidate.setPartyId(candidateInput.getPartyId());
        candidate.setElectionId(candidateInput.getElectionId());
        candidate.setNumberElection(candidateInput.getNumberElection());
        candidate = candidateRepository.save(candidate);
        return modelMapper.map(candidate, CandidateOutput.class);
    }

    public GenericOutput delete(Long electionId) {

        candidateRepository.delete(byId(electionId));

        return new GenericOutput("Candidate deleted");

    }

    private Candidate byId(Long candidateId) {
        return candidateRepository.findById(candidateId).orElseThrow(() -> new EntityNotFoundException(MESSAGE_NOT_FOUND));
    }

    private void validateDuplicate(CandidateInput candidateInput) {
        Optional.ofNullable(candidateRepository.findFirstByNumberElectionAndAndElectionId(candidateInput.getNumberElection(), candidateInput.getElectionId()))
                .ifPresent(x -> {
                    throw new GenericOutputException("Duplicate Candidate!");
                });
    }

    private void validateInput(CandidateInput candidateInput) {
        if (StringUtils.isBlank(candidateInput.getName())
                || candidateInput.getName().length() < 5
                || !candidateInput.getName().matches(NAME_REGEX)) {
            throw new EntityNotFoundException(MESSAGE_INVALID_NAME);
        }
        if (candidateInput.getNumberElection() == null) {
            throw new GenericOutputException("Invalid Number Election");
        }
        if (candidateInput.getPartyId() == null) {
            throw new GenericOutputException("Invalid Party");
        }

        try {
            PartyOutput partyOutput = partyService.getById(candidateInput.getPartyId());
            if (!candidateInput.getNumberElection().toString().startsWith(partyOutput.getNumber().toString())) {
                throw new GenericOutputException("Number doesn't belong to party");
            }
        } catch (FeignException e) {
            if (e.status() == 500) {
                throw new GenericOutputException("Invalid Party");
            }
        }

        if (candidateInput.getElectionId() == null) {
            throw new GenericOutputException(MESSAGE_INVALID_ELECTION_ID);
        }
        try {
            electionService.getById(candidateInput.getElectionId());
        } catch (FeignException e) {
            if (e.status() == 500) {
                throw new GenericOutputException(MESSAGE_INVALID_ELECTION_ID);
            }
        }
    }

    private CandidateOutput toCandidateOutput(Candidate candidate) {
        CandidateOutput candidateOutput = modelMapper.map(candidate, CandidateOutput.class);
        ElectionOutput electionOutput = electionService.getById(candidate.getElectionId());
        candidateOutput.setElectionOutput(electionOutput);
        PartyOutput partyOutput = partyService.getById(candidate.getPartyId());
        candidateOutput.setPartyOutput(partyOutput);
        return candidateOutput;
    }

}
