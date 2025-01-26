package org.example.mysocialnetworkgui.domain;

public class Entity<ID>  {
    private ID id;

    /**
     * @return the id of the entity
     */
    public ID getId() {
        return id;
    }

    /**
     * @param id the new value of the id
     */
    public void setId(ID id) {
        this.id = id;
    }
}