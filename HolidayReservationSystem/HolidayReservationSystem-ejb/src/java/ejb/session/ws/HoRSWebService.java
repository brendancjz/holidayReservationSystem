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
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
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

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;

    
    @WebMethod(operationName = "checkPartnerExists")
    public Boolean checkPartnerExists(@WebParam(name = "email") String email) {
        return partnerSessionBean.checkPartnerExists(email);
    }

    @WebMethod(operationName = "createNewPartner")
    public Long createNewPartner(@WebParam(name = "p") Partner p) {
        
        return partnerSessionBean.createNewPartner(p);
    }
    
    @WebMethod(operationName = "getPartnerByEmail")
    public Partner getPartnerByEmail(@WebParam(name = "email") String email) {
        
        return partnerSessionBean.getPartnerByEmail(email);
    }
    
    @WebMethod(operationName = "retrieveAllPartners")
    public List<Partner> retrieveAllPartners() {
        
        try {
            List<Partner> partners = partnerSessionBean.retrieveAllPartners();
            for (Partner partner : partners ) {
                em.detach(partner);
                for (Reservation reservation : partner.getReservations()) {
                    em.detach(reservation);
                    reservation.setCustomer(null);
                }
            }
            return  partners;
        } catch (EmptyListException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
