package deck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> pachet;

    public Deck(){
        pachet = new ArrayList<Card>();
        for(int i=1;i<=13;i++)
        {
            pachet.add(new Card(i,"Diamonds"));
            pachet.add(new Card(i,"Clubs"));
            pachet.add(new Card(i,"Hearts"));
            pachet.add(new Card(i,"Spades"));
        }
        Collections.shuffle(pachet);
    }

    public Card getCard() {
        Card temp;
        temp = pachet.get(pachet.size()-1);
        pachet.remove(pachet.size()-1);
        return temp;
    }
}
