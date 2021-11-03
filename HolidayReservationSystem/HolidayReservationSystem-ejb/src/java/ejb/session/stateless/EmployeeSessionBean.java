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
import util.exception.EmptyListException;

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
    public List<Employee> retrieveAllEmployees() throws EmptyListException {
        List<Employee> employees;
        Query query = em.createQuery("SELECT e FROM Employee e");
        employees = query.getResultList();
        
        if (employees.isEmpty()) throw new EmptyListException("List of employees is empty.\n");
        
        return employees;
    }

    @Override
    public boolean verifyLoginDetails(Long emId, String password) {
        return true;
    }

    @Override
    public boolean checkEmployeeExists(Long emId, String password) {
        Employee employee = this.getEmployeeById(emId);
        
        return employee.getPassword().equals(password);
    }

    @Override
    public Employee getEmployeeById(Long emId) {
        Employee employee = em.find(Employee.class, emId);
        
        return employee;
    }

    
}
