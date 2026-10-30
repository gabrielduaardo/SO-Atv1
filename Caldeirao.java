package Selvagens;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Recurso compartilhado: o caldeirão com M porções.
 *
 * Semáforos:
 *  - mutex (1) : só um selvagem se serve por vez (região crítica = variável porcoes)
 *  - vazio (0) : selvagem acorda o cozinheiro
 *  - cheio (0) : cozinheiro avisa que encheu
 *
 * Prevenção de deadlock: o cozinheiro nunca usa o mutex, então sempre consegue
 * encher e liberar o selvagem que espera em "cheio".
 */
public class Caldeirao {
    private final int capacidadeCaldeirao;
    private int porcoesRestantes = 0;

    private final Semaphore mutexAcesso = new Semaphore(1);
    private final Semaphore caldeiraoVazio = new Semaphore(0);
    private final Semaphore caldeiraoCheio = new Semaphore(0);

    // Métricas de validação
    private volatile boolean ficouNegativo = false;
    private volatile boolean encheuSemEstarVazio = false;
    private final AtomicInteger servidas = new AtomicInteger();
    private final AtomicInteger recargas = new AtomicInteger();

    public Caldeirao(int capacidade) {
        this.capacidadeCaldeirao = capacidade;
    }

    /** Código do selvagem. */
    public void servir(int identificadorSelvagem) throws InterruptedException {
        mutexAcesso.acquire();              // entra na região crítica
        if (porcoesRestantes == 0) {
            System.out.println("Selvagem " + identificadorSelvagem + ": caldeirao vazio, acorda o cozinheiro");
            caldeiraoVazio.release();       // acorda o cozinheiro
            caldeiraoCheio.acquire();      // espera encher
        }
        porcoesRestantes--;
        if (porcoesRestantes < 0) ficouNegativo = true;
        servidas.incrementAndGet();
        System.out.println("Selvagem " + identificadorSelvagem + ": se serviu (restam " + porcoesRestantes + ")");
        mutexAcesso.release();              // sai da região crítica
    }

    /** Código do cozinheiro. */
    public void encher() throws InterruptedException {
        caldeiraoVazio.acquire();           // dorme até ser acordado
        if (porcoesRestantes != 0) encheuSemEstarVazio = true;
        porcoesRestantes = capacidadeCaldeirao;
        recargas.incrementAndGet();
        System.out.println("Cozinheiro: encheu o caldeirao com " + capacidadeCaldeirao);
        caldeiraoCheio.release();          // libera o selvagem
    }

    public boolean isFicouNegativo()       { return ficouNegativo; }
    public boolean isEncheuSemEstarVazio() { return encheuSemEstarVazio; }
    public int getServidas()               { return servidas.get(); }
    public int getRecargas()               { return recargas.get(); }
}
