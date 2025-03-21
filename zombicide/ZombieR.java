package zombicide;

import javax.swing.JComponent;
import java.io.Serializable;

public class ZombieR extends Fixed implements Serializable {
    private static final long serialVersionUID = 1L;
    private int posX, posY;
    private int health;

    public ZombieR(int posX, int posY) {
        super('R'); // Internal type is now 'V'
        this.posX = posX;
        this.posY = posY;
        this.health = 1; // Initial health
    }

    @Override
    public JComponent getVisual() {
        return new Cell('V'); // Appearance of empty space
    }

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
}