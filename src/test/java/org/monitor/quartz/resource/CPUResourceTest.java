package org.monitor.quartz.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;

@QuarkusTest
class CPUResourceTest {
    @Test
    public void tasks() throws InterruptedException {
        Thread.sleep(10000); // wait at least 10 seconds to have the first task created
        given()
                .when().get("/tasks/cpu")
                .then()
                .statusCode(200)
                .body("size()", is(greaterThanOrEqualTo(1)));
    }
}
