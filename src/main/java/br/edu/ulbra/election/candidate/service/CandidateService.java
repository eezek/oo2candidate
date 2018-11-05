package br.edu.ulbra.election.candidate.service;

import br.edu.ulbra.election.candidate.input.v1.CandidateInput;
import br.edu.ulbra.election.candidate.model.Candidate;
import br.edu.ulbra.election.candidate.output.v1.CandidateOutput;
import br.edu.ulbra.election.candidate.output.v1.ElectionOutput;
import br.edu.ulbra.election.candidate.output.v1.GenericOutput;
import br.edu.ulbra.election.candidate.output.v1.PartyOutput;
import br.edu.ulbra.election.candidate.repository.CandidateRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.Type;
import java.util.List;

@Service
@AllArgsConstructor
public class CandidateService {

    private CandidateRepository candidateRepository;

    private ModelMapper modelMapper;

    private static final String MESSAGE_NOT_FOUND = "Not found";

    public List<CandidateOutput> getAll() {
        Type candidateOutputListType = new TypeToken<List<CandidateOutput>>() {
        }.getType();
        return modelMapper.map(candidateRepository.findAll(), candidateOutputListType);
    }

    public CandidateOutput getById(Long candidateId) {
        return modelMapper.map(byId(candidateId), CandidateOutput.class);
    }

    public CandidateOutput create(CandidateInput candidateInput) {

        ElectionOutput electionOutput = new ElectionOutput();
        PartyOutput partyOutput = new PartyOutput();

        Candidate candidate = modelMapper.map(candidateInput, Candidate.class);

        candidate = candidateRepository.save(candidate);

        CandidateOutput response = modelMapper.map(candidate, CandidateOutput.class);

        electionOutput.setId(candidate.getElectionId());
        response.setElectionOutput(electionOutput);
        partyOutput.setId(candidate.getPartyId());
        response.setPartyOutput(partyOutput);

        return response;
    }

    public CandidateOutput update(Long candidateId, CandidateInput candidateInput) {

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

}
