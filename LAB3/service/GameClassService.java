package com.example.lab3.service;

import com.example.lab3.entity.GameClass;
import com.example.lab3.entity.GameActionLog;
import com.example.lab3.entity.Summoner;
import com.example.lab3.entity.Paladin;
import com.example.lab3.repository.SummonerRepository;
import com.example.lab3.repository.PaladinRepository;
import com.example.lab3.repository.GameActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import com.example.lab3.repository.PlayerRepository;
import com.example.lab3.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.NoSuchElementException;

@Service
@Transactional
public class GameClassService {

    @Autowired
    private GameActionLogRepository logRepository;  // ← ТЕПЕРЬ РАБОТАЕТ

    @Autowired
    private SummonerRepository summonerRepository;

    @Autowired
    private PaladinRepository paladinRepository;

    public void logAction(GameClass character, String actionType, String description, Integer resourceChange, Integer damageTaken) {
        GameActionLog log = new GameActionLog(character, actionType, description);
        log.setResourceChange(resourceChange);
        log.setDamageTaken(damageTaken);
        logRepository.save(log);
    }

    public List<GameActionLog> getLogsForCharacter(Long characterId) {

        GameClass character = getCharacterById(characterId, "summoner");
        if (character == null) {
            character = getCharacterById(characterId, "paladin");
        }
        if (character == null) {
            return new ArrayList<>();
        }

        character.getActionLogs().size();
        return character.getActionLogs();
    }


