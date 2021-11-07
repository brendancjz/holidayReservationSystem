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
import util.exception.EmptyListException;

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
    public List<AllocationException> retrieveAllExceptions() throws EmptyListException {
        Query query = em.createQuery("SELECT a FROM AllocationException a");
        List<AllocationException> list = query.getResultList();
        if (list.isEmpty()) {
            throw new EmptyListException("List of Allocation Exceptions is empty.\n");
        }
        for (AllocationException ex : list) {
            ex.getReservation();
        }
        return list;
    }

    @Override
    public void associateAllocationExceptionWithReservation(AllocationException exception, Long reservationId) {
        Reservation r = em.find(Reservation.class, reservationId);

        exception.setReservation(r);
    }

    @Override
    public void createNewAllocationException(AllocationException exception, Long reservationId) {
        //ASSOCIATE
        this.associateAllocationExceptionWithReservation(exception, reservationId);
        this.createNewAllocationException(exception);
    }
}
