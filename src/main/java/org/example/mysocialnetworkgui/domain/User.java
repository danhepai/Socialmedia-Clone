package org.example.mysocialnetworkgui.domain;

import java.util.Objects;
import org.example.mysocialnetworkgui.repository.PasswordHasher;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private String password;
    private String picture;

    public User (String firstName, String lastName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.picture = "default.jpg";
    }

    public User (String firstName, String lastName, String password, String picture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.picture = picture;
    }

    /**
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "firstName = " + firstName +
                ", lastName = " + lastName +
                ", userID = " + getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }

    public String getPassword() {
        return this.password;
    }

    public String getPicture() {
        return this.picture;
    }
}