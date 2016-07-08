package server;

import deck.Card;

import java.util.ArrayList;

public class Player {

    private ArrayList<Card> hand;
    private ArrayList<Integer> total;


    public Player(Card card1, Card card2)
    {
        hand = new ArrayList<Card>();
        hand.add(card1);
        hand.add(card2);
        total.add(0);
        if(card1.getValue() == 1){
            ace();
        }
        else addValue(card1.getValue());

        if(card2.getValue() == 1){
            ace();
        }
        else addValue(card2.getValue());
    }

    public void addValue(int x){
        for(int i=0;i<total.size();i++){
            total.set(i,total.get(i) + x);
            if(total.get(i) > 21) {
                total.remove(i);
                i--;
            }
        }
    }

    public void ace(){
        int dimensiune = total.size();
        for(int i=0;i<dimensiune;i++)
        {
            total.add(total.get(i) + 11);
        }
        for(int i=0;i<dimensiune;i++)
        {
            total.set(i,total.get(i) + 1);
        }
        for(int i=0;i<total.size();i++)
        {
            if(total.get(i) > 21) {
                total.remove(i);
                i--;
            }
            if(i>=1 && total.get(i-1) == total.get(i)) {
                total.remove(i - 1);
                i--;
            }
        }
    }


    public void addCard(Card card){
        hand.add(card);
        if(card.getValue() == 1)
            ace();
        else
            addValue(card.getValue());
    }
}