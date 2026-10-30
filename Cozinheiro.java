package Selvagens;

/** Thread do cozinheiro: laço infinito, dorme até o caldeirão esvaziar. */
public class Cozinheiro extends Thread {
    private final Caldeirao caldeirao;

    public Cozinheiro(Caldeirao caldeirao) {
        super("Cozinheiro");
        this.caldeirao = caldeirao;
    }

    @Override
    public void run() {
        try {
            while (true) {
                caldeirao.encher();
            }
        } catch (InterruptedException e) {
            System.out.println("Cozinheiro: fim do jantar.");
        }
    }
}
