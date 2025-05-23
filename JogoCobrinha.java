import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class JogoCobrinha extends JFrame {

    // Constantes que definem a largura e altura do painel do jogo
    private static final int LARGURA = 600;
    private static final int ALTURA = 600;
    private static final int TAMANHO_BLOCO = 25;

    public JogoCobrinha() {
        this.setTitle("Jogo da Cobrinha 🐍");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(new GamePanel());
        this.pack();
        this.setLocationRelativeTo(null); // Centraliza a janela na tela
        this.setVisible(true);
    }

    // Painel principal do jogo, onde tudo é desenhado
    class GamePanel extends JPanel implements ActionListener, KeyListener {

        // A cobra e a maçã são objetos que serão manipulados ao longo do jogo
        private Snake snake;
        private Apple apple;

        // Timer para controlar a velocidade do jogo (a cada 100ms, o jogo avança)
        private final Timer timer;

        private char direcao = 'D'; // Direção inicial da cobra (direita)

        public GamePanel() {
            // Configura o painel de jogo
            this.setPreferredSize(new Dimension(LARGURA, ALTURA));
            this.setBackground(Color.black);
            this.setFocusable(true);
            this.addKeyListener(this);

            // Inicializa a cobra e a maçã
            snake = new Snake(LARGURA, ALTURA, TAMANHO_BLOCO);
            apple = new Apple(LARGURA, ALTURA, TAMANHO_BLOCO);

            // Inicia o timer que chama o actionPerformed a cada 100ms
            timer = new Timer(100, this);
            timer.start();
        }

        // Método para desenhar tudo no painel
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            desenharFundo(g); // Chama o método para desenhar o fundo
            g.setColor(Color.red); // Cor da maçã
            g.fillOval(apple.getX(), apple.getY(), TAMANHO_BLOCO, TAMANHO_BLOCO); // Desenha a maçã

            g.setColor(Color.green); // Cor da cobra
            for (int i = 0; i < snake.getTamanho(); i++) {
                g.fillRect(snake.getX()[i], snake.getY()[i], TAMANHO_BLOCO, TAMANHO_BLOCO); // Desenha a cobra
            }
        }

        // Desenha o fundo do jogo com um padrão quadriculado
        private void desenharFundo(Graphics g) {
            Color cor1 = new Color(220, 220, 220);
            Color cor2 = new Color(200, 200, 200);
            for (int i = 0; i < LARGURA; i += TAMANHO_BLOCO) {
                for (int j = 0; j < ALTURA; j += TAMANHO_BLOCO) {
                    if ((i / TAMANHO_BLOCO + j / TAMANHO_BLOCO) % 2 == 0) {
                        g.setColor(cor1);
                    } else {
                        g.setColor(cor2);
                    }
                    g.fillRect(i, j, TAMANHO_BLOCO, TAMANHO_BLOCO);
                }
            }
        }

        // Método chamado a cada tick do timer (a cada 100ms)
        @Override
        public void actionPerformed(ActionEvent e) {
            snake.mover(direcao); // Move a cobra na direção atual

            // Verifica se a cobra bateu nas bordas da tela
            if (snake.getX()[0] < 0 || snake.getX()[0] >= LARGURA || snake.getY()[0] < 0 || snake.getY()[0] >= ALTURA) {
                gameOver(); // Se bateu, termina o jogo
            }

            // Verifica se a cobra bateu em si mesma
            for (int i = 1; i < snake.getTamanho(); i++) {
                if (snake.getX()[0] == snake.getX()[i] && snake.getY()[0] == snake.getY()[i]) {
                    gameOver(); // Se bateu, termina o jogo
                }
            }

            // Verifica se a cobra comeu a maçã
            if (snake.getX()[0] == apple.getX() && snake.getY()[0] == apple.getY()) {
                snake.crescer(); // Cobra cresce
                apple.gerar(); // Nova maçã aparece
            }

            repaint(); // Redesenha o painel
        }

        // Método chamado quando o jogo termina
        private void gameOver() {
            int resposta = JOptionPane.showConfirmDialog(this, "Game Over! Pontuação: " + (snake.getTamanho() - 3) + "\nDeseja jogar novamente?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                reiniciarJogo(); // Reinicia o jogo
            } else {
                System.exit(0); // Fecha o jogo
            }
        }

        // Método para reiniciar o jogo
        private void reiniciarJogo() {
            snake = new Snake(LARGURA, ALTURA, TAMANHO_BLOCO); // Reinicia a cobra
            apple = new Apple(LARGURA, ALTURA, TAMANHO_BLOCO); // Reinicia a maçã
            repaint(); // Redesenha o painel
        }

        // Métodos para manipular as teclas pressionadas
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if (direcao != 'D') direcao = 'U'; // Não pode ir para a direção oposta
                    break;
                case KeyEvent.VK_DOWN:
                    if (direcao != 'U') direcao = 'D'; // Não pode ir para a direção oposta
                    break;
                case KeyEvent.VK_LEFT:
                    if (direcao != 'R') direcao = 'L'; // Não pode ir para a direção oposta
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direcao != 'L') direcao = 'R'; // Não pode ir para a direção oposta
                    break;
            }
        }

        public void keyReleased(KeyEvent e) {}
        public void keyTyped(KeyEvent e) {}
    }

    // Classe Snake: Controla a cobra do jogo
    public class Snake {
        private int[] x;
        private int[] y;
        private int tamanho;
        private final int largura;
        private final int altura;
        private final int tamanhoBloco;

        // Inicializa a cobra
        public Snake(int largura, int altura, int tamanhoBloco) {
            this.largura = largura;
            this.altura = altura;
            this.tamanhoBloco = tamanhoBloco;
            this.tamanho = 5; // Tamanho inicial da cobra
            this.x = new int[largura * altura / (tamanhoBloco * tamanhoBloco)];
            this.y = new int[largura * altura / (tamanhoBloco * tamanhoBloco)];
            iniciarCobra();
        }

        // Define a posição inicial da cobra
        private void iniciarCobra() {
            for (int i = 0; i < tamanho; i++) {
                x[i] = 100 - (i * tamanhoBloco);
                y[i] = 100;
            }
        }

        // Move a cobra na direção indicada
        public void mover(char direcao) {
            for (int i = tamanho - 1; i > 0; i--) {
                x[i] = x[i - 1];
                y[i] = y[i - 1];
            }

            switch (direcao) {
                case 'U': // Cima
                    y[0] -= tamanhoBloco;
                    break;
                case 'D': // Baixo
                    y[0] += tamanhoBloco;
                    break;
                case 'L': // Esquerda
                    x[0] -= tamanhoBloco;
                    break;
                case 'R': // Direita
                    x[0] += tamanhoBloco;
                    break;
            }
        }

        // Faz a cobra crescer
        public void crescer() {
            tamanho++;
        }

        public int[] getX() {
            return x;
        }

        public int[] getY() {
            return y;
        }

        public int getTamanho() {
            return tamanho;
        }
    }

    // Classe Apple: Controla a maçã do jogo
    public class Apple {
        private int x;
        private int y;
        private final int largura;
        private final int altura;
        private final int tamanhoBloco;

        // Inicializa a maçã
        public Apple(int largura, int altura, int tamanhoBloco) {
            this.largura = largura;
            this.altura = altura;
            this.tamanhoBloco = tamanhoBloco;
            gerar();
        }

        // Gera uma nova maçã aleatória
        public void gerar() {
            Random rand = new Random();
            x = rand.nextInt(largura / tamanhoBloco) * tamanhoBloco;
            y = rand.nextInt(altura / tamanhoBloco) * tamanhoBloco;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static void main(String[] args) {
        new JogoCobrinha();
    }
}