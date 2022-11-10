import java.util.Arrays;
import java.util.Collections;

public class SimpleMahjongProgram {
    private SimpleMahjongTile[] discard;
    private SimpleMahjongTile[] hand;
    private SimpleMahjongTile[] deck;
    private SimpleMahjongTile[] backup;

    private boolean timeToDiscard;

    public SimpleMahjongProgram() {
        discard = new SimpleMahjongTile[6];
        hand = new SimpleMahjongTile[5];
        deck = new SimpleMahjongTile[36];
        backup = new SimpleMahjongTile[25];
        timeToDiscard = false;

        initialize();
    }

    public final void initialize() {
        Arrays.fill(discard, null);
        Arrays.fill(hand, null);
        for (int rank = 1; rank <= 9; rank++) {
            for (int copy = 1; copy <= 4; copy++) {
                deck[rank*4 + copy - 5] = new SimpleMahjongTile(rank);
            }
        }
        Arrays.fill(backup, null);
    }

    public void startGame() {
        Collections.shuffle(Arrays.asList(deck));
        drawTile();
        drawTile();
        drawTile();
        drawTile();
        drawTile();
        Arrays.sort(hand);
    }

    public void drawTile() {
        for (int i = 0; i < deck.length; i++) {
            if (deck[i] != null) {
                for (int j = 0; j < hand.length; j++) {
                    if (hand[j] == null) {
                        hand[j] = deck[i];
                        deck[i] = null;
                        timeToDiscard = true;
                        return;
                    }
                }
                throw new AssertionError("Hand is full!");
            }
        }
        throw new AssertionError("Deck is empty!");
    }

    public void sortDrawnTile() {
        if (hand[hand.length - 1].compareTo(hand[hand.length - 2]) < 0) {
            Arrays.sort(hand);
        }
    }

    public void discardTile(int index) {
        if (discard[discard.length - 1] != null) {  //discard is full
            if (backup[backup.length - 1] != null) {    //backup is full -> implies deck is empty
                reshuffle();
            }
            for (int i = 0; i < backup.length; i++) {
                if (backup[i] == null) {
                    backup[i] = discard[0];
                    discard[0] = null;
                    for (int j = 1; j < discard.length; j++) {
                        discard[j-1] = discard[j];
                        discard[j] = null;
                    }
                    discard[discard.length - 1] = hand[index];
                    hand[index] = null;
                    consolidate(hand);
                    timeToDiscard = false;
                    return;
                }
            }
        } else {
            for (int j = 0; j < discard.length; j++) {
                if (discard[j] == null) {
                    discard[j] = hand[index];
                    hand[index] = null;
                    consolidate(hand);
                    timeToDiscard = false;
                    return;
                }
            }
        }
    }

    public void reshuffle() {
        for (int i = 0; i < backup.length; i++) {
            deck[i] = backup[i];
            backup[i] = null;
            Collections.shuffle(Arrays.asList(deck));
            consolidate(deck);
        }
    }

    public static void consolidate(SimpleMahjongTile[] array) {
        SimpleMahjongTile[] tempArray = new SimpleMahjongTile[array.length];
        int ind = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                tempArray[ind] = array[i];
                ind++;
            }
        }
        for (int i = 0; i < array.length; i++) {
            array[i] = tempArray[i];
        }
    }

    public boolean isWinningHand() {
        return isTripletWin() || isSequenceWin();
    }

    public boolean isTripletWin() {
        if (!timeToDiscard)
            return false;
        SimpleMahjongTile[] winningHand = new SimpleMahjongTile[5];
        for (int pairRank = 1; pairRank <= 9; pairRank++) {
            for (int tripletRank = 1; tripletRank <= 9; tripletRank++) {
                if (pairRank == tripletRank)
                    continue;
                winningHand[0] = new SimpleMahjongTile(pairRank);
                winningHand[1] = new SimpleMahjongTile(pairRank);
                winningHand[2] = new SimpleMahjongTile(tripletRank);
                winningHand[3] = new SimpleMahjongTile(tripletRank);
                winningHand[4] = new SimpleMahjongTile(tripletRank);
                Arrays.sort(winningHand);
                if (Arrays.deepEquals(hand, winningHand)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSequenceWin() {
        if (!timeToDiscard)
            return false;
        SimpleMahjongTile[] winningHand = new SimpleMahjongTile[5];
        for (int pairRank = 1; pairRank <= 9; pairRank++) {
            for (int sequenceRank = 2; sequenceRank <= 8; sequenceRank++) {
                winningHand[0] = new SimpleMahjongTile(pairRank);
                winningHand[1] = new SimpleMahjongTile(pairRank);
                winningHand[2] = new SimpleMahjongTile(sequenceRank - 1);
                winningHand[3] = new SimpleMahjongTile(sequenceRank);
                winningHand[4] = new SimpleMahjongTile(sequenceRank + 1);
                Arrays.sort(winningHand);
                if (Arrays.deepEquals(hand, winningHand)) {
                    return true;
                }
            }
        }
        return false;
    }

    public SimpleMahjongTile[] getDiscard() {
        return discard;
    }

    public SimpleMahjongTile[] getHand() {
        return hand;
    }

    public SimpleMahjongTile[] getDeck() {
        return deck;
    }

    public SimpleMahjongTile[] getBackup() {
        return backup;
    }

    public boolean isTimeToDiscard() {
        return timeToDiscard;
    }
}
