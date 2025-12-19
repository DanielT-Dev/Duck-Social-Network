package service;

import domain.Event;
import domain.User;
import repository.EventRepository;
import java.sql.SQLException;
import java.util.List;

public class EventService {
    private EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository =  eventRepository;
    }

    /**
     * Create a new event
     */
    public Event createEvent(String name, String description) throws SQLException {
        Event event = new Event(name, description);
        // Generate ID (you might want to use a proper ID generator)
        event.setId(System.currentTimeMillis());
        eventRepository.save(event);
        return event;
    }

    /**
     * Get event by ID
     */
    public Event getEventById(long eventId) throws SQLException {
        Event event = eventRepository.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found with ID: " + eventId);
        }
        return event;
    }

    /**
     * Get all events
     */
    public List<Event> getAllEvents() throws SQLException {
        return eventRepository.findAll();
    }

    /**
     * Search events by name
     */
    public List<Event> searchEventsByName(String name) throws SQLException {
        return eventRepository.findByName(name);
    }

    /**
     * Update an existing event
     */
    public Event updateEvent(long eventId, String name, String description) throws SQLException {
        Event event = getEventById(eventId);

        event.setName(name);
        event.setDescription(description);
        eventRepository.update(event);

        // Notify subscribers about the update
        event.notifySubscribers("Event details have been updated");

        return event;
    }

    /**
     * Delete an event
     */
    public void deleteEvent(long eventId) throws SQLException {
        Event event = eventRepository.findById(eventId);
        if (event != null) {
            // Notify subscribers before deletion
            event.notifySubscribers("This event has been cancelled and will be removed");
        }
        eventRepository.delete(eventId);
    }

    /**
     * Subscribe a user to an event
     */
    public void subscribeToEvent(long eventId, User user) throws SQLException {
        Event event = getEventById(eventId);

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Add to database
        eventRepository.addSubscriber(eventId, user.getId());

        // Add to in-memory object
        event.subscribe(user);

        // Notify about new subscription
        event.notifySubscribers(user.getUsername() + " has subscribed to the event");
    }

    /**
     * Unsubscribe a user from an event
     */
    public void unsubscribeFromEvent(long eventId, User user) throws SQLException {
        Event event = getEventById(eventId);

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Remove from database
        eventRepository.removeSubscriber(eventId, user.getId());

        // Remove from in-memory object
        event.unsubscribe(user);
    }

    /**
     * Get all subscribers for an event
     */
    public List<User> getEventSubscribers(long eventId) throws SQLException {
        getEventById(eventId); // Validate event exists
        return eventRepository.getSubscribers(eventId);
    }

    /**
     * Get events that a user is subscribed to
     */
    public List<Event> getUserSubscribedEvents(long userId) throws SQLException {
        return eventRepository.findByUserId(userId);
    }

    /**
     * Send notification to all subscribers of an event
     */
    public void notifyEventSubscribers(long eventId, String message) throws SQLException {
        Event event = getEventById(eventId);
        event.notifySubscribers(message);
    }

    /**
     * Get subscriber count for an event
     */
    public int getSubscriberCount(long eventId) throws SQLException {
        Event event = getEventById(eventId);
        return event.getSubscriberCount();
    }

    /**
     * Check if a user is subscribed to an event
     */
    public boolean isUserSubscribed(long eventId, User user) throws SQLException {
        Event event = getEventById(eventId);
        return event.isSubscribed(user);
    }

    /**
     * Get events created after a specific timestamp
     * (You would need to add this method to EventRepository first)
     */
    public List<Event> getRecentEvents(java.util.Date since) throws SQLException {
        // This is a placeholder - you would need to implement this in EventRepository
        List<Event> allEvents = eventRepository.findAll();
        return allEvents.stream()
                .filter(event -> event.getCreatedAt() != null &&
                        event.getCreatedAt().after(new java.sql.Timestamp(since.getTime())))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Update event repository (for dependency injection/testing)
     */
    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Get event repository
     */
    public EventRepository getEventRepository() {
        return eventRepository;
    }
}