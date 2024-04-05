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
    private boolean movingLeft = false;
    private boolean movingRight = false;
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
    private boolean bossSpawned = false;
    private boolean bossDefeated = false;
    private int bossX;
    private int bossY = -200;
    private Image bossImage;
    private int bossWidth = 200;
    private int bossHeight = 200;
    private int lifeBarWidth = 200; // Lățimea barei de viață
    private int lifeBarHeight = 20; // Înălțimea barei de viață
    private int lifeBarY = 50; // Coordonata Y a barei de viață
    private int lifeBarX; // Coordonata X a barei de viață

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
        alienImages.add(new ImageIcon("C:\\Users\\tiber\\New folder\\Space-Invaders\\src\\Alien1.png").getImage());
        alienImages.add(new ImageIcon("C:\\Users\\tiber\\New folder\\Space-Invaders\\src\\Alien2.png").getImage());
        alienImages.add(new ImageIcon("C:\\Users\\tiber\\New folder\\Space-Invaders\\src\\Alien3.png").getImage());
        alienImages.add(new ImageIcon("C:\\Users\\tiber\\New folder\\Space-Invaders\\src\\Alien4.png").getImage());

        playerImage = new ImageIcon("C:\\Users\\tiber\\New folder\\Space-Invaders\\src\\Rocket.png").getImage();

        bulletImage = new ImageIcon("C:\\Users\\tiber\\New folder\\Space-Invaders\\src\\Bullet.png").getImage();
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

    private void drawBackground(Graphics g) {
        Image backgroundImage = new ImageIcon("C:\\Users\\tiber\\New folder\\Space-Invaders\\src\\background.png").getImage();
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        getWidth();
        g.drawImage(playerImage, playerX, 550 - newPlayerHeight, newPlayerWidth, newPlayerHeight, this);
        for (Alien alien : aliens) {
            g.drawImage(alienImages.get(alien.row), alien.x, alien.y, alienWidth, alienHeight, this);
        }
        if (bulletFired) {
            g.drawImage(bulletImage, bulletX, bulletY, newBulletWidth, newBulletHeight, this);
        }
        if (bossSpawned) {
            g.drawImage(bossImage, bossX, bossY, bossWidth, bossHeight, this);
        }
        // Desenăm bara de viață
        g.setColor(Color.RED);
        g.fillRect(lifeBarX, lifeBarY, lifeBarWidth, lifeBarHeight);
        g.setColor(Color.WHITE);
        g.drawRect(lifeBarX, lifeBarY, lifeBarWidth, lifeBarHeight);
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
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            movingLeft = true;
        } else if (key == KeyEvent.VK_RIGHT) {
            movingRight = true;
        } else if (key == KeyEvent.VK_SPACE) {
            if (!bulletFired) {
                bulletX = playerX + (newPlayerWidth - newBulletWidth) / 2;
                bulletY = 550 - newPlayerHeight;
                bulletFired = true;
            }
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            movingLeft = false;
        } else if (key == KeyEvent.VK_RIGHT) {
            movingRight = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Verificăm dacă jocul este încă în desfășurare
        if (lives > 0) {
            int moveDownInterval = 1200;
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
            
            // Verificăm dacă scorul a ajuns la 4400 și boss-ul nu a fost încă spawnat
            if (score >= 4400 && !bossSpawned) {
                checkBossSpawn(); // Apariția boss-ului
            }
    
            movePlayer();
            repaint();
        }
    }
    
    private void resetAliens() {
        aliens.clear();
        initAliens(getWidth());
    }

    private void movePlayer() {
        if (movingLeft && playerX > 0) {
            playerX -= 5;
        }
        if (movingRight && playerX < getWidth() - newPlayerWidth) {
            playerX += 5;
        }
    }

    private void checkBossSpawn() {
        if (aliens.isEmpty() && !bossSpawned) {
            bossSpawned = true;
            bossImage = new ImageIcon("C:\\Users\\tiber\\New folder\\Space-Invaders\\src\\Boss.png").getImage();
            bossX = (getWidth() - bossWidth) / 2;
            animateBossSequence();
        }
    }

    private void animateBossSequence() {
        Timer bossAnimationTimer = new Timer(10, new ActionListener() {
            int animationCounter = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (animationCounter < 200) {
                    bossY += 2; // Animation for life line
                } else {
                    bossY += 1; // Animation for boss
                }
                if (bossY >= 100) {
                    // Adăugăm un dreptunghi pentru bara de viață
                    lifeBarX = (getWidth() - lifeBarWidth) / 2;
                    repaint();
                    ((Timer) e.getSource()).stop();
                }
                repaint();
                animationCounter++;
            }
        });
        bossAnimationTimer.start();
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
