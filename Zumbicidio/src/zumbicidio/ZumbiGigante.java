package zumbicidio;

import javax.swing.JComponent;
import java.io.Serializable;

public class ZumbiGigante extends Imovel implements Serializable {
    private static final long serialVersionUID = 1L;
    private int posX, posY;
    private int vida;

    public ZumbiGigante(int posX, int posY) {
        super('G');
        this.posX = posX;
        this.posY = posY;
        this.vida = 4; // Vida inicial
    }

    @Override
    public JComponent getVisual() {
        return new Celula('G');
    }

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getVida() { return vida; }
    public void setVida(int vida) { this.vida = vida; }
}