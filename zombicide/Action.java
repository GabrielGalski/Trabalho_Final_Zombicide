package zombicide;

import java.util.Random;

public class Action {
    private static final Random random = new Random();

    public static class AttackResult {
        public final int damage;
        public final int criticalRoll;

        public AttackResult(int damage, int criticalRoll) {
            this.damage = damage;
            this.criticalRoll = criticalRoll;
        }
    }

    public static AttackResult attack(Player attacker, Entity target) {
        int damage = attacker.getAttack(); // Atualizado de getAtaque
        int criticalRoll = random.nextInt(6) + 1;
        if (criticalRoll == 6) {
            damage = 3;
        }
        return new AttackResult(damage, criticalRoll);
    }

    public static AttackResult attack(Entity attacker, Player target) {
        int damage = 1;
        int perceptionRoll = random.nextInt(5);
        if (perceptionRoll <= target.getPerception()) { 
            damage = 0;
        } else {
            int criticalRoll = random.nextInt(6) + 1;
            if (criticalRoll == 6) {
                damage = 3;
            }
        }
        return new AttackResult(damage, perceptionRoll);
    }

    public static AttackResult shoot(Player player, Entity target) {
        if (player.getBullets() > 0) { // Atualizado de getBalas
            player.useBullet(); // Atualizado de usarBala
            int criticalRoll = random.nextInt(6) + 1;
            int damage = (criticalRoll == 6) ? 3 : 2;
            return new AttackResult(damage, criticalRoll);
        }
        return new AttackResult(0, -1);
    }

    public static void heal(Player player, Engine engine) {
        if (player.getBandages() > 0) { // Atualizado de getBandagens
            int newHealth = Math.min(player.getHealth() + 2, 5); // Atualizado de getVida
            player.setHealth(newHealth); // Atualizado de setVida
            player.useBandage(); // Atualizado de usarBandagem
        }
    }

    public static String collect(Player player, Engine engine, int x, int y) {
        if (engine.getCell(x, y).getType() != 'B') {
            return "Nenhum baú aqui!";
        }

        player.incrementChestsOpened();

        if (player.getChestsOpened() == 3 && !player.hasBat()) {
            player.setHasBat(true);
            engine.setCell(x, y, Fixed.createFixed('V')); // Remove o baú
            return "Encontrou um taco de basebol! Seu ataque agora causa 2 de dano.";
        }

        int chance = random.nextInt(100); 

        if (chance < 5) {
            ZombieR zombieR = new ZombieR(x, y);
            engine.setCell(x, y, zombieR);
            engine.startCombat(x, y);
            return "Você abriu o baú, mas era uma armadilha! Um zumbi rastejante apareceu!";
        } else if (chance < 45) { // 40% de chance (5-44)
            player.addBandage();
            return "Você encontrou uma bandagem! Bandagens: " + player.getBandages();
        } else if (chance < 85) { // 40% de chance (45-84)
            player.addBullet();
            return "Você encontrou uma pistola com 1 bala! Balas: " + player.getBullets();
        } else if (chance < 100) { // 15% de chance (85-99)
            if (!player.hasBat()) {
                player.setHasBat(true);
                return "Você encontrou um taco de basebol! Seu ataque agora causa 2 de dano.";
            } else {
                player.addBandage();
                return "Encontrou uma bandagem. Bandagens: " + player.getBandages();
            }
        }

        engine.setCell(x, y, Fixed.createFixed('V')); 
        return "Você abriu o baú, mas não encontrou nada útil.";
    }
    public static boolean dodge(Player player) {
        return random.nextInt(5) < player.getPerception(); 
    }
}