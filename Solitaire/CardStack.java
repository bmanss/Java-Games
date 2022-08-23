package Solitaire;
import java.awt.*;
import java.util.*;


public class CardStack {

    final int spacingIncrement = 32;

    private int DefaultX;
    private int DefaultY;
    private int stackLocationX;
    private int stackLocationY;

    private int suitPileCount = 0;
    private int selectedCardIndex = -1;
    private int cardSpacing;
    private int stackSize = 0;

    private String suitLimitation = ""; 

    private boolean isSingle = false;

    private Random random = new Random();
    private ArrayList<Card> cardList;


    public CardStack(int x, int y, int width, int height, boolean setSingle){
        isSingle = setSingle;

        DefaultX = x;
        DefaultY = y;

        cardSpacing = y;

        stackLocationX = x;
        stackLocationY = y;

        cardList = new ArrayList<Card>();
    }

    public void drawCardStack(Graphics g){
        for (Card Cards : cardList){
            Cards.drawCard(g);
        }
    }

    public void addCard(Card newCard, boolean isFaceDown){
        if (isSingle){
            newCard.setNewDefault(DefaultX, DefaultY);
            newCard.resetPosition();
            newCard.setFacing(isFaceDown);
            cardList.add(newCard);
            ++stackSize;
        }
        else {
            newCard.moveCard(this.stackLocationX, this.cardSpacing);
            newCard.setNewDefault(this.stackLocationX, this.cardSpacing);
            newCard.setFacing(isFaceDown);
            cardList.add(newCard);
            cardSpacing += spacingIncrement;
            ++stackSize;
        }
    }

    public void removeCard(Card index){
        if (!this.cardList.isEmpty()){
            cardList.remove(index);
            stackSize--;
            if (isSingle == false)
                cardSpacing -= spacingIncrement;
        }
    }

    public void replace(CardStack newStack){
        Card newCard = newStack.getLastCard();
        while(newCard != null){
            this.addCard(newCard, true);
            newStack.removeCard(newCard);
            newCard = newStack.getLastCard();
        }
    }

    public void merge(CardStack newCards){
        Card nextCard = newCards.getSelectedCard();
        while (nextCard != null){
            this.addCard(nextCard, false);
            newCards.removeCard(nextCard);
            nextCard = newCards.getSelectedCard();
        }
        newCards.returnStackPosition();
        if (isSingle)
            ++suitPileCount;
    }

    public void moveCardStack(int x, int y){
        int newCardSpacing = 0;
        stackLocationX = x;
        stackLocationY = y;
        for (int i = selectedCardIndex; i < cardList.size(); i++ ){
            cardList.get(i).moveCard(x, y + newCardSpacing);
            newCardSpacing += spacingIncrement;
        }
    }

    public void setSelectedCardIndex(Point mousePress){
        int nextCardOffset = 30;
        if (isSingle)
            nextCardOffset = -1;
        for (int searchIndex = 0; searchIndex < cardList.size(); searchIndex++){
            if (searchIndex + 1 == cardList.size())
                nextCardOffset = 190;
            if (mousePress.getY() >= cardList.get(searchIndex).getCardY() && mousePress.getY() <= cardList.get(searchIndex).getCardY() + nextCardOffset){
                if (!cardList.get(searchIndex).isFaceDown()){
                    selectedCardIndex = searchIndex;
                    return;
                }
            }
        }
    }

    public void returnStackPosition(){
        stackLocationX = DefaultX;
        stackLocationY = DefaultY;
        for (Card card : cardList){
            card.resetPosition();
        }
        selectedCardIndex = -1;
    }

    public void setSuitLimitation(String limitation){
        suitLimitation = limitation;
    }

    public String getSuitLimitation(){
        return suitLimitation;
    }

    public Card getRandomCard(){
        return cardList.get(random.nextInt(stackSize));
    }

    public Card getLastCard(){
        if (cardList.isEmpty())
            return null;
        return cardList.get(stackSize - 1);
    }

    public Card getSelectedCard(){
        if (selectedCardIndex < 0 || selectedCardIndex + 1 > cardList.size())
            return null;
        return cardList.get(selectedCardIndex);
    }

    public boolean isSingle(){
        return isSingle;
    }

    public int getSuitPileCount(){
        return suitPileCount;
    }

    public int getX(){
        return this.stackLocationX;
    }

    public int getY(){
        return this.stackLocationY;
    }

    public int getLastCardY(){
        return cardSpacing;
    }

    public boolean isEmpty(){
        return cardList.isEmpty();
    }

}
