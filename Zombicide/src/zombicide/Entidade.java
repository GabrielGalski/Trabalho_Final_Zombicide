package zombicide;

import javax.swing.JComponent;

public abstract class Entidade {
    private final char tipo;

    public Entidade(char tipo) {
        this.tipo = tipo;
    }

    public char getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return String.valueOf(tipo);
    }

    public abstract JComponent getVisual();
}