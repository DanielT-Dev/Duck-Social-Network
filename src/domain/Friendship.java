package domain;

public class Friendship {
    private final long user1Id;
    private final long user2Id;

    public Friendship(long user1Id, long user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public long getUser1Id() {
        return user1Id;
    }

    public long getUser2Id() {
        return user2Id;
    }
}