package zumbicidio;

import javax.swing.JComponent;
import java.io.Serializable;

public class Personagem extends Movel implements Serializable {
    private static final long serialVersionUID = 1L;
    private int vida;
    private int ataque;
    private int percepcao;
    private int bandagens;
    private int balas;
    private boolean temTaco;

    public Personagem(int posX, int posY, int vida, int ataque) {
        this(posX, posY, vida, ataque, 2);
    }

    public Personagem(int posX, int posY, int vida, int ataque, int percepcao) {
        super('P', posX, posY);
        this.vida = vida;
        this.ataque = ataque;
        this.percepcao = percepcao;
        this.bandagens = 0;
        this.balas = 0;
        this.temTaco = false;
    }

    @Override
    public void mover(int deltaX, int deltaY) {
        setPosX(getPosX() + deltaX);
        setPosY(getPosY() + deltaY);
    }

    @Override
    public JComponent getVisual() {
        return new Celula('P');
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public int getAtaque() {
        return temTaco ? 2 : 1;
    }

    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    public int getPercepcao() {
        return percepcao;
    }

    public void setPercepcao(int percepcao) {
        this.percepcao = percepcao;
    }

    public int getBandagens() {
        return bandagens;
    }

    public void adicionarBandagem() {
        if (bandagens < 3) bandagens++;
    }

    public void usarBandagem() {
        if (bandagens > 0) bandagens--;
    }

    public int getBalas() {
        return balas;
    }

    public void adicionarBala() {
        if (balas < 3) balas++;
    }

    public void usarBala() {
        if (balas > 0) balas--;
    }

    public boolean temTaco() {
        return temTaco;
    }

    public void setTemTaco(boolean temTaco) {
        this.temTaco = temTaco;
    }
}