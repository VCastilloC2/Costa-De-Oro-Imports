package com.application.configuration.ia.tools;

import com.application.persistence.entity.empresa.Empresa;
import com.application.persistence.repository.EmpresaRepository;
import com.application.service.interfaces.empresa.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmpresaTools {

    private final EmpresaRepository empresaRepository;
    private final EmpresaService empresaService;

    private static final int MAX_RESULTADOS = 5;

    @Tool(
            name = "buscar_empresa_por_nit",
            description = """
                    Busca una empresa registrada mediante su NIT.
                    """
    )
    public String buscarEmpresaPorNit(String nit) {
        if (nit == null || nit.isBlank()) {
            return "El NIT es requerido.";
        }

        // Simulación de búsqueda ya que no tenemos un método específico en el repo expuesto,
        // pero el modelo puede usar MCP para esto. Aquí lo hacemos vía Java para consistencia.
        Empresa empresa = empresaRepository.findAll().stream()
                .filter(e -> e.getNit().equalsIgnoreCase(nit))
                .findFirst()
                .orElse(null);

        if (empresa == null) {
            return "No se encontró ninguna empresa con el NIT: " + nit;
        }

        return formatearEmpresa(empresa);
    }

    @Tool(
            name = "listar_empresas_activas",
            description = """
                    Lista todas las empresas proveedoras que están activas.
                    """
    )
    public String listarEmpresasActivas() {
        List<Empresa> empresas = empresaRepository.findAll().stream()
                .filter(Empresa::isActivo)
                .limit(MAX_RESULTADOS)
                .toList();

        if (empresas.isEmpty()) {
            return "No hay empresas activas registradas.";
        }

        return empresas.stream()
                .map(this::formatearEmpresa)
                .collect(Collectors.joining("\n-------------------------\n"));
    }

    private String formatearEmpresa(Empresa empresa) {
        return """
                ID: %d
                Razón Social: %s
                NIT: %s
                Ciudad: %s
                Teléfono: %s
                Correo: %s
                Sector: %s
                Estado: %s
                """.formatted(
                empresa.getEmpresaId(),
                empresa.getRazonSocial(),
                empresa.getNit(),
                empresa.getCiudad(),
                empresa.getTelefono(),
                empresa.getCorreo(),
                empresa.getESector(),
                empresa.isActivo() ? "ACTIVO" : "INACTIVO"
        );
    }
}
