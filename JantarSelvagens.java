package Selvagens;

/** Classe principal (main) do Jantar dos Selvagens, com validação automática. */
public class JantarSelvagens {

    static boolean cenario(int capacidadeCaldeirao, int quantidadeSelvagens, int quantidadeRefeicoesPorSelvagem) throws InterruptedException {
        System.out.println("\n==================== capacidade=" + capacidadeCaldeirao
                + ", selvagens=" + quantidadeSelvagens + ", vezes=" + quantidadeRefeicoesPorSelvagem + " ====================");

        Caldeirao caldeirao = new Caldeirao(capacidadeCaldeirao);
        Cozinheiro cozinheiro = new Cozinheiro(caldeirao);
        cozinheiro.start();

        Selvagem[] selvagens = new Selvagem[quantidadeSelvagens];
        for (int indiceSelvagem = 0; indiceSelvagem < quantidadeSelvagens; indiceSelvagem++) {
            selvagens[indiceSelvagem] = new Selvagem(caldeirao, indiceSelvagem + 1, quantidadeRefeicoesPorSelvagem);
            selvagens[indiceSelvagem].start();
        }

        boolean semDeadlock = true;
        for (Selvagem selvagem : selvagens) {
            selvagem.join(20000);
            if (selvagem.isAlive()) {
                semDeadlock = false;
                selvagem.interrupt();
            }
        }
        cozinheiro.interrupt();
        cozinheiro.join();

        int totalPorcoesEsperadas = quantidadeSelvagens * quantidadeRefeicoesPorSelvagem;
        int recargasEsperadas = (totalPorcoesEsperadas + capacidadeCaldeirao - 1) / capacidadeCaldeirao;
        boolean porcoesServidasCorretas = caldeirao.getServidas() == totalPorcoesEsperadas;
        boolean naoFicouNegativo = !caldeirao.isFicouNegativo();
        boolean cozinheiroNaoEncheuComCaldeiraoCheio = !caldeirao.isEncheuSemEstarVazio();
        boolean recargasCorretas = caldeirao.getRecargas() == recargasEsperadas;

        System.out.println("\n--- Validacao ---");
        System.out.println("Porcoes servidas: " + caldeirao.getServidas() + " (esperado " + totalPorcoesEsperadas + ") -> " + ok(porcoesServidasCorretas));
        System.out.println("Porcoes nunca negativas -> " + ok(naoFicouNegativo));
        System.out.println("Cozinheiro so encheu vazio -> " + ok(cozinheiroNaoEncheuComCaldeiraoCheio));
        System.out.println("Recargas: " + caldeirao.getRecargas() + " (esperado " + recargasEsperadas + ") -> " + ok(recargasCorretas));
        System.out.println("Sem deadlock -> " + ok(semDeadlock));
        return porcoesServidasCorretas && naoFicouNegativo && cozinheiroNaoEncheuComCaldeiraoCheio && recargasCorretas && semDeadlock;
    }

    static String ok(boolean valor) { return valor ? "OK" : "FALHOU"; }

    public static void main(String[] args) throws InterruptedException {
        boolean tudoOk = true;
        tudoOk &= cenario(5, 3, 4);      // curto
        tudoOk &= cenario(3, 10, 20);    // estresse
        System.out.println("\n==================== RESULTADO FINAL ====================");
        System.out.println(tudoOk ? "TODOS OS TESTES PASSARAM" : "HOUVE FALHA EM ALGUM TESTE");
    }
}
