package zombicide;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JFrame;

public final class Zombicide extends JFrame {
    private Engine engine;
    private janelaMenu menu;
    private static final String CAMINHO_SALVO = "D:\\Arquivos\\Java\\Poo\\Recursos\\save.dat";
    private static final String CAMINHO_BASE_MAPA = "D:\\Arquivos\\Java\\Poo\\Recursos\\Mapas\\mapa";
    private static final Random random = new Random();

    private String mapaAtual;
    private int percepcaoAtual;
    private boolean debugAtual;

    public Zombicide() {
        engine = new Engine(this);
        menu = new janelaMenu(this, engine);
        setupMenu();
    }

    private void setupMenu() {
        setTitle("Zombicide - Gabriel Galski Machado");
        setSize(900, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(menu);
    }

    public void startDifficulty(boolean debugMode) {
        getContentPane().removeAll();
        setTitle("Zombicide - Gabriel Galski Machado");
        setSize(900, 900);
        janelaPerc janelaPerc = new janelaPerc(this, engine, debugMode);
        add(janelaPerc);
        revalidate();
        repaint();
    }

    public void startGame(boolean debugMode, int percepcao) {
        engine = new Engine(this);
        carregaMapaAleatorio();
        getContentPane().removeAll();
        setTitle("Zombicide");
        setSize(900, 900);
        engine.setDebugMode(debugMode);
        engine.setPercepcao(percepcao);
        debugAtual = debugMode;
        percepcaoAtual = percepcao;
        janelaJogo janelaJogo = new janelaJogo(this, engine, debugMode, percepcao);
        add(janelaJogo);
        revalidate();
        repaint();
    }

    private void carregaMapa(String caminho) {
        try {
            File arquivo = new File(caminho);
            Scanner scanner = new Scanner(arquivo);
            engine.carregarMapa(scanner);
            scanner.close();
        } catch (FileNotFoundException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Mapa não encontrado: " + caminho);
            System.exit(1);
        }
    }

    private void carregaMapaAleatorio() {
        int numMapa = random.nextInt(10) + 1;
        mapaAtual = CAMINHO_BASE_MAPA + numMapa + ".txt";
        carregaMapa(mapaAtual);
    }

    public void loadGame() {
        engine.loadGame(CAMINHO_SALVO);
        int percepcao = engine.getPercepcao();
        boolean debugMode = engine.isDebugMode();
        startGame(debugMode, percepcao);
    }

    public String getSavePath() {
        return CAMINHO_SALVO;
    }

    public janelaMenu getMenu() {
        return menu;
    }

    public void reiniciaTudo() {
        engine.setVerificaFimJogo(false);
        engine = new Engine(this);
        carregaMapaAleatorio();
        startGame(debugAtual, percepcaoAtual);
        engine.setVerificaFimJogo(true);
    }

    public void reiniciaJogo() {
        engine.setVerificaFimJogo(false);
        engine = new Engine(this);
        carregaMapa(mapaAtual);
        startGame(debugAtual, percepcaoAtual);
        engine.setVerificaFimJogo(true);
    }

    public void voltaMenu() {
        engine.setVerificaFimJogo(false);
        getContentPane().removeAll();
        engine = new Engine(this);
        menu = new janelaMenu(this, engine);
        add(menu);
        revalidate();
        repaint();
        engine.setVerificaFimJogo(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Zombicide jogo = new Zombicide();
            jogo.carregaMapaAleatorio();
            jogo.setVisible(true);
        });
    }
}