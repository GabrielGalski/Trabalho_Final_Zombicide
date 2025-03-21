package zombicide;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random; // Importar Random
import java.util.Scanner;
import javax.swing.JFrame;

public final class Zombicide extends JFrame {
    private static final long serialVersionUID = 1L;
    private Engine engine;
    private WindowMenu windowMenu;
    private static final String SAVE_PATH = "D:\\Arquivos\\Java\\Poo\\Recursos\\save.dat";
    private static final String MAP_BASE_PATH = "D:\\Arquivos\\Java\\Poo\\Recursos\\Mapas\\mapa"; // Caminho base
    private static final Random random = new Random(); // Instância de Random

    public Zombicide() {
        engine = new Engine(this);
        windowMenu = new WindowMenu(this, engine);
        setupMenuWindow();
    }

    private void setupMenuWindow() {
        setTitle("Zombicide - Gabriel Galski Machado");
        setSize(900, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(windowMenu);
    }

    public void startDifficulty(boolean debugMode) {
        getContentPane().removeAll();
        setTitle("Zombicide - Gabriel Galski Machado");
        setSize(900, 900);
        WindowPerc windowPerc = new WindowPerc(this, engine, debugMode);
        add(windowPerc);
        revalidate();
        repaint();
    }

    public void startGame(boolean debugMode, int perception) {
        getContentPane().removeAll();
        setTitle("Zombicide");
        setSize(900, 900);
        engine.setDebugMode(debugMode);
        engine.setPerception(perception); // Salva a percepção no engine
        WindowGame windowGame = new WindowGame(this, engine, debugMode, perception);
        add(windowGame);
        revalidate();
        repaint();
    }

    private void loadMap(String path) {
        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            engine.loadMap(scanner);
            scanner.close();
        } catch (FileNotFoundException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Arquivo de mapa não encontrado: " + path);
            System.exit(1);
        }
    }

    // Novo método para carregar um mapa aleatório
    private void loadRandomMap() {
        int mapNumber = random.nextInt(10) + 1; // Gera número entre 1 e 10
        String mapPath = MAP_BASE_PATH + mapNumber + ".txt"; // Constrói o caminho do arquivo
        loadMap(mapPath); // Chama o método loadMap com o caminho gerado
    }

    public void loadGame() {
        engine.loadGame(SAVE_PATH);
        int perception = engine.getPerception();
        boolean debugMode = engine.isDebugMode();
        startGame(debugMode, perception); // Inicia o jogo diretamente com a dificuldade carregada
    }

    public String getSavePath() {
        return SAVE_PATH;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Zombicide game = new Zombicide();
            game.loadRandomMap(); // Substitui loadMap por loadRandomMap
            game.setVisible(true);
        });
    }
}