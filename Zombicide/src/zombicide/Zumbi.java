package zombicide;

import javax.swing.JComponent;

public class Zumbi extends Movel {
    private int vida;

    public Zumbi(int posX, int posY) {
        super('Z', posX, posY);
        this.vida = 2; 
    }

    public void move(int deltaX, int deltaY) {
        setPosX(getPosX() + deltaX);
        setPosY(getPosY() + deltaY);
    }

    public JComponent getVisual() {
        return new Celula('Z');
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }
}