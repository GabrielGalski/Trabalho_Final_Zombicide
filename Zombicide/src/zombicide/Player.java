package zombicide;

import javax.swing.JComponent;
import java.io.Serializable;

public class Player extends Movel implements Serializable {
    private static final long serialVersionUID = 1L;
    private int vida;
    private int percep;
    private int temCura;
    private int tiros;
    private boolean temTaco;
    private int baus;

    public Player(int posX, int posY, int vida, int attack) {
        this(posX, posY, vida, attack, 2);
    }

    public Player(int posX, int posY, int vida, int attack, int percep) {
        super('P', posX, posY);
        this.vida = vida;
        this.percep = percep;
        this.temCura = 0;
        this.tiros = 0;
        this.temTaco = false;
        this.baus = 0; 
    }

    @Override
    public void move(int deltaX, int deltaY) {
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

    public int getAttack() {
        return temTaco ? 2 : 1;
    }

    public void setAttack(int attack) {
    }

    public int getPercep() {
        return percep;
    }

    public void setPercep(int percep) {
        this.percep = percep;
    }

    public int getTemCura() {
        return temCura;
    }

    public void addCura() {
        if (temCura < 3) temCura++;
    }

    public void useCura() {
        if (temCura > 0) temCura--;
    }

    public int getTiros() {
        return tiros;
    }

    public void addTiro() {
        if (tiros < 3) tiros++;
    }

    public void useTiro() {
        if (tiros > 0) tiros--;
    }

    public boolean temTaco() {
        return temTaco;
    }

    public void setTemTaco(boolean temTaco) {
        this.temTaco = temTaco;
    }

    public void incrementBaus() {
        this.baus++;
    }

    public int getBaus() {
        return this.baus;
    }
}