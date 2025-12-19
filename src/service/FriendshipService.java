package service;

import domain.Friendship;
import repository.FriendshipRepository;
import repository.MemoryRepository;

import java.sql.SQLException;
import java.util.*;

public class FriendshipService {
    private final FriendshipRepository friendshipRepository;

    public FriendshipService(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
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

    private void dfs(Long u, Map<Long, Set<Long>> graph, Set<Long> visited) {
        visited.add(u);
        for (Long v : graph.getOrDefault(u, Set.of())) {
            if (!visited.contains(v)) dfs(v, graph, visited);
        }
    }

    public int getTotalCommunities() {
        List<Friendship> all = getFriendships(); // already handles SQLException
        Map<Long, Set<Long>> graph = new HashMap<>();
        for (Friendship f : all) {
            graph.computeIfAbsent(f.getUser1Id(), k -> new HashSet<>()).add(f.getUser2Id());
            graph.computeIfAbsent(f.getUser2Id(), k -> new HashSet<>()).add(f.getUser1Id());
        }

        Set<Long> visited = new HashSet<>();
        int count = 0;

        for (Long user : graph.keySet()) {
            if (!visited.contains(user)) {
                dfs(user, graph, visited);
                count++;
            }
        }
        return count;
    }

    public List<Long> getMostSocialCommunityWithMembers() {
        List<Friendship> all = getFriendships();
        Map<Long, Set<Long>> graph = new HashMap<>();
        for (Friendship f : all) {
            graph.computeIfAbsent(f.getUser1Id(), k -> new HashSet<>()).add(f.getUser2Id());
            graph.computeIfAbsent(f.getUser2Id(), k -> new HashSet<>()).add(f.getUser1Id());
        }

        Set<Long> visited = new HashSet<>();
        List<Long> largest = new ArrayList<>();

        for (Long user : graph.keySet()) {
            if (!visited.contains(user)) {
                List<Long> component = new ArrayList<>();
                Queue<Long> queue = new LinkedList<>();
                queue.add(user);
                visited.add(user);

                while (!queue.isEmpty()) {
                    Long u = queue.poll();
                    component.add(u);
                    for (Long v : graph.get(u)) {
                        if (!visited.contains(v)) {
                            visited.add(v);
                            queue.add(v);
                        }
                    }
                }

                if (component.size() > largest.size()) {
                    largest = component;
                }
            }
        }
        return largest;
    }



}