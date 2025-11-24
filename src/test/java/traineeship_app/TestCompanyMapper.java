package traineeship_app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import traineeship_app.domainmodel.Company;
import traineeship_app.mappers.CompanyMapper;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TestCompanyMapper {

    @Autowired
    private CompanyMapper companyMapper;

    @Test
    void testFindByUsername() {
        Company company = new Company();
        company.setUsername("testcompany");
        company.setPassword("somepassword");
        company.setCompanyName("Test Company");

        companyMapper.save(company);

        Company found = companyMapper.findByUsername("testcompany");

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("testcompany");
    }

}

