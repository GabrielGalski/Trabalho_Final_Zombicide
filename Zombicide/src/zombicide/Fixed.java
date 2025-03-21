package zombicide;
import javax.swing.JComponent;
import java.io.Serializable;

public class Fixed extends Entity implements Serializable {
    private static final long serialVersionUID = 1L;

    public Fixed(char type) {
        super(type);
    }

    @Override
    public JComponent getVisual() {
        return new Cell(getType());
    }

    public static Fixed createFixed(char type) {
        return new Fixed(type);
    }
}