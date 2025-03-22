package zombicide;

import javax.swing.JOptionPane;

public class Reinicia {

    private Zombicide frame;
    @SuppressWarnings("unused")
    private transient Engine engine;

    public Reinicia(Zombicide frame, Engine engine) {
        this.frame = frame;
        this.engine = engine;
    }

    public void mostrarOpcoes() {
        String[] opcoes = {"Reiniciar", "Menu", "Sair"};
        int escolha = JOptionPane.showOptionDialog(
            null,
            "O que você deseja fazer?",
            "Fim",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcoes,
            opcoes[0]
        );

        switch (escolha) {
            case 0 -> 
                reiniciaTudo();
            case 1 -> 
                voltaMenu();
            case 2 -> 
                sair();
            default -> sair();
        }
    }

    private void reiniciaTudo() {
        frame.reiniciaTudo(); 
    }

    private void voltaMenu() {
        frame.voltaMenu(); 
    }

    private void sair() {
        frame.dispose();
    }
}