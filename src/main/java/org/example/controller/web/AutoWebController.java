package org.example.controller.web;

import org.example.model.Auto;
import org.example.service.AutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;


@Controller
@RequestMapping("/autok")
public class AutoWebController {

    private final AutoService autoService;


    public AutoWebController(AutoService autoService) {
        this.autoService = autoService;
    }


    @GetMapping
    public String getAllAutok(
            @RequestParam(name = "marka", required = false) String marka,
            @RequestParam(name = "minEvjarat", required = false) Integer minEvjarat,
            Model model) {

        var autok = autoService.getAllAutok();

        if (marka != null && !marka.isEmpty()) {
            autok = autoService.findByMarka(marka);
        }

        if (minEvjarat != null) {
            if (marka != null && !marka.isEmpty()) {
                autok = autok.stream()
                        .filter(auto -> auto.getEvjarat() >= minEvjarat)
                        .toList();
            } else {
                autok = autoService.findByEvjaratGreaterThanEqual(minEvjarat);
            }
        }
        
        model.addAttribute("autok", autok);
        model.addAttribute("marka", marka);
        model.addAttribute("minEvjarat", minEvjarat);
        return "auto/list";
    }

    @GetMapping("/{id}/reszletek")
    public String getAutoDetails(@PathVariable Long id, Model model) {
        Optional<Auto> auto = autoService.getAutoById(id);
        if (auto.isPresent()) {
            model.addAttribute("auto", auto.get());
            return "auto/details";
        } else {
            return "redirect:/autok";
        }
    }

    @GetMapping("/uj")
    public String createAutoForm(Model model) {
        model.addAttribute("auto", new Auto());
        return "auto/form";
    }

    @PostMapping("/uj")
    public String createAuto(@ModelAttribute Auto auto, RedirectAttributes redirectAttributes) {
        autoService.saveAuto(auto);
        redirectAttributes.addFlashAttribute("message", "Az autó sikeresen létrehozva!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/autok";
    }

    @GetMapping("/{id}/szerkesztes")
    public String editAutoForm(@PathVariable Long id, Model model) {
        Optional<Auto> auto = autoService.getAutoById(id);
        if (auto.isPresent()) {
            model.addAttribute("auto", auto.get());
            return "auto/form";
        } else {
            return "redirect:/autok";
        }
    }

    @PostMapping("/{id}/szerkesztes")
    public String updateAuto(@PathVariable Long id, @ModelAttribute Auto auto, RedirectAttributes redirectAttributes) {
        auto.setId(id);
        autoService.saveAuto(auto);
        redirectAttributes.addFlashAttribute("message", "Az autó sikeresen frissítve!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/autok";
    }

    @GetMapping("/{id}/torles")
    public String deleteAuto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Auto> auto = autoService.getAutoById(id);
        if (auto.isPresent()) {
            autoService.deleteAuto(id);
            redirectAttributes.addFlashAttribute("message", "Az autó sikeresen törölve!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Az autó nem található!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        return "redirect:/autok";
    }
}
