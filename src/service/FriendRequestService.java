package service;

import domain.FriendRequest;
import repository.DuckRepository;
import repository.FriendRequestRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import repository.PersonRepository;

import java.sql.SQLException;
import java.util.List;

public class FriendRequestService {
    private final DuckRepository duckRepository;
    private final PersonRepository personRepository;
    private final FriendRequestRepository friendRequestRepository;
    public final FriendshipService friendshipService;

    // Observable list for UI updates
    private final ObservableList<FriendRequest> pendingRequests = FXCollections.observableArrayList();

    public FriendRequestService(DuckRepository duckRepository, PersonRepository personRepository, FriendRequestRepository friendRequestRepository, FriendshipService friendshipService) {
        this.duckRepository = duckRepository;
        this.personRepository = personRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipService = friendshipService;
    }

    public ObservableList<FriendRequest> getPendingRequestsObservable() {
        return pendingRequests;
    }

    public void sendRequest(long senderId, long receiverId) {
        if (senderId == receiverId) {
            System.err.println("Error: Cannot send request to yourself");
            return;
        }

        try {
            if (friendshipService.areFriends(senderId, receiverId)) {
                System.err.println("Error: Users are already friends");
                return;
            }

            if (friendRequestRepository.exists(senderId, receiverId)) {
                System.err.println("Error: Request already exists");
                return;
            }

            FriendRequest fr = new FriendRequest(senderId, receiverId);
            friendRequestRepository.save(fr);
            pendingRequests.add(fr); // Notify observers
            System.out.println("Friend request sent successfully!");
        } catch (SQLException e) {
            System.err.println("Error sending friend request: " + e.getMessage());
        }
    }

    public void cancelRequest(long senderId, long receiverId) {
        try {
            friendRequestRepository.delete(senderId, receiverId);
            pendingRequests.removeIf(fr -> fr.getSenderId() == senderId && fr.getReceiverId() == receiverId);
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
            pendingRequests.removeIf(fr -> fr.getSenderId() == senderId && fr.getReceiverId() == receiverId);

            // Only create friendship if accepted
            if (status.equalsIgnoreCase("accepted")) {
                friendshipService.addFriendship(senderId, receiverId);
            }

            System.out.println("Friend request handled: " + status);
        } catch (SQLException e) {
            System.err.println("Error responding to friend request: " + e.getMessage());
        }
    }

    public void refreshPendingRequests(long userId) {
        try {
            List<FriendRequest> requests = friendRequestRepository.findPendingForUser(userId);
            pendingRequests.setAll(requests); // Notify observers
        } catch (SQLException e) {
            System.err.println("Error refreshing pending requests: " + e.getMessage());
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

    public boolean hasPendingRequests(long userId) {
        return !pendingRequests.isEmpty();
    }
}
