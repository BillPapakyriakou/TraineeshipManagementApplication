package traineeship_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import traineeship_app.domainmodel.Professor;
import traineeship_app.mappers.ProfessorMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // use your real DB or set accordingly
class TestProfessorMapper {

    @Autowired
    private ProfessorMapper professorMapper;

    private Professor professor;

    @BeforeEach
    void setup() {
        professor = new Professor();
        professor.setUsername("prof123");
        professor.setPassword("somePassword"); // set password if User superclass requires it
        professor.setProfessorName("Dr. Jane Doe");
        professor.setInterests("AI, ML");
        professor.setSupervisedPositions(Collections.emptyList());

        professor = professorMapper.save(professor);
    }

    @Test
    void testFindByUsername() {
        Professor found = professorMapper.findByUsername("prof123");
        assertNotNull(found);
        assertEquals("Dr. Jane Doe", found.getProfessorName());
    }

    @Test
    void testFindById() {
        Optional<Professor> optionalProfessor = professorMapper.findById(professor.getId());
        assertTrue(optionalProfessor.isPresent());
        assertEquals("prof123", optionalProfessor.get().getUsername());
    }

    @Test
    void testCountSupervisedPositions() {
        int count = professorMapper.countSupervisedPositions(professor.getId());
        assertEquals(0, count);  // No positions assigned yet
    }

    @Test
    void testFindLeastBusyProfessor() {
        String username = professorMapper.findLeastBusyProfessor();
        assertEquals("giannis", username);
    }

    @Test
    void testFindAllProfessorsSortedByLoad() {
        List<Object[]> result = professorMapper.findAllProfessorsSortedByLoad();
        assertFalse(result.isEmpty());

        Object[] entry = result.get(0);
        Professor p = (Professor) entry[0];
        Long count = (Long) entry[1];

        assertEquals("giannis", p.getUsername());
        assertEquals(0L, count);
    }
}
