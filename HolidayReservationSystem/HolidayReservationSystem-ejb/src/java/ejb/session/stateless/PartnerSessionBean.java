/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author brend
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {
    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public boolean verifyLoginDetails(String email) {
        return true;
    }
    
    @Override
    public boolean verifyRegisterDetails(String firstName, String lastName, Long contactNum, String email) {
        return true;
    }
    
    @Override
    public boolean checkPartnerExists(String email) {
        boolean partnerExists = false;
        
        try {
            Query query = em.createQuery("SELECT p FROM Partner p");
        
            
            List<Partner> partnerList = query.getResultList();
            for (Partner p : partnerList) {
                if (p.getEmail().equals(email)) {
                    partnerExists = true;
                }
            }
        } catch (Exception e) {
            System.out.println("** checkCustomerExists throwing error " + e.getMessage());
        }
   
        return partnerExists;
    }
    
    @Override
    public Long createNewPartner(Partner p) {
        em.persist(p);
        em.flush();
        
        return p.getCustomerId();
    }
    
    @Override
    public Partner getPartnerByEmail(String email) {
        Partner p = null;
        try {
            Query query = em.createQuery("SELECT p FROM Partner p WHERE p.email = :email");
            query.setParameter("email", email);
            
            p = (Partner) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("** getGuestByEmail throwing error " + e.getMessage());
        }
        
        return p;
    }
    
    @Override
    public List<Partner> retrieveAllPartners() {
        Query query = em.createQuery("SELECT p FROM Partner p");
        List<Partner> partners = query.getResultList();
        for (Partner p: partners) {
            p.getReservations().size();
        }
        
        return partners;
    }

    @Override
    public Partner getPartnerByPartnerId(Long partnerId) {
        return em.find(Partner.class, partnerId);
    }
}
