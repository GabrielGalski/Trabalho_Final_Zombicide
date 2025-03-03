package zumbicidio;

import java.awt.Color;
import javax.swing.BorderFactory;

public final class FabricaDeCelula {
    public static Celula fabrica(char tipo) {
        return new Celula(tipo);
    }

    private static Celula criarCelula(Color cor, char tipo) {
        Celula celula = new Celula(tipo);
        celula.setBackground(cor);
        celula.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return celula;
    }
}