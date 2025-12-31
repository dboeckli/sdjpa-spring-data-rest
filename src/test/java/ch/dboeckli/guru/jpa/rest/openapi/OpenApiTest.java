package ch.dboeckli.guru.jpa.rest.openapi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
class OpenApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    BuildProperties buildProperties;

    @Test
    void openapiGetJsonTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        log.info("Response:\n{}", objectMapper.writeValueAsString(jsonNode));

        JsonNode infoNode = jsonNode.get("info");

        assertAll("OpenAPI Info validations",
            () -> assertThat(jsonNode.has("info")).isTrue(),
            () -> assertThat(infoNode.has("title")).isTrue(),
            () -> assertThat(infoNode.get("title").asString()).isEqualTo(buildProperties.getName()),
            () -> assertThat(infoNode.get("version").asString()).isEqualTo(buildProperties.getVersion()),
            () -> assertThat(infoNode.has("description")).isTrue(),
            () -> assertThat(infoNode.get("description").asString()).isEqualTo("Some long and useful description"),
            () -> assertThat(infoNode.has("license")).isTrue(),
            () -> assertThat(infoNode.get("license").get("name").asString()).isEqualTo("Apache 2.0"),
            () -> assertThat(infoNode.get("license").get("url").asString()).isEqualTo("https://www.apache.org/licenses/LICENSE-2.0")
        );
    }

}
