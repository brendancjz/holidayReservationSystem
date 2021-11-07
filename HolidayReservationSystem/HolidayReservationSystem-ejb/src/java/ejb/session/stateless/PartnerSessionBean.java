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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public boolean checkPartnerExists(String email) {
        boolean partnerExists = false;

        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.email = :email");
        query.setParameter("email", email);
        
        try {
            Partner partner = (Partner) query.getSingleResult();
            
            return true;
        } catch (NoResultException e) {
            return false;
        }
        
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
    public List<Partner> retrieveAllPartners() throws EmptyListException {
        Query query = em.createQuery("SELECT p FROM Partner p");
        List<Partner> partners = query.getResultList();
        if (partners.isEmpty()) throw new EmptyListException("List of Partners is empty.\n");
        for (Partner p : partners) {
            p.getReservations().size();
        }

        return partners;
    }

    @Override
    public Partner getPartnerByPartnerId(Long partnerId) {
        return em.find(Partner.class, partnerId);
    }

    @Override
    public boolean verifyLoginDetails(String email) {
        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.email = :email");
        query.setParameter("email", email);
        try {
            Partner p = (Partner) query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    
    @Override
    public boolean verifyRegisterDetails(String firstName, String lastName, Long contactNum, String email) {
        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.email = :email");
        query.setParameter("email", email);
        try {
            Partner p = (Partner) query.getSingleResult();
            return false;
        } catch (NoResultException e) {
            return true;
        }
        
    }
}
