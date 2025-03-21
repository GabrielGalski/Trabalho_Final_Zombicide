package zombicide;

import javax.swing.JComponent;
import java.io.Serializable;

public abstract class Mobile extends Entity implements Serializable {
    private static final long serialVersionUID = 1L;
    private int posX;
    private int posY;

    public Mobile(char type, int posX, int posY) {
        super(type);
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public abstract void move(int deltaX, int deltaY);

    @Override
    public abstract JComponent getVisual(); // Forces subclasses to define their own cell
}