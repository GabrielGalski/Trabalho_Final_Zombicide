package zombicide;

import javax.swing.JComponent;
import java.io.Serializable;

public class ZombieC extends Mobile implements Serializable {
    private static final long serialVersionUID = 1L;
    private int health;

    public ZombieC(int posX, int posY) {
        super('C', posX, posY);
        this.health = 2; // Initial health
    }

    @Override
    public void move(int deltaX, int deltaY) {
        setPosX(getPosX() + deltaX);
        setPosY(getPosY() + deltaY);
    }

    @Override
    public JComponent getVisual() {
        return new Cell('C');
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}