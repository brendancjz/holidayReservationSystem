/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Inheritance(strategy= InheritanceType.JOINED)
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long customerId;
    @Column(nullable = false, length = 30)
    @NotNull
    @Size(min=1, max=30)
    protected String firstName;
    @Column(nullable = false, length = 30)
    @NotNull
    @Size(min=1, max=30)
    protected String lastName;
    @Column(nullable = false, length = 8, unique = true)
    @NotNull
    @Digits(integer=8, fraction=0)
    protected Long contactNumber;
    @Column(nullable = false, length = 50, unique = true)
    @NotNull
    @Email
    protected String email;
    @OneToMany(mappedBy = "customer")
    protected ArrayList<Reservation> reservations;

    public Customer() {
        this.reservations = new ArrayList<>();
    }
    
    public Customer(String firstName, String lastName, Long contactNumber, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.email = email;
        
    }
    
    public Long getCustomerId() {
        return customerId;
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

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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
