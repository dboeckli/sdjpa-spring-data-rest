package ch.dboeckli.guru.jpa.rest.domain;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(indexes = {
    @Index(name = "idx_beer_name", columnList = "beerName")
})
public class Beer {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "char(36)", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Version
    private Long version;

    private String beerName;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private BeerStyleEnum beerStyle;

    private String upc;

    private Integer quantityOnHand;
    private BigDecimal price;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;
}
