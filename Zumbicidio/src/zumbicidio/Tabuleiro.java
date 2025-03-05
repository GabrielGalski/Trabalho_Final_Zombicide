package zumbicidio;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Tabuleiro extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Entidade[][] celulas;
    private transient JButton[][] botoes;
    private boolean[][] visivel;
    private boolean[][] memorizado;
    public final int TAMANHO = 10;
    private int jogadorX, jogadorY;
    private Personagem personagem;
    private boolean modoDebug = false;
    private boolean emCombate = false;
    private Entidade zumbiEmCombate = null;
    private int combateX, combateY;
    private static final Random random = new Random();
    private StringBuilder relatorioCombate;
    private final transient Zumbicidio frame;

    public Tabuleiro(Zumbicidio frame) {
        this.frame = frame;
        celulas = new Entidade[TAMANHO][TAMANHO];
        botoes = new JButton[TAMANHO][TAMANHO];
        visivel = new boolean[TAMANHO][TAMANHO];
        memorizado = new boolean[TAMANHO][TAMANHO];
        personagem = new Personagem(0, 0, 5, 3);
        setLayout(new GridLayout(TAMANHO, TAMANHO));
        inicializarTabuleiro();
    }

    public boolean isEmCombate() {
        return emCombate;
    }

    public int getJogadorX() {
        return jogadorX;
    }

    public int getJogadorY() {
        return jogadorY;
    }

    public Personagem getPersonagem() {
        return personagem;
    }

    private void inicializarTabuleiro() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                celulas[i][j] = Imovel.criarImovel('V');
                visivel[i][j] = false;
                memorizado[i][j] = false;
                botoes[i][j] = criarBotao(i, j);
                add(botoes[i][j]);
            }
        }
        jogadorX = 0;
        jogadorY = 0;
        celulas[jogadorX][jogadorY] = personagem;
    }

    public void carregarMapa(Scanner scanner) {
        removeAll();
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                char tipo = scanner.next().charAt(0);
                if (tipo == '0') tipo = 'V';
                celulas[i][j] = criarEntidade(tipo, i, j);
                visivel[i][j] = false;
                memorizado[i][j] = false;
                botoes[i][j] = criarBotao(i, j);
                add(botoes[i][j]);
                if (tipo == 'P') {
                    jogadorX = i;
                    jogadorY = j;
                    celulas[i][j] = personagem;
                }
            }
        }
        atualizarVisibilidade();
        revalidate();
        repaint();
    }

    private JButton criarBotao(int i, int j) {
        Entidade entidade = celulas[i][j];
        Celula celula = (Celula) entidade.getVisual();

        JButton botao = new JButton(String.valueOf(entidade.getTipo()));
        botao.setBackground(celula.getBackground());
        botao.setOpaque(true);
        botao.setBorderPainted(true);
        botao.addActionListener(e -> moverJogador(i, j));

        return botao;
    }

    private void moverJogador(int destinoX, int destinoY) {
        if (emCombate) return;

        int dx = Math.abs(destinoX - jogadorX);
        int dy = Math.abs(destinoY - jogadorY);
        if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
            if (destinoX >= 0 && destinoX < TAMANHO && destinoY >= 0 && destinoY < TAMANHO) {
                Entidade destino = celulas[destinoX][destinoY];
                char tipoDestino = destino.getTipo();

                if (destino instanceof ZumbiRastejante) {
                    personagem.setVida(personagem.getVida() - 1);
                    JOptionPane.showMessageDialog(this, "Você foi emboscado por um Zumbi Rastejante! Perdeu 1 de vida.", 
                                                  "Emboscada", JOptionPane.WARNING_MESSAGE);
                    iniciarCombate(destinoX, destinoY);
                } else if (ehZumbi(tipoDestino)) {
                    iniciarCombate(destinoX, destinoY);
                } else if (!ehSolido(destinoX, destinoY)) {
                    if (tipoDestino == 'B') {
                        String resultado = Agir.coletar(personagem, this, destinoX, destinoY);
                        JOptionPane.showMessageDialog(this, resultado, "Item Coletado", JOptionPane.INFORMATION_MESSAGE);
                        celulas[destinoX][destinoY] = Imovel.criarImovel('V');
                    }
                    celulas[jogadorX][jogadorY] = Imovel.criarImovel('V');
                    jogadorX = destinoX;
                    jogadorY = destinoY;
                    personagem.mover(dx, dy);
                    celulas[jogadorX][jogadorY] = personagem;
                    atualizarVisibilidade();
                    moverZumbis();
                    verificarFimDeJogo();
                }
            }
        }
    }

    private void iniciarCombate(int x, int y) {
        emCombate = true;
        zumbiEmCombate = celulas[x][y];
        combateX = x;
        combateY = y;
        relatorioCombate = new StringBuilder("Combate iniciado!\n");

        Agir.ResultadoAtaque resultadoZumbi = Agir.bater(zumbiEmCombate, personagem);
        int danoZumbi = resultadoZumbi.dano;
        
        if (danoZumbi > 0) {
            personagem.setVida(personagem.getVida() - danoZumbi);
            relatorioCombate.append("Zumbi ataca e causa ").append(danoZumbi).append(" de dano!");
            if (resultadoZumbi.dadoCritico == 6) {
                relatorioCombate.append(" (Crítico!)");
            }
            relatorioCombate.append("\n");
        } else {
            relatorioCombate.append("Zumbi ataca, mas você esquiva!").append("\n");
        }

        atualizarVisibilidade();
        JOptionPane.showMessageDialog(this, relatorioCombate.toString(), "Combate", JOptionPane.INFORMATION_MESSAGE);
    }

