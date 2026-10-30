package Filosofos;

/** Classe principal (main) do Jantar dos Filósofos, com validação automática. */
public class JantarFilosofos {

    static boolean cenario(int quantidadeRefeicoesPorFilosofo) throws InterruptedException {
        System.out.println("\n==================== " + quantidadeRefeicoesPorFilosofo + " refeicoes por filosofo ====================");
        Mesa mesa = new Mesa();
        Filosofo[] filosofos = new Filosofo[Mesa.QUANTIDADE_DE_FILOSOFOS];
        for (int indiceFilosofo = 0; indiceFilosofo < Mesa.QUANTIDADE_DE_FILOSOFOS; indiceFilosofo++) {
            filosofos[indiceFilosofo] = new Filosofo(mesa, indiceFilosofo, quantidadeRefeicoesPorFilosofo);
            filosofos[indiceFilosofo].start();
        }

        boolean semDeadlock = true;
        for (Filosofo filosofo : filosofos) {
            filosofo.join(20000);
            if (filosofo.isAlive()) {
                semDeadlock = false;
                filosofo.interrupt();
            }
        }

        boolean totalRefeicoesCorretas = mesa.getRefeicoes() == Mesa.QUANTIDADE_DE_FILOSOFOS * quantidadeRefeicoesPorFilosofo;
        boolean vizinhosNaoComeramJuntos = !mesa.isVizinhosJuntos();

        System.out.println("\n--- Validacao ---");
        System.out.println("Refeicoes: " + mesa.getRefeicoes() + " (esperado " + Mesa.QUANTIDADE_DE_FILOSOFOS * quantidadeRefeicoesPorFilosofo + ") -> " + ok(totalRefeicoesCorretas));
        System.out.println("Vizinhos nunca comeram juntos -> " + ok(vizinhosNaoComeramJuntos));
        System.out.println("Sem deadlock -> " + ok(semDeadlock));
        return totalRefeicoesCorretas && vizinhosNaoComeramJuntos && semDeadlock;
    }

    static String ok(boolean valor) { return valor ? "OK" : "FALHOU"; }

    public static void main(String[] args) throws InterruptedException {
        boolean tudoOk = true;
        tudoOk &= cenario(3);     // curto, para conferir a saída
        tudoOk &= cenario(50);    // estresse
        System.out.println("\n==================== RESULTADO FINAL ====================");
        System.out.println(tudoOk ? "TODOS OS TESTES PASSARAM" : "HOUVE FALHA EM ALGUM TESTE");
    }
}
