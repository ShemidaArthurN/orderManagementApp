package com.example.orderManagement.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SelectBeforeUpdate
@Entity
@Table(name = "\"Orders\"")
@DynamicUpdate
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Slf4j
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", foreignKey = @ForeignKey(name = "none", value = javax.persistence.ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private Customer customer;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code", foreignKey = @ForeignKey(name = "none", value = javax.persistence.ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private Product product;

    @Column(name = "quantity", nullable = false)
    @Min(1)
    private int quantity;


    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "total", nullable = true)
    private BigDecimal total;

    @Transient
    private int custId;

    @Transient
    private String prdCode;
}