public void turnoPersonagem(String acao) {
    relatorioCombate = new StringBuilder(); // Reinicia o relatório

    int vidaZumbi = 0;
    boolean combateAtivo = emCombate && zumbiEmCombate != null;

    if (combateAtivo) {
        if (zumbiEmCombate instanceof ZumbiComum) {
            vidaZumbi = ((ZumbiComum) zumbiEmCombate).getVida();
        } else if (zumbiEmCombate instanceof ZumbiCorredor) {
            vidaZumbi = ((ZumbiCorredor) zumbiEmCombate).getVida();
        } else if (zumbiEmCombate instanceof ZumbiRastejante) {
            vidaZumbi = ((ZumbiRastejante) zumbiEmCombate).getVida();
        } else if (zumbiEmCombate instanceof ZumbiGigante) {
            vidaZumbi = ((ZumbiGigante) zumbiEmCombate).getVida();
        }
    }

    int dano = 0;
    switch (acao) {
        case "bater":
            if (!combateAtivo) return;
            Agir.ResultadoAtaque resultadoAtaque = Agir.bater(personagem, zumbiEmCombate);
            dano = resultadoAtaque.dano;
            vidaZumbi -= dano;
            relatorioCombate.append("Você bateu e causou ").append(dano).append(" de dano!");
            if (resultadoAtaque.dadoCritico == 6) {
                relatorioCombate.append(" (Crítico!)");
            }
            relatorioCombate.append("\n");
            break;
        case "atirar":
            if (!combateAtivo) return;
            Agir.ResultadoAtaque resultadoTiro = Agir.atirar(personagem, zumbiEmCombate);
            dano = resultadoTiro.dano;
            if (dano > 0) {
                vidaZumbi -= dano;
                relatorioCombate.append("Você atirou e causou ").append(dano).append(" de dano!");
                if (resultadoTiro.dadoCritico == 6) {
                    relatorioCombate.append(" (Crítico!)");
                }
                relatorioCombate.append("\n");
            } else {
                relatorioCombate.append("Sem balas para atirar!\n");
            }
            break;
        case "curar":
            int vidaAntes = personagem.getVida();
            Agir.heal(personagem, this);
            int vidaDepois = personagem.getVida();
            if (vidaDepois > vidaAntes) {
                relatorioCombate.append("Você usou uma bandagem e recuperou ")
                              .append(vidaDepois - vidaAntes)
                              .append(" pontos de vida!\n");
            } else {
                relatorioCombate.append("Você não tem bandagens para usar!\n");
            }
            break;
        case "moverZumbis":
            if (!combateAtivo) {
                moverZumbis();
                atualizarVisibilidade();
            }
            return;
    }

    if (combateAtivo) {
        if (zumbiEmCombate instanceof ZumbiComum) {
            ((ZumbiComum) zumbiEmCombate).setVida(vidaZumbi);
        } else if (zumbiEmCombate instanceof ZumbiCorredor) {
            ((ZumbiCorredor) zumbiEmCombate).setVida(vidaZumbi);
        } else if (zumbiEmCombate instanceof ZumbiRastejante) {
            ((ZumbiRastejante) zumbiEmCombate).setVida(vidaZumbi);
        } else if (zumbiEmCombate instanceof ZumbiGigante) {
            ((ZumbiGigante) zumbiEmCombate).setVida(vidaZumbi);
        }

        if (vidaZumbi > 0) {
            Agir.ResultadoAtaque resultadoZumbi = Agir.bater(zumbiEmCombate, personagem);
            int danoZumbi = resultadoZumbi.dano;
            
            if (danoZumbi > 0) {
                personagem.setVida(personagem.getVida() - danoZumbi);
                relatorioCombate.append("Zumbi ataca e causa ").append(danoZumbi).append(" de dano!");
                if (resultadoZumbi.dadoCritico == 6) {
                    relatorioCombate.append(" (Crítico!)");
                }
                relatorioCombate.append("\n");
            } else {
                relatorioCombate.append("Zumbi ataca, mas você esquiva!").append("\n");
            }
        } else {
            String classeZumbi = "";
            if (zumbiEmCombate instanceof ZumbiComum) classeZumbi = "comum";
            else if (zumbiEmCombate instanceof ZumbiCorredor) classeZumbi = "corredor";
            else if (zumbiEmCombate instanceof ZumbiRastejante) classeZumbi = "rastejante";
            else if (zumbiEmCombate instanceof ZumbiGigante) classeZumbi = "gigante";
            relatorioCombate.append("Zumbi ").append(classeZumbi).append(" derrotado!\n");
            celulas[combateX][combateY] = Imovel.criarImovel('V');
            emCombate = false;
            zumbiEmCombate = null;
        }

        if (personagem.getVida() <= 0) {
            relatorioCombate.append("Você foi derrotado!\n");
            emCombate = false;
            JOptionPane.showMessageDialog(this, relatorioCombate.toString(), "Fim de Jogo", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            return;
        }
    }

    atualizarVisibilidade();
    if (combateAtivo || acao.equals("curar")) { // Exibir para combate ou cura
        JOptionPane.showMessageDialog(this, relatorioCombate.toString(), 
                                      combateAtivo ? "Combate" : "Ação", JOptionPane.INFORMATION_MESSAGE);
    }
    verificarFimDeJogo();
}
    private void moverZumbis() {
        if (emCombate) return;

        Entidade[][] celulasCopia = new Entidade[TAMANHO][TAMANHO];
        for (int i = 0; i < TAMANHO; i++) {
            System.arraycopy(celulas[i], 0, celulasCopia[i], 0, TAMANHO);
        }

        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                Entidade entidade = celulasCopia[i][j];
                if (entidade instanceof Movel && ehZumbi(entidade.getTipo())) {
                    boolean isZumbiCorredor = entidade.getTipo() == 'C';
                    int distX = jogadorX - i;
                    int distY = jogadorY - j;
                    boolean moveHorizontalFirst = Math.abs(distX) >= Math.abs(distY);
                    int deltaX = Integer.signum(distX);
                    int deltaY = Integer.signum(distY);
                    int maxStep = isZumbiCorredor ? 2 : 1;
                    
                    boolean movido = false;
                    if (moveHorizontalFirst) {
                        movido = tentarMoverZumbi(i, j, deltaX * maxStep, 0, entidade, isZumbiCorredor);
                        if (!movido) {
                            movido = tentarMoverZumbi(i, j, 0, deltaY * maxStep, entidade, isZumbiCorredor);
                        }
                    } else {
                        movido = tentarMoverZumbi(i, j, 0, deltaY * maxStep, entidade, isZumbiCorredor);
                        if (!movido) {
                            movido = tentarMoverZumbi(i, j, deltaX * maxStep, 0, entidade, isZumbiCorredor);
                        }
                    }
                }
            }
        }
        atualizarVisibilidade();
        verificarFimDeJogo();
    }
    
    private boolean tentarMoverZumbi(int x, int y, int deltaX, int deltaY, Entidade zumbi, boolean isZumbiCorredor) {
        if (deltaX == 0 && deltaY == 0) return false;
        
        int maxStep = Math.max(Math.abs(deltaX), Math.abs(deltaY));
        
        for (int step = maxStep; step > 0; step--) {
            int passoX = Integer.signum(deltaX) * step;
            int passoY = Integer.signum(deltaY) * step;
            
            int novoX = x + passoX;
            int novoY = y + passoY;
            
            if (isDentroDoTabuleiro(novoX, novoY)) {
                if (novoX == jogadorX && novoY == jogadorY) {
                    iniciarCombate(x, y);
                    return true;
                }
                
                if (step == 2 && isZumbiCorredor) {
                    int interX = x + Integer.signum(deltaX);
                    int interY = y + Integer.signum(deltaY);
                    
                    if (ehSolido(interX, interY) || celulas[interX][interY].getTipo() == 'B') {
                        continue;
                    }
                }
                
                // Bloquear movimento dos zumbis para posições com baús
                if (!ehSolido(novoX, novoY) && celulas[novoX][novoY].getTipo() != 'B') {
                    celulas[x][y] = Imovel.criarImovel('V');
                    ((Movel) zumbi).mover(passoX, passoY);
                    celulas[novoX][novoY] = zumbi;
                    
                    if (Math.abs(novoX - jogadorX) + Math.abs(novoY - jogadorY) == 1) {
                        iniciarCombate(novoX, novoY);
                    }
                    return true;
                }
            }
        }
        
        int[][] direcoes = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int i = 0; i < direcoes.length; i++) {
            int j = random.nextInt(direcoes.length);
            int[] temp = direcoes[i];
            direcoes[i] = direcoes[j];
            direcoes[j] = temp;
        }
        
        for (int[] dir : direcoes) {
            int novoX = x + dir[0];
            int novoY = y + dir[1];
            
            if (isDentroDoTabuleiro(novoX, novoY) && !ehSolido(novoX, novoY) && celulas[novoX][novoY].getTipo() != 'B') {
                celulas[x][y] = Imovel.criarImovel('V');
                ((Movel) zumbi).mover(dir[0], dir[1]);
                celulas[novoX][novoY] = zumbi;
                
                if (Math.abs(novoX - jogadorX) + Math.abs(novoY - jogadorY) == 1) {
                    iniciarCombate(novoX, novoY);
                }
                return true;
            }
        }
        
        return false;
    }

    private boolean isDentroDoTabuleiro(int x, int y) {
        return x >= 0 && x < TAMANHO && y >= 0 && y < TAMANHO;
    }

    private boolean ehSolido(int x, int y) {
        char tipo = celulas[x][y].getTipo();
        return tipo == '1' || tipo == 'Z' || tipo == 'C' || tipo == 'G' || tipo == 'P' || tipo == 'R';
    }

    private boolean ehZumbi(char tipo) {
        return tipo == 'Z' || tipo == 'C' || tipo == 'G' || tipo == 'R';
    }

    public void setModoDebug(boolean debug) {
        modoDebug = debug;
        atualizarVisibilidade();
    }

   private void atualizarVisibilidade() {
    for (int i = 0; i < TAMANHO; i++) {
        for (int j = 0; j < TAMANHO; j++) {
            visivel[i][j] = modoDebug || podeVerPosicao(i, j);
            if (visivel[i][j]) {
                memorizado[i][j] = true;
            }
        }
    }
    
    for (int i = 0; i < TAMANHO; i++) {
        for (int j = 0; j < TAMANHO; j++) {
            char tipo = celulas[i][j].getTipo();
            String texto = "";
            Color background = ((Celula) celulas[i][j].getVisual()).getBackground();
            
            if (visivel[i][j]) {
                if (tipo == 'P' || tipo == 'Z' || tipo == 'C' || tipo == 'G' || tipo == 'B') {
                    texto = String.valueOf(tipo);
                } else if (tipo == 'R' && modoDebug) { // 'R' só aparece em modo DEBUG
                    texto = "R";
                }
                botoes[i][j].setText(texto);
                botoes[i][j].setBackground(background);
            } else if (memorizado[i][j]) {
                Color corEsmaecida = new Color(
                    Math.min(255, background.getRed() + 50),
                    Math.min(255, background.getGreen() + 50),
                    Math.min(255, background.getBlue() + 50)
                );
                
                if (tipo == '1' || tipo == 'V') {
                    texto = "";
                } else if (tipo == 'B') {
                    texto = "B";
                }
                
                botoes[i][j].setText(texto);
                botoes[i][j].setBackground(corEsmaecida);
            } else {
                botoes[i][j].setText("");
                botoes[i][j].setBackground(Color.WHITE);
            }
        }
    }
    revalidate();
    repaint();
}

    private boolean podeVerPosicao(int x, int y) {
        if (Math.abs(x - jogadorX) + Math.abs(y - jogadorY) <= 1) {
            return true;
        }
        
        if (x == jogadorX) {
            int inicio = Math.min(jogadorY, y);
            int fim = Math.max(jogadorY, y);
            
            for (int j = inicio + 1; j < fim; j++) {
                if (ehObstaculo(jogadorX, j)) {
                    return false;
                }
            }
            return true;
        } else if (y == jogadorY) {
            int inicio = Math.min(jogadorX, x);
            int fim = Math.max(jogadorX, x);
            
            for (int i = inicio + 1; i < fim; i++) {
                if (ehObstaculo(i, jogadorY)) {
                    return false;
                }
            }
            return true;
        }
        
        return false;
    }

    private boolean ehObstaculo(int x, int y) {
        char tipo = celulas[x][y].getTipo();
        return tipo == '1' || tipo == 'Z' || tipo == 'C' || tipo == 'G' || tipo == 'R';
    }

    private Entidade criarEntidade(char tipo, int x, int y) {
        switch (tipo) {
            case 'P': return personagem;
            case 'Z': return new ZumbiComum(x, y);
            case 'C': return new ZumbiCorredor(x, y);
            case 'R': return new ZumbiRastejante(x, y);
            case 'G': return new ZumbiGigante(x, y);
            case 'B': return new Bau();
            case '1': return Imovel.criarImovel('1');
            case 'V': return Imovel.criarImovel('V');
            default: return Imovel.criarImovel('V');
        }
    }

    public void salvarJogo(String caminho) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminho))) {
            oos.writeObject(this);
            JOptionPane.showMessageDialog(this, "Jogo salvo em " + caminho);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o jogo: " + e.getMessage());
        }
    }

    public void carregarJogo(String caminho) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminho))) {
            Tabuleiro loaded = (Tabuleiro) ois.readObject();
            for (int i = 0; i < TAMANHO; i++) {
                System.arraycopy(loaded.celulas[i], 0, this.celulas[i], 0, TAMANHO);
                System.arraycopy(loaded.visivel[i], 0, this.visivel[i], 0, TAMANHO);
                System.arraycopy(loaded.memorizado[i], 0, this.memorizado[i], 0, TAMANHO);
            }
            this.jogadorX = loaded.jogadorX;
            this.jogadorY = loaded.jogadorY;
            this.modoDebug = loaded.modoDebug;
            this.personagem = (Personagem) loaded.celulas[jogadorX][jogadorY];
            removeAll();
            botoes = new JButton[TAMANHO][TAMANHO];
            for (int i = 0; i < TAMANHO; i++) {
                for (int j = 0; j < TAMANHO; j++) {
                    botoes[i][j] = criarBotao(i, j);
                    add(botoes[i][j]);
                }
            }
            atualizarVisibilidade();
            revalidate();
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar o jogo: " + e.getMessage());
        }
    }

    public String getRelatorioCombate() {
        return relatorioCombate != null ? relatorioCombate.toString() : "";
    }

    public Entidade getCelula(int x, int y) {
        return celulas[x][y];
    }

    public void setCelula(int x, int y, Entidade entidade) {
        celulas[x][y] = entidade;
    }

    private void verificarFimDeJogo() {
        boolean zumbisRestantes = false;
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                Entidade entidade = celulas[i][j];
                if (entidade instanceof ZumbiComum || entidade instanceof ZumbiCorredor || 
                    entidade instanceof ZumbiRastejante || entidade instanceof ZumbiGigante) {
                    zumbisRestantes = true;
                    break;
                }
            }
            if (zumbisRestantes) break;
        }

        if (!zumbisRestantes) {
            JOptionPane.showMessageDialog(this, "Mapa Finalizado! Você exterminou todos os zumbis.", 
                                          "Vitória", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        }
    }
}