import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Invaders");
        SpaceInvaders game = new SpaceInvaders();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

        frame.setExtendedState(JFrame.NORMAL);
        frame.setMaximizedBounds(new Rectangle(frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight()));

        game.initAliens(frame.getWidth());
        game.playerX = (frame.getWidth() - game.newPlayerWidth) / 2;
        game.repaint();
    }
}
