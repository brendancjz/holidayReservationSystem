/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Allocation;
import entity.Room;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AllocationQueryException;

/**
 *
 * @author brend
 */
@Remote
public interface AllocationSessionBeanRemote {
    public Long createNewAllocation(Allocation allocation);
    public Allocation getAllocationByAllocationId(Long allocationId);
    public void associateAllocationsWithExistingRoomsAndReservation(Long allocationId, List<Room> availRooms, Long reservationId);
    public List<Allocation> retrieveAllAllocations() throws AllocationQueryException;
    public Allocation getAllocationForGuestForCurrentDay(Long guestId, LocalDate currDate);
    public void associateAllocationWithRoom(Long allocationId, Long roomId);
    public void associateAllocationWithReservation(Long allocationId, Long reservationId);

    public Allocation getAllocationForGuestForCheckOutDay(Long customerId, LocalDate currDate);
    
}
