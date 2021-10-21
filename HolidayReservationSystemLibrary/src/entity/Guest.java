/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author brend
 */
@Entity
public class Guest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;
    @NotNull
    @Size(min=3, max=15)
    private String firstName;
    @NotNull
    @Size(min=3, max=15)
    private String lastName;
    @NotNull
    @Digits(integer=8, fraction=0)
    private Long contactNumber;
    @NotNull
    @Email
    private String email;
    @OneToMany(mappedBy = "guest")
    private ArrayList<Reservation> reservations;

    public Guest() {
        this.reservations = new ArrayList<>();
    }

    public Guest(String firstName, String lastName, Long contactNumber, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.email = email;
        
    }

    public Long getGuestId() {
        return guestId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setContactNumber(Long contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setReservations(ArrayList<Reservation> reservations) {
        this.reservations = reservations;
    }

    
    
}
