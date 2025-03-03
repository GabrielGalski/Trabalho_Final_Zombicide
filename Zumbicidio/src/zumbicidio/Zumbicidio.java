package zumbicidio;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JFrame;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public final class Zumbicidio extends JFrame {
    private Tabuleiro tabuleiro;
    private Menu menu;
    private static final String SAVE_PATH = "D:\\Arquivos\\Java\\Poo\\Recursos\\save.dat";
    private static final String MAP_PATH = "D:\\Arquivos\\Java\\Poo\\Recursos\\Mapas\\mapa1.txt";

    public Zumbicidio() {
        tabuleiro = new Tabuleiro(this);
        menu = new Menu(this, tabuleiro);
        configurarJanelaMenu();
    }

    private void configurarJanelaMenu() {
        setTitle("Zumbicídio - Menu");
        setSize(900, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(menu);
    }

    public void iniciarDificuldade(boolean modoDebug) { // Novo parâmetro
        getContentPane().removeAll();
        setTitle("Zumbicídio - Dificuldade");
        setSize(900, 900); // Padronizando para 900x900
        Dificuldade dificuldade = new Dificuldade(this, tabuleiro, modoDebug);
        add(dificuldade);
        revalidate();
        repaint();
    }

    public void iniciarJogo(boolean modoDebug, int percepcao) {
        getContentPane().removeAll();
        setTitle("Zumbicídio");
        setSize(900, 900); // Padronizando para 900x900
        tabuleiro.setModoDebug(modoDebug);
        PainelDeJogo painelDeJogo = new PainelDeJogo(this, tabuleiro, modoDebug, percepcao);
        add(painelDeJogo);
        revalidate();
        repaint();
    }

    private void carregarMapa(String caminho) {
        try {
            File arquivo = new File(caminho);
            Scanner scanner = new Scanner(arquivo);
            tabuleiro.carregarMapa(scanner);
            scanner.close();
        } catch (FileNotFoundException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Arquivo de mapa não encontrado!");
            System.exit(1);
        }
    }

    public String getSavePath() {
        return SAVE_PATH;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Zumbicidio jogo = new Zumbicidio();
            jogo.carregarMapa(MAP_PATH);
            jogo.setVisible(true);
        });
    }
}