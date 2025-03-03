package zumbicidio;

import javax.swing.JComponent;
import java.io.Serializable;

public class ZumbiCorredor extends Movel implements Serializable {
    private static final long serialVersionUID = 1L;
    private int vida;

    public ZumbiCorredor(int posX, int posY) {
        super('C', posX, posY);
        this.vida = 2; // Vida inicial
    }

    @Override
    public void mover(int deltaX, int deltaY) {
        setPosX(getPosX() + deltaX);
        setPosY(getPosY() + deltaY);
    }

    @Override
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