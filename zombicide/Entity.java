package zombicide;

import javax.swing.JComponent;
import java.io.Serializable;

public abstract class Entity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final char type;

    public Entity(char type) {
        this.type = type;
    }

    public char getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.valueOf(type);
    }

    public abstract JComponent getVisual();
}