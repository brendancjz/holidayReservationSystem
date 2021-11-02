/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AllocationException;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author brend
 */
@Remote
public interface AllocationExceptionSessionBeanRemote {

    public Long createNewAllocationException(AllocationException exception);

    public AllocationException getAllocationExceptionByExceptionId(Long exceptionId);

    public List<AllocationException> retrieveAllExceptions();

    public void associateAllocationExceptionWithReservation(AllocationException exception, Long reservationId);
    
}
