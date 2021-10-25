/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author brend
 */
@Entity
public class Partner extends Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public Partner() {
        super();
    }
    
    public Partner(String firstName, String lastName, Long contactNumber, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.email = email;
        
    }  
}
