/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Allocation;
import entity.Room;
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
    public void associateAllocationsWithExistingRooms(Long allocationId, List<Room> allocatedRooms) {
        System.out.println("Code reaches in associateAllocationsWithExistingRooms");
        Allocation allocation = this.getAllocationByAllocationId(allocationId);
        
        for (Room room : allocatedRooms) {
            em.merge(room);
            room.setIsVacant(false);
            allocation.getRooms().add(room);
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

    
}
