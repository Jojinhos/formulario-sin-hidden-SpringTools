package com.example.demo;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PaginaController {


    @GetMapping("/")
    public String index(HttpSession session,Model model) {
        PersonasFormulario personasFormulario = new PersonasFormulario();
        session.setAttribute("personasFormulario", personasFormulario);
        model.addAttribute("personasFormulario", personasFormulario);
    	return "formulario"; 
    }

    @PostMapping("/paso2")
    public String procesarPaso1(@Valid @ModelAttribute("personasFormulario") PersonasFormulario personasFormulario,
                                BindingResult result,
                                HttpSession session,
                                Model model) {
        if (personasFormulario.getFechaNacimiento() != null && personasFormulario.getEdad() != null) {
            int edadCalculada = Period.between(personasFormulario.getFechaNacimiento(), LocalDate.now()).getYears();
            if (edadCalculada != personasFormulario.getEdad()) {
                result.rejectValue("edad", "error.edad", "La edad no coincide con la fecha de nacimiento.");
            }
        }

        if (result.hasFieldErrors("nombre") || result.hasFieldErrors("edad") || result.hasFieldErrors("fechaNacimiento")) {
            return "formulario";
        }
        PersonasFormulario sesionPersona = (PersonasFormulario) session.getAttribute("personasFormulario");
        if(sesionPersona == null) {
        	sesionPersona = new PersonasFormulario();
        }
        sesionPersona.setNombre(personasFormulario.getNombre());
        sesionPersona.setEdad(personasFormulario.getEdad());
        sesionPersona.setFechaNacimiento(personasFormulario.getFechaNacimiento());
        session.setAttribute("personasFormulario", sesionPersona);
        model.addAttribute("personasFormulario", sesionPersona);
        
        return "paso2";
    }
    @GetMapping("/paso2")
    public String verPaso2(HttpSession session, Model model) {
    	PersonasFormulario personasFormulario = (PersonasFormulario) session.getAttribute("personasFormulario");
    	if (personasFormulario == null) {
    		return "redirect:/";
    	}
    	model.addAttribute("personasFormulario", personasFormulario);
    	return "paso2";
    }


    @PostMapping("/paso3")
    public String procesarPaso3(@Valid @ModelAttribute("personasFormulario") PersonasFormulario personasFormulario,
                                BindingResult result,
                                HttpSession session,
                                Model model) {
    	
    	PersonasFormulario sesionPersona = (PersonasFormulario) session.getAttribute("personasFormulario");
    	
    	if (result.hasFieldErrors("consola")) {
    		PersonasFormulario sesionTemp = (PersonasFormulario) session.getAttribute("personasFormulario");
    		if (sesionTemp != null) {
    			personasFormulario.setNombre(sesionTemp.getNombre());
    			personasFormulario.setEdad(sesionTemp.getEdad());
    			personasFormulario.setFechaNacimiento(sesionTemp.getFechaNacimiento());
    		}
    		model.addAttribute("personasFormulario", personasFormulario);
    		return "paso2";
    	}
    	
    	
    	if (sesionPersona != null) {
    		sesionPersona.setConsola(personasFormulario.getConsola());
    		session.setAttribute("personasFormulario", sesionPersona);
    		
    	}
    	return "redirect:/paso3";
    }
    
    @GetMapping("/paso3")
    public String verPaso3(HttpSession session, Model model) {
    	PersonasFormulario personasFormulario = (PersonasFormulario) session.getAttribute("personasFormulario");
    	if (personasFormulario == null) {
    		return "redirect:/";
    	}
    	model.addAttribute("personasFormulario", personasFormulario);
    	return "paso3";
    }
    
    @PostMapping("/resultado")
    public String procesarResultado(@Valid @ModelAttribute("personasFormulario") PersonasFormulario personasFormulario,
    							BindingResult result,
    							HttpSession session,
    							Model model) {
    	PersonasFormulario sessionPersona = (PersonasFormulario) session.getAttribute("personasFormulario");
    	if (result.hasFieldErrors("juegos")) {
    		if (sessionPersona != null) {
    			personasFormulario.setNombre(sessionPersona.getNombre());
    			personasFormulario.setEdad(sessionPersona.getEdad());
    			personasFormulario.setFechaNacimiento(sessionPersona.getFechaNacimiento());
    			personasFormulario.setConsola(sessionPersona.getConsola());
    		}
    		return "paso3";
    	}
    	
    	if (sessionPersona != null) {
    		sessionPersona.setJuegos(personasFormulario.getJuegos());
    		
    		List<String> listaResultados = new ArrayList<>();
    		listaResultados.add("Paso 1 - Nombre: " + sessionPersona.getNombre()
    					+ "Edad: " + sessionPersona.getEdad()
    					+ "Fecha de Nacimiento: " + sessionPersona.getFechaNacimiento());
    		listaResultados.add("consola: " + sessionPersona.getConsola());
    		listaResultados.add("Juegos: " + String.join(", ", sessionPersona.getJuegos()));
    		
    		model.addAttribute("listaResultados", listaResultados);
    	}
    	return "resultado";
    }
}