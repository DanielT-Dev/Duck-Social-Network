package service;

import domain.Friendship;
import repository.FriendshipRepository;
import repository.MemoryRepository;

import java.sql.SQLException;
import java.util.List;

public class FriendshipService {
    private final FriendshipRepository friendshipRepository;

    public FriendshipService(MemoryRepository memoryRepository) {
        this.friendshipRepository = new FriendshipRepository();
    }

    public void addFriendship(long user1Id, long user2Id) {
        if (user1Id == user2Id) {
            System.err.println("Error: Cannot be friends with yourself");
            return;
        }

        try {
            if (friendshipRepository.exists(user1Id, user2Id)) {
                System.err.println("Error: Friendship already exists");
                return;
            }

            friendshipRepository.save(new Friendship(user1Id, user2Id));
            System.out.println("Friendship added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding friendship: " + e.getMessage());
        }
    }

    public void removeFriendship(long user1Id, long user2Id) {
        try {
            friendshipRepository.delete(user1Id, user2Id);
            System.out.println("Friendship removed successfully!");
        } catch (SQLException e) {
            System.err.println("Error removing friendship: " + e.getMessage());
        }
    }

    public List<Friendship> getFriendships() {
        try {
            return friendshipRepository.findAll();
        } catch (SQLException e) {
            System.err.println("Error getting friendships: " + e.getMessage());
            return List.of();
        }
    }

    public List<Long> getFriendsOf(long userId) {
        try {
            return friendshipRepository.getFriendsOf(userId);
        } catch (SQLException e) {
            System.err.println("Error getting friends: " + e.getMessage());
            return List.of();
        }
    }

    public boolean areFriends(long user1Id, long user2Id) {
        try {
            return friendshipRepository.exists(user1Id, user2Id);
        } catch (SQLException e) {
            System.err.println("Error checking friendship: " + e.getMessage());
            return false;
        }
    }
}