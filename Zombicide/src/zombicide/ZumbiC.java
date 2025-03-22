package zombicide;

import javax.swing.JComponent;

public class ZumbiC extends Movel {
    private int vida;

    public ZumbiC(int posX, int posY) {
        super('C', posX, posY);
        this.vida = 2; // Vida inicial
    }

    public void move(int deltaX, int deltaY) {
        setPosX(getPosX() + deltaX);
        setPosY(getPosY() + deltaY);
    }

    public JComponent getVisual() {
        return new Celula('C');
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }
}