/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeEnum;
import util.exception.EmployeeQueryException;

/**
 *
 * @author brend
 */
public class SystemAdminModule {
    
    private PartnerSessionBeanRemote partnerSessionBean;
    private EmployeeSessionBeanRemote employeeSessionBean;
    
    SystemAdminModule(PartnerSessionBeanRemote partnerSessionBean, EmployeeSessionBeanRemote employeeSessionBean) {
        this.partnerSessionBean = partnerSessionBean;
        this.employeeSessionBean = employeeSessionBean;
    }
    
    public void doSystemAdminDashboardFeatures(Scanner sc, Long emId) {
        
        System.out.println("==== System Admin Dashboard Interface ====");
        System.out.println("> 1. Create New Employee");
        System.out.println("> 2. View All Employees");
        System.out.println("> 3. Create New Partner");
        System.out.println("> 4. View All Partners");
        System.out.println("> 5. Logout");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();
        
        switch (input) {
            case 1:
                System.out.println("You have selected 'Create New Employee'\n");
                doCreateNewEmployee(sc, emId);
                break;
            case 2:
                System.out.println("You have selected 'View All Employees'\n");
                doViewAllEmployees(sc, emId);
                break;
            case 3:
                System.out.println("You have selected 'Create New Partner'\n");
                doCreateNewPartner(sc, emId);
                break;
            case 4:
                System.out.println("You have selected 'View All Partners'\n");
                doViewAllPartners(sc, emId);
                break;
            case 5:
                System.out.println("You have logged out.\n");
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doSystemAdminDashboardFeatures(sc, emId);
                break;
        }
    }

    private void doCreateNewEmployee(Scanner sc, Long emId) {
        try {
            System.out.println("==== Create New Employee Interface ====");
            System.out.println("Please input the following details. To cancel creation at anytime, enter 'q'.");
            System.out.print("> First Name: ");
            String firstName = sc.nextLine();
            if (firstName.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            System.out.print("> Last Name: ");
            String lastName = sc.nextLine();
            if (lastName.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            System.out.print("> Password: ");
            String password = sc.nextLine();
            if (password.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            System.out.println("> Employee Role:\n   > 1. System Administrator"
                    + "\n   > 2. Operation Manager\n   > 3. Sales Manager"
                    + "\n   > 4. Guest Relation Manager");
            System.out.print("> ");
            String inputR = sc.next(); sc.nextLine();
            if (inputR.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            int inputRole = Integer.parseInt(inputR);
            System.out.println();
            
            String role;
            
            switch (inputRole) {
                case 1:
                    role = EmployeeEnum.SYSTEMADMIN.toString();
                    break;
                case 2:
                    role = EmployeeEnum.OPSMANAGER.toString();
                    break;
                case 3:
                    role = EmployeeEnum.SALESMANAGER.toString();
                    break;
                case 4:
                    role = EmployeeEnum.GRELMANAGER.toString();
                    break;
                default:
                    System.out.println("Invalid role input. Try again.");
                    doCreateNewEmployee(sc, emId);
                    return; //code ends
            }
            
            Employee newEmployee = new Employee(firstName, lastName, role, password);
            newEmployee = employeeSessionBean.getEmployeeById(employeeSessionBean.createNewEmployee(newEmployee));
            
            System.out.println("You have successfully created a new Employee.");
            System.out.println("Employee Details:");
            System.out.println("   > Employee ID: " + newEmployee.getEmployeeId());
            System.out.println("   > First Name: " + newEmployee.getFirstName());
            System.out.println("   > Last Name: " + newEmployee.getLastName());
            System.out.println("   > Employee Role: " + newEmployee.getEmployeeRole());
            System.out.println("   > Password: " + newEmployee.getPassword() + "\n");
            
            doSystemAdminDashboardFeatures(sc, emId);
            
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.");
            doCreateNewEmployee(sc, emId);
        }
    }

    private void doViewAllEmployees(Scanner sc, Long emId) {
        System.out.println("==== View Al Employees Interface ====");
        try {
            List<Employee> list = employeeSessionBean.retrieveAllEmployees();
            
            System.out.printf("\n%3s%15s%15s%25s", "ID", "First Name", "Last Name", "Employee Role");
            for (Employee em : list) {
                System.out.printf("\n%3s%15s%15s%25s", em.getEmployeeId(), 
                        em.getFirstName(), em.getLastName(), em.getEmployeeRole());
            }
            System.out.println();
            System.out.println();
            doSystemAdminDashboardFeatures(sc, emId);
        } catch (EmployeeQueryException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
    }
    
    private void doCreateNewPartner(Scanner sc, Long emId) {
        try {
            System.out.println("==== Create New Partner Interface ====");
            System.out.println("Enter partner details. To cancel creation at anytime, enter 'q'.");
            System.out.print("> First Name: ");
            String firstName = sc.nextLine();
            if (firstName.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            System.out.print("> Last Name: ");
            String lastName = sc.nextLine();
            if (lastName.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            System.out.print("> Email: ");
            String email = sc.nextLine();
            if (email.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            System.out.print("> Contact Number: ");
            String numberInput = sc.nextLine();
            if (numberInput.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            Long number = Long.parseLong(numberInput);
            
            if (partnerSessionBean.verifyRegisterDetails(firstName, lastName, number, email)) {
                Partner newPartner = new Partner(firstName, lastName, number, email);
                Long partnerId = partnerSessionBean.createNewPartner(newPartner);
                System.out.println("You have successfully created a new partner.\n");
                
                newPartner = partnerSessionBean.getPartnerByPartnerId(partnerId);
                System.out.println(":: Partner ID: " + newPartner.getCustomerId());
                System.out.println("   > Name: " + newPartner.getFirstName() + " " + newPartner.getLastName());
                System.out.println("   > Email: " + newPartner.getEmail());
                System.out.println("   > Contact Number: " + newPartner.getContactNumber());
                System.out.println();
                doSystemAdminDashboardFeatures(sc, emId);
            } else {
                System.out.println("You have inputted wrong details. Please try again.\n");
                
                doCreateNewPartner(sc, emId);
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.");
            doCreateNewPartner(sc, emId);
        }
        
    }
    
    private void doCancelledEntry(Scanner sc, Long emId) {
        System.out.println("\n You have cancelled entry. Taking you back to dashboard.\n");
        
        doSystemAdminDashboardFeatures(sc, emId);
    }
    
    private void doViewAllPartners(Scanner sc, Long emId) {
        System.out.println("==== View All Partners Interface ====");
        List<Partner> partners = partnerSessionBean.retrieveAllPartners();
        System.out.printf("\n%3s%15s%15s%20s%30s", "ID", "First Name", "Last Name", "Contact Number", "Email");
        for (Partner partner : partners) {
            System.out.printf("\n%3s%15s%15s%20s%30s", partner.getCustomerId(), 
                        partner.getFirstName(), partner.getLastName(), 
                        partner.getContactNumber(), partner.getEmail());
        }
        System.out.println(); System.out.println();
        doSystemAdminDashboardFeatures(sc, emId);
    }
}
