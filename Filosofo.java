package Filosofos;

/** Thread de um filósofo: alterna entre meditar e comer. */
public class Filosofo extends Thread {
    private final Mesa mesa;
    private final int identificadorFilosofo;
    private final int quantidadeRefeicoes;

    public Filosofo(Mesa mesa, int identificadorFilosofo, int quantidadeRefeicoes) {
        super("Filosofo-" + identificadorFilosofo);
        this.mesa = mesa;
        this.identificadorFilosofo = identificadorFilosofo;
        this.quantidadeRefeicoes = quantidadeRefeicoes;
    }

    @Override
    public void run() {
        try {
            for (int indiceRefeicao = 0; indiceRefeicao < quantidadeRefeicoes; indiceRefeicao++) {
                System.out.println("Filosofo " + identificadorFilosofo + ": meditando");
                Thread.sleep((int) (Math.random() * 50));
                mesa.pegarPalitos(identificadorFilosofo);
                mesa.comer(identificadorFilosofo); // região crítica
                mesa.soltarPalitos(identificadorFilosofo);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
