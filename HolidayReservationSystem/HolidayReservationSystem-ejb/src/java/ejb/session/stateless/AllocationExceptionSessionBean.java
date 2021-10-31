/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AllocationException;
import entity.Reservation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author brend
 */
@Stateless
public class AllocationExceptionSessionBean implements AllocationExceptionSessionBeanRemote, AllocationExceptionSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewAllocationException(AllocationException exception) {
        em.persist(exception);
        em.flush();
        
        return exception.getExceptionId();
    }
    
    @Override
    public AllocationException getAllocationExceptionByExceptionId(Long exceptionId) {
        AllocationException exception = em.find(AllocationException.class, exceptionId);
        
        return exception; 
    }
    
    @Override
    public List<AllocationException> retrieveAllExceptions() {
        Query query = em.createQuery("SELECT a FROM AllocationException a");
        
        return query.getResultList();
    }
    
    @Override
    public void associateAllocationExceptionWithReservation(Long exceptionId, Long reservationId) {
        Reservation r = em.find(Reservation.class, reservationId);
        AllocationException exception = em.find(AllocationException.class, exceptionId);
        exception.setReservation(r);
    }
}
