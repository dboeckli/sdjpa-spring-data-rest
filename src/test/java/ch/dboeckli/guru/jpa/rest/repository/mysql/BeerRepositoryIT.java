package ch.dboeckli.guru.jpa.rest.repository.mysql;

import ch.dboeckli.guru.jpa.rest.domain.Beer;
import ch.dboeckli.guru.jpa.rest.domain.BeerStyleEnum;
import ch.dboeckli.guru.jpa.rest.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static ch.dboeckli.guru.jpa.rest.bootstrap.BeerLoader.BEER_1_UPC;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BeerRepositoryIT {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void findAllByBeerName() {
        Page<Beer> beerPage = beerRepository.findAllByBeerName("Mango Bobs", Pageable.unpaged());
        assertEquals(1, beerPage.getTotalElements());
        beerPage.getContent().forEach(beer -> assertEquals("Mango Bobs", beer.getBeerName()));
    }

    @Test
    void findAllByBeerStyle() {
        Page<Beer> beerPage = beerRepository.findAllByBeerStyle(BeerStyleEnum.ALE, Pageable.unpaged());
        assertEquals(3, beerPage.getTotalElements());
        beerPage.getContent().forEach(beer -> assertAll(
                () -> assertEquals(BeerStyleEnum.ALE, beer.getBeerStyle()),
                () -> assertTrue(
                        beer.getBeerName().equals("Mango Bobs") ||
                                beer.getBeerName().equals("Beach Blond Ale") ||
                                beer.getBeerName().equals("Rod Bender Red Ale"))
        ));
    }

    @Test
    void findAllByBeerNameAndBeerStyle() {
        Page<Beer> beerPage = beerRepository.findAllByBeerNameAndBeerStyle("Mango Bobs", BeerStyleEnum.ALE, Pageable.unpaged());

        assertEquals(1, beerPage.getTotalElements());
        beerPage.getContent().forEach(beer -> assertAll(
                () -> assertEquals("Mango Bobs", beer.getBeerName()),
                () -> assertEquals(BeerStyleEnum.ALE, beer.getBeerStyle())
        ));

    }

    @Test
    void findByUpc() {
        Beer beer = beerRepository.findByUpc(BEER_1_UPC);
        assertEquals(BEER_1_UPC, beer.getUpc());
    }
}