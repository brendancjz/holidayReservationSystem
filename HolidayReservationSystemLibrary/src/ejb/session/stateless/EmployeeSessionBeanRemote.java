/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Remote
public interface EmployeeSessionBeanRemote {

    public List<Employee> retrieveAllEmployees() throws EmptyListException;

    public Long createNewEmployee(Employee employee);
    
    public boolean checkEmployeeExists(String username, String password);

    public Employee getEmployeeById(Long emId);

    public Employee getEmployeeByUsername(String username);
    
}
