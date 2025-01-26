package org.example.mysocialnetworkgui.domain;
import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Long> {
    private Long firstUserId;
    private Long secondUserId;
    private LocalDateTime date;

    private Status status;

    public Friendship(Long firstUserId, Long secondUserId) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.date = LocalDateTime.now();
        this.status = Status.PENDING;
    }

    public Friendship(Long firstUserId, Long secondUserId, LocalDateTime date) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.date = date;
        this.status = Status.PENDING;
    }
    /**
     * @return the id of the first user
     */
    public Long getFirstUserId() {
        return firstUserId;
    }

    /**
     * @param firstUserId the new id of the first user
     */
    public void setFirstUserId(Long firstUserId) {
        this.firstUserId = firstUserId;
    }

    /**
     * @return the id of the second user
     */
    public Long getSecondUserId() {
        return secondUserId;
    }

    /**
     * @param secondUserId the new id of the second user
     */
    public void setSecondUserId(Long secondUserId) {
        this.secondUserId = secondUserId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "firstUser = " + getFirstUserId() +
                ", secondUser = " + getSecondUserId() +
                ", friendshipID = " + getId() +
                ", date = " + getDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship friendship = (Friendship) o;
        return Objects.equals(firstUserId, friendship.firstUserId) && Objects.equals(secondUserId, friendship.secondUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstUserId, secondUserId);
    }
}