    @Autowired
    private PlayerRepository playerRepository;

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        playerRepository.findAll().forEach(players::add);
        return players;
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    public void addPlayer(Player player) {
        if (player.getName() == null || player.getName().trim().isEmpty()) {
            throw new RuntimeException("Имя игрока не может быть пустым!");
        }
        playerRepository.save(player);
    }

    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }


    public void addCharacterToPlayer(Long playerId, Long characterId) {
        Player player = getPlayerById(playerId);
        if (player == null) {
            throw new RuntimeException("Игрок не найден!");
        }

        GameClass character = getCharacterById(characterId, "summoner");
        if (character == null) {
            character = getCharacterById(characterId, "paladin");
        }
        if (character == null) {
            throw new RuntimeException("Персонаж не найден!");
        }

        // ПРОВЕРКА 1: Есть ли уже этот персонаж у этого игрока?
        if (player.getCharacters().stream().anyMatch(c -> c.getId().equals(characterId))) {
            throw new RuntimeException("Этот персонаж уже привязан к этому игроку!");
        }

        // ПРОВЕРКА 2: Принадлежит ли этот персонаж уже другому игроку?
        List<Player> allPlayers = getAllPlayersWithCharacters();
        for (Player p : allPlayers) {
            if (!p.getId().equals(playerId)) {
                if (p.getCharacters().stream().anyMatch(c -> c.getId().equals(characterId))) {
                    throw new RuntimeException("Этот персонаж уже принадлежит другому игроку (" + p.getName() + ")!");
                }
            }
        }

        // Если все проверки пройдены — добавляем
        player.getCharacters().add(character);
        playerRepository.save(player);
    }

    public void removeCharacterFromPlayer(Long playerId, Long characterId) {
        Player player = getPlayerById(playerId);
        if (player != null) {
            player.getCharacters().removeIf(c -> c.getId().equals(characterId));
            playerRepository.save(player);
        }
    }



    @PostConstruct
    public void initData() {
        if (summonerRepository.count() == 0 && paladinRepository.count() == 0) {
            Summoner s = new Summoner("ТестПриз", 5, 100, 150, 2);
            summonerRepository.save(s);

            Paladin p = new Paladin("ТестПал", 3, 150, 60, 40);
            paladinRepository.save(p);

            System.out.println("Тестовые данные добавлены!");
        }
    }

    @Transactional(readOnly = true)
    public List<Player> getAllPlayersWithCharacters() {
        List<Player> players = new ArrayList<>();
        playerRepository.findAll().forEach(players::add);
        for (Player p : players) {
            p.getCharacters().size();
        }
        return players;
    }

    @Transactional(readOnly = true)
    public List<GameClass> getAllCharacters() {
        List<GameClass> characters = new ArrayList<>();

        // CrudRepository.findAll() возвращает Iterable, преобразуем в List
        List<Summoner> summoners = new ArrayList<>();
        summonerRepository.findAll().forEach(summoners::add);
        characters.addAll(summoners);

        List<Paladin> paladins = new ArrayList<>();
        paladinRepository.findAll().forEach(paladins::add);
        characters.addAll(paladins);

        return characters;
    }





    @Transactional
    public void refreshData() {
        // Преобразуем Iterable в List, чтобы вызвать size()
        List<Summoner> summoners = new ArrayList<>();
        summonerRepository.findAll().forEach(summoners::add);
        summoners.size();

        List<Paladin> paladins = new ArrayList<>();
        paladinRepository.findAll().forEach(paladins::add);
        paladins.size();
    }

    public List<Summoner> getSummoners() {
        List<Summoner> summoners = new ArrayList<>();
        summonerRepository.findAll().forEach(summoners::add);
        return summoners;
    }

    public List<Paladin> getPaladins() {
        List<Paladin> paladins = new ArrayList<>();
        paladinRepository.findAll().forEach(paladins::add);
        return paladins;
    }

    public GameClass getCharacterById(Long id, String type) {
        if ("summoner".equals(type)) {
            return summonerRepository.findById(id).orElse(null);
        } else if ("paladin".equals(type)) {
            return paladinRepository.findById(id).orElse(null);
        }
        return null;
    }

    public void addSummoner(Summoner summoner) {
        if (summoner.getName() == null || summoner.getName().trim().isEmpty()) {
            throw new RuntimeException("Имя не может быть пустым!");
        }
        summonerRepository.save(summoner);
    }

    public void addPaladin(Paladin paladin) {
        if (paladin.getName() == null || paladin.getName().trim().isEmpty()) {
            throw new RuntimeException("Имя не может быть пустым!");
        }
        paladinRepository.save(paladin);
    }

    public void updateCharacter(GameClass character) {
        if (character instanceof Summoner) {
            summonerRepository.save((Summoner) character);
        } else if (character instanceof Paladin) {
            paladinRepository.save((Paladin) character);
        }
    }

    public void deleteCharacter(Long id, String type) {
        if ("summoner".equals(type)) {
            summonerRepository.deleteById(id);
        } else if ("paladin".equals(type)) {
            paladinRepository.deleteById(id);
        }
    }

    public void executeAction(GameClass character, String action, Integer value) {
        String description = "";
        Integer resourceChange = null;
        Integer damageTaken = null;

        switch (action) {
            case "takeDamage":
                int damage = value != null ? value : 30;
                character.takeDamage(damage);
                damageTaken = damage;
                description = "Получено урона: " + damage;
                break;
            case "levelUp":
                character.levelUp();
                description = "Повышение уровня до " + character.getLevel();
                break;
            case "specialAction":
                character.specialAction();
                description = "Специальное действие";
                break;
            case "castSpell":
                if (character instanceof Summoner s) {
                    s.castSpell("Огненный шар");
                    resourceChange = -30;
                    description = "Произнесено заклинание (мана -30)";
                }
                break;
            case "summonCreature":
                if (character instanceof Summoner s) {
                    s.summonCreature();
                    resourceChange = -50;
                    description = "Призыв существа (мана -50)";
                }
                break;
            case "regenerateMana":
                if (character instanceof Summoner s) {
                    int amount = value != null ? value : 50;
                    s.regenerateMana(amount);
                    resourceChange = amount;
                    description = "Восстановление маны +" + amount;
                }
                break;
            case "shieldWall":
                if (character instanceof Paladin p) {
                    p.shieldWall();
                    resourceChange = -20;
                    description = "Стена щита (св.сила -20)";
                }
                break;
            case "holyStrike":
                if (character instanceof Paladin p) {
                    p.holyStrike();
                    resourceChange = -25;
                    description = "Священный удар (св.сила -25)";
                }
                break;
            case "increaseArmor":
                if (character instanceof Paladin p) {
                    int amount = value != null ? value : 20;
                    p.increaseArmor(amount);
                    description = "Увеличение брони +" + amount;
                }
                break;
        }
        updateCharacter(character);
        logAction(character, action, description, resourceChange, damageTaken);
    }
}