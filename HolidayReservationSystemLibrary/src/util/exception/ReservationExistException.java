/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author brend
 */
public class ReservationExistException extends Exception {

    public ReservationExistException(String message) {
        super(message);
    }

}
