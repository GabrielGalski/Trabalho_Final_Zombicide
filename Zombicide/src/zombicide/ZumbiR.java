package zombicide;

import javax.swing.JComponent;

public class ZumbiR extends Movel {
    private int vida;
    private boolean isVisibleInDebug; // Flag para controle de visibilidade no debug

    public ZumbiR(int posX, int posY) {
        super('R', posX, posY); 
        this.vida = 1; 
        this.isVisibleInDebug = false; 
    }

    public void setVisibleInDebug(boolean isVisibleInDebug) {
        this.isVisibleInDebug = isVisibleInDebug;
    }

    public JComponent getVisual() {
        return new Celula(isVisibleInDebug ? 'R' : 'V');
    }

    public void move(int deltaX, int deltaY) {
        setPosX(getPosX() + deltaX);
        setPosY(getPosY() + deltaY);
    }

    public int getVida() { return vida; }
    public void setVida(int vida) { this.vida = vida; }
}