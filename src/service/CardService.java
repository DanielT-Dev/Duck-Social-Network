package service;

import domain.Card;
import domain.Duck;
import repository.CardRepository;
import repository.DuckRepository;
import repository.MemoryRepository;

import java.sql.SQLException;
import java.util.List;

public class CardService {
    private final CardRepository cardRepository;
    private final DuckRepository duckRepository;

    public CardService(CardRepository cardRepository, DuckRepository duckRepository) {
        this.cardRepository = cardRepository;
        this.duckRepository = duckRepository;
    }

    public void addCard(Card card) {
        try {
            cardRepository.save(card);
            System.out.println("Card added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding card: " + e.getMessage());
        }
    }

    public void removeCard(long cardId) {
        try {
            cardRepository.delete(cardId);
            System.out.println("Card deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting card: " + e.getMessage());
        }
    }

    public Card getCard(long cardId) {
        try {
            return cardRepository.findById(cardId);
        } catch (SQLException e) {
            System.err.println("Error finding card: " + e.getMessage());
            return null;
        }
    }

    public List<Card> getAllCards() {
        try {
            return cardRepository.findAll();
        } catch (SQLException e) {
            System.err.println("Error getting cards: " + e.getMessage());
            return List.of();
        }
    }

    public void addMemberToCard(long cardId, long duckId) {
        try {
            // Check if duck exists
            Duck duck = duckRepository.findById(duckId);
            if (duck == null) {
                System.err.println("Error: Duck with ID " + duckId + " not found");
                return;
            }

            cardRepository.addMember(cardId, duckId);
            System.out.println("Duck added to card successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding member to card: " + e.getMessage());
        }
    }

    public void removeMemberFromCard(long cardId, long duckId) {
        try {
            cardRepository.removeMember(cardId, duckId);
            System.out.println("Duck removed from card successfully!");
        } catch (SQLException e) {
            System.err.println("Error removing member from card: " + e.getMessage());
        }
    }

    public double getCardPerformance(long cardId) {
        try {
            Card card = cardRepository.findById(cardId);
            return card != null ? card.getPerformantaMedie() : 0.0;
        } catch (SQLException e) {
            System.err.println("Error getting card performance: " + e.getMessage());
            return 0.0;
        }
    }
}