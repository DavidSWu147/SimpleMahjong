import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SimpleMahjongTile implements Comparable<SimpleMahjongTile> {
    public static final String PATHWAY = "/Users/Demonstration/Desktop/";
    public static final String EXTENSION = "_dot.jpg";

    private int rank;
    private Image image;

    public SimpleMahjongTile(int rank) {
        if (rank < 1 || rank > 9) {
            throw new IllegalArgumentException("Invalid rank! " + rank);
        }
        this.rank = rank;

        try {
            image = ImageIO.read(new File(PATHWAY + rank + EXTENSION));
        } catch (IOException e) {
            System.err.println("Oh my goodness!");
            System.exit(1);
        }
    }

    public int getRank() {
        return rank;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleMahjongTile) {
            SimpleMahjongTile other = (SimpleMahjongTile)(obj);
            return this.rank == other.rank;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(SimpleMahjongTile other) {
        if (other == null) {
            return this.rank - 10;
        }
        return this.rank - other.rank;
    }

    @Override
    public String toString() {
        return rank + " dot";
    }
}
