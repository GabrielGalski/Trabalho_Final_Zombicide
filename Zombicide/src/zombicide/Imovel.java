package zombicide;

import javax.swing.JComponent;

public class Imovel extends Entidade {
    public Imovel(char tipo) {
        super(tipo);
    }

    public JComponent getVisual() {
        return new Celula(getTipo());
    }

    public static Imovel createImovel(char tipo) {
        return new Imovel(tipo);
    }
}