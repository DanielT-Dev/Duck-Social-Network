package service;

import domain.FriendRequest;
import repository.FriendRequestRepository;

import java.sql.SQLException;
import java.util.List;

public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository = new FriendRequestRepository();
    private final FriendshipService friendshipService = new FriendshipService();

    public void sendRequest(long senderId, long receiverId) {
        if (senderId == receiverId) {
            System.err.println("Error: Cannot send request to yourself");
            return;
        }

        try {
            if (friendRequestRepository.exists(senderId, receiverId)) {
                System.err.println("Error: Request already exists");
                return;
            }

            friendRequestRepository.save(new FriendRequest(senderId, receiverId));
            System.out.println("Friend request sent successfully!");
        } catch (SQLException e) {
            System.err.println("Error sending friend request: " + e.getMessage());
        }
    }

    public void cancelRequest(long senderId, long receiverId) {
        try {
            friendRequestRepository.delete(senderId, receiverId);
            System.out.println("Friend request cancelled successfully!");
        } catch (SQLException e) {
            System.err.println("Error cancelling friend request: " + e.getMessage());
        }
    }

    public void respondToRequest(long senderId, long receiverId, String status) {
        if (!status.equalsIgnoreCase("accepted") && !status.equalsIgnoreCase("rejected")) {
            System.err.println("Error: Invalid status");
            return;
        }

        try {
            if (!friendRequestRepository.exists(senderId, receiverId)) {
                System.err.println("Error: Request does not exist");
                return;
            }

            // Always delete the request
            friendRequestRepository.delete(senderId, receiverId);

            // Only create friendship if accepted
            if (status.equalsIgnoreCase("accepted")) {
                friendshipService.addFriendship(senderId, receiverId);
            }

            System.out.println("Friend request handled: " + status);
        } catch (SQLException e) {
            System.err.println("Error responding to friend request: " + e.getMessage());
        }
    }

    public List<FriendRequest> getAllRequests() {
        try {
            return friendRequestRepository.findAll();
        } catch (SQLException e) {
            System.err.println("Error getting friend requests: " + e.getMessage());
            return List.of();
        }
    }

    public List<FriendRequest> getPendingRequestsForUser(long userId) {
        try {
            return friendRequestRepository.findPendingForUser(userId);
        } catch (SQLException e) {
            System.err.println("Error getting pending requests: " + e.getMessage());
            return List.of();
        }
    }

    public boolean requestExists(long senderId, long receiverId) {
        try {
            return friendRequestRepository.exists(senderId, receiverId);
        } catch (SQLException e) {
            System.err.println("Error checking request existence: " + e.getMessage());
            return false;
        }
    }

    public int getTotalRequests() {
        try {
            return friendRequestRepository.getTotalCount();
        } catch (SQLException e) {
            System.err.println("Error getting total request count: " + e.getMessage());
            return 0;
        }
    }
}
