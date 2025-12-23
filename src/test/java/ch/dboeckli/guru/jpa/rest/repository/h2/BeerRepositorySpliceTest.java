package ch.dboeckli.guru.jpa.rest.repository.h2;

import ch.dboeckli.guru.jpa.rest.domain.Beer;
import ch.dboeckli.guru.jpa.rest.domain.BeerStyleEnum;
import ch.dboeckli.guru.jpa.rest.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BeerRepositorySpliceTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void findAllByBeerName() {
        beerRepository.save(Beer.builder().beerName("Mango Bobs").build());

        Page<Beer> beerPage = beerRepository.findAllByBeerName("Mango Bobs", Pageable.unpaged());
        assertEquals(1, beerPage.getTotalElements());
        beerPage.getContent().forEach(beer -> assertEquals("Mango Bobs", beer.getBeerName()));
    }

    @Test
    void findAllByBeerStyle() {
        beerRepository.save(Beer.builder()
            .beerName("Mango Bobs")
            .beerStyle(BeerStyleEnum.IPA)
            .build());

        Page<Beer> beerPage = beerRepository.findAllByBeerStyle(BeerStyleEnum.IPA, Pageable.unpaged());
        assertEquals(1, beerPage.getTotalElements());
        beerPage.getContent().forEach(beer -> assertAll(
            () -> assertEquals("Mango Bobs", beer.getBeerName()),
            () -> assertEquals(BeerStyleEnum.IPA, beer.getBeerStyle())
        ));
    }

    @Test
    void findAllByBeerNameAndBeerStyle() {
        beerRepository.save(Beer.builder()
            .beerName("Mango Bobs")
            .beerStyle(BeerStyleEnum.IPA)
            .build());

        Page<Beer> beerPage = beerRepository.findAllByBeerNameAndBeerStyle("Mango Bobs", BeerStyleEnum.IPA, Pageable.unpaged());

        assertEquals(1, beerPage.getTotalElements());
        beerPage.getContent().forEach(beer -> assertAll(
            () -> assertEquals("Mango Bobs", beer.getBeerName()),
            () -> assertEquals(BeerStyleEnum.IPA, beer.getBeerStyle())
        ));

    }

    @Test
    void findByUpc() {
        beerRepository.save(Beer.builder()
            .beerName("Mango Bobs")
            .beerStyle(BeerStyleEnum.IPA)
            .upc("1234567890123")
            .build());

       Beer beer = beerRepository.findByUpc("1234567890123");
       assertEquals("1234567890123", beer.getUpc());
    }
}