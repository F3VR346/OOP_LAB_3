package com.example.lab3.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "game_classes")
public abstract class GameClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private int level = 1;
    private int health = 100;

    @OneToMany(mappedBy = "gameClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GameActionLog> actionLogs = new ArrayList<>();


    @ManyToMany(mappedBy = "characters")
    private List<Player> players = new ArrayList<>();


    public GameClass() {}

    public GameClass(String name, int level, int health) {
        this.name = name;
        this.level = level;
        this.health = health;
    }

    public abstract void specialAction();
    public abstract void takeDamage(int damage);
    public abstract void levelUp();
    public abstract String getStats();
}