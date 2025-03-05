package zumbicidio;

import javax.swing.JComponent;
import java.io.Serializable;

public class ZumbiRastejante extends Imovel implements Serializable {
    private static final long serialVersionUID = 1L;
    private int posX, posY;
    private int vida;

    public ZumbiRastejante(int posX, int posY) {
        super('R'); // Tipo interno agora é 'V'
        this.posX = posX;
        this.posY = posY;
        this.vida = 1; // Vida inicial
    }

    @Override
    public JComponent getVisual() {
        return new Celula('V'); // Aparência de espaço vazio
    }

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getVida() { return vida; }
    public void setVida(int vida) { this.vida = vida; }
}