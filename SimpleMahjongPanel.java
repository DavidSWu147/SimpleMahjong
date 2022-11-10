import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

public class SimpleMahjongPanel extends JPanel implements MouseListener, ActionListener {
    public static final String PATHWAY = "/Users/Demonstration/Desktop/";
    public static final String EXTENSION = "_dot.jpg";

    //numbers after scaling up by 1.5
    public static final int BIG_WIDTH = 162;
    public static final int BIG_HEIGHT = 225;
    public static final int WIDTH = 156;
    public static final int HEIGHT = 219;
    public static final int SHIFT = 50;

    private Image arrow;
    private Image mahjongWin;
    private Image tenhou;

    public static final String INSTRUCTIONS = "MAKE A FULL HOUSE OF TRIPLET + PAIR OR RUN OF 3 + PAIR";
    public static final String NEW_GAME = "CLICK TO START A NEW GAME";

    private Timer brightenTimer;
    private Timer darkenTimer;

    private SimpleMahjongProgram program;
    private int selectedTile;       //-1 means no tile selected
    private boolean isGameWon;
    private boolean isTenhou;
    private int timesGameWon;
    private int timesTripletWon;
    private int timesTenhouWon;

    public SimpleMahjongPanel() {
        setLayout(null);

        setBackground(Color.WHITE);
        addMouseListener(this);

        brightenTimer = new Timer(5, this);
        darkenTimer = new Timer(5, this);

        try {
            arrow = ImageIO.read(new File(PATHWAY + "Arrow.png"));
            mahjongWin = ImageIO.read(new File(PATHWAY + "mahjongWin.png"));
            tenhou = ImageIO.read(new File(PATHWAY + "Tenhou.png"));
        } catch (IOException e) {
            System.err.println("Oh my goodness!");
            System.exit(1);
        }

        program = new SimpleMahjongProgram();
        selectedTile = -1;
        isGameWon = false;
        isTenhou = false;
        timesGameWon = 0;
        timesTripletWon = 0;
        timesTenhouWon = 0;

        program.initialize();
        program.startGame();
        if (program.isWinningHand()) {  //tenhou
            isGameWon = true;
            isTenhou = true;
            timesGameWon++;
            timesTenhouWon++;
            if (program.isTripletWin()) {
                timesTripletWon++;
            }
            System.out.println("Stats: (" + timesGameWon + ", " + timesTripletWon + ", " + timesTenhouWon + ")");
        } else {
            darkenTimer.start();
        }
    }

    public void paintComponent(Graphics g) {
        if (isGameWon && getBackground().equals(Color.WHITE)) {
            g.drawImage(mahjongWin, 300, 300, this);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
            g.drawString(NEW_GAME, 303, 650);
        } if (isTenhou) {
            g.drawImage(tenhou, 0, 0, this);
        }

        g.fillRect(50, 150 + BIG_HEIGHT, BIG_WIDTH, BIG_HEIGHT);
        g.fillRect(50 + BIG_WIDTH * 5, 150 + BIG_HEIGHT, BIG_WIDTH, BIG_HEIGHT);

        g.drawImage(arrow, 0, 650, this);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        g.drawString("YOUR", 36, 862);
        g.drawString("HAND", 34, 910);

        g.setColor(Color.WHITE);
        if (!isGameWon) {
            if (program.isTimeToDiscard()) {
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
                g.drawString("DOUBLE", 50 + 18, 150 + BIG_HEIGHT + 75);
                g.drawString("CLICK TO", 50 + 9, 150 + BIG_HEIGHT + 75 + 48);
                g.drawString("DISCARD", 50 + 12, 150 + BIG_HEIGHT + 75 + 48 * 2);
            } else {
                g.drawString("CLICK", 50 + BIG_WIDTH * 5 + 8, 150 + BIG_HEIGHT + 80);
                g.drawString("TO", 50 + BIG_WIDTH * 5 + 44, 150 + BIG_HEIGHT + 80 + 48);
                g.drawString("DRAW", 50 + BIG_WIDTH * 5 + 6, 150 + BIG_HEIGHT + 80 + 48 * 2);
            }
        }

        g.fillRect(0, 1000, this.getWidth(), 27);
        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        g.drawString(INSTRUCTIONS, 146, 1023);

        SimpleMahjongTile[] discard = program.getDiscard();
        for (int i = 0; i < discard.length && discard[i] != null; i++) {
            g.fillRect(50 + BIG_WIDTH * i, 50, BIG_WIDTH, BIG_HEIGHT);
            g.drawImage(discard[i].getImage(), 53 + BIG_WIDTH * i, 53, WIDTH, HEIGHT, this);
        }

        SimpleMahjongTile[] hand = program.getHand();
        for (int i = 0; i < hand.length && hand[i] != null; i++) {
            if (i == selectedTile) {
                g.fillRect(50 + BIG_WIDTH * (i + 1), 250 + BIG_HEIGHT * 2 - SHIFT, BIG_WIDTH, BIG_HEIGHT);
                g.drawImage(hand[i].getImage(), 53 + BIG_WIDTH * (i + 1), 253 + BIG_HEIGHT * 2 - SHIFT, WIDTH, HEIGHT, this);
            } else {
                g.fillRect(50 + BIG_WIDTH * (i + 1), 250 + BIG_HEIGHT * 2, BIG_WIDTH, BIG_HEIGHT);
                g.drawImage(hand[i].getImage(), 53 + BIG_WIDTH * (i + 1), 253 + BIG_HEIGHT * 2, WIDTH, HEIGHT, this);
            }
        }
    }

