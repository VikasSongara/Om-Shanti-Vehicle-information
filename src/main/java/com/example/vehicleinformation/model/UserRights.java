package com.example.vehicleinformation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_rights")
public class UserRights {

    public static final long SINGLETON_ID = 1L;

    @Id
    private Long id = SINGLETON_ID;

    @Column(name = "allow_edit", nullable = false)
    private boolean allowEdit;

    @Column(name = "allow_delete", nullable = false)
    private boolean allowDelete;

    public UserRights() {
    }

    public UserRights(boolean allowEdit, boolean allowDelete) {
        this.id = SINGLETON_ID;
        this.allowEdit = allowEdit;
        this.allowDelete = allowDelete;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAllowEdit() {
        return allowEdit;
    }

    public void setAllowEdit(boolean allowEdit) {
        this.allowEdit = allowEdit;
    }

    public boolean isAllowDelete() {
        return allowDelete;
    }

    public void setAllowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
    }
}
