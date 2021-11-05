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
    public Long createNewPartner(@WebParam(name = "firstName") String firstName,
            @WebParam(name = "lastName") String lastName,
            @WebParam(name = "contactNum") Long contactNum,
            @WebParam(name = "email") String email) {

        Partner p = new Partner(firstName, lastName, contactNum, email);
        return partnerSessionBean.createNewPartner(p);
    }

    @WebMethod(operationName = "getPartnerByEmail")
    public Partner getPartnerByEmail(@WebParam(name = "email") String email) {

        Partner partner = partnerSessionBean.getPartnerByEmail(email);

        em.detach(partner);
        for (Reservation reservation : partner.getReservations()) {
            em.detach(reservation);
            reservation.setCustomer(null);
        }
        return partner;
    }

    @WebMethod(operationName = "retrieveAllPartners")
    public List<Partner> retrieveAllPartners() {

        try {
            List<Partner> partners = partnerSessionBean.retrieveAllPartners();
            for (Partner partner : partners) {
                em.detach(partner);
                for (Reservation reservation : partner.getReservations()) {
                    em.detach(reservation);
                    reservation.setCustomer(null);
                }
            }
            return partners;
        } catch (EmptyListException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    @WebMethod(operationName = "getPartnerByPartnerId")
    public Partner getPartnerByPartnerId(@WebParam(name = "partnerId") Long partnerId) {
        Partner partner = partnerSessionBean.getPartnerByPartnerId(partnerId);

        em.detach(partner);
        for (Reservation reservation : partner.getReservations()) {
            em.detach(reservation);
            reservation.setCustomer(null);
        }
        return partner;
    }

    @WebMethod(operationName = "verifyLoginDetails")
    public boolean verifyLoginDetails(@WebParam(name = "email") String email) {

        return partnerSessionBean.verifyLoginDetails(email);
    }

    @WebMethod(operationName = "verifyRegisterDetails")
    public boolean verifyRegisterDetails(@WebParam(name = "firstName") String firstName,
            @WebParam(name = "lastName") String lastName,
            @WebParam(name = "contactNum") Long contactNum,
            @WebParam(name = "email") String email) {

        return partnerSessionBean.verifyRegisterDetails(firstName, lastName, contactNum, email);
    }

}
