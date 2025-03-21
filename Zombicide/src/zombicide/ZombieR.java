package zombicide;

import javax.swing.JComponent;
import java.io.Serializable;

public class ZombieR extends Mobile implements Serializable {
    private static final long serialVersionUID = 1L;
    private int health;
    private boolean isVisibleInDebug; // Flag para controle de visibilidade no debug

    public ZombieR(int posX, int posY) {
        super('R', posX, posY); // Tipo interno é 'R', passa posição para Mobile
        this.health = 1; // Vida inicial
        this.isVisibleInDebug = false; // Por padrão, não é visível
    }

    // Método para ativar/desativar a visibilidade no debug
    public void setVisibleInDebug(boolean isVisibleInDebug) {
        this.isVisibleInDebug = isVisibleInDebug;
    }

    @Override
    public JComponent getVisual() {
        // Se a flag estiver ativada, retorna uma Cell com tipo 'R'
        // Caso contrário, retorna uma Cell com tipo 'V' (vazio)
        return new Cell(isVisibleInDebug ? 'R' : 'V');
    }

    @Override
    public void move(int deltaX, int deltaY) {
        setPosX(getPosX() + deltaX);
        setPosY(getPosY() + deltaY);
    }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
}