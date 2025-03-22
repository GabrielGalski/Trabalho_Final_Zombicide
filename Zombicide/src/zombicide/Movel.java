package zombicide;

import javax.swing.JComponent;

public abstract class Movel extends Entidade {
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

    public abstract void move(int deltaX, int deltaY);

    /**
     *
     * @return
     */
    @Override
    public abstract JComponent getVisual();
}