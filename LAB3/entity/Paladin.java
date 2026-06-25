package com.example.lab3.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "paladins")
@PrimaryKeyJoinColumn(name = "game_class_id")
public class Paladin extends GameClass {

    private int armor = 50;
    private int holyPower = 30;
    private int maxHolyPower = 100;

    public Paladin() {
        super("Неизвестный паладин", 1, 120);
    }

    public Paladin(String name, int level, int health, int armor, int holyPower) {
        super(name, level, health);
        this.armor = armor;
        this.holyPower = holyPower;
    }

    public void shieldWall() {
        if (holyPower >= 20) {
            holyPower -= 20;
            armor += 30;
        }
    }

    public void holyStrike() {
        if (holyPower >= 25) {
            holyPower -= 25;
        }
    }

    public void increaseArmor(int amount) {
        armor += amount;
    }

    @Override
    public void specialAction() {
        shieldWall();
    }

    @Override
    public void takeDamage(int damage) {
        int reduced = damage - (armor / 10);
        if (reduced < 0) reduced = 0;
        setHealth(getHealth() - reduced);
        if (getHealth() < 0) setHealth(0);
    }

    @Override
    public void levelUp() {
        setLevel(getLevel() + 1);
        setHealth(getHealth() + 25);
        setArmor(armor + 10);
        setHolyPower(Math.min(holyPower + 15, maxHolyPower));
    }

    @Override
    public String getStats() {
        return String.format("""
            ⚔️ ПАЛАДИН
            Имя: %s
            Уровень: %d
            Здоровье: %d
            Броня: %d
            Святая сила: %d / %d
            """, getName(), getLevel(), getHealth(), armor, holyPower, maxHolyPower);
    }
}