    public int tileClicked(int x, int y) {
        for (int i = 0; i < program.getHand().length; i++) {
            if (i == selectedTile) {
                if (50 + BIG_WIDTH * (i + 1) < x && x < 50 + BIG_WIDTH * (i + 2) && 250 + BIG_HEIGHT * 2 - SHIFT < y && y < 250 + BIG_HEIGHT * 3 - SHIFT) {
                    return i;
                }
            } else {
                if (50 + BIG_WIDTH * (i + 1) < x && x < 50 + BIG_WIDTH * (i + 2) && 250 + BIG_HEIGHT * 2 < y && y < 250 + BIG_HEIGHT * 3) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (brightenTimer.isRunning() || darkenTimer.isRunning()) { //no actions during animation
            return;
        }
        if (isGameWon) {
            program.initialize();
            program.startGame();
            isGameWon = false;
            isTenhou = false;
            if (program.isWinningHand()) {  //tenhou
                isGameWon = true;
                isTenhou = true;
                timesGameWon++;
                timesTenhouWon++;
                if (program.isTripletWin()) {
                    timesTripletWon++;
                }
                System.out.println("Stats: (" + timesGameWon + ", " + timesTripletWon + ", " + timesTenhouWon + ")");
            } else {
                darkenTimer.start();
            }
        } else {
            if (program.isTimeToDiscard()) {
                if (selectedTile != -1 && selectedTile == tileClicked(e.getX(), e.getY())) {
                    program.discardTile(selectedTile);
                    selectedTile = -1;
                } else {
                    selectedTile = tileClicked(e.getX(), e.getY());
                }
            } else {
                program.drawTile();
                program.sortDrawnTile();
                if (program.isWinningHand()) {
                    isGameWon = true;
                    timesGameWon++;
                    if (program.isTripletWin()) {
                        timesTripletWon++;
                    }
                    System.out.println("Stats: (" + timesGameWon + ", " + timesTripletWon + ", " + timesTenhouWon + ")");
                    brightenTimer.start();
                }
            }
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == brightenTimer) {
            if (getBackground().equals(new Color(254, 255, 254)) ){
                setBackground(Color.WHITE);
                brightenTimer.stop();
            } else {
                setBackground(new Color(getBackground().getRed() + 2, getBackground().getGreen() + 1, getBackground().getBlue() + 2));
            }
        } else if (e.getSource() == darkenTimer) {
            if (getBackground().equals(Color.WHITE)) {
                setBackground(new Color(254, 255, 254));
            } else {
                setBackground(new Color(getBackground().getRed() - 2, getBackground().getGreen() - 1, getBackground().getBlue() - 2));
                if (getBackground().equals(new Color(0, 128, 0))) {
                    darkenTimer.stop();
                }
            }
        }
    }
}
