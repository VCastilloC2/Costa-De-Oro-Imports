package com.application.presentation.controller.principal;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.historia.response.HistoriaResponse;
import com.application.presentation.dto.producto.request.FiltroRequest;
import com.application.presentation.dto.producto.response.ProductoResponse;
import com.application.service.interfaces.historia.HistoriaService;
import com.application.service.interfaces.producto.ProductoService;
import com.application.service.interfaces.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class PrincipalController {

    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final HistoriaService historiaService;

    @GetMapping({""})
    public String index(Model model) {

        List<ProductoResponse> productosMasVendidos = productoService.getProductosMasVendidosActivos();

        List<ProductoResponse> productosConCategoriaVino = productoService.getProductosMasVendidosByCategoriaIdActivos(1L);
        List<ProductoResponse> productosConCategoriaWhisky = productoService.getProductosMasVendidosByCategoriaIdActivos(2L);
        List<ProductoResponse> productosConCategoriaCerveza = productoService.getProductosMasVendidosByCategoriaIdActivos(8L);

        List<ProductoResponse> productosConCategoriaVodkaGinebra = productoService.getProductosMasVendidosByCategoriaIdsActivos(List.of(4L, 6L));
        List<ProductoResponse> productosConCategoriaTequilaMezcal = productoService.getProductosMasVendidosByCategoriaIdsActivos(List.of(5L, 7L));
        List<ProductoResponse> productosConCategoriaRonAguardiente = productoService.getProductosMasVendidosByCategoriaIdsActivos(List.of(3L, 9L));

        model.addAttribute("productosMasVendidos", productosMasVendidos);
        model.addAttribute("productosConCategoriaVino", productosConCategoriaVino);
        model.addAttribute("productosConCategoriaWhisky", productosConCategoriaWhisky);
        model.addAttribute("productosConCategoriaCerveza", productosConCategoriaCerveza);
        model.addAttribute("productosConCategoriaVodkaGinebra", productosConCategoriaVodkaGinebra);
        model.addAttribute("productosConCategoriaTequilaMezcal", productosConCategoriaTequilaMezcal);
        model.addAttribute("productosConCategoriaRonAguardiente", productosConCategoriaRonAguardiente);
        return "Index";
    }

    @GetMapping("/paks")
    public String pack(Model model) {

        List<ProductoResponse> packsActivos = productoService.getPacksActivos();

        model.addAttribute("packsActivos", packsActivos);

        return "Pack";
    }

    @GetMapping("/productos")
    public String producto(Model model) {

        List<ProductoResponse> productosActivos = productoService.getProductosActivos();

        // Países
        List<String> paises = productoService.getPaisesProducto();

        // Marcas
        List<String> marcas = productoService.getMarcasProductos();

        // Categorías y subcategorías
        Map<String, List<String>> categorias = productoService.getCategoriasActivasConSubcategorias();

        model.addAttribute("productosActivos", productosActivos);
        model.addAttribute("paises", paises);
        model.addAttribute("marcas", marcas);
        model.addAttribute("categorias", categorias);
        return "Productos";
    }

    @GetMapping("/api/productos/mas-vendidos")
    @ResponseBody
    public List<ProductoResponse> getProductosMasVendidos() {
        return productoService.getProductosMasVendidos();
    }

    @GetMapping("/api/productos/precio-asc")
    @ResponseBody
    public List<ProductoResponse> getProductosPrecioAsc() {
        return productoService.getProductosPorPrecioAsc();
    }

    @GetMapping("/api/productos/precio-desc")
    @ResponseBody
    public List<ProductoResponse> getProductosPrecioDesc() {
        return productoService.getProductosPorPrecioDesc();
    }

    @GetMapping("/api/productos/nombre-asc")
    @ResponseBody
    public List<ProductoResponse> getProductosNombreAsc() {
        return productoService.getProductosPorNombreAsc();
    }

    @GetMapping("/api/productos/nombre-desc")
    @ResponseBody
    public List<ProductoResponse> getProductosNombreDesc() {
        return productoService.getProductosPorNombreDesc();
    }

    @PostMapping("/api/productos/filtrar")
    @ResponseBody
    public List<ProductoResponse> filtrarProductos(@RequestBody FiltroRequest filtros) {
        // Convertir listas vacías a null para el repository
        FiltroRequest filtrosProcesados = new FiltroRequest(
                filtros.paises().isEmpty() ? null : filtros.paises(),
                filtros.marcas().isEmpty() ? null : filtros.marcas(),
                filtros.categorias().isEmpty() ? null : filtros.categorias(),
                filtros.subcategorias().isEmpty() ? null : filtros.subcategorias(),
                filtros.precioMin(),
                filtros.precioMax(),
                filtros.ordenarPor()
        );

        return productoService.filtrarProductos(filtrosProcesados);
    }

    @GetMapping("/blog")
    public String blog(Model model) {

        List<HistoriaResponse> historiaList = historiaService.getHistoriasActivas();

        model.addAttribute("historiaList", historiaList);
        return "Blog";
    }

    @GetMapping("/blog/info")
    public String blogInformation() {
        return "BlogInformation";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "Contactos";
    }

    @GetMapping("/carrito")
    public String carrito() {
        return "Carrito";
    }

    @GetMapping("/favorito")
    public String favorito() {
        return "Favorito";
    }

    @GetMapping("/ver")
    public String Ver(Model model) {
        return "Ver";
    }

    @GetMapping("/descripcion-producto/{productoId}")
    public String getDescripcionProducto(@AuthenticationPrincipal CustomUserPrincipal principal,
                                         @PathVariable Long productoId,
                                         Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getCorreo());
        ProductoResponse productoResponse = productoService.getProductoResponseById(productoId);

        List<ProductoResponse> productosActivos = productoService.getProductosActivos();
        Collections.shuffle(productosActivos);
        List<ProductoResponse> productosAleatorios = productosActivos.stream().limit(4).toList();

        model.addAttribute("usuario", usuario);
        model.addAttribute("producto", productoResponse);
        model.addAttribute("productoList", productosAleatorios);
        return "Aqui la vista de desccripcion del producto";
    }

}