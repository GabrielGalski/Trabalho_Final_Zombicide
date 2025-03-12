package zumbicidio;

import javax.swing.JComponent;
import java.io.Serializable;

public class Bau extends Imovel implements Serializable {
    private static final long serialVersionUID = 1L;

    public Bau() {
        super('B');
    }

    @Override
    public JComponent getVisual() {
        return new Celula('B'); 
    }
}