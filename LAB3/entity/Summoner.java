package com.example.lab3.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "summoners")
@PrimaryKeyJoinColumn(name = "game_class_id")
public class Summoner extends GameClass {

    private int mana = 100;
    private int summonsCount = 0;
    private int maxMana = 500;

    public Summoner() {
        super("Неизвестный призыватель", 1, 80);
    }

    public Summoner(String name, int level, int health, int mana, int summonsCount) {
        super(name, level, health);
        this.mana = mana;
        this.summonsCount = summonsCount;
    }

    public void castSpell(String spellName) {
        if (mana >= 30) {
            mana -= 30;
        }
    }

    public void summonCreature() {
        if (mana >= 50) {
            mana -= 50;
            summonsCount++;
        }
    }

    public void regenerateMana(int amount) {
        mana = Math.min(mana + amount, maxMana);
    }

    @Override
    public void specialAction() {
        summonCreature();
    }

    @Override
    public void takeDamage(int damage) {
        if (mana >= 20) {
            mana -= 20;
            damage /= 2;
        }
        setHealth(getHealth() - damage);
        if (getHealth() < 0) setHealth(0);
    }

    @Override
    public void levelUp() {
        setLevel(getLevel() + 1);
        setHealth(getHealth() + 20);
        setMana(Math.min(mana + 30, maxMana));
    }

    @Override
    public String getStats() {
        return String.format("""
            🔮 ПРИЗЫВАТЕЛЬ
            Имя: %s
            Уровень: %d
            Здоровье: %d
            Мана: %d / %d
            Призывы: %d
            """, getName(), getLevel(), getHealth(), mana, maxMana, summonsCount);
    }
}