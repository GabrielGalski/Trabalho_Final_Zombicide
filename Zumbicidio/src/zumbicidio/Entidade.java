package zumbicidio;

import javax.swing.JComponent;
import java.io.Serializable;

public abstract class Entidade implements Serializable {
    private static final long serialVersionUID = 1L;
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