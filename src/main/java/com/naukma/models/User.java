package com.naukma.models;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name="user")
@NamedQuery(name="findByEmail", query="SELECT u FROM User u WHERE u.email = :email")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer id;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;

    @NotNull
    @NotEmpty
    @Column
    @Length(min = 8)
    private String password;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private boolean enabled = true;

    @GeneratedValue
    @Column
    private Date timeRegistered;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getTimeRegistered() {
        return timeRegistered;
    }

    public void setTimeRegistered(Date timeRegistered) {
        this.timeRegistered = timeRegistered;
    }
}
