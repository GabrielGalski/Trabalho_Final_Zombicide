package zumbicidio;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.BorderFactory;
import java.io.Serializable;

public class Celula extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final char tipo;

    public Celula(char tipo) {
        this.tipo = tipo;
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        configurarAparencia();
    }

    public char getTipo() {
        return tipo;
    }

    private void configurarAparencia() {
        switch (tipo) {
            case 'P': setBackground(Color.GREEN); break;
            case 'Z': setBackground(Color.RED); break;
            case 'C': setBackground(Color.ORANGE); break;
            case 'R': setBackground(Color.YELLOW); break;
            case 'G': setBackground(Color.MAGENTA); break;
            case 'B': setBackground(Color.BLUE); break;
            case '1': setBackground(Color.GRAY); break;
            case 'V': setBackground(Color.WHITE); break;
            default: setBackground(Color.WHITE); break;
        }
    }
}