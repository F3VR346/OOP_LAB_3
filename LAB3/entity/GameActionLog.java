package com.example.lab3.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "game_action_logs")
public class GameActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_class_id", nullable = false)
    private GameClass gameClass;

    @Column(nullable = false, length = 50)
    private String actionType;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "resource_change")
    private Integer resourceChange;

    @Column(name = "damage_taken")
    private Integer damageTaken;

    public GameActionLog(GameClass gameClass, String actionType, String description) {
        this.gameClass = gameClass;
        this.actionType = actionType;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
}