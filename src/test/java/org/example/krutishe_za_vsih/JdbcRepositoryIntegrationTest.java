
package org.example.krutishe_za_vsih;

import org.example.krutishe_za_vsih.repository.JdbcAnimatorRepository;
import org.example.krutishe_za_vsih.repository.JdbcClientAnimatorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JdbcRepositoryIntegrationTest {

    @Autowired
    private JdbcAnimatorRepository jdbcTemplateDao;

    @Autowired
    private JdbcClientAnimatorRepository jdbcClientDao;

    @Test
    void jdbcTemplateCrudTest() {
        long id = jdbcTemplateDao.create(
                "JUnit Template",
                "Тестувальник",
                1,
                "Автоматичний тест"
        );

        assertEquals(
                "JUnit Template",
                jdbcTemplateDao.findById(id).orElseThrow().getName()
        );

        assertEquals(1, jdbcTemplateDao.update(
                id, "JUnit Template", "Старший тестувальник",
                2, "Оновлено"
        ));

        assertEquals(
                "Старший тестувальник",
                jdbcTemplateDao.findById(id).orElseThrow().getRole()
        );

        assertEquals(1, jdbcTemplateDao.deleteById(id));
        assertTrue(jdbcTemplateDao.findById(id).isEmpty());
    }

    @Test
    void jdbcClientCrudTest() {
        long id = jdbcClientDao.create(
                "JUnit Client",
                "Тестувальник",
                1,
                "Автоматичний тест"
        );

        assertEquals(
                "JUnit Client",
                jdbcClientDao.findById(id).orElseThrow().getName()
        );

        assertEquals(1, jdbcClientDao.update(
                id, "JUnit Client", "Старший тестувальник",
                3, "Оновлено через JdbcClient"
        ));

        assertEquals(
                "Старший тестувальник",
                jdbcClientDao.findById(id).orElseThrow().getRole()
        );

        assertEquals(1, jdbcClientDao.deleteById(id));
        assertTrue(jdbcClientDao.findById(id).isEmpty());
    }
}
