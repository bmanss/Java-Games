package Solitaire;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SolitairePanel extends JPanel implements Runnable, MouseInputListener{

    final int STACK_HEIGHT = 50;
    final int STACK_WIDTH = 140;

    final int CARD_WIDTH = 140;
    final int CARD_HEIGHT = 190;

    private int mouseMoveOffsetX;
    private int mouseMoveOffsetY;

    private boolean winCondition = false;
    private boolean exitGame = false;

    CardStack selectedStack = null;
    CardStack releasedStack = null;

    ArrayList<CardStack> allCardStacks = new ArrayList<CardStack>();

    CardStack startingDeck;
    CardStack drawPile;
    CardStack drawnCards;

    CardStack heartsPile;
    CardStack spadesPile;
    CardStack diamondsPile;
    CardStack clubsPile;

    CardStack column1;
    CardStack column2;
    CardStack column3;
    CardStack column4;
    CardStack column5;
    CardStack column6;
    CardStack column7;

    Image bufferedGraphics;
    Graphics gfx;

    Thread gameThread;

    public SolitairePanel(){
        this.setLayout(null);
        this.setPreferredSize(new Dimension(1250,850));
        this.setBackground(new Color(10, 214, 69));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        gameThread = new Thread(this);
        gameThread.start(); 
    }

    public void paint(Graphics g){
        bufferedGraphics = createImage(getWidth(), getHeight());
        gfx = bufferedGraphics.getGraphics();

        gfx.setColor(new Color(10, 214, 69));
        gfx.fillRect(0, 0, getWidth(), getHeight());

        // card pile locations
        gfx.setColor(Color.BLACK);
        gfx.drawRect(this.getX() + 1, this.getY() + 1, CARD_WIDTH, CARD_HEIGHT);                                              // drawpile
        gfx.drawRect(this.getWidth()  - CARD_WIDTH - 15, this.getY() + 10, CARD_WIDTH, CARD_HEIGHT);                           // stack collection spot for hearts
        gfx.drawImage(new ImageIcon("./Solitaire/cardPngs/heart.png").getImage(), heartsPile.getX() + 45, heartsPile.getY() + 70, null);
        gfx.drawRect(this.getWidth()  - CARD_WIDTH - 15, this.getY() + CARD_HEIGHT + 20, CARD_WIDTH, CARD_HEIGHT);             // stack collection spot for diamonds
        gfx.drawImage(new ImageIcon("./Solitaire/cardPngs/diamond.png").getImage(), diamondsPile.getX() + 45, diamondsPile.getY() + 70, null);
        gfx.drawRect(this.getWidth()  - CARD_WIDTH - 15, this.getY() + (2 * CARD_HEIGHT) + 30, CARD_WIDTH, CARD_HEIGHT);       // stack collection spot for clubs
        gfx.drawImage(new ImageIcon("./Solitaire/cardPngs/club.png").getImage(), clubsPile.getX() + 45, clubsPile.getY() + 70, null);
        gfx.drawRect(this.getWidth()  - CARD_WIDTH - 15, this.getY() + (3 * CARD_HEIGHT) + 40, CARD_WIDTH, CARD_HEIGHT);       // stack collection spot for spades
        gfx.drawImage(new ImageIcon("./Solitaire/cardPngs/spade.png").getImage(), spadesPile.getX() + 45, spadesPile.getY() + 70, null);
 
        // column start locations
        gfx.drawRect(this.getX() + 1, this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT);                                 //column1
        gfx.drawRect(this.getX() + 5 + CARD_WIDTH, this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT);                    //column2
        gfx.drawRect(this.getX() + 2 * (5 + CARD_WIDTH), this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT);              //column3
        gfx.drawRect(this.getX() + 3 * (5 + CARD_WIDTH), this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT);              //column4
        gfx.drawRect(this.getX() + 4 * (5 + CARD_WIDTH), this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT);              //column5
        gfx.drawRect(this.getX() + 5 * (5 + CARD_WIDTH), this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT);              //column6
        gfx.drawRect(this.getX() + 6 * (5 + CARD_WIDTH), this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT);              //column7
             
        for (CardStack nextStack : allCardStacks){
            nextStack.drawCardStack(gfx);
        }
        
        // priority draw on selected card so it appears in front of other cards
        if (selectedStack != null)
            selectedStack.drawCardStack(gfx);

        //print you win if game is won
        if (winCondition){
            youWin(gfx);
        }
        
        // draw buffered image
        g.drawImage(bufferedGraphics, 0, 0, this);
    }

    public void setNewGame(){
        allCardStacks.add(startingDeck = new CardStack(-500, -500, 0, 0, false));
        allCardStacks.add(drawPile = new CardStack(this.getX() + 1, this.getY() + 1, CARD_WIDTH, CARD_HEIGHT, true));
        allCardStacks.add(drawnCards = new CardStack(drawPile.getX() + CARD_WIDTH + 5,this.getY() + 1, CARD_WIDTH, CARD_HEIGHT, true));
        allCardStacks.add(heartsPile = new CardStack(this.getWidth()  - CARD_WIDTH - 15, this.getY() + 10, CARD_WIDTH, CARD_HEIGHT, true));
        allCardStacks.add(diamondsPile = new CardStack(this.getWidth()  - CARD_WIDTH - 15, this.getY() + CARD_HEIGHT + 20, CARD_WIDTH, CARD_HEIGHT, true));
        allCardStacks.add(clubsPile = new CardStack(this.getWidth()  - CARD_WIDTH - 15, this.getY() + (2 * CARD_HEIGHT) + 30, CARD_WIDTH, CARD_HEIGHT, true));
        allCardStacks.add(spadesPile = new CardStack(this.getWidth()  - CARD_WIDTH - 15, this.getY() + (3 * CARD_HEIGHT) + 40, CARD_WIDTH, CARD_HEIGHT, true));
        allCardStacks.add(column1 = new CardStack(this.getX() + 1, this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT, false));
        allCardStacks.add(column2 = new CardStack(this.getX() + 5 + CARD_WIDTH, this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT, false));
        allCardStacks.add(column3 = new CardStack(this.getX() + 2 * (5 + CARD_WIDTH), this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT, false));
        allCardStacks.add(column4 = new CardStack(this.getX() + 3 * (5 + CARD_WIDTH), this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT, false));
        allCardStacks.add(column5 = new CardStack(this.getX() + 4 * (5 + CARD_WIDTH), this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT, false));
        allCardStacks.add(column6 = new CardStack(this.getX() + 5 * (5 + CARD_WIDTH), this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT, false));
        allCardStacks.add(column7 = new CardStack(this.getX() + 6 * (5 + CARD_WIDTH), this.getY() + CARD_HEIGHT + 5, CARD_WIDTH, CARD_HEIGHT, false));
        
        heartsPile.setSuitLimitation("heart");
        diamondsPile.setSuitLimitation("diamond");
        spadesPile.setSuitLimitation("spade");
        clubsPile.setSuitLimitation("club");

        fillStartingDeck();
        initializeColumns();
    }

    public void fillStartingDeck(){
        String nextSuit = "";
        int suitIndex = 0;

        for (int i = 1; i <= 52; ++i){
            if (suitIndex == 0)
                nextSuit = "heart";
            if (suitIndex == 1)
                nextSuit = "diamond";
            if (suitIndex == 2)
                nextSuit = "club";
            if (suitIndex == 3)
                nextSuit = "spade";
                
            for (int cardRank = 1; cardRank <= 13; ++cardRank){
                startingDeck.addCard(new Card(0, 0, cardRank, nextSuit),false);
                i++;
            }
            ++suitIndex;
        }
    }

    public void initializeColumns(){
        int faceDownCounter = 0;
        int cardsToAdd = 1;

        CardStack currentStack;
        Card randomCard;
        for (int columnIndex = 7; columnIndex <= 13; ++columnIndex){
            currentStack = allCardStacks.get(columnIndex);
            for (int i = 0; i < cardsToAdd; ++i){
                for (int j = 0; j < faceDownCounter; j++){
                    randomCard = startingDeck.getRandomCard();
                    currentStack.addCard(randomCard, true);
                    startingDeck.removeCard(randomCard);
                    ++i;
                }
                randomCard = startingDeck.getRandomCard();
                currentStack.addCard(randomCard, false);
                startingDeck.removeCard(randomCard);
            }
            ++cardsToAdd;
            ++faceDownCounter;
        }

        while (!startingDeck.isEmpty()){
            randomCard = startingDeck.getRandomCard();
            drawPile.addCard(randomCard, true);
            startingDeck.removeCard(randomCard);
        }
    }

    public void checkWinCondition(){
        if (heartsPile.getSuitPileCount() == 13 && diamondsPile.getSuitPileCount() == 13 && clubsPile.getSuitPileCount() == 13 && spadesPile.getSuitPileCount() == 13)
            winCondition = true;
    }

    public void youWin(Graphics g){
        g.setColor(Color.black);
        g.setFont(new Font("Times New Roman",Font.BOLD,50));
        g.drawString("You Win!",(this.getWidth() / 2) - 150 , this.getY() + 150);
    }

    public boolean shouldExitGame(){
        return exitGame;
    } 

    // run the game
    public void run() {
        setNewGame();
        long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
	    double delta = 0;
		while(true) {
            long now = System.nanoTime();
            delta += (now -lastTime)/ns;
            lastTime = now;
            if(delta >=1) {
                if (!winCondition){
                    checkWinCondition();
                    repaint();
                }
            delta--;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point mousePress = new Point(e.getX(),e.getY());

        if (mousePress.getX() >= drawPile.getX() && mousePress.getX() <= drawPile.getX() + STACK_WIDTH){
            if (mousePress.getY() >= drawPile.getY() && mousePress.getY() <= drawPile.getY() + CARD_HEIGHT){
                if (drawPile.isEmpty()){
                    drawPile.replace(drawnCards);
                }
                else {
                    Card topCard = drawPile.getLastCard();
                    drawnCards.addCard(topCard, false);
                    drawPile.removeCard(topCard);
                }
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (selectedStack != null)
            return;
        Point mousePress = new Point(e.getX(),e.getY());
        
        for (CardStack cardStack : allCardStacks){
            // skip empty stacks and any single stack that is not the drawpile
            if (cardStack.isEmpty() || (cardStack.isSingle() && (cardStack != drawPile && cardStack != drawnCards)))
                continue;

            // if mouse clicked within stack x-boundry
            if (mousePress.getX() >= cardStack.getX() && mousePress.getX() <= (cardStack.getX() + STACK_WIDTH)){
                if (mousePress.getY() >= cardStack.getY() && mousePress.getY() <= cardStack.getLastCardY() + CARD_HEIGHT){
                    cardStack.setSelectedCardIndex(mousePress);
                    if (cardStack.getSelectedCard() != null){
                        selectedStack = cardStack;
                    }
                }
            }
        }
        // get offset positions to move card with mouse
       if (selectedStack != null){
            mouseMoveOffsetX = e.getX() - selectedStack.getX();       
            mouseMoveOffsetY = e.getY() - selectedStack.getSelectedCard().getCardY();       
       }
    }



    @Override
    public void mouseReleased(MouseEvent e) {
        
        // check if mouse was released on another stack and check if a merge is possible or the card can be added to the suit piles
        for (CardStack cardStack : allCardStacks){

            // if mouse clicked within stack x-boundry
            if (e.getX() >= cardStack.getX() && e.getX() <= (cardStack.getX() + STACK_WIDTH)){
                if (e.getY() >= cardStack.getY() && e.getY() <= cardStack.getLastCardY() + CARD_HEIGHT){
                    if (selectedStack != null && cardStack != selectedStack){

                        // if a column is empty add selected stack if it is a king 
                        if (!cardStack.isSingle() && cardStack.isEmpty()){
                            if (selectedStack.getSelectedCard().getRank() != 13)
                                break;
                            cardStack.merge(selectedStack);
                        }

                        // if selected card is 1 less than what it's trying to be combined with and is not the same color
                        else if ( !cardStack.isSingle() && (selectedStack.getSelectedCard().getRank() + 1 == cardStack.getLastCard().getRank()) &&
                                    selectedStack.getSelectedCard().getColor() != cardStack.getLastCard().getColor())
                            {
                            cardStack.merge(selectedStack);
                        }

                        // add cards to suit piles if the suits match and the rank is +1
                        else if (cardStack.isSingle() && cardStack.getSuitLimitation() == selectedStack.getSelectedCard().getSuit()){
                            if (selectedStack.getSelectedCard().getRank() - 1 ==  cardStack.getSuitPileCount())
                                cardStack.merge(selectedStack);
                        }

                        // check the original selected stack's last card is facedown if so then flip it
                        if (!selectedStack.isEmpty() && selectedStack.getLastCard().getFaceDown() == true )
                            selectedStack.getLastCard().setFacing(false);
                    }
                }
            }
        }
        if (selectedStack != null){
            selectedStack.returnStackPosition();
            selectedStack = null;
        }
    }



    @Override
    public void mouseEntered(MouseEvent e) {
    }



    @Override
    public void mouseExited(MouseEvent e) {
    }



    @Override
    public void mouseDragged(MouseEvent e) {
        // move cards from where the mouse clicked on them
        if (selectedStack != null)
            selectedStack.moveCardStack(e.getX() - mouseMoveOffsetX, e.getY() - mouseMoveOffsetY);
    }


    @Override
    public void mouseMoved(MouseEvent e) {
    }

}
