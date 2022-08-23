package Solitaire;
import java.awt.*;
import javax.swing.ImageIcon;
import java.awt.Font;

public class Card {
    
    final int cardWidth = 140;
    final int cardHeight = 190;

    private Image cardFace;
    private Image rankSymbol;
    private Integer cardNumberRank;
    private String suit;
    private Color color;

    private int defaultLocationX;
    private int defaultLocationY;

    private int cardLocationX;
    private int cardLocationY;

    private boolean isFaceDown;

    public Card(int x, int y, Integer rank, String setSuit){
        suit = setSuit;
        cardNumberRank = rank;
        cardLocationX = x;
        cardLocationY = y;
        defaultLocationX = x;
        defaultLocationY = y;

        setRankSuit();
    }

    public void drawCard(Graphics g){

        String cardRank = cardNumberRank.toString();
        if (cardNumberRank == 1)
            cardRank = "A";
        if (cardNumberRank == 11)
            cardRank = "J";
        if (cardNumberRank == 12)
            cardRank = "Q";
        if (cardNumberRank == 13)
            cardRank = "K";

        g.drawImage(cardFace, cardLocationX, cardLocationY, cardWidth, cardHeight, null);
        if (!isFaceDown){
            g.setColor(color);
            g.setFont(new Font("Times New Roman",Font.BOLD,17));
            g.drawString(cardRank, cardLocationX + 5, cardLocationY + 15);
            g.drawImage(rankSymbol,cardLocationX + 1 , cardLocationY + 17, 18,18, null );
        }
    }

    public void moveCard(int x, int y){
        cardLocationX = x;
        cardLocationY = y;
    }

    public void setNewDefault(int x, int y){
        defaultLocationX = x;
        defaultLocationY = y;

    }

    public void resetPosition(){
        cardLocationX = defaultLocationX;
        cardLocationY = defaultLocationY;
    }

    public void setFacing(boolean faceDown){
        isFaceDown = faceDown;

        if (faceDown){
            cardFace = new ImageIcon("./Solitaire/cardPngs/cardBack.png").getImage();
            isFaceDown = true;
        }
        else{
            cardFace = new ImageIcon("./Solitaire/cardPngs/cardFace.png").getImage();
            isFaceDown = false;
        }
    }

    public boolean isFaceDown(){
        if (isFaceDown)
            return true;
        else{
            return false;
        }
    }

    private void setRankSuit(){
        switch(suit){
            case("spade"):
                this.color = Color.black;
                this.rankSymbol = new ImageIcon("./Solitaire/cardPngs/spade.png").getImage();
                break;
            case("club"):
                this.color = Color.black;
                this.rankSymbol = new ImageIcon("./Solitaire/cardPngs/club.png").getImage();
                break;
            case("heart"):
                this.color = Color.red;
                this.rankSymbol = new ImageIcon("./Solitaire/cardPngs/heart.png").getImage();
                break;
            case("diamond"):
                this.color = Color.red;
                this.rankSymbol = new ImageIcon("./Solitaire/cardPngs/diamond.png").getImage();
                break;
        }
    }

    public int getRank(){
        return cardNumberRank;
    }

    public int getCardY(){
        return cardLocationY;
    } 

    public int getCardX(){
        return cardLocationX;
    }

    public Color getColor(){
        return color;
    }

    public String getSuit(){
        return suit;
    }

    public boolean getFaceDown(){
        return isFaceDown;
    }

}


