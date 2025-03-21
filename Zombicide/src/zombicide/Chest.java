package zombicide;

import javax.swing.JComponent;
import java.io.Serializable;

public class Chest extends Fixed implements Serializable {
    private static final long serialVersionUID = 1L;

    public Chest() {
        super('B');
    }

    @Override
    public JComponent getVisual() {
        return new Cell('B'); 
    }
}