/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmployeeQueryException;
import util.exception.FindEmployeeException;

/**
 *
 * @author brend
 */
@Remote
public interface EmployeeSessionBeanRemote {

    public List<Employee> retrieveAllEmployees() throws EmployeeQueryException;

    public Long createNewEmployee(Employee employee);

    public boolean verifyLoginDetails(Long emId, String password);

    public boolean checkEmployeeExists(Long emId, String password) throws FindEmployeeException;

    public Employee getEmployeeById(Long emId) throws FindEmployeeException;
    
}
