package traineeship_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import traineeship_app.domainmodel.Company;
import traineeship_app.domainmodel.Evaluation;
import traineeship_app.domainmodel.TraineeshipPosition;
import traineeship_app.mappers.CompanyMapper;
import traineeship_app.mappers.EvaluationMapper;
import traineeship_app.mappers.TraineeshipPositionsMapper;
import traineeship_app.mappers.UserMapper;
import traineeship_app.services.CompanyServiceImpl;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.InjectMocks;



@ExtendWith(MockitoExtension.class)
class TestCompanyService {

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private EvaluationMapper evaluationMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TraineeshipPositionsMapper traineeshipPositionMapper;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @BeforeEach
    void setUp() {
        companyMapper = mock(CompanyMapper.class);
        traineeshipPositionMapper = mock(TraineeshipPositionsMapper.class);
        evaluationMapper = mock(EvaluationMapper.class);
        userMapper = mock(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);

        companyService = new CompanyServiceImpl(
                companyMapper,
                evaluationMapper,
                userMapper,
                passwordEncoder,
                traineeshipPositionMapper
        );
    }


    @Test
    void testRetrieveProfile() {
        Company company = new Company();
        company.setUsername("testCompany");
        when(companyMapper.findByUsername("testCompany")).thenReturn(company);

        Company result = companyService.retrieveProfile("testCompany");

        assertNotNull(result);
        assertEquals("testCompany", result.getUsername());
    }

    @Test
    void testSaveProfile() {
        Company company = new Company();
        company.setUsername("testUser");
        company.setPassword("pass");
        company.setCompanyName("TechCorp");
        company.setCompanyLocation("Location");

        when(userMapper.existsByUsername(company.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(company.getPassword())).thenReturn("encodedPass");

        companyService.SaveProfile(company);

        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(companyMapper).save(captor.capture());

        Company savedCompany = captor.getValue();

        assertEquals(company.getUsername(), savedCompany.getUsername());
        assertEquals("encodedPass", savedCompany.getPassword());
        assertEquals(company.getRole(), savedCompany.getRole());
        assertEquals(company.getCompanyName(), savedCompany.getCompanyName());
        assertEquals(company.getCompanyLocation(), savedCompany.getCompanyLocation());
    }

    @Test
    void testUpdateProfile() {
        Company company = new Company();
        company.setUsername("testCompany");   // <-- Add this line
        company.setCompanyName("Update Inc.");

        // Mock existing company returned by companyMapper
        Company existingCompany = new Company();
        existingCompany.setUsername("testCompany");
        when(companyMapper.findByUsername("testCompany")).thenReturn(existingCompany);

        companyService.UpdateProfile(company);

        verify(companyMapper).save(existingCompany);
    }

    @Test
    void testRetrieveAvailablePositions() {
        when(traineeshipPositionMapper.findByCompanyUsernameAndIsCompletedFalseAndIsAssignedFalse("testCompany"))

                .thenReturn(Collections.emptyList());

        var result = companyService.retrieveAvailablePositions("testCompany");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testAddPosition() {
        TraineeshipPosition position = new TraineeshipPosition();
        Company company = new Company();
        company.setUsername("testCompany");

        when(companyMapper.findByUsername("testCompany")).thenReturn(company);

        companyService.addPosition("testCompany", position);

        ArgumentCaptor<TraineeshipPosition> captor = ArgumentCaptor.forClass(TraineeshipPosition.class);
        verify(traineeshipPositionMapper).save(captor.capture());

        TraineeshipPosition savedPosition = captor.getValue();

        assertEquals(company, savedPosition.getCompany());
    }

    @Test
    void testRetrieveAssignedPositions() {
        when(traineeshipPositionMapper.findAssignedByCompanyUsername("testCompany"))
                .thenReturn(Collections.emptyList());

        var result = companyService.retrieveAssignedPositions("testCompany");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeletePosition() {
        when(traineeshipPositionMapper.existsById(1)).thenReturn(true);  // <-- mock existence

        companyService.deletePosition(1);

        verify(traineeshipPositionMapper).deleteById(1);
    }


    @Test
    void testSaveEvaluation() {
        Evaluation evaluation = new Evaluation();

        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(1);

        evaluation.setTraineeshipPosition(position);

        when(traineeshipPositionMapper.findById(1))
                .thenReturn(Optional.of(position));

        companyService.saveEvaluation(evaluation);

        verify(evaluationMapper).save(evaluation);
    }

    @Test
    void testUpdateEvaluation() {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(1);

        Evaluation evaluation = new Evaluation();
        evaluation.setTraineeshipPosition(position);
        evaluation.setEvaluationType(Evaluation.EvaluationType.COMPANY_EVALUATION);

        Evaluation existingEvaluation = new Evaluation();
        existingEvaluation.setTraineeshipPosition(position);
        existingEvaluation.setEvaluationType(Evaluation.EvaluationType.COMPANY_EVALUATION);

        when(evaluationMapper.findByEvaluationTypeAndTraineeshipPosition(
                evaluation.getEvaluationType(),
                evaluation.getTraineeshipPosition()))
                .thenReturn(Optional.of(existingEvaluation));


        evaluation.setMotivation(5);
        evaluation.setEfficiency(4);
        evaluation.setEffectiveness(3);

        companyService.updateEvaluation(evaluation);

        verify(evaluationMapper).save(existingEvaluation);

        assertEquals(5, existingEvaluation.getMotivation());
        assertEquals(4, existingEvaluation.getEfficiency());
        assertEquals(3, existingEvaluation.getEffectiveness());
    }


    @Test
    void testFindCompanyEvaluationByPosition() {
        TraineeshipPosition position = new TraineeshipPosition();
        when(evaluationMapper.findByEvaluationTypeAndTraineeshipPosition(Evaluation.EvaluationType.COMPANY_EVALUATION, position))
                .thenReturn(Optional.of(new Evaluation()));

        Optional<Evaluation> result = companyService.findCompanyEvaluationByPosition(position);
        assertTrue(result.isPresent());
    }

}
