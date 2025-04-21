package ch.dboeckli.guru.jpa.rest.repository.mysql;

import ch.dboeckli.guru.jpa.rest.domain.Beer;
import ch.dboeckli.guru.jpa.rest.domain.BeerStyleEnum;
import ch.dboeckli.guru.jpa.rest.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test_mysql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // to assure that it is not replaced with h2
class BeerRepositorySpliceIT {

    private static final String BEER_NAME = "Walliser Bier";
    private static final String UPC = "9999999999";

    @Autowired
    BeerRepository beerRepository;

    @Test
    void findAllByBeerName() {
        beerRepository.save(Beer.builder().beerName(BEER_NAME).build());

        Page<Beer> beerPage = beerRepository.findAllByBeerName(BEER_NAME, Pageable.unpaged());
        assertEquals(1, beerPage.getTotalElements());
        beerPage.getContent().forEach(beer -> assertEquals(BEER_NAME, beer.getBeerName()));
    }

    @Test
    void findAllByBeerStyle() {
        beerRepository.save(Beer.builder()
            .beerName(BEER_NAME)
            .beerStyle(BeerStyleEnum.PILSNER)
            .build());

        Page<Beer> beerPage = beerRepository.findAllByBeerStyle(BeerStyleEnum.PILSNER, Pageable.unpaged());
        assertEquals(1, beerPage.getTotalElements());
        beerPage.getContent().forEach(beer -> assertAll(
            () -> assertEquals(BEER_NAME, beer.getBeerName()),
            () -> assertEquals(BeerStyleEnum.PILSNER, beer.getBeerStyle())
        ));
    }

    @Test
    void findAllByBeerNameAndBeerStyle() {
        beerRepository.save(Beer.builder()
            .beerName(BEER_NAME)
            .beerStyle(BeerStyleEnum.PILSNER)
            .build());

        Page<Beer> beerPage = beerRepository.findAllByBeerNameAndBeerStyle(BEER_NAME, BeerStyleEnum.PILSNER, Pageable.unpaged());

        assertEquals(1, beerPage.getTotalElements());
        beerPage.getContent().forEach(beer -> assertAll(
            () -> assertEquals(BEER_NAME, beer.getBeerName()),
            () -> assertEquals(BeerStyleEnum.PILSNER, beer.getBeerStyle())
        ));

    }

    @Test
    void findByUpc() {
        beerRepository.save(Beer.builder()
            .beerName(BEER_NAME)
            .beerStyle(BeerStyleEnum.PILSNER)
            .upc(UPC)
            .build());

       Beer beer = beerRepository.findByUpc(UPC);
       assertEquals(UPC, beer.getUpc());
    }
}