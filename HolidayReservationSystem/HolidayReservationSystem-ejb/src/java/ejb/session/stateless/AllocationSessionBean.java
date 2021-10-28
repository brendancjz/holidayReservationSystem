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
    public void associateAllocationsWithExistingRooms(Long allocationId, List<Room> availRooms) {
        Allocation allocation = this.getAllocationByAllocationId(allocationId);
        
        allocation.getRooms().addAll(availRooms);
    }
}
