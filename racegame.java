import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import javax.sound.sampled.*;

public class RaceGame extends JPanel implements ActionListener, KeyListener {

    Timer timer;
    int carX = 200;
    int carY = 500;
    int carWidth = 50;
    int carHeight = 80;
    int score = 0;
    int highScore = 0;
    int obstacleSpeed = 5;
    final String highScoreFile = "highscore.txt";

    boolean leftPressed = false;
    boolean rightPressed = false;

    ArrayList<Rectangle> obstacles = new ArrayList<>();
    Random rand = new Random();
    Image carImage;

    public RaceGame() {
        this.setPreferredSize(new Dimension(500, 600));
        this.setBackground(Color.gray);
        this.setFocusable(true);
        this.addKeyListener(this);
        timer = new Timer(20, this);
        timer.start();
        spawnObstacle();
        loadHighScore();
        carImage = new ImageIcon("ferrari-296-gts-n-largo-by-novitec-2025-thumb.jpg").getImage();
    }

    public void spawnObstacle() {
        int x = rand.nextInt(450);
        obstacles.add(new Rectangle(x, 0, 50, 80));
    }

    public void loadHighScore() {
        try {
            File file = new File(highScoreFile);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                highScore = Integer.parseInt(reader.readLine());
                reader.close();
            }
        } catch (Exception e) {
            highScore = 0;
        }
    }

    public void saveHighScore() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFile));
            writer.write(String.valueOf(highScore));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw road lines
        g.setColor(Color.white);
        for (int i = 0; i < getHeight(); i += 40) {
            g.fillRect(245, i, 10, 20);
        }

        // Draw car image
        g.drawImage(carImage, carX, carY, carWidth, carHeight, this);

        // Draw obstacles
        g.setColor(Color.black);
        for (Rectangle obs : obstacles) {
            g.fillRect(obs.x, obs.y, obs.width, obs.height);
        }

        // Draw score and high score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);
        g.drawString("High Score: " + highScore, 330, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (leftPressed && carX > 0) carX -= 5;
        if (rightPressed && carX < getWidth() - carWidth) carX += 5;

        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle obs = obstacles.get(i);
            obs.y += obstacleSpeed;

            if (obs.intersects(new Rectangle(carX, carY, carWidth, carHeight))) {
                timer.stop();
                playSound("crash.wav");
                JOptionPane.showMessageDialog(this, "Game Over! Final Score: " + score);
                System.exit(0);
            }

            if (obs.y > getHeight()) {
                obstacles.remove(i);
                score++;

                // Increase difficulty
                if (score % 10 == 0 && obstacleSpeed < 15) {
                    obstacleSpeed++;
                }

                // Update high score
                if (score > highScore) {
                    highScore = score;
                    saveHighScore();
                }

                spawnObstacle();
            }
        }

        repaint();
    }

    // Key handling
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Main method
    public static void main(String[] args) {
        JFrame frame = new JFrame("Race Game");
        RaceGame game = new RaceGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
