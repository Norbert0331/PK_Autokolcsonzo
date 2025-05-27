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
    public String getAllBerlesek(
            @RequestParam(name = "berloNev", required = false) String berloNev,
            @RequestParam(name = "kezdoDatum", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate kezdoDatum,
            @RequestParam(name = "vegDatum", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vegDatum,
            Model model) {

        var berlesek = berlesService.getAllBerlesek();

        if (berloNev != null && !berloNev.isEmpty()) {
            berlesek = berlesService.findByBerloNevContaining(berloNev);
        }

        if (kezdoDatum != null && vegDatum != null) {
            if (berloNev != null && !berloNev.isEmpty()) {
                berlesek = berlesek.stream()
                        .filter(berles -> !berles.getKezdoDatum().isBefore(kezdoDatum) && 
                                          !berles.getKezdoDatum().isAfter(vegDatum)).toList();
            } else {
                berlesek = berlesService.findByKezdoDatumBetween(kezdoDatum, vegDatum);
            }
        }
        
        model.addAttribute("berlesek", berlesek);
        model.addAttribute("berloNev", berloNev);
        model.addAttribute("kezdoDatum", kezdoDatum);
        model.addAttribute("vegDatum", vegDatum);
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
    public String createBerles(@ModelAttribute Berles berles, Model model, RedirectAttributes redirectAttributes) {
        Optional<Auto> auto = autoService.getAutoById(berles.getAuto().getId());
        if (auto.isPresent()) {
            if (berlesService.isAutoAvailable(auto.get(), berles.getKezdoDatum(), berles.getVegDatum())) {
                berles.setAuto(auto.get());
                berlesService.saveBerles(berles);
                redirectAttributes.addFlashAttribute("message", "A bérlés sikeresen létrehozva!");
                redirectAttributes.addFlashAttribute("messageType", "success");
                return "redirect:/berlesek";
            } else {
                model.addAttribute("errorMessage", "Az autó már foglalt a megadott időszakban!");
                model.addAttribute("berles", berles);
                model.addAttribute("autok", autoService.getAllAutok());
                return "berles/form";
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Az autó nem található!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/berlesek";
        }
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
    public String updateBerles(@PathVariable Long id, @ModelAttribute Berles berles, Model model, RedirectAttributes redirectAttributes) {
        Optional<Auto> auto = autoService.getAutoById(berles.getAuto().getId());
        if (auto.isPresent()) {
            if (berlesService.isAutoAvailable(auto.get(), berles.getKezdoDatum(), berles.getVegDatum(), id)) {
                berles.setId(id);
                berles.setAuto(auto.get());
                berlesService.saveBerles(berles);
                redirectAttributes.addFlashAttribute("message", "A bérlés sikeresen frissítve!");
                redirectAttributes.addFlashAttribute("messageType", "success");
                return "redirect:/berlesek";
            } else {
                model.addAttribute("errorMessage", "Az autó már foglalt a megadott időszakban!");
                model.addAttribute("berles", berles);
                model.addAttribute("autok", autoService.getAllAutok());
                return "berles/form";
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Az autó nem található!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/berlesek";
        }
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
