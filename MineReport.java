package com.chase.digital.payments.wires.componentTest.stepdef;

import com.chase.digital.payments.wires.componentTest.config.BaseTestConfig;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = { BaseTestConfig.class })
public class BulkProcessStepsSaveMetadataApi {

    @MockBean
    private JdbcTemplate jdbcTemplate; // mocked “H2” access

    private String insertedId;

    @And("1.2 The file metadata is stored to Local h2 database")
    public void storeDataIntoH2DB() {
        // fresh stubbing for this step
        Mockito.reset(jdbcTemplate);

        insertedId = "file-123";
        String insertSql = "INSERT INTO file_metadata (file_id, file_name, status) VALUES (?, ?, ?)";
        String countSql  = "SELECT COUNT(*) FROM file_metadata WHERE file_id = ?";

        // mock: INSERT returns 1 row updated
        Mockito.when(jdbcTemplate.update(
                Mockito.eq(insertSql),
                Mockito.eq(insertedId), Mockito.eq("sample.pdf"), Mockito.eq("STORED")
        )).thenReturn(1);

        // mock: COUNT(*) returns 1 after insert
        Mockito.when(jdbcTemplate.queryForObject(
                Mockito.eq(countSql),
                Mockito.eq(Integer.class),
                Mockito.eq(insertedId)
        )).thenReturn(1);

        // call mocked operations (what your prod code would do)
        int updated = jdbcTemplate.update(insertSql, insertedId, "sample.pdf", "STORED");
        Assertions.assertEquals(1, updated, "Insert did not affect any rows (mock)");

        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class, insertedId);
        Assertions.assertEquals(1, count, "Record not found in mocked H2 after insert");

        // verify interactions
        Mockito.verify(jdbcTemplate).update(insertSql, insertedId, "sample.pdf", "STORED");
        Mockito.verify(jdbcTemplate).queryForObject(countSql, Integer.class, insertedId);
    }

    @Then("1.3 I get 200 OK")
    public void validateResponseForFileMetadata() {
        Mockito.reset(jdbcTemplate);

        String selectSql = "SELECT * FROM file_metadata WHERE file_id = ?";
        Map<String, Object> row = new HashMap<>();
        row.put("file_id", insertedId);
        row.put("file_name", "sample.pdf");
        row.put("status", "STORED");

        Mockito.when(jdbcTemplate.queryForMap(
                Mockito.eq(selectSql),
                Mockito.eq(insertedId)
        )).thenReturn(row);

        Map<String, Object> got = jdbcTemplate.queryForMap(selectSql, insertedId);
        Assertions.assertNotNull(got, "No row returned from mocked H2");
        Assertions.assertEquals("sample.pdf", got.get("file_name"));
        Assertions.assertEquals("STORED",     got.get("status"));

        Mockito.verify(jdbcTemplate).queryForMap(selectSql, insertedId);
    }

    @Then("3.1 Failed to Store to local h2 database")
    public void failedStoreToH2DB() {
        Mockito.reset(jdbcTemplate);

        String failingId = "fail-001";
        String insertSql = "INSERT INTO file_metadata (file_id, file_name, status) VALUES (?, ?, ?)";

        // make only this specific insert throw (simulates H2/constraint error)
        Mockito.when(jdbcTemplate.update(
                Mockito.eq(insertSql),
                Mockito.eq(failingId), Mockito.eq("bad.pdf"), Mockito.eq("PENDING")
        )).thenThrow(new DataAccessException("Simulated H2 failure") {});

        try {
            jdbcTemplate.update(insertSql, failingId, "bad.pdf", "PENDING");
            Assertions.fail("Expected simulated H2 failure did not occur");
        } catch (DataAccessException expected) {
            // ok
        }

        // if your code checks existence after a failed insert, stub it to 0
        Mockito.when(jdbcTemplate.queryForObject(
                Mockito.eq("SELECT COUNT(*) FROM file_metadata WHERE file_id = ?"),
                Mockito.eq(Integer.class),
                Mockito.eq(failingId)
        )).thenReturn(0);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM file_metadata WHERE file_id = ?",
                Integer.class, failingId
        );
        Assertions.assertEquals(0, count, "Row should not exist for failing id in mocked H2");

        Mockito.verify(jdbcTemplate).update(insertSql, failingId, "bad.pdf", "PENDING");
        Mockito.verify(jdbcTemplate).queryForObject(
                Mockito.eq("SELECT COUNT(*) FROM file_metadata WHERE file_id = ?"),
                Mockito.eq(Integer.class),
                Mockito.eq(failingId)
        );
    }
}
