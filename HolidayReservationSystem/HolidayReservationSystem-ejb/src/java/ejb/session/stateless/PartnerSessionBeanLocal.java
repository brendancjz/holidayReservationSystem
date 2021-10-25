/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author brend
 */
@Local
public interface PartnerSessionBeanLocal {
    public boolean verifyLoginDetails(String email);

    public boolean verifyRegisterDetails(String firstName, String lastName, Long contactNum, String email);

    public boolean checkPartnerExists(String email);

    public Long createNewPartner(Partner p);

    public Partner getPartnerByEmail(String email);

    public List<Partner> retrieveAllPartners();
}
