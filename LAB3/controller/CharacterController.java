package com.example.lab3.controller;

import com.example.lab3.entity.*;
import com.example.lab3.service.GameClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/characters")
public class CharacterController {

    @Autowired
    private GameClassService characterService;

    // ========== GET методы ==========

    @GetMapping
    public String listAll(Model model) {
        model.addAttribute("characters", characterService.getAllCharacters());
        return "characters";
    }

    @GetMapping("/summoners")
    public String listSummoners(Model model) {
        model.addAttribute("summoners", characterService.getSummoners());
        return "summoners";
    }

    @GetMapping("/paladins")
    public String listPaladins(Model model) {
        model.addAttribute("paladins", characterService.getPaladins());
        return "paladins";
    }

    // ==================== РЕДАКТИРОВАНИЕ ====================

    @GetMapping("/edit/{type}/{id}")
    public String showEditForm(@PathVariable String type, @PathVariable Long id, Model model) {
        GameClass character = characterService.getCharacterById(id, type);
        if (character == null) {
            return "redirect:/characters";
        }
        model.addAttribute("character", character);
        model.addAttribute("type", type);
        return "edit-character";
    }

    @PostMapping("/edit/{type}/{id}")
    public String editCharacter(@PathVariable String type,
                                @PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam int level,
                                @RequestParam int health,
                                @RequestParam(required = false) Integer mana,
                                @RequestParam(required = false) Integer summonsCount,
                                @RequestParam(required = false) Integer armor,
                                @RequestParam(required = false) Integer holyPower,
                                RedirectAttributes redirectAttributes) {
        try {
            GameClass character = characterService.getCharacterById(id, type);
            if (character == null) {
                throw new RuntimeException("Персонаж не найден!");
            }

            // Обновляем общие поля
            character.setName(name);
            character.setLevel(level);
            character.setHealth(health);

            // Обновляем специфические поля
            if (character instanceof Summoner summoner) {
                if (mana != null) summoner.setMana(mana);
                if (summonsCount != null) summoner.setSummonsCount(summonsCount);
            } else if (character instanceof Paladin paladin) {
                if (armor != null) paladin.setArmor(armor);
                if (holyPower != null) paladin.setHolyPower(holyPower);
            }

            characterService.updateCharacter(character);
            redirectAttributes.addFlashAttribute("success", "Персонаж " + name + " обновлён!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/characters";
    }


    @GetMapping("/delete/summoner/{id}")
    public String deleteSummonerGet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        characterService.deleteCharacter(id, "summoner");
        redirectAttributes.addFlashAttribute("success", "Призыватель удалён!");
        return "redirect:/characters/summoners";
    }

    @GetMapping("/delete/paladin/{id}")
    public String deletePaladinGet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        characterService.deleteCharacter(id, "paladin");
        redirectAttributes.addFlashAttribute("success", "Паладин удалён!");
        return "redirect:/characters/paladins";
    }


    @GetMapping("/logs/{id}")
    public String viewLogs(@PathVariable Long id, Model model) {
        GameClass character = characterService.getCharacterById(id, "summoner");
        if (character == null) {
            character = characterService.getCharacterById(id, "paladin");
        }
        if (character == null) {
            return "redirect:/characters";
        }
        model.addAttribute("character", character);
        model.addAttribute("logs", characterService.getLogsForCharacter(id)); // через сервис
        return "logs";
    }




    @GetMapping("/api/table")
    public String getTableFragment(Model model) {
        model.addAttribute("characters", characterService.getAllCharacters());
        return "characters :: #tableContainer";
    }

    // ========== POST методы (CRUD) ==========

    @PostMapping("/add/summoner")
    public String addSummoner(@RequestParam String name,
                              @RequestParam int level,
                              @RequestParam int health,
                              @RequestParam int mana,
                              @RequestParam int summonsCount,
                              RedirectAttributes redirectAttributes) {
        try {
            Summoner summoner = new Summoner(name, level, health, mana, summonsCount);
            characterService.addSummoner(summoner);
            redirectAttributes.addFlashAttribute("success", "Призыватель " + name + " добавлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/characters/summoners";
    }

    @PostMapping("/add/paladin")
    public String addPaladin(@RequestParam String name,
                             @RequestParam int level,
                             @RequestParam int health,
                             @RequestParam int armor,
                             @RequestParam int holyPower,
                             RedirectAttributes redirectAttributes) {
        try {
            Paladin paladin = new Paladin(name, level, health, armor, holyPower);
            characterService.addPaladin(paladin);
            redirectAttributes.addFlashAttribute("success", "Паладин " + name + " добавлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/characters/paladins";
    }

    @PostMapping("/delete/summoner/{id}")
    public String deleteSummoner(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        characterService.deleteCharacter(id, "summoner");
        redirectAttributes.addFlashAttribute("success", "Призыватель удалён!");
        return "redirect:/characters/summoners";
    }

    @PostMapping("/delete/paladin/{id}")
    public String deletePaladin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        characterService.deleteCharacter(id, "paladin");
        redirectAttributes.addFlashAttribute("success", "Паладин удалён!");
        return "redirect:/characters/paladins";
    }

    // ========== AJAX методы (действия с персонажем) ==========

    @PostMapping("/action/{type}/{id}")
    @ResponseBody
    public String performAction(@PathVariable String type,
                                @PathVariable Long id,
                                @RequestParam String action,
                                @RequestParam(required = false) Integer value) {
        GameClass character = characterService.getCharacterById(id, type);
        if (character == null) {
            return "error: Персонаж не найден!";
        }
        try {
            characterService.executeAction(character, action, value);
            return character.getStats();
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
}