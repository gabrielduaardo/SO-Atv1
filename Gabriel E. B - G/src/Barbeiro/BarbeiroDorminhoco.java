package Barbeiro;

/**
 * Classe principal (main) do problema do Barbeiro Dorminhoco.
 * Executa cenários e valida automaticamente as propriedades de sincronização.
 */
public class BarbeiroDorminhoco {

    static boolean cenario(String nome, int cadeiras, int clientes, int maxIntervaloMs) throws InterruptedException {
        System.out.println("\n==================== " + nome + " ====================");
        System.out.println("cadeiras=" + cadeiras + ", clientes=" + clientes);

        Barbearia barbearia = new Barbearia(cadeiras, clientes);
        Barbeiro barbeiro = new Barbeiro(barbearia);
        barbeiro.start();

        Cliente[] cs = new Cliente[clientes];
        for (int i = 0; i < clientes; i++) {
            cs[i] = new Cliente(barbearia, i + 1);
            cs[i].start();
            Thread.sleep((int) (Math.random() * maxIntervaloMs)); // chegadas em instantes aleatórios
        }

        // todo cliente deve ser atendido ou recusado dentro do limite (senão há deadlock)
        boolean semDeadlock = true;
        for (Cliente c : cs) {
            c.join(20000);
            if (c.isAlive()) { semDeadlock = false; c.interrupt(); }
        }
        if (semDeadlock) {
            semDeadlock = barbearia.aguardarTodos(20000);
        }
        barbeiro.interrupt();
        barbeiro.join();

        boolean total = barbearia.getAtendidos() + barbearia.getRecusados() == clientes;
        boolean espera = barbearia.getMaxEspera() <= barbearia.getCadeiras();
        boolean um = barbearia.getMaxCortando() <= 1;
        boolean vazia = barbearia.getWaiting() == 0;

        System.out.println("\n--- Validação: " + nome + " ---");
        System.out.println("Atendidos + recusados == clientes : " + barbearia.getAtendidos() + " + "
                + barbearia.getRecusados() + " == " + clientes + "  -> " + ok(total));
        System.out.println("Máx. em espera <= cadeiras        : " + barbearia.getMaxEspera() + " <= "
                + barbearia.getCadeiras() + "  -> " + ok(espera));
        System.out.println("Máx. cortes simultâneos <= 1      : " + barbearia.getMaxCortando() + "  -> " + ok(um));
        System.out.println("Sala de espera vazia ao final     : waiting=" + barbearia.getWaiting() + "  -> " + ok(vazia));
        System.out.println("Sem deadlock (todos terminaram)   : " + ok(semDeadlock));
        return total && espera && um && vazia && semDeadlock;
    }

    static String ok(boolean b) { return b ? "OK" : "FALHOU"; }

    public static void main(String[] args) throws InterruptedException {
        boolean tudo = true;

        // Cenário 1: chegadas lentas -> barbeiro dorme entre clientes, ninguém é recusado
        tudo &= cenario("Cenário 1 - chegadas lentas (barbeiro dorme)", 3, 5, 400);

        // Cenário 2: chegadas rápidas e poucas cadeiras -> clientes são recusados
        tudo &= cenario("Cenário 2 - rajada de clientes, 2 cadeiras", 2, 12, 30);

        // Cenário 3: estresse com muitos clientes
        tudo &= cenario("Cenário 3 - estresse, 3 cadeiras, 30 clientes", 3, 30, 40);

        System.out.println("\n==================== RESULTADO FINAL ====================");
        System.out.println(tudo ? "TODOS OS TESTES PASSARAM" : "HOUVE FALHA EM ALGUM TESTE");
    }
}
