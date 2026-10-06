package com.application.persistence.entity.compra;

import com.application.persistence.entity.compra.enums.EEstado;
import com.application.persistence.entity.compra.enums.EMetodoPago;
import com.application.persistence.entity.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_id")
    private Long compraId;
    @Column(name = "metodo_pago")
    @Enumerated(EnumType.STRING)
    private EMetodoPago eMetodoPago;
    private double subtotal;
    private String cuponDescuento;
    private double iva;
    private double total;
    private LocalDateTime fecha;
    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EEstado estado;

    // Cardinalidad con la tabla usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "usuario_id", foreignKey = @ForeignKey(name = "fk_compra_usuario"))
    @JsonIgnore
    private Usuario usuario;

    // Cardinalidad con la tabla detalle_ventas
    @Builder.Default
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private Set<DetalleVenta> detalleVentas = new HashSet<>();

    // Agregar compra a detalle_venta y viceversa (bidireccional)
    public void addDetalleVenta(DetalleVenta detalleVenta) {
        detalleVenta.setCompra(this);
        this.detalleVentas.add(detalleVenta);
    }

    // Eliminar compra a detalle_venta y viceversa (bidireccional)
    public void deleteDetalleVenta(DetalleVenta detalleVenta) {
        detalleVenta.setCompra(null);
        this.detalleVentas.remove(detalleVenta);
    }
}