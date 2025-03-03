package zumbicidio;

import javax.swing.JComponent;
import java.io.Serializable;

public class Imovel extends Entidade implements Serializable {
    private static final long serialVersionUID = 1L;

    public Imovel(char tipo) {
        super(tipo);
    }

    @Override
    public JComponent getVisual() {
        return new Celula(getTipo());
    }

    public static Imovel criarImovel(char tipo) {
        return new Imovel(tipo);
    }
}