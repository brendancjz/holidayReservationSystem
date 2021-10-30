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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AllocationQueryException;

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
    public void associateAllocationsWithExistingRoomsAndReservation(Long allocationId, List<Room> allocatedRooms, Long reservationId) {
        System.out.println("Code reaches in associateAllocationsWithExistingRooms");
        Allocation allocation = this.getAllocationByAllocationId(allocationId);
        
        this.associateAllocationWithReservation(allocationId, reservationId);
        
        for (Room room : allocatedRooms) {
            this.associateAllocationWithRoom(allocationId, room.getRoomId());
            System.out.println("Code reaches here in the loop of associateAllocationsWithExistingRooms");
        }
    }

    @Override
    public List<Allocation> retrieveAllAllocations() throws AllocationQueryException {
        List<Allocation> list;
        Query query = em.createQuery("SELECT a FROM Allocation a");
        list = query.getResultList();
        if (list.isEmpty()) throw new AllocationQueryException("Allocation List is empty.");
        
        return list;
    }

    @Override
    public Allocation getAllocationForGuestForCurrentDay(Long guestId, LocalDate currDate) {
        
        Date curr = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        
        Query query = em.createQuery("SELECT a FROM Allocation a WHERE a.reservation.customer.customerId = :guestId AND a.currentDate = :currDate");
        query.setParameter("guestId", guestId);
        query.setParameter("currDate", curr);
        
        Allocation allocation = (Allocation) query.getSingleResult();
        
        allocation.getRooms().size();
        
        return allocation;
    }

    @Override
    public void associateAllocationWithRoom(Long allocationId, Long roomId) {
        Room room = em.find(Room.class, roomId);
        room.setIsVacant(Boolean.FALSE);
        Allocation allocation = em.find(Allocation.class, allocationId);
        allocation.getRooms().add(room);
    }

    @Override
    public void associateAllocationWithReservation(Long allocationId, Long reservationId) {
        Reservation r = em.find(Reservation.class, reservationId);
        Allocation allocation = em.find(Allocation.class, allocationId);
        allocation.setReservation(r);
    }

    @Override
    public Allocation getAllocationForGuestForCheckOutDay(Long customerId, LocalDate currDate) {
        Date curr = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        System.out.println("curr Day " + curr.toString());
        Query query = em.createQuery("SELECT a FROM Allocation a WHERE a.reservation.customer.customerId = :customerId AND a.reservation.endDate = :endDate");
        query.setParameter("customerId", customerId);
        query.setParameter("endDate", curr);
        
        Allocation allocation = (Allocation) query.getSingleResult();
        
        allocation.getRooms().size();
        
        return allocation;
    }

    
}
