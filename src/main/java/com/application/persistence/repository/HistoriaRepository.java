package com.application.persistence.repository;

import com.application.persistence.entity.historia.Historia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoriaRepository extends JpaRepository<Historia, Long> {
    List<Historia> findByActivoTrue();
    List<Historia> findByTituloContainingIgnoreCaseAndActivoTrue(String titulo);
    List<Historia> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
            String titulo,
            String descripcion
    );
}