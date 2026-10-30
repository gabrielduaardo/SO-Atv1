package Selvagens;

/** Thread de um selvagem: se serve (exclusivo) e come (em paralelo). */
public class Selvagem extends Thread {
    private final Caldeirao caldeirao;
    private final int identificadorSelvagem;
    private final int quantidadeRefeicoes;

    public Selvagem(Caldeirao caldeirao, int identificadorSelvagem, int quantidadeRefeicoes) {
        super("Selvagem-" + identificadorSelvagem);
        this.caldeirao = caldeirao;
        this.identificadorSelvagem = identificadorSelvagem;
        this.quantidadeRefeicoes = quantidadeRefeicoes;
    }

    @Override
    public void run() {
        try {
            for (int indiceRefeicao = 0; indiceRefeicao < quantidadeRefeicoes; indiceRefeicao++) {
                caldeirao.servir(identificadorSelvagem);
                Thread.sleep((int) (Math.random() * 50)); // come fora do mutex
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
