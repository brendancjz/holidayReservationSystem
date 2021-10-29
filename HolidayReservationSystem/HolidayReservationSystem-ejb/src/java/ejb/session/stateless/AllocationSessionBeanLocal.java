/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Allocation;
import entity.Room;
import java.util.List;
import javax.ejb.Local;
import util.exception.AllocationQueryException;

/**
 *
 * @author brend
 */
@Local
public interface AllocationSessionBeanLocal {

    public List<Allocation> retrieveAllAllocations() throws AllocationQueryException;

    public Allocation getAllocationByAllocationId(Long allocationId);
    
}
