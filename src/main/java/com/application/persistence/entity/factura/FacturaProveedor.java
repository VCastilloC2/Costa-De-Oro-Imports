package com.application.persistence.entity.factura;

import com.application.persistence.entity.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "factura_proveedor")
public class FacturaProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "factura_id")
    private Long facturaId;

    @Column(name = "numero_factura")
    private String numeroFactura;
    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    private double total;
    private boolean activo;

    // Cardinalidad con la tabla Usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id",
            referencedColumnName = "usuario_id",
            foreignKey = @ForeignKey(name = "fk_factura_proveedor")
    )
    @JsonIgnore
    private Usuario usuario;

    // Cardinalidad con la tabla detalle_factura
    @Builder.Default
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private Set<DetalleFactura> detalleFacturas = new HashSet<>();

    // Agregar factura a detalle_factura y viceversa (bidireccional)
    public void addDetalleFactura(DetalleFactura detalleFactura) {
        detalleFactura.setFactura(this);
        this.detalleFacturas.add(detalleFactura);
    }

    // Eliminar factura a detalle_factura y viceversa (bidireccional)
    public void deleteDetalleFactura(DetalleFactura detalleFactura) {
        detalleFactura.setFactura(null);
        this.detalleFacturas.remove(detalleFactura);
    }
}
