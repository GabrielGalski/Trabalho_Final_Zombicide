package zombicide;

import javax.swing.JComponent;

public class ZumbiG extends Imovel {
    private int posX, posY;
    private int vida;

    public ZumbiG(int posX, int posY) {
        super('G');
        this.posX = posX;
        this.posY = posY;
        this.vida = 4; // Vida inicial
    }

    public JComponent getVisual() {
        return new Celula('G');
    }

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getVida() { return vida; }
    public void setVida(int vida) { this.vida = vida; }
}