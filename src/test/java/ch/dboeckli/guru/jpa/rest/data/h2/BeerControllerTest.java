package ch.dboeckli.guru.jpa.rest.data.h2;

import ch.dboeckli.guru.jpa.rest.domain.Beer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class BeerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllBeers() throws Exception {
        // Perform GET request and validate response
        MvcResult mvcResult = mockMvc.perform(get("/api/v9/beer")
                .param("sort", "beerName,asc")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.page.size", is(20)))
            .andExpect(jsonPath("$.page.totalElements", is(30)))
            .andExpect(jsonPath("$.page.totalPages", is(2)))
            .andExpect(jsonPath("$.page.number", is(0)))
            .andExpect(jsonPath("$._embedded.beer", hasSize(20)))
            .andExpect(jsonPath("$._embedded.beer[0].beerName", is("Adjunct Trail")))
            .andExpect(jsonPath("$._embedded.beer[0].beerStyle", is("STOUT")))
            .andExpect(jsonPath("$._embedded.beer[0].upc", is("8380495518610"))).andReturn();

        // Extract the JSON content from the response
        String jsonContent = mvcResult.getResponse().getContentAsString();

        // Parse the JSON content
        JsonNode rootNode = objectMapper.readTree(jsonContent);
        JsonNode beersNode = rootNode.path("_embedded").path("beer");

        // Convert the beers node to a List of Beer objects
        List<Beer> beerList = objectMapper.readValue(
            beersNode.traverse(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, Beer.class)
        );

        log.info("Beer list: {}", beerList);
        assertEquals(20, beerList.size());

        // Get the first beer
        JsonNode firstBeer = beersNode.get(0);
        String firstBeerSelfLink = firstBeer.path("_links").path("self").path("href").asText();

        log.info("First beer self link: {}", firstBeerSelfLink);
        // Perform GET request to fetch the first beer's details
        mockMvc.perform(get(firstBeerSelfLink)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.beerName", is("Adjunct Trail")))
            .andExpect(jsonPath("$.beerStyle", is("STOUT")))
            .andExpect(jsonPath("$.upc", is("8380495518610")))
            .andExpect(jsonPath("$.quantityOnHand").isNumber())
            .andExpect(jsonPath("$.price").isNumber())
            .andExpect(jsonPath("$._links.self.href", is(firstBeerSelfLink)));

        log.info("Successfully fetched and validated first beer details");
    }
}
