package traineeship_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import traineeship_app.domainmodel.*;
import traineeship_app.mappers.*;
import traineeship_app.services.ProfessorServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TestProfessorService {

    @Mock
    private ProfessorMapper professorMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TraineeshipPositionsMapper positionsMapper;

    @Mock
    private TraineeshipPositionsMapper traineeshipPositionsMapper;

    @Mock
    private EvaluationMapper evaluationMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfessorServiceImpl professorService;

    private Professor sampleProfessor;
    private TraineeshipPosition samplePosition;
    private Evaluation sampleEvaluation;

    @BeforeEach
    void setUp() {
        sampleProfessor = new Professor();
        sampleProfessor.setUsername("prof123");
        sampleProfessor.setProfessorName("Dr. Jane Doe");
        sampleProfessor.setPassword("secret");

        samplePosition = new TraineeshipPosition();
        samplePosition.setId(1);
        samplePosition.setSupervisor(sampleProfessor);

        sampleEvaluation = new Evaluation();
        sampleEvaluation.setProfessor(sampleProfessor);
        sampleEvaluation.setTraineeshipPosition(samplePosition);
        sampleEvaluation.setEvaluationType(Evaluation.EvaluationType.PROFESSOR_EVALUATION);
        sampleEvaluation.setMotivation(5);
        sampleEvaluation.setEfficiency(4);
        sampleEvaluation.setEffectiveness(3);
        sampleEvaluation.setCompanyFacilities(4);
        sampleEvaluation.setCompanyGuidance(5);
    }

    @Test
    void testRetrieveProfile() {
        when(professorMapper.findByUsername("prof123")).thenReturn(sampleProfessor);

        Professor found = professorService.retrieveProfile("prof123");

        assertNotNull(found);
        assertEquals("prof123", found.getUsername());
    }

    @Test
    void testSaveProfile_Success() {
        when(userMapper.existsByUsername("prof123")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encodedPassword");

        professorService.SaveProfile(sampleProfessor);

        ArgumentCaptor<Professor> captor = ArgumentCaptor.forClass(Professor.class);
        verify(professorMapper).save(captor.capture());

        Professor savedProf = captor.getValue();
        assertEquals("prof123", savedProf.getUsername());
        assertEquals("encodedPassword", savedProf.getPassword());
        assertEquals("Dr. Jane Doe", savedProf.getProfessorName());

    }

    @Test
    void testSaveProfile_UsernameExists_Throws() {
        when(userMapper.existsByUsername("prof123")).thenReturn(true);

        DataIntegrityViolationException ex = assertThrows(DataIntegrityViolationException.class,
                () -> professorService.SaveProfile(sampleProfessor));

        assertEquals("Username already exists", ex.getMessage());
        verify(professorMapper, never()).save(any());
    }

    @Test
    void testUpdateProfile_Success() {
        when(professorMapper.findByUsername("prof123")).thenReturn(sampleProfessor);

        sampleProfessor.setProfessorName("Dr. Jane Updated");
        sampleProfessor.setInterests("New Interests");

        professorService.UpdateProfile(sampleProfessor);

        verify(professorMapper).save(sampleProfessor);
        assertEquals("Dr. Jane Updated", sampleProfessor.getProfessorName());
        assertEquals("New Interests", sampleProfessor.getInterests());
    }

    @Test
    void testUpdateProfile_NoUsername_Throws() {
        sampleProfessor.setUsername(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> professorService.UpdateProfile(sampleProfessor));

        assertEquals("No username provided in form.", ex.getMessage());
        verify(professorMapper, never()).save(any());
    }

    @Test
    void testUpdateProfile_ProfessorNotFound_Throws() {
        when(professorMapper.findByUsername("prof123")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> professorService.UpdateProfile(sampleProfessor));

        assertEquals("Professor not found.", ex.getMessage());
        verify(professorMapper, never()).save(any());
    }

    @Test
    void testRetrieveAssignedPositions() {
        when(traineeshipPositionsMapper.findBySupervisorUsername("prof123"))
                .thenReturn(Arrays.asList(samplePosition));

        List<TraineeshipPosition> positions = professorService.retrieveAssignedPositions("prof123");

        assertEquals(1, positions.get(0).getId());

    }

    @Test
    void testSaveEvaluation() {
        when(positionsMapper.findById(1)).thenReturn(Optional.of(samplePosition));

        professorService.saveEvaluation(sampleEvaluation);

        verify(evaluationMapper).save(sampleEvaluation);
        assertEquals(Evaluation.EvaluationType.PROFESSOR_EVALUATION, sampleEvaluation.getEvaluationType());
        assertEquals(samplePosition, sampleEvaluation.getTraineeshipPosition());
        assertEquals(sampleProfessor, sampleEvaluation.getProfessor());
    }

    @Test
    void testSaveEvaluation_PositionNotFound_Throws() {
        when(positionsMapper.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> professorService.saveEvaluation(sampleEvaluation));

        assertEquals("Position not found", ex.getMessage());
        verify(evaluationMapper, never()).save(any());
    }

    @Test
    void testUpdateEvaluation_Success() {
        when(evaluationMapper.findByEvaluationTypeAndTraineeshipPosition(
                Evaluation.EvaluationType.PROFESSOR_EVALUATION, samplePosition))
                .thenReturn(Optional.of(sampleEvaluation));

        Evaluation newEval = new Evaluation();
        newEval.setTraineeshipPosition(samplePosition);
        newEval.setMotivation(8);
        newEval.setEfficiency(7);
        newEval.setEffectiveness(6);
        newEval.setCompanyFacilities(9);
        newEval.setCompanyGuidance(10);

        professorService.updateEvaluation(newEval);

        verify(evaluationMapper).save(sampleEvaluation);

        assertEquals(8, sampleEvaluation.getMotivation());
        assertEquals(7, sampleEvaluation.getEfficiency());
        assertEquals(6, sampleEvaluation.getEffectiveness());
        assertEquals(9, sampleEvaluation.getCompanyFacilities());
        assertEquals(10, sampleEvaluation.getCompanyGuidance());
    }

    @Test
    void testUpdateEvaluation_NoTraineeshipPosition_Throws() {
        Evaluation newEval = new Evaluation();
        newEval.setTraineeshipPosition(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> professorService.updateEvaluation(newEval));

        assertEquals("Traineeship position must be specified", ex.getMessage());
    }

    @Test
    void testUpdateEvaluation_NoExistingEvaluation_Throws() {
        when(evaluationMapper.findByEvaluationTypeAndTraineeshipPosition(
                Evaluation.EvaluationType.PROFESSOR_EVALUATION, samplePosition))
                .thenReturn(Optional.empty());

        Evaluation newEval = new Evaluation();
        newEval.setTraineeshipPosition(samplePosition);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> professorService.updateEvaluation(newEval));

        assertEquals("No existing evaluation found to update", ex.getMessage());
        verify(evaluationMapper, never()).save(any());
    }

    @Test
    void testFindByEvaluationTypeAndPosition() {
        when(evaluationMapper.findByEvaluationTypeAndTraineeshipPosition(
                Evaluation.EvaluationType.PROFESSOR_EVALUATION, samplePosition))
                .thenReturn(Optional.of(sampleEvaluation));

        Optional<Evaluation> result = professorService.findByEvaluationTypeAndPosition(
                Evaluation.EvaluationType.PROFESSOR_EVALUATION, samplePosition);

        assertTrue(result.isPresent());
        assertEquals(sampleProfessor.getUsername(), result.get().getProfessor().getUsername());
    }

    @Test
    void testFindProfessorEvaluationByPosition() {
        when(evaluationMapper.findByEvaluationTypeAndTraineeshipPosition(
                Evaluation.EvaluationType.PROFESSOR_EVALUATION, samplePosition))
                .thenReturn(Optional.of(sampleEvaluation));

        Optional<Evaluation> result = professorService.findProfessorEvaluationByPosition(samplePosition);

        assertTrue(result.isPresent());
    }

    @Test
    void testFindByUsername() {
        when(professorMapper.findByUsername("prof123")).thenReturn(sampleProfessor);

        Professor result = professorService.findByUsername("prof123");

        assertNotNull(result);
        assertEquals("Dr. Jane Doe", result.getProfessorName());
    }
}
