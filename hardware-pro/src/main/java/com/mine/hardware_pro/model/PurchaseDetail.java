package com.mine.hardware_pro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "purchase_detail")
public class PurchaseDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_purchase_detail")
    private Long idPurchaseDetail;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    // ✅ CAMPO AÑADIDO: Es útil tener el total por línea
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    // ✅ RELACIÓN: Este detalle pertenece a UNA Compra
    @ManyToOne(fetch = FetchType.LAZY) // LAZY es más eficiente aquí
    @JoinColumn(name = "id_purchase", nullable = false)
    private Purchase purchase;

    // ✅ RELACIÓN: Este detalle corresponde a UN Artículo
    @ManyToOne
    @JoinColumn(name = "id_article", nullable = false)
    private Article article;
}