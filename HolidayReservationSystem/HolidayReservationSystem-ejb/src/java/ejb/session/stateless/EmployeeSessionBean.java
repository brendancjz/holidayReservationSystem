/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmployeeQueryException;
import util.exception.FindEmployeeException;

/**
 *
 * @author brend
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewEmployee(Employee employee) {
        em.persist(employee);
        em.flush();
        
        return employee.getEmployeeId();
    }
    
    @Override
    public List<Employee> retrieveAllEmployees() throws EmployeeQueryException {
        List<Employee> employees;
        Query query = em.createQuery("SELECT e FROM Employee e");
        employees = query.getResultList();
        
        if (employees.isEmpty()) throw new EmployeeQueryException("List of employees is empty.");
        
        return employees;
    }

    @Override
    public boolean verifyLoginDetails(Long emId, String password) {
        return true;
    }

    @Override
    public boolean checkEmployeeExists(Long emId, String password) throws FindEmployeeException {
        Employee employee = this.getEmployeeById(emId);
        
        return employee.getPassword().equals(password);
    }

    @Override
    public Employee getEmployeeById(Long emId) throws FindEmployeeException {
        Employee employee = em.find(Employee.class, emId);
        if (employee == null) throw new FindEmployeeException("emId cannot be found.");
        
        return employee;
    }

    
}
