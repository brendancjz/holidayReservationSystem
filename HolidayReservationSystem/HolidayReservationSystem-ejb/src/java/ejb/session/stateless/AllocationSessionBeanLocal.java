/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Allocation;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Local
public interface AllocationSessionBeanLocal {
    public Long createNewAllocation(Allocation allocation);
    public List<Allocation> retrieveAllAllocations() throws EmptyListException;

    public Allocation getAllocationByAllocationId(Long allocationId);

    public void associateAllocationWithRoom(Allocation allocation, Long roomId);

    public void associateAllocationWithReservation(Allocation allocation, Long reservationId);

    public void doRoomAllocation(LocalDate currDate);
    
}
