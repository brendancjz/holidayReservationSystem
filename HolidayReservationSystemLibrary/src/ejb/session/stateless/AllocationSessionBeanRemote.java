/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Allocation;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Remote
public interface AllocationSessionBeanRemote {
    public Long createNewAllocation(Allocation allocation);
    public Allocation getAllocationByAllocationId(Long allocationId);
    public List<Allocation> retrieveAllAllocations() throws EmptyListException;
    public List<Allocation> getAllocationsForGuestForCurrentDay(Long guestId, LocalDate currDate) throws EmptyListException;
    public void associateAllocationWithRoom(Allocation allocation, Long roomId);
    public void associateAllocationWithReservation(Allocation allocation, Long reservationId);

    public List<Allocation> getAllocationsForGuestForCheckOutDay(Long customerId, LocalDate currDate) throws EmptyListException;

    public void removeAllocation(Allocation newAllocation);

    public void dissociateAllocationWithRoomsAndReservation(Allocation newAllocation);

    public Long createNewAllocation(Allocation newAllocation, Long reservationId);

    public void associateAllocationWithRooms(Long newAllocationId, List<Long> allocatedRoomIds);
    
}
