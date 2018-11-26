package br.edu.ulbra.election.candidate.api.v1;

import br.edu.ulbra.election.candidate.input.v1.CandidateInput;
import br.edu.ulbra.election.candidate.output.v1.CandidateOutput;
import br.edu.ulbra.election.candidate.output.v1.GenericOutput;
import br.edu.ulbra.election.candidate.service.CandidateService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/candidate")
@AllArgsConstructor
public class CandidateApi {

    private CandidateService candidateService;

    @GetMapping("/")
    @ApiOperation(value = "Get candidates List")
    public List<CandidateOutput> getAll() {
        return candidateService.getAll();
    }

    @GetMapping("/{candidateId}")
    @ApiOperation(value = "Get candidate by Id")
    public CandidateOutput getById(@PathVariable Long candidateId) {
        return candidateService.getById(candidateId);
    }

    @GetMapping("/number/{candidateNum}")
    @ApiOperation(value = "Get candidate by Number")
    public CandidateOutput getByNumber(@PathVariable Long candidateNum) {
        return candidateService.getByNumber(candidateNum);
    }

    @GetMapping("/election/{electionId}")
    @ApiOperation(value = "Get election by Id")
    public ResponseEntity<CandidateOutput> getByElectionId(@PathVariable Long electionId) {
        return ResponseEntity.ok(candidateService.getByElectionId(electionId));
    }

    @PostMapping("/")
    @ApiOperation(value = "Create new candidate")
    public CandidateOutput create(@RequestBody CandidateInput candidateInput) {
        return candidateService.create(candidateInput);
    }

    @PutMapping("/{candidateId}")
    @ApiOperation(value = "Update candidate")
    public CandidateOutput update(@PathVariable Long candidateId, @RequestBody CandidateInput candidateInput) {
        return candidateService.update(candidateId, candidateInput);
    }

    @DeleteMapping("/{candidateId}")
    @ApiOperation(value = "Delete candidate")
    public GenericOutput delete(@PathVariable Long candidateId) {
        return candidateService.delete(candidateId);
    }
}
