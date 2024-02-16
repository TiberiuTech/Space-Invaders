import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SpaceInvaders extends JPanel implements KeyListener, ActionListener {
    int playerX = 400;
    private int bulletX = -100;
    private int bulletY = -100;
    private boolean bulletFired = false;
    private final ArrayList<Alien> aliens = new ArrayList<>();
    private final int alienWidth = 50;
    private final int alienHeight = 30;
    private final List<Image> alienImages = new ArrayList<>();
    private Image playerImage;
    private Image bulletImage;
    private final Timer timer;
    final int newPlayerWidth = 100;
    private final int newPlayerHeight = 100;
    private final int newBulletWidth = 40;
    private final int newBulletHeight = 60;
    private long lastMoveDownTime = System.currentTimeMillis();
    private int score = 0;
    private int lives = 1;

    public SpaceInvaders() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        loadImages();
        timer = new Timer(10, this);
        timer.start();
        setFocusable(true);
        addKeyListener(this);
    }

    private void loadImages() {
        alienImages.add(new ImageIcon("C:\\Users\\tiber\\OneDrive\\Documents\\Space Invaders\\src\\Photos\\Alien1.png").getImage());
        alienImages.add(new ImageIcon("C:\\Users\\tiber\\OneDrive\\Documents\\Space Invaders\\src\\Photos\\Alien2.png").getImage());
        alienImages.add(new ImageIcon("C:\\Users\\tiber\\OneDrive\\Documents\\Space Invaders\\src\\Photos\\Alien3.png").getImage());
        alienImages.add(new ImageIcon("C:\\Users\\tiber\\OneDrive\\Documents\\Space Invaders\\src\\Photos\\Alien4.png").getImage());

        playerImage = new ImageIcon("C:\\Users\\tiber\\OneDrive\\Documents\\Space Invaders\\src\\Photos\\Rocket.png").getImage();

        bulletImage = new ImageIcon("C:\\Users\\tiber\\OneDrive\\Documents\\Space Invaders\\src\\Photos\\Bullet.png").getImage();
    }

    void initAliens(int width) {
        int totalAliens = 11;
        int totalAlienBlockWidth = (alienWidth + 10) * totalAliens - 10;
        int startX = (width - totalAlienBlockWidth) / 2;

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < totalAliens; col++) {
                aliens.add(new Alien(startX + col * (alienWidth + 10), 50 + row * (alienHeight + 10), row));
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        getWidth();
        g.drawImage(playerImage, playerX, 550 - newPlayerHeight, newPlayerWidth, newPlayerHeight, this);
        for (Alien alien : aliens) {
            g.drawImage(alienImages.get(alien.row), alien.x, alien.y, alienWidth, alienHeight, this);
        }
        if (bulletFired) {
            g.drawImage(bulletImage, bulletX, bulletY, newBulletWidth, newBulletHeight, this);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        String scoreText = "Score: " + score;
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        int scoreX = getWidth() - scoreWidth - alienImages.getFirst().getWidth(null) - 20;
        int scoreY = getHeight() - 30;
        g.drawString(scoreText, scoreX, scoreY);

        String livesText = "Lives: " + lives;
        int livesX = 20;
        int livesY = getHeight() - 30;
        g.drawString(livesText, livesX, livesY);
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX > 0) playerX -= 10;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX < getWidth() - newPlayerWidth) playerX += 10;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!bulletFired) {
                bulletFired = true;
                bulletX = playerX + newPlayerWidth / 2 - newBulletWidth / 2;
                bulletY = 550 - newPlayerHeight;
            }
        }
        repaint();
    }


    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int moveDownInterval = 800;
        if (System.currentTimeMillis() - lastMoveDownTime > moveDownInterval) {
            for (Alien alien : aliens) {
                alien.y += 10;
            }
            lastMoveDownTime = System.currentTimeMillis();
        }
        if (bulletFired) {
            bulletY -= 5;
            if (bulletY < 0) bulletFired = false;
            Rectangle bulletRect = new Rectangle(bulletX, bulletY, newBulletWidth, newBulletHeight);
            for (int i = 0; i < aliens.size(); i++) {
                Alien alien = aliens.get(i);
                if (alien.getRectangle().intersects(bulletRect)) {
                    aliens.remove(i);
                    bulletFired = false;
                    bulletY = -100;
                    score += 100;
                    break;
                }
            }
        }

        boolean alienPassed = aliens.removeIf(alien -> alien.y + alienHeight >= 550 - newPlayerHeight);
        if (alienPassed) {
            lives--;
            resetAliens();
            if (lives <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Game Over! Your score: " + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);

            }
        }

        repaint();
    }

    private void resetAliens() {
        aliens.clear();
        initAliens(getWidth());
    }

    private class Alien {
        int x, y, row;

        Alien(int x, int y, int row) {
            this.x = x;
            this.y = y;
            this.row = row;
        }

        public Rectangle getRectangle() {
            return new Rectangle(x, y, alienWidth, alienHeight);
        }
    }
}