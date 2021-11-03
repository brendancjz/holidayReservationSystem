/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Allocation;
import entity.Reservation;
import entity.Room;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Stateless
public class AllocationSessionBean implements AllocationSessionBeanRemote, AllocationSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewAllocation(Allocation allocation) {
        em.persist(allocation);
        em.flush();

        return allocation.getAllocationId();
    }

    @Override
    public Allocation getAllocationByAllocationId(Long allocationId) {
        Allocation allocation = em.find(Allocation.class, allocationId);
        allocation.getRooms().size();
        return allocation;
    }

    @Override
    public List<Allocation> retrieveAllAllocations() throws EmptyListException {
        List<Allocation> list;
        Query query = em.createQuery("SELECT a FROM Allocation a");
        list = query.getResultList();
        if (list.isEmpty()) {
            throw new EmptyListException("Allocation List is empty.\n");
        }

        return list;
    }

    @Override
    public Allocation getAllocationForGuestForCurrentDay(Long guestId, LocalDate currDate) {

        Date curr = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        Query query = em.createQuery("SELECT a FROM Allocation a WHERE a.reservation.customer.customerId = :guestId AND a.currentDate = :currDate");
        query.setParameter("guestId", guestId);
        query.setParameter("currDate", curr);

        try {
            Allocation allocation = (Allocation) query.getSingleResult();
            allocation.getRooms().size();
            allocation.getReservation();
            return allocation;
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public void associateAllocationWithRoom(Allocation allocation, Long roomId) {
        Room room = em.find(Room.class, roomId);
        room.setIsVacant(Boolean.FALSE);

        allocation.getRooms().add(room);
    }

    @Override
    public void associateAllocationWithReservation(Allocation allocation, Long reservationId) {
        Reservation r = em.find(Reservation.class, reservationId);

        allocation.setReservation(r);
    }

    @Override
    public Allocation getAllocationForGuestForCheckOutDay(Long customerId, LocalDate currDate) {
        Date curr = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        System.out.println("curr Day " + curr.toString());
        Query query = em.createQuery("SELECT a FROM Allocation a WHERE a.reservation.customer.customerId = :customerId AND a.reservation.endDate = :endDate");
        query.setParameter("customerId", customerId);
        query.setParameter("endDate", curr);

        try {
            Allocation allocation = (Allocation) query.getSingleResult();
            allocation.getRooms().size();
            allocation.getReservation();
            return allocation;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void removeAllocation(Allocation newAllocation) {
        newAllocation = em.merge(newAllocation);

        em.remove(newAllocation);
    }

    @Override
    public void dissociateAllocationWithRoomsAndReservation(Allocation newAllocation) {
        newAllocation = em.merge(newAllocation);
        int size = newAllocation.getRooms().size();
        for (int i = size - 1; i >= 0; i--) {
            newAllocation.getRooms().remove(i);
        }
        newAllocation.setReservation(null);
        newAllocation.setRoom(null);

        em.remove(newAllocation);
    }

}
