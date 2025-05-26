package org.example.controller.web;

import org.example.model.Auto;
import org.example.model.Berles;
import org.example.service.AutoService;
import org.example.service.BerlesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/berlesek")
public class BerlesWebController {

    private final BerlesService berlesService;
    private final AutoService autoService;

    public BerlesWebController(BerlesService berlesService, AutoService autoService) {
        this.berlesService = berlesService;
        this.autoService = autoService;
    }

    @GetMapping
    public String getAllBerlesek(Model model) {
        model.addAttribute("berlesek", berlesService.getAllBerlesek());
        return "berles/list";
    }

    @GetMapping("/{id}/reszletek")
    public String getBerlesDetails(@PathVariable Long id, Model model) {
        Optional<Berles> berles = berlesService.getBerlesById(id);
        if (berles.isPresent()) {
            model.addAttribute("berles", berles.get());
            return "berles/details";
        } else {
            return "redirect:/berlesek";
        }
    }

    @GetMapping("/uj")
    public String createBerlesForm(@RequestParam(required = false) Long autoId, Model model) {
        Berles berles = new Berles();
        berles.setKezdoDatum(LocalDate.now());
        berles.setVegDatum(LocalDate.now().plusDays(7));
        
        if (autoId != null) {
            Optional<Auto> auto = autoService.getAutoById(autoId);
            auto.ifPresent(a -> {
                Auto selectedAuto = new Auto();
                selectedAuto.setId(a.getId());
                berles.setAuto(selectedAuto);
            });
        } else {
            berles.setAuto(new Auto());
        }
        
        model.addAttribute("berles", berles);
        model.addAttribute("autok", autoService.getAllAutok());
        model.addAttribute("autoId", autoId);
        return "berles/form";
    }

    @PostMapping("/uj")
    public String createBerles(@ModelAttribute Berles berles, RedirectAttributes redirectAttributes) {
        Optional<Auto> auto = autoService.getAutoById(berles.getAuto().getId());
        if (auto.isPresent()) {
            berles.setAuto(auto.get());
            berlesService.saveBerles(berles);
            redirectAttributes.addFlashAttribute("message", "A bérlés sikeresen létrehozva!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Az autó nem található!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        return "redirect:/berlesek";
    }

    @GetMapping("/{id}/szerkesztes")
    public String editBerlesForm(@PathVariable Long id, Model model) {
        Optional<Berles> berles = berlesService.getBerlesById(id);
        if (berles.isPresent()) {
            model.addAttribute("berles", berles.get());
            model.addAttribute("autok", autoService.getAllAutok());
            return "berles/form";
        } else {
            return "redirect:/berlesek";
        }
    }

    @PostMapping("/{id}/szerkesztes")
    public String updateBerles(@PathVariable Long id, @ModelAttribute Berles berles, RedirectAttributes redirectAttributes) {
        Optional<Auto> auto = autoService.getAutoById(berles.getAuto().getId());
        if (auto.isPresent()) {
            berles.setId(id);
            berles.setAuto(auto.get());
            berlesService.saveBerles(berles);
            redirectAttributes.addFlashAttribute("message", "A bérlés sikeresen frissítve!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Az autó nem található!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        return "redirect:/berlesek";
    }

    @GetMapping("/{id}/torles")
    public String deleteBerles(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Berles> berles = berlesService.getBerlesById(id);
        if (berles.isPresent()) {
            berlesService.deleteBerles(id);
            redirectAttributes.addFlashAttribute("message", "A bérlés sikeresen törölve!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "A bérlés nem található!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        return "redirect:/berlesek";
    }
}
