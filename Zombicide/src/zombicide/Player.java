package zombicide;

import javax.swing.JComponent;
import java.io.Serializable;

public class Player extends Mobile implements Serializable {
    private static final long serialVersionUID = 1L;
    private int health;
    private int perception;
    private int bandages;
    private int bullets;
    private boolean hasBat;
    private int chestsOpened; // Novo atributo para contar baús abertos

    public Player(int posX, int posY, int health, int attack) {
        this(posX, posY, health, attack, 2);
    }

    public Player(int posX, int posY, int health, int attack, int perception) {
        super('P', posX, posY);
        this.health = health;
        this.perception = perception;
        this.bandages = 0;
        this.bullets = 0;
        this.hasBat = false;
        this.chestsOpened = 0; // Inicializa o contador de baús
    }

    @Override
    public void move(int deltaX, int deltaY) {
        setPosX(getPosX() + deltaX);
        setPosY(getPosY() + deltaY);
    }

    @Override
    public JComponent getVisual() {
        return new Cell('P');
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttack() {
        return hasBat ? 2 : 1;
    }

    public void setAttack(int attack) {
        // Método vazio conforme o original
    }

    public int getPerception() {
        return perception;
    }

    public void setPerception(int perception) {
        this.perception = perception;
    }

    public int getBandages() {
        return bandages;
    }

    public void addBandage() {
        if (bandages < 3) bandages++;
    }

    public void useBandage() {
        if (bandages > 0) bandages--;
    }

    public int getBullets() {
        return bullets;
    }

    public void addBullet() {
        if (bullets < 3) bullets++;
    }

    public void useBullet() {
        if (bullets > 0) bullets--;
    }

    public boolean hasBat() {
        return hasBat;
    }

    public void setHasBat(boolean hasBat) {
        this.hasBat = hasBat;
    }

    public void incrementChestsOpened() {
        this.chestsOpened++;
    }

    public int getChestsOpened() {
        return this.chestsOpened;
    }
}