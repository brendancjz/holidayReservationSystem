/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@WebService(serviceName = "HoRSWebService")
@Stateless()
public class HoRSWebService {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "retrieveAllPartners")
    public List<Partner> retrieveAllPartners() {
        try {
            List<Partner> partners = partnerSessionBean.retrieveAllPartners();
            
            for (Partner partner : partners) {
                
                for (Reservation reservation: partner.getReservations()) {
                    em.detach(reservation);
                }
                
                em.detach(partner);
            }
            
            return partners;
        } catch (EmptyListException ex) {
            return null;
        }
    }
    
    @WebMethod(operationName = "checkPartnerExists")
    public Boolean checkPartnerExists(@WebParam(name = "email") String email) {
        return partnerSessionBean.checkPartnerExists(email);
    }
    
    @WebMethod(operationName = "createNewPartner")
    public Long createNewPartner(@WebParam(name = "firstName") String firstName, 
                                 @WebParam(name = "lastName") String lastName, 
                                 @WebParam(name = "contactNumber") Long contactNumber, 
                                 @WebParam(name = "email") String email) {
        
        Partner partner = new Partner(firstName, lastName, contactNumber, email);
        return partnerSessionBean.createNewPartner(partner);
    }
    
    
}
