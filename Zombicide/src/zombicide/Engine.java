package zombicide;

import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Engine extends JPanel {
    private final Entidade[][] celulas;
    private transient JButton[][] botoes;
    private boolean[][] visivel;
    private boolean[][] mem;
    public final int TAM = 10;
    private int playerX, playerY;
    private Player player;
    private boolean debugMode = false;
    private boolean combate = false;
    private Entidade zumbiCombate = null;
    private int combateX, combateY;
    private StringBuilder combateTxt;
    private final transient Zombicide frame;
    private int percepcao;

    private boolean verificaFimJogo = true;

    public Engine(Zombicide frame) {
        this.frame = frame;
        celulas = new Entidade[TAM][TAM];
        botoes = new JButton[TAM][TAM];
        visivel = new boolean[TAM][TAM];
        mem = new boolean[TAM][TAM];
        player = new Player(0, 0, 5, 3);
        setLayout(new GridLayout(TAM, TAM));
        initMap();
    }

    public void setVerificaFimJogo(boolean verificaFimJogo) {
        this.verificaFimJogo = verificaFimJogo;
    }

    public boolean isVerificaFimJogo() {
        return verificaFimJogo;
    }

    public int getPercepcao() {
        return percepcao;
    }

    public void setPercepcao(int percepcao) {
        this.percepcao = percepcao;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public boolean isCombate() {
        return combate;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public Player getPlayer() {
        return player;
    }

    private void initMap() {
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                celulas[i][j] = Imovel.createImovel('V');
                visivel[i][j] = false;
                mem[i][j] = false;
                botoes[i][j] = addBotao(i, j);
                add(botoes[i][j]);
            }
        }
        playerX = 0;
        playerY = 0;
        celulas[playerX][playerY] = player;
    }

    public void carregarMapa(Scanner scanner) {
        removeAll();
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                char tipo = scanner.next().charAt(0);
                if (tipo == '0') tipo = 'V';
                celulas[i][j] = criaEntidade(tipo, i, j);
                visivel[i][j] = false;
                mem[i][j] = false;
                botoes[i][j] = addBotao(i, j);
                add(botoes[i][j]);
                if (tipo == 'P') {
                    playerX = i;
                    playerY = j;
                    celulas[i][j] = player;
                }
            }
        }
        trocaVisao();
        revalidate();
        repaint();
    }

    private JButton addBotao(int i, int j) {
        Entidade entidade = celulas[i][j];
        Celula celula = (Celula) entidade.getVisual();

        JButton botao = new JButton();
        botao.setOpaque(true);
        botao.setBorderPainted(true);
        botao.addActionListener(e -> movePlayer(i, j));
        botao.setPreferredSize(new java.awt.Dimension(80, 80));

        celula.botaoDesign(botao, false, false, debugMode);
        return botao;
    }

    private void movePlayer(int destX, int destY) {
        int dx = Math.abs(destX - playerX);
        int dy = Math.abs(destY - playerY);
        if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
            if (noBoard(destX, destY)) {
                Entidade destino = celulas[destX][destY];
                char tipoDest = destino.getTipo();

                if (combate) {
                    StringBuilder logFuga = new StringBuilder("Você tenta fugir!\n");
                    Agir.danoTotal zumbiResult = Agir.ataque(zumbiCombate, player);
                    int danoZumbi = zumbiResult.dano;
                    if (danoZumbi == 0) {
                        danoZumbi = 1;
                    }
                    player.setVida(player.getVida() - danoZumbi);
                    logFuga.append("O zumbi ataca na fuga e causa ")
                           .append(danoZumbi)
                           .append(" de dano!\n");

                    if (player.getVida() <= 0) {
                        logFuga.append("Morreu ao fugir!\n");
                        combate = false;
                        zumbiCombate = null;
                        JOptionPane.showMessageDialog(this, logFuga.toString(), "Fim", JOptionPane.ERROR_MESSAGE);
                        frame.dispose();
                        return;
                    }

                    combate = false;
                    zumbiCombate = null;
                    logFuga.append("Escapou do combate!\n");
                    JOptionPane.showMessageDialog(this, logFuga.toString(), "Fuga", JOptionPane.INFORMATION_MESSAGE);
                }

                if (destino instanceof ZumbiR) {
                    player.setVida(player.getVida() - 1);
                    JOptionPane.showMessageDialog(this, "Emboscado por Zumbi Rastejante! Perdeu 1 vida.",
                                                  "Emboscada", JOptionPane.WARNING_MESSAGE);
                    combateStart(destX, destY);
                } else if (umZumbi(tipoDest)) {
                    combateStart(destX, destY);
                } else if (!solido(destX, destY)) {
                    if (tipoDest == 'B') {
                        String resultado = Agir.pegou(player, this, destX, destY);
                        JOptionPane.showMessageDialog(this, resultado, "Item", JOptionPane.INFORMATION_MESSAGE);
                        celulas[destX][destY] = Imovel.createImovel('V');
                    }
                    celulas[playerX][playerY] = Imovel.createImovel('V');
                    playerX = destX;
                    playerY = destY;
                    player.move(dx, dy);
                    celulas[playerX][playerY] = player;
                    trocaVisao();
                    moveZumbis();
                    verificaFim();
                }
            }
        }
    }

    void combateStart(int x, int y) {
        combate = true;
        zumbiCombate = celulas[x][y];
        combateX = x;
        combateY = y;
        combateTxt = new StringBuilder("Combate iniciado!\n");

        Agir.danoTotal zumbiResult = Agir.ataque(zumbiCombate, player);
        int danoZumbi = zumbiResult.dano;

        if (danoZumbi > 0) {
            player.setVida(player.getVida() - danoZumbi);
            combateTxt.append("Zumbi ataca e causa ").append(danoZumbi).append(" de dano!");
            if (zumbiResult.critico == 6) {
                combateTxt.append(" (Crítico!)");
            }
            combateTxt.append("\n");
        } else {
            combateTxt.append("Zumbi ataca, mas você esquiva!\n");
        }

        trocaVisao();
        JOptionPane.showMessageDialog(this, combateTxt.toString(), "Combate", JOptionPane.INFORMATION_MESSAGE);
    }

    public void playerTurno(String acao) {
        combateTxt = new StringBuilder();

        int vidaZumbi = 0;
        boolean combateAtivo = combate && zumbiCombate != null;

        if (combateAtivo) {
            switch (zumbiCombate) {
                case Zumbi zumbi -> vidaZumbi = zumbi.getVida();
                case ZumbiC zumbiC -> vidaZumbi = zumbiC.getVida();
                case ZumbiR zumbiR -> vidaZumbi = zumbiR.getVida();
                case ZumbiG zumbiG -> vidaZumbi = zumbiG.getVida();
                default -> {
                }
            }
        }

        int dano = 0;
        switch (acao) {
            case "bater" -> {
                if (!combateAtivo) return;
                Agir.danoTotal ataqueResult = Agir.ataque(player, zumbiCombate);
                dano = ataqueResult.dano;

                if (zumbiCombate instanceof ZumbiG && dano < 2) {
                    dano = 0;
                    combateTxt.append("Golpe sem efeito!\n");
                } else if (dano > 0) {
                    vidaZumbi -= dano;
                    combateTxt.append("Você bateu e causou ").append(dano).append(" de dano!");
                    if (ataqueResult.critico == 6) {
                        combateTxt.append(" (Crítico!)");
                    }
                    combateTxt.append("\n");
                }
            }

            case "atirar" -> {
                if (!combateAtivo) return;
                Agir.danoTotal tiroResult = Agir.atira(player, zumbiCombate);
                dano = tiroResult.dano;

                if (dano > 0) {
                    if (zumbiCombate instanceof ZumbiC) {
                        combateTxt.append("Zumbi corredor desviou!\n");
                        dano = 0;
                    } else {
                        vidaZumbi -= dano;
                        combateTxt.append("Você atirou e causou ").append(dano).append(" de dano!");
                        if (tiroResult.critico == 6) {
                            combateTxt.append(" (Crítico!)");
                        }
                        combateTxt.append("\n");
                    }
                } else {
                    combateTxt.append("Sem balas!\n");
                }
            }

            case "curar" -> {
                int vidaAntes = player.getVida();
                Agir.cura(player, this);
                int vidaDepois = player.getVida();
                if (vidaDepois > vidaAntes) {
                    combateTxt.append("Usou bandagem e recuperou ")
                            .append(vidaDepois - vidaAntes)
                            .append(" de vida!\n");
                } else {
                    combateTxt.append("Tentou usar bandagem, mas vida já está cheia.\n");
                }

                if (!combateAtivo) {
                    moveZumbis();
                    trocaVisao();
                }
            }

            case "moveZumbis" -> {
                if (!combateAtivo) {
                    moveZumbis();
                    trocaVisao();
                }
                return;
            }
        }

        if (combateAtivo) {
            switch (zumbiCombate) {
                case Zumbi zumbi -> zumbi.setVida(vidaZumbi);
                case ZumbiC zumbiC -> zumbiC.setVida(vidaZumbi);
                case ZumbiR zumbiR -> zumbiR.setVida(vidaZumbi);
                case ZumbiG zumbiG -> zumbiG.setVida(vidaZumbi);
                default -> {
                }
            }

            if (vidaZumbi <= 0) {
                String classeZumbi = "";
                if (zumbiCombate instanceof Zumbi) classeZumbi = "comum";
                else if (zumbiCombate instanceof ZumbiC) classeZumbi = "corredor";
                else if (zumbiCombate instanceof ZumbiR) classeZumbi = "rastejante";
                else if (zumbiCombate instanceof ZumbiG) classeZumbi = "gigante";
                combateTxt.append("Zumbi ").append(classeZumbi).append(" derrotado!\n");
                celulas[combateX][combateY] = Imovel.createImovel('V');
                combate = false;
                zumbiCombate = null;
            } else {
                Agir.danoTotal zumbiResult = Agir.ataque(zumbiCombate, player);
                int danoZumbi = zumbiResult.dano;
                if (danoZumbi > 0) {
                    player.setVida(player.getVida() - danoZumbi);
                    combateTxt.append("Zumbi ataca e causa ").append(danoZumbi).append(" de dano!");
                    if (zumbiResult.critico == 6) {
                        combateTxt.append(" (Crítico!)");
                    }
                    combateTxt.append("\n");
                } else {
                    combateTxt.append("Zumbi ataca, mas você esquiva!\n");
                }
            }

            if (player.getVida() <= 0) {
                combateTxt.append("Você perdeu!\n");
                combate = false;
                zumbiCombate = null;
                JOptionPane.showMessageDialog(this, combateTxt.toString(), "Fim", JOptionPane.ERROR_MESSAGE);
                frame.dispose();
                return;
            }
        }

        trocaVisao();
        if (combateAtivo || acao.equals("curar")) {
            JOptionPane.showMessageDialog(this, combateTxt.toString(),
                                          combateAtivo ? "Combate" : "Ação", JOptionPane.INFORMATION_MESSAGE);
        }
        verificaFim();
    }

    private void moveZumbis() {
        if (combate) return;

        Entidade[][] copiaCelulas = new Entidade[TAM][TAM];
        for (int i = 0; i < TAM; i++) {
            System.arraycopy(celulas[i], 0, copiaCelulas[i], 0, TAM);
        }

        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                Entidade entidade = copiaCelulas[i][j];
                if (entidade instanceof Movel && umZumbi(entidade.getTipo()) && !(entidade instanceof ZumbiG)) {
                    int maxPassos = (entidade.getTipo() == 'C') ? 2 : 1;
                    tentaMoverZumbi(i, j, entidade, maxPassos);
                }
            }
        }
        trocaVisao();
        verificaFim();
    }

    private void tentaMoverZumbi(int x, int y, Entidade zumbi, int maxPassos) {
        boolean ehBauOriginal = celulas[x][y].getTipo() == 'B';

        int distX = playerX - x;
        int distY = playerY - y;
        int sinalX = Integer.signum(distX);
        int sinalY = Integer.signum(distY);
        boolean prioridadeHorizontal = Math.abs(distX) >= Math.abs(distY);

        if (Math.abs(distX) + Math.abs(distY) == 1) {
            combateStart(x, y);
            return;
        }

        int passosDados = 0;
        int atualX = x;
        int atualY = y;

        while (passosDados < maxPassos) {
            int proxX = atualX;
            int proxY = atualY;

            if (prioridadeHorizontal) {
                proxX = atualX + sinalX;
            } else {
                proxY = atualY + sinalY;
            }

            if (!noBoard(proxX, proxY)) {
                break;
            }

            if (proxX == playerX && proxY == playerY) {
                combateStart(atualX, atualY);
                return;
            }

            if (liberado(proxX, proxY)) {
                if (passosDados == 0) {
                    if (ehBauOriginal) {
                        celulas[x][y] = new Bau();
                    } else {
                        celulas[x][y] = Imovel.createImovel('V');
                    }
                } else {
                    celulas[atualX][atualY] = Imovel.createImovel('V');
                }

                atualX = proxX;
                atualY = proxY;
                passosDados++;

                celulas[atualX][atualY] = zumbi;

                if (Math.abs(atualX - playerX) + Math.abs(atualY - playerY) == 1) {
                    combateStart(atualX, atualY);
                    return;
                }
            } else {
                if (prioridadeHorizontal) {
                    proxX = atualX;
                    proxY = atualY + sinalY;
                } else {
                    proxX = atualX + sinalX;
                    proxY = atualY;
                }

                if (!noBoard(proxX, proxY)) {
                    break;
                }

                if (proxX == playerX && proxY == playerY) {
                    combateStart(atualX, atualY);
                    return;
                }

                if (liberado(proxX, proxY)) {
                    if (passosDados == 0) {
                        if (ehBauOriginal) {
                            celulas[x][y] = new Bau();
                        } else {
                            celulas[x][y] = Imovel.createImovel('V');
                        }
                    } else {
                        celulas[atualX][atualY] = Imovel.createImovel('V');
                    }

                    atualX = proxX;
                    atualY = proxY;
                    passosDados++;

                    celulas[atualX][atualY] = zumbi;

                    if (Math.abs(atualX - playerX) + Math.abs(atualY - playerY) == 1) {
                        combateStart(atualX, atualY);
                        return;
                    }
                } else {
                    break;
                }
            }
        }

        if (atualX == x && atualY == y) {
            return;
        }

        ((Movel) zumbi).move(atualX - x, atualY - y);
    }

    private boolean noBoard(int x, int y) {
        return x >= 0 && x < TAM && y >= 0 && y < TAM;
    }

    private boolean solido(int x, int y) {
        char tipo = celulas[x][y].getTipo();
        return tipo == '1' || tipo == 'Z' || tipo == 'C' || tipo == 'G' || tipo == 'P' || tipo == 'R';
    }

    private boolean liberado(int x, int y) {
        char tipo = celulas[x][y].getTipo();
        return tipo == 'V' || tipo == 'B';
    }

    private boolean umZumbi(char tipo) {
        return tipo == 'Z' || tipo == 'C' || tipo == 'G' || tipo == 'R';
    }

    public void setDebugMode(boolean debug) {
        debugMode = debug;
        trocaVisao();
    }

    private void trocaVisao() {
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                Entidade entidade = celulas[i][j];
                if (entidade instanceof ZumbiR zumbiR) {
                    zumbiR.setVisibleInDebug(debugMode);
                }

                visivel[i][j] = debugMode || temVisao(i, j);
                if (visivel[i][j]) {
                    mem[i][j] = true;
                }
            }
        }

        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                Celula celula = (Celula) celulas[i][j].getVisual();
                celula.botaoDesign(botoes[i][j], visivel[i][j], mem[i][j], debugMode);
            }
        }
        revalidate();
        repaint();
    }

    private boolean temVisao(int x, int y) {
        if (Math.abs(x - playerX) + Math.abs(y - playerY) == 1) {
            return true;
        }

        if (x == playerX) {
            int inicio = Math.min(playerY, y);
            int fim = Math.max(playerY, y);
            for (int j = inicio + 1; j < fim; j++) {
                if (semVisao(playerX, j)) {
                    return false;
                }
            }
            return true;
        }

        if (y == playerY) {
            int inicio = Math.min(playerX, x);
            int fim = Math.max(playerX, x);
            for (int i = inicio + 1; i < fim; i++) {
                if (semVisao(i, playerY)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean semVisao(int x, int y) {
        char tipo = celulas[x][y].getTipo();
        return tipo == '1' || tipo == 'Z' || tipo == 'C' || tipo == 'G' || tipo == 'B';
    }

    private Entidade criaEntidade(char tipo, int x, int y) {
        return switch (tipo) {
            case 'P' -> player;
            case 'Z' -> new Zumbi(x, y);
            case 'C' -> new ZumbiC(x, y);
            case 'R' -> new ZumbiR(x, y);
            case 'G' -> new ZumbiG(x, y);
            case 'B' -> new Bau();
            case '1' -> Imovel.createImovel('1');
            case 'V' -> Imovel.createImovel('V');
            default -> Imovel.createImovel('V');
        };
    }

    public void saveGame(String caminho) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminho))) {
            oos.writeObject(this);
            JOptionPane.showMessageDialog(this, "Jogo salvo em " + caminho);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }

    public void loadGame(String caminho) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminho))) {
            Engine carregado = (Engine) ois.readObject();
            for (int i = 0; i < TAM; i++) {
                System.arraycopy(carregado.celulas[i], 0, this.celulas[i], 0, TAM);
                System.arraycopy(carregado.visivel[i], 0, this.visivel[i], 0, TAM);
                System.arraycopy(carregado.mem[i], 0, this.mem[i], 0, TAM);
            }
            this.playerX = carregado.playerX;
            this.playerY = carregado.playerY;
            this.debugMode = carregado.debugMode;
            this.percepcao = carregado.percepcao;
            this.player = (Player) carregado.celulas[playerX][playerY];
            removeAll();
            botoes = new JButton[TAM][TAM];
            for (int i = 0; i < TAM; i++) {
                for (int j = 0; j < TAM; j++) {
                    botoes[i][j] = addBotao(i, j);
                    add(botoes[i][j]);
                }
            }
            trocaVisao();
            revalidate();
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + e.getMessage());
        }
    }

    public String getCombateTxt() {
        return combateTxt != null ? combateTxt.toString() : "";
    }

    public Entidade getCelula(int x, int y) {
        return celulas[x][y];
    }

    public void setCelula(int x, int y, Entidade entidade) {
        celulas[x][y] = entidade;
    }

    private void verificaFim() {
        if (!verificaFimJogo) {
            return;
        }

        boolean zumbisRelevantes = false;
        int rastejantesCount = 0;

        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                Entidade entidade = celulas[i][j];
                if (entidade instanceof Zumbi || entidade instanceof ZumbiC || entidade instanceof ZumbiG) {
                    zumbisRelevantes = true;
                    break;
                } else if (entidade instanceof ZumbiR) {
                    rastejantesCount++;
                }
            }
            if (zumbisRelevantes) break;
        }

        if (!zumbisRelevantes) {
            String msgVitoria = rastejantesCount > 0 ?
                "Mapa concluído! Todos os zumbis mortos\n" :
                "Mapa concluído! Todos os zumbis mortos.";
            JOptionPane.showMessageDialog(this, msgVitoria, "Vitória", JOptionPane.INFORMATION_MESSAGE);
            new Reinicia(frame, this).mostrarOpcoes();
        }

        if (player.getVida() <= 0) {
            JOptionPane.showMessageDialog(this, "Você morreu.", "É o fim", JOptionPane.ERROR_MESSAGE);
            new Reinicia(frame, this).mostrarOpcoes();
        }
    }
}