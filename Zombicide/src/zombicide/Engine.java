package zombicide;

import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Engine extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Entity[][] cells;
    private transient JButton[][] buttons;
    private boolean[][] visible;
    private boolean[][] memorized;
    public final int TAM = 10;
    private int playerX, playerY;
    private Player player;
    private boolean debugMode = false;
    private boolean inCombat = false;
    private Entity combatZombie = null;
    private int combatX, combatY;
    private StringBuilder combatLog;
    private final transient Zombicide frame;
    private int perception;

    public Engine(Zombicide frame) {
        this.frame = frame;
        cells = new Entity[TAM][TAM];
        buttons = new JButton[TAM][TAM];
        visible = new boolean[TAM][TAM];
        memorized = new boolean[TAM][TAM];
        player = new Player(0, 0, 5, 3);
        setLayout(new GridLayout(TAM, TAM));
        initBoard();
    }

    public int getPerception() {
        return perception;
    }

    public void setPerception(int perception) {
        this.perception = perception;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public boolean isInCombat() {
        return inCombat;
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

    private void initBoard() {
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                cells[i][j] = Fixed.createFixed('V');
                visible[i][j] = false;
                memorized[i][j] = false;
                buttons[i][j] = createButton(i, j);
                add(buttons[i][j]);
            }
        }
        playerX = 0;
        playerY = 0;
        cells[playerX][playerY] = player;
    }

    public void loadMap(Scanner scanner) {
        removeAll();
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                char type = scanner.next().charAt(0);
                if (type == '0') type = 'V';
                cells[i][j] = createEntity(type, i, j);
                visible[i][j] = false;
                memorized[i][j] = false;
                buttons[i][j] = createButton(i, j);
                add(buttons[i][j]);
                if (type == 'P') {
                    playerX = i;
                    playerY = j;
                    cells[i][j] = player;
                }
            }
        }
        updateVisibility();
        revalidate();
        repaint();
    }

    private JButton createButton(int i, int j) {
        Entity entity = cells[i][j];
        Cell cell = (Cell) entity.getVisual();

        JButton button = new JButton();
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.addActionListener(e -> movePlayer(i, j));
        button.setPreferredSize(new java.awt.Dimension(80, 80));

        cell.configureButton(button, false, false, debugMode);
        return button;
    }

    private void movePlayer(int destX, int destY) {
        int dx = Math.abs(destX - playerX);
        int dy = Math.abs(destY - playerY);
        if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
            if (destX >= 0 && destX < TAM && destY >= 0 && destY < TAM) {
                Entity destination = cells[destX][destY];
                char destType = destination.getType();

                if (inCombat) {
                    StringBuilder fleeLog = new StringBuilder("Você tenta fugir do combate!\n");
                    Action.AttackResult zombieResult = Action.attack(combatZombie, player);
                    int zombieDamage = zombieResult.damage;
                    if (zombieDamage == 0) {
                        zombieDamage = 1;
                    }
                    player.setHealth(player.getHealth() - zombieDamage);
                    fleeLog.append("O zumbi ataca enquanto você foge e causa ")
                            .append(zombieDamage)
                            .append(" de dano!\n");

                    if (player.getHealth() <= 0) {
                        fleeLog.append("Morreu ao tentar fugir!\n");
                        inCombat = false;
                        combatZombie = null;
                        JOptionPane.showMessageDialog(this, fleeLog.toString(), "Fim de Jogo", JOptionPane.ERROR_MESSAGE);
                        frame.dispose();
                        return;
                    }

                    inCombat = false;
                    combatZombie = null;
                    fleeLog.append("Você conseguiu escapar do combate!\n");
                    JOptionPane.showMessageDialog(this, fleeLog.toString(), "Fuga", JOptionPane.INFORMATION_MESSAGE);
                }

                if (destination instanceof ZombieR) {
                    player.setHealth(player.getHealth() - 1);
                    JOptionPane.showMessageDialog(this, "Você foi emboscado por um Zumbi Rastejante! Perdeu 1 de vida.",
                                                  "Emboscada", JOptionPane.WARNING_MESSAGE);
                    startCombat(destX, destY);
                } else if (isZombie(destType)) {
                    startCombat(destX, destY);
                } else if (!isSolid(destX, destY)) {
                    if (destType == 'B') {
                        String result = Action.collect(player, this, destX, destY);
                        JOptionPane.showMessageDialog(this, result, "Item Coletado", JOptionPane.INFORMATION_MESSAGE);
                        cells[destX][destY] = Fixed.createFixed('V');
                    }
                    cells[playerX][playerY] = Fixed.createFixed('V');
                    playerX = destX;
                    playerY = destY;
                    player.move(dx, dy);
                    cells[playerX][playerY] = player;
                    updateVisibility();
                    moveZombies();
                    checkGameEnd();
                }
            }
        }
    }

    void startCombat(int x, int y) {
        inCombat = true;
        combatZombie = cells[x][y];
        combatX = x;
        combatY = y;
        combatLog = new StringBuilder("Combate iniciado!\n");

        Action.AttackResult zombieResult = Action.attack(combatZombie, player);
        int zombieDamage = zombieResult.damage;

        if (zombieDamage > 0) {
            player.setHealth(player.getHealth() - zombieDamage);
            combatLog.append("Zumbi ataca e causa ").append(zombieDamage).append(" de dano!");
            if (zombieResult.criticalRoll == 6) {
                combatLog.append(" (Crítico!)");
            }
            combatLog.append("\n");
        } else {
            combatLog.append("Zumbi ataca, mas você esquiva!\n");
        }

        updateVisibility();
        JOptionPane.showMessageDialog(this, combatLog.toString(), "Combate", JOptionPane.INFORMATION_MESSAGE);
    }

    public void playerTurn(String action) {
        combatLog = new StringBuilder();

        int zombieHealth = 0;
        boolean combatActive = inCombat && combatZombie != null;

        if (combatActive) {
            if (combatZombie instanceof Zombie) {
                zombieHealth = ((Zombie) combatZombie).getHealth();
            } else if (combatZombie instanceof ZombieC) {
                zombieHealth = ((ZombieC) combatZombie).getHealth();
            } else if (combatZombie instanceof ZombieR) {
                zombieHealth = ((ZombieR) combatZombie).getHealth();
            } else if (combatZombie instanceof ZombieG) {
                zombieHealth = ((ZombieG) combatZombie).getHealth();
            }
        }

        int damage = 0;
        switch (action) {
            case "bater":
                if (!combatActive) return;
                Action.AttackResult attackResult = Action.attack(player, combatZombie);
                damage = attackResult.damage;

                if (combatZombie instanceof ZombieG && damage < 2) {
                    damage = 0;
                    combatLog.append("Seu golpe não surtiu efeito!\n");
                } else if (damage > 0) {
                    zombieHealth -= damage;
                    combatLog.append("Você bateu e causou ").append(damage).append(" de dano!");
                    if (attackResult.criticalRoll == 6) {
                        combatLog.append(" (Crítico!)");
                    }
                    combatLog.append("\n");
                }
                break;

            case "atirar":
                if (!combatActive) return;
                Action.AttackResult shootResult = Action.shoot(player, combatZombie);
                damage = shootResult.damage;

                if (damage > 0) {
                    if (combatZombie instanceof ZombieC) {
                        combatLog.append("Zumbi corredor desviou!\n");
                        damage = 0;
                    } else {
                        zombieHealth -= damage;
                        combatLog.append("Você atirou e causou ").append(damage).append(" de dano!");
                        if (shootResult.criticalRoll == 6) {
                            combatLog.append(" (Crítico!)");
                        }
                        combatLog.append("\n");
                    }
                } else {
                    combatLog.append("Sem balas para atirar!\n");
                }
                break;

            case "curar":
                int healthBefore = player.getHealth();
                Action.heal(player, this);
                int healthAfter = player.getHealth();
                if (healthAfter > healthBefore) {
                    combatLog.append("Você usou uma bandagem e recuperou ")
                             .append(healthAfter - healthBefore)
                             .append(" pontos de vida!\n");
                } else {
                    combatLog.append("Você tentou usar uma bandagem, mas já está com a vida máxima.\n");
                }
                break;

            case "moveZombies":
                if (!combatActive) {
                    moveZombies();
                    updateVisibility();
                }
                return;
        }

        if (combatActive) {
            if (combatZombie instanceof Zombie) {
                ((Zombie) combatZombie).setHealth(zombieHealth);
            } else if (combatZombie instanceof ZombieC) {
                ((ZombieC) combatZombie).setHealth(zombieHealth);
            } else if (combatZombie instanceof ZombieR) {
                ((ZombieR) combatZombie).setHealth(zombieHealth);
            } else if (combatZombie instanceof ZombieG) {
                ((ZombieG) combatZombie).setHealth(zombieHealth);
            }

            if (zombieHealth <= 0) {
                String zombieClass = "";
                if (combatZombie instanceof Zombie) zombieClass = "comum";
                else if (combatZombie instanceof ZombieC) zombieClass = "corredor";
                else if (combatZombie instanceof ZombieR) zombieClass = "rastejante";
                else if (combatZombie instanceof ZombieG) zombieClass = "gigante";
                combatLog.append("Zumbi ").append(zombieClass).append(" derrotado!\n");
                cells[combatX][combatY] = Fixed.createFixed('V');
                inCombat = false;
                combatZombie = null;
            } else {
                Action.AttackResult zombieResult = Action.attack(combatZombie, player);
                int zombieDamage = zombieResult.damage;
                if (zombieDamage > 0) {
                    player.setHealth(player.getHealth() - zombieDamage);
                    combatLog.append("Zumbi ataca e causa ").append(zombieDamage).append(" de dano!");
                    if (zombieResult.criticalRoll == 6) {
                        combatLog.append(" (Crítico!)");
                    }
                    combatLog.append("\n");
                } else {
                    combatLog.append("Zumbi ataca, mas você esquiva!\n");
                }
            }

            if (player.getHealth() <= 0) {
                combatLog.append("Você foi derrotado!\n");
                inCombat = false;
                combatZombie = null;
                JOptionPane.showMessageDialog(this, combatLog.toString(), "Fim de Jogo", JOptionPane.ERROR_MESSAGE);
                frame.dispose();
                return;
            }
        }

        updateVisibility();
        if (combatActive || action.equals("curar")) {
            JOptionPane.showMessageDialog(this, combatLog.toString(),
                                          combatActive ? "Combate" : "Ação", JOptionPane.INFORMATION_MESSAGE);
        }
        checkGameEnd();
    }

    private void moveZombies() {
        if (inCombat) return;

        Entity[][] cellsCopy = new Entity[TAM][TAM];
        for (int i = 0; i < TAM; i++) {
            System.arraycopy(cells[i], 0, cellsCopy[i], 0, TAM);
        }

        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                Entity entity = cellsCopy[i][j];
                if (entity instanceof Mobile && isZombie(entity.getType()) && !(entity instanceof ZombieG)) {
                    int maxSteps = (entity.getType() == 'C') ? 2 : 1;
                    tryMoveZombie(i, j, entity, maxSteps);
                }
            }
        }
        updateVisibility();
        checkGameEnd();
    }

    private void tryMoveZombie(int x, int y, Entity zombie, int maxSteps) {
        boolean isOriginalPositionChest = cells[x][y].getType() == 'B';

        // Calcula a direção para o jogador
        int distX = playerX - x;
        int distY = playerY - y;
        int signX = Integer.signum(distX);
        int signY = Integer.signum(distY);
        boolean horizontalPriority = Math.abs(distX) >= Math.abs(distY);

        // Verifica se o jogador já está a 1 casa de distância
        if (Math.abs(distX) + Math.abs(distY) == 1) {
            startCombat(x, y);
            return;
        }

        // Rastreia os passos dados
        int stepsTaken = 0;
        int currentX = x;
        int currentY = y;

        // Movimentação ortogonal (sem diagonais)
        while (stepsTaken < maxSteps) {
            int nextX = currentX;
            int nextY = currentY;

            // Tenta a direção prioritária primeiro
            if (horizontalPriority) {
                nextX = currentX + signX;
            } else {
                nextY = currentY + signY;
            }

            // Verifica se a próxima posição é válida
            if (!isWithinBoard(nextX, nextY)) {
                break; // Para se sair do tabuleiro
            }

            // Verifica se atingiria o jogador
            if (nextX == playerX && nextY == playerY) {
                startCombat(currentX, currentY); // Inicia combate na posição atual
                return;
            }

            // Verifica se o caminho é passável
            if (isPassable(nextX, nextY)) {
                // Move para a próxima posição
                if (stepsTaken == 0) {
                    // Primeiro passo, atualiza a posição original
                    if (isOriginalPositionChest) {
                        cells[x][y] = new Chest();
                    } else {
                        cells[x][y] = Fixed.createFixed('V');
                    }
                } else {
                    // Passos subsequentes, atualiza a posição atual
                    cells[currentX][currentY] = Fixed.createFixed('V');
                }

                // Atualiza a posição do zumbi
                currentX = nextX;
                currentY = nextY;
                stepsTaken++;

                // Coloca o zumbi na nova posição
                cells[currentX][currentY] = zombie;

                // Verifica se está adjacente ao jogador após o movimento
                if (Math.abs(currentX - playerX) + Math.abs(currentY - playerY) == 1) {
                    startCombat(currentX, currentY);
                    return;
                }
            } else {
                // Caminho bloqueado, tenta a direção secundária
                if (horizontalPriority) {
                    nextX = currentX;
                    nextY = currentY + signY;
                } else {
                    nextX = currentX + signX;
                    nextY = currentY;
                }

                // Verifica se o caminho alternativo é válido
                if (!isWithinBoard(nextX, nextY)) {
                    break; // Para se sair do tabuleiro
                }

                // Verifica se atingiria o jogador
                if (nextX == playerX && nextY == playerY) {
                    startCombat(currentX, currentY); // Inicia combate na posição atual
                    return;
                }

                // Verifica se o caminho alternativo é passável
                if (isPassable(nextX, nextY)) {
                    // Move para a próxima posição
                    if (stepsTaken == 0) {
                        // Primeiro passo, atualiza a posição original
                        if (isOriginalPositionChest) {
                            cells[x][y] = new Chest();
                        } else {
                            cells[x][y] = Fixed.createFixed('V');
                        }
                    } else {
                        // Passos subsequentes, atualiza a posição atual
                        cells[currentX][currentY] = Fixed.createFixed('V');
                    }

                    // Atualiza a posição do zumbi
                    currentX = nextX;
                    currentY = nextY;
                    stepsTaken++;

                    // Coloca o zumbi na nova posição
                    cells[currentX][currentY] = zombie;

                    // Verifica se está adjacente ao jogador após o movimento
                    if (Math.abs(currentX - playerX) + Math.abs(currentY - playerY) == 1) {
                        startCombat(currentX, currentY);
                        return;
                    }
                } else {
                    // Ambas as direções estão bloqueadas
                    break;
                }
            }
        }

        // Se não se moveu (ainda na posição original), não faz nada
        if (currentX == x && currentY == y) {
            return;
        }

        // Atualiza a posição interna do zumbi
        ((Mobile) zombie).move(currentX - x, currentY - y);
    }

    private boolean isWithinBoard(int x, int y) {
        return x >= 0 && x < TAM && y >= 0 && y < TAM;
    }

    private boolean isSolid(int x, int y) {
        char type = cells[x][y].getType();
        return type == '1' || type == 'Z' || type == 'C' || type == 'G' || type == 'P' || type == 'R';
    }

    private boolean isPassable(int x, int y) {
        char type = cells[x][y].getType();
        return type == 'V' || type == 'B';
    }

    private boolean isZombie(char type) {
        return type == 'Z' || type == 'C' || type == 'G' || type == 'R';
    }

    public void setDebugMode(boolean debug) {
        debugMode = debug;
        updateVisibility();
    }

    private void updateVisibility() {
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                Entity entity = cells[i][j];
                if (entity instanceof ZombieR) {
                    ((ZombieR) entity).setVisibleInDebug(debugMode);
                }

                visible[i][j] = debugMode || canSeePosition(i, j);
                if (visible[i][j]) {
                    memorized[i][j] = true;
                }
            }
        }

        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                Cell cell = (Cell) cells[i][j].getVisual();
                cell.configureButton(buttons[i][j], visible[i][j], memorized[i][j], debugMode);
            }
        }
        revalidate();
        repaint();
    }

    private boolean canSeePosition(int x, int y) {
        if (Math.abs(x - playerX) + Math.abs(y - playerY) <= 1) {
            return true;
        }

        if (x == playerX) {
            int start = Math.min(playerY, y);
            int end = Math.max(playerY, y);
            for (int j = start + 1; j < end; j++) {
                if (blocksView(playerX, j)) {
                    return false;
                }
            }
            return true;
        } else if (y == playerY) {
            int start = Math.min(playerX, x);
            int end = Math.max(playerX, x);
            for (int i = start + 1; i < end; i++) {
                if (blocksView(i, playerY)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean blocksView(int x, int y) {
        char type = cells[x][y].getType();
        return type == '1' || type == 'Z' || type == 'C' || type == 'G' || type == 'B';
    }

    private Entity createEntity(char type, int x, int y) {
        switch (type) {
            case 'P': return player;
            case 'Z': return new Zombie(x, y);
            case 'C': return new ZombieC(x, y);
            case 'R': return new ZombieR(x, y);
            case 'G': return new ZombieG(x, y);
            case 'B': return new Chest();
            case '1': return Fixed.createFixed('1');
            case 'V': return Fixed.createFixed('V');
            default: return Fixed.createFixed('V');
        }
    }

    public void saveGame(String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(this);
            JOptionPane.showMessageDialog(this, "Jogo salvo em " + path);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o jogo: " + e.getMessage());
        }
    }

    public void loadGame(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Engine loaded = (Engine) ois.readObject();
            for (int i = 0; i < TAM; i++) {
                System.arraycopy(loaded.cells[i], 0, this.cells[i], 0, TAM);
                System.arraycopy(loaded.visible[i], 0, this.visible[i], 0, TAM);
                System.arraycopy(loaded.memorized[i], 0, this.memorized[i], 0, TAM);
            }
            this.playerX = loaded.playerX;
            this.playerY = loaded.playerY;
            this.debugMode = loaded.debugMode;
            this.perception = loaded.perception;
            this.player = (Player) loaded.cells[playerX][playerY];
            removeAll();
            buttons = new JButton[TAM][TAM];
            for (int i = 0; i < TAM; i++) {
                for (int j = 0; j < TAM; j++) {
                    buttons[i][j] = createButton(i, j);
                    add(buttons[i][j]);
                }
            }
            updateVisibility();
            revalidate();
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar o jogo: " + e.getMessage());
        }
    }

    public String getCombatLog() {
        return combatLog != null ? combatLog.toString() : "";
    }

    public Entity getCell(int x, int y) {
        return cells[x][y];
    }

    public void setCell(int x, int y, Entity entity) {
        cells[x][y] = entity;
    }

    private void checkGameEnd() {
        boolean significantZombiesRemaining = false;
        int rastejantesCount = 0;

        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                Entity entity = cells[i][j];
                if (entity instanceof Zombie || entity instanceof ZombieC || entity instanceof ZombieG) {
                    significantZombiesRemaining = true;
                    break;
                } else if (entity instanceof ZombieR) {
                    rastejantesCount++;
                }
            }
            if (significantZombiesRemaining) break;
        }
        if (!significantZombiesRemaining) {
            String victoryMessage;
            if (rastejantesCount > 0) {
                victoryMessage = "Mapa Finalizado! Você exterminou todos os zumbis\n";
            } else {
                victoryMessage = "Mapa Finalizado! Você exterminou todos os zumbis.";
            }
            JOptionPane.showMessageDialog(this, victoryMessage, "Vitória", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        }
    }
}