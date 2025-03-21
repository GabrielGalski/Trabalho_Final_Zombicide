package zombicide;

import javax.swing.JComponent;
import java.io.Serializable;

public class ZombieG extends Fixed implements Serializable {
    private static final long serialVersionUID = 1L;
    private int posX, posY;
    private int health;

    public ZombieG(int posX, int posY) {
        super('G');
        this.posX = posX;
        this.posY = posY;
        this.health = 4; // Initial health
    }

    @Override
    public JComponent getVisual() {
        return new Cell('G');
    }

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
}