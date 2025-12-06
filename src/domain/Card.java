package domain;

import java.util.List;

public class Card {
    private final long id;
    private final String numeCard;
    private final List<Duck> membri;

    public Card(long id, String numeCard, List<Duck> membri) {
        this.id = id;
        this.numeCard = numeCard;
        this.membri = membri;
    }

    public long getId() {
        return id;
    }

    public String getNumeCard() {
        return numeCard;
    }

    public List<Duck> getMembri() {
        return membri;
    }

    public double getPerformantaMedie() {
        if (membri == null || membri.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Duck duck : membri) {
            total += (duck.getViteza() / duck.getRezistenta());
        }
        return total / membri.size();
    }

    @Override
    public String toString() {
        return "Card [id=" + id +
                ", numeCard=" + numeCard +
                ", numarMembri=" + (membri != null ? membri.size() : 0) +
                ", performantaMedie=" + String.format("%.2f", getPerformantaMedie()) +
                "]";
    }
}