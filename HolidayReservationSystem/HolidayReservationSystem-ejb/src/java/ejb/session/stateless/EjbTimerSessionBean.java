/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import java.time.LocalDate;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

/**
 *
 * @author brend
 */
@Stateless
public class EjbTimerSessionBean implements EjbTimerSessionBeanRemote, EjbTimerSessionBeanLocal {

    @EJB
    private AllocationSessionBeanLocal allocationSessionBean;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    
    @Schedule(dayOfWeek="*", hour = "2", minute = "0", second = "0")
    public void automaticTimer()
    {   
        LocalDate  currDate= LocalDate.now();
        allocationSessionBean.doRoomAllocation(currDate);
       
    }
}
