package model;

import java.util.Random;

public class Player {

    private final Random rand = new Random();

    private final String name;
    private int hp;
    private final int maxHp = 5;
    private int mana;
    private final int maxMana = 10;
    private int position;
    private int revivePosition;
    private boolean shieldActive;
    private final int minDice = 1;
    private final int maxDice = 6;

    public Player(String name) {
        this.name = name;
        this.hp = maxHp;
        this.mana = 0;
        this.position = 1;
        this.revivePosition = 1;
        this.shieldActive = false;
    }

    public void loseHp(int damage) {
        if (shieldActive) {
            shieldActive = false;
        } else {
            hp -= damage;
        }
    }

    public void gainHp(int amount) {
        hp = Math.min(hp + amount, maxHp);
    }

    public boolean useMana(int cost) {
        if (mana >= cost) {
            mana -= cost;
            return true;
        }
        return false;
    }

    public void gainMana(int amount) {
        mana = Math.min(mana + amount, maxMana);
    }

    public void activateShield() {
        shieldActive = true;
    }

    public int rollDice() {
        return rand.nextInt(maxDice - minDice + 1) + minDice;
    }

    public void move(int steps, int totalCells) {
        if (position + steps >= totalCells) {
            position = totalCells; // win — any overshoot counts
        } else {
            position += steps;
        }
    }

    public void setPosition(int position) { this.position = position; }
    public void setRevivePosition(int pos) { this.revivePosition = pos; }

    public boolean isAlive()        { return hp > 0; }
    public String getName()         { return name; }
    public int getHp()              { return hp; }
    public int getMaxHp()           { return maxHp; }
    public int getMana()            { return mana; }
    public int getMaxMana()         { return maxMana; }
    public int getPosition()        { return position; }
    public int getRevivePosition()  { return revivePosition; }
    public boolean hasShield()      { return shieldActive; }

    @Override
    public String toString() {
        return name + " [HP:" + hp + "/" + maxHp + " Mana:" + mana + "/" + maxMana
                + " Pos:" + position + (shieldActive ? " SHIELD" : "") + "]";
    }
}