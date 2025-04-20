package ch.dboeckli.guru.jpa.rest.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(indexes = {
    @Index(name = "idx_customer_name", columnList = "name")
})
public class Customer {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "char(36)", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    private String name;
}
