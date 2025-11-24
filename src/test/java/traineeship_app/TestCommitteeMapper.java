package traineeship_app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import traineeship_app.domainmodel.Committee;
import traineeship_app.mappers.CommitteeMapper;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TestCommitteeMapper {

    @Autowired
    private CommitteeMapper committeeMapper;

    private Committee testCommittee;

    @BeforeEach
    void setUp() {
        testCommittee = new Committee();
        testCommittee.setUsername("committeeUser");
        testCommittee.setPassword("secret");
        testCommittee.setCommitteeMemberName("John Doe");
        committeeMapper.save(testCommittee);
    }


    @Test
    @DisplayName("Should find committee by username")
    void testFindByUsername() {
        Committee result = committeeMapper.findByUsername("committeeUser");

        assertNotNull(result);
        assertEquals("John Doe", result.getCommitteeMemberName());
        assertEquals("committeeUser", result.getUsername());
    }

    @Test
    @DisplayName("Should return null for non-existing username")
    void testFindByUsername_NotFound() {
        Committee result = committeeMapper.findByUsername("nonExistentUser");

        assertNull(result);
    }
}

