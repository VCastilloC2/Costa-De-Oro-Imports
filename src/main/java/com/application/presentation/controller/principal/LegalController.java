package com.application.presentation.controller.principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/legal")
public class LegalController {

    @GetMapping("/terminos-condiciones")
    public String terminosCondiciones() {
        return "legal/TerminosCondiciones";
    }

    @GetMapping("/politica-privacidad")
    public String politicaPrivacidad() {
        return "legal/PoliticaPrivacidad";
    }

    @GetMapping("/cookies")
    public String cookies() {
        return "legal/Cookies";
    }

    @GetMapping("/autorizacion-datos")
    public String autorizacionDatos() {
        return "legal/AutorizacionDatos";
    }

    @GetMapping("/proteccion-consumidor")
    public String proteccionConsumidor() {
        return "legal/ProteccionConsumidor";
    }

    @GetMapping("/retracto")
    public String retracto() {
        return "legal/DerechoRetracto";
    }

    @GetMapping("/reversion-pago")
    public String reversionPago() {
        return "legal/ReversionPago";
    }

    @GetMapping("/pqrs")
    public String pqrs() {
        return "legal/PQRS";
    }

    @GetMapping("/mayoria-edad")
    public String mayoriaEdad() {
        return "legal/MayoriaDeEdad";
    }

    @GetMapping("/ecommerce-cervezas")
    public String ecommerceCervezas() {
        return "legal/EcommerceCervezas";
    }

    @GetMapping("/ecommerce-licores")
    public String ecommerceLicores() {
        return "legal/EcommerceLicores";
    }

}