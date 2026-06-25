package com.example.lab3.controller;

import com.example.lab3.entity.GameClass;
import com.example.lab3.entity.Player;
import com.example.lab3.service.GameClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;  // ← ЭТОТ ИМПОРТ

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private GameClassService gameClassService;

    @GetMapping
    @Transactional
    public String listPlayers(Model model) {
        model.addAttribute("players", gameClassService.getAllPlayersWithCharacters());

        List<GameClass> allCharacters = gameClassService.getAllCharacters();
        List<Long> takenCharacterIds = new ArrayList<>();
        for (Player p : gameClassService.getAllPlayersWithCharacters()) {
            for (GameClass c : p.getCharacters()) {
                takenCharacterIds.add(c.getId());
            }
        }
        List<GameClass> freeCharacters = allCharacters.stream()
                .filter(c -> !takenCharacterIds.contains(c.getId()))
                .collect(Collectors.toList());

        model.addAttribute("allCharacters", freeCharacters);
        return "players";
    }

    @PostMapping("/add")
    public String addPlayer(@RequestParam String name,
                            @RequestParam String email,
                            RedirectAttributes redirectAttributes) {
        try {
            Player player = new Player(name, email);
            gameClassService.addPlayer(player);
            redirectAttributes.addFlashAttribute("success", "Игрок " + name + " добавлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/players";
    }

    @PostMapping("/delete/{id}")
    public String deletePlayer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        gameClassService.deletePlayer(id);
        redirectAttributes.addFlashAttribute("success", "Игрок удалён!");
        return "redirect:/players";
    }

    @PostMapping("/add-character")
    public String addCharacterToPlayer(@RequestParam Long playerId,
                                       @RequestParam Long characterId,
                                       RedirectAttributes redirectAttributes) {
        try {
            gameClassService.addCharacterToPlayer(playerId, characterId);
            redirectAttributes.addFlashAttribute("success", "Персонаж привязан к игроку!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/players";
    }

    @PostMapping("/remove-character/{playerId}/{characterId}")
    public String removeCharacterFromPlayer(@PathVariable Long playerId,
                                            @PathVariable Long characterId,
                                            RedirectAttributes redirectAttributes) {
        gameClassService.removeCharacterFromPlayer(playerId, characterId);
        redirectAttributes.addFlashAttribute("success", "Персонаж отвязан от игрока!");
        return "redirect:/players";
    }
}