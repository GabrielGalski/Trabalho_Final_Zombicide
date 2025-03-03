package zumbicidio;

import javax.swing.JComponent;
import java.io.Serializable;

public abstract class Movel extends Entidade implements Serializable {
    private static final long serialVersionUID = 1L;
    private int posX;
    private int posY;

    public Movel(char tipo, int posX, int posY) {
        super(tipo);
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

    public abstract void mover(int deltaX, int deltaY);

    @Override
    public abstract JComponent getVisual(); // Força as subclasses a definir sua própria célula
}