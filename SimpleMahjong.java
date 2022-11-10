import javax.swing.*;

public class SimpleMahjong extends JFrame {
    public SimpleMahjong() {
        super("Simple Mahjong");
        setLocation(0, 0);
        setSize(1072, 1027);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        SimpleMahjongPanel smp = new SimpleMahjongPanel();
        setContentPane(smp);
        setVisible(true);
        smp.repaint();
    }

    public static void main(String[] args) {
        new SimpleMahjong();
    }
}
