/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.RoomManagementSessionBeanRemote;
import entity.Employee;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.ejb.EJB;
import util.enumeration.EmployeeEnum;
import util.enumeration.RoomRateEnum;
import util.exception.EmployeeQueryException;
import util.exception.FindEmployeeException;
import util.exception.FindRoomRateException;
import util.exception.RoomRateQueryException;
import util.exception.RoomTypeQueryException;

/**
 *
 * @author brend
 */
public class Main {

    @EJB
    private static RoomManagementSessionBeanRemote roomManagementSessionBean;

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBean;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        doMainApp(sc);
    }
    
    public static void doMainApp(Scanner sc) {
        
        System.out.println("=== Welcome to HoRS Management Client. ===");
        System.out.println("Select an action:");
        System.out.println("> 1. Login");
        System.out.println("> 2. Exit");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();
        System.out.println();
        
        if (input == 1) {
            doLogin(sc);
            
        } else if (input == 2) {
            doExit();
            
        } else {
            doMainApp(sc);
        }
    }

    private static void doExit() {
        System.out.println("You have exited. Goodbye.");
    }

    private static void doLogin(Scanner sc) {
        System.out.println("==== Login Interface ====");
            System.out.println("Enter login details:");
            System.out.print("> Employee ID: ");
            Long emId = sc.nextLong();
            sc.nextLine();
            System.out.print("> Password: ");
            String password = sc.nextLine();
            try {
                if (employeeSessionBean.verifyLoginDetails(emId, password) && 
                        employeeSessionBean.checkEmployeeExists(emId, password)) {

                    Employee currEm = employeeSessionBean.getEmployeeById(emId);
                    System.out.println("Welcome " + currEm.getEmployeeRole() + " " + currEm.getFirstName() + "\n");

                    doDashboardFeatures(sc, currEm.getEmployeeId(), currEm.getEmployeeRole());
                } else {
                    System.out.println("No account match or wrong login details. Try again.\n");
                    doLogin(sc);
                }
            } catch (FindEmployeeException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e ) {
                System.out.println("doLogin throwing error: " + e.getMessage());
            }
            
    }
    
    private static void doDashboardFeatures(Scanner sc, Long emId, String emRole) {
        System.out.println("==== " + emRole + " Dashboard Interface ====");
        
        if (emRole.equals(EmployeeEnum.SYSTEMADMIN.toString())) {
            doSystemAdminDashboardFeatures(sc, emId, emRole);
        } else if (emRole.equals(EmployeeEnum.OPSMANAGER.toString())) {
            doOpsManagerDashboardFeatures(sc, emId, emRole);
        } else if (emRole.equals(EmployeeEnum.SALESMANAGER.toString())) {
            doSalesManagerDashboardFeatures(sc, emId, emRole);
        } else if (emRole.equals(EmployeeEnum.GRELMANAGER.toString())) {
            doGRelManagerDashboardFeatures(sc, emId, emRole);
        }

    }

    private static void doSystemAdminDashboardFeatures(Scanner sc, Long emId, String emRole) {
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
                doCreateNewEmployee(sc, emId, emRole);
                break;
            case 2:
                System.out.println("You have selected 'View All Employees'\n");
                doViewAllEmployees(sc, emId, emRole);
                break;
            case 3:
                System.out.println("You have selected 'Create New Partner'\n");
                //doViewMyReservationDetails(sc, guestId);
                break;
            case 4:
                System.out.println("You have selected 'View All Partners'\n");
                //doViewAllMyReservations(sc, guestId);
                break;
            case 5:
                System.out.println("You have logged out.\n");
                doMainApp(sc);
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doDashboardFeatures(sc, emId, emRole);
                break;
        }
    }

    private static void doSalesManagerDashboardFeatures(Scanner sc, Long emId, String emRole) {
        System.out.println("> 1. Create New Room Rate");
        System.out.println("> 2. View Room Rate Details");
        System.out.println("> 3. View All Room Rates");
        System.out.println("> 4. Logout");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();
        
        switch (input) {
            case 1:
                System.out.println("You have selected 'Create New Room Rate'\n");
                doCreateNewRoomRate(sc, emId, emRole);
                break;
            case 2:
                System.out.println("You have selected 'View Room Rate Details'\n");
                doViewRoomRateDetails(sc, emId, emRole);
                break;
            case 3:
                System.out.println("You have selected 'View All Room Rates'\n");
                break;
            case 4:
                System.out.println("You have logged out.\n");
                doMainApp(sc);
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doDashboardFeatures(sc, emId, emRole);
                break;
        }
    }

    private static void doGRelManagerDashboardFeatures(Scanner sc, Long emId, String emRole) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void doOpsManagerDashboardFeatures(Scanner sc, Long emId, String emRole) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void doCreateNewEmployee(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== Create New Employee Interface ====");
            System.out.println("Please input the following details.");
            System.out.print("> First Name: ");
            String firstName = sc.nextLine();
            System.out.print("> Last Name: ");
            String lastName = sc.nextLine();
            System.out.print("> Password: ");
            String password = sc.nextLine();
            System.out.println("> Employee Role:\n   > 1. System Administrator"
                    + "\n   > 2. Operation Manager\n   > 3. Sales Manager"
                    + "\n   > 4. Guest Relation Manager");
            System.out.print("> ");
            int inputRole = sc.nextInt(); sc.nextLine();
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
                    System.out.println("Invalid role input.");
                    doCreateNewEmployee(sc, emId, emRole);
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
            
            doDashboardFeatures(sc, emId, emRole);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void doViewAllEmployees(Scanner sc, Long emId, String emRole) {
        System.out.println("==== View Al Employees Interface ====");
        try {
            List<Employee> list = employeeSessionBean.retrieveAllEmployees();
            int count = 0;
            
            for (Employee em : list) {
                System.out.println(":: Employee ID: " + em.getEmployeeId());
                System.out.println("     > Name: " + em.getFirstName() + " " + em.getLastName());
                System.out.println("     > Role: " + em.getEmployeeRole() + "\n");
                count++;
            }
            System.out.println("Total Employees: " + count + "\n");

            doDashboardFeatures(sc, emId, emRole);
        } catch (EmployeeQueryException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
    }

    private static void doCreateNewRoomRate(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== Create New Room Rate Interface ====");
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            System.out.println("Select Room Type to have the new Room Rate:");
            int idx = 1;
            for (RoomType type : types ) {
                System.out.println("> " + idx++ + ". " + type.getRoomTypeName());
            }
            System.out.print("> ");
            int typeInput = sc.nextInt(); sc.nextLine();
            System.out.println("** You have selected: " + types.get(typeInput - 1).getRoomTypeName() + "\n");
            System.out.println("Select Room Rate Type:");
            String[] rateEnums = new String[] {RoomRateEnum.PublishedRate.toString(), 
                                                    RoomRateEnum.NormalRate.toString(), 
                                                    RoomRateEnum.PeakRate.toString(), 
                                                    RoomRateEnum.PromotionRate.toString()};
            for (int i = 0; i < rateEnums.length; i++) {
                System.out.println("> " + (i+1) + ". " + rateEnums[i]);
            }
            System.out.print("> ");
            int rateInput = sc.nextInt(); sc.nextLine();
            System.out.println("** You have selected: " + rateEnums[rateInput - 1] + "\n");
            
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            
            if (rateInput == 3 || rateInput == 4) {
                System.out.println("Input validity period of selected room rate:");
                System.out.print("> Start Date [DD MM YYYY]: ");
                String start = sc.nextLine();
                startDate = LocalDate.parse(start, dtFormat).atStartOfDay();
                System.out.print("> End Date [DD MM YYYY]: ");
                String end = sc.nextLine();
                endDate = LocalDate.parse(end, dtFormat).atStartOfDay();
                
                System.out.println("** You have selected the period of " + ChronoUnit.DAYS.between(startDate, endDate) +
                        " day(s): " + start + " -> " + end + "\n");
            }
            
            
            System.out.print("> Rate Per Night: ");
            double rateAmount = sc.nextDouble(); sc.nextLine();
            System.out.println("** You have selected: $" + rateAmount + "\n");
            
            RoomRate rate = roomManagementSessionBean.createNewRoomRate(types.get(typeInput - 1).getRoomTypeId(), rateEnums[rateInput - 1], startDate, endDate, rateAmount);
            System.out.println("You have successfully created a new Room Rate.");
            System.out.println("> Name: " + rate.getRoomRateName());
            System.out.println("> Type: " + rate.getRoomRateType());
            System.out.println("> Amount: " + rate.getRatePerNight());
            if (rate.getStartDate() != null) System.out.println("> Validity Period: " + rate.getStartDate().toString() + 
                    " -> " + rate.getEndDate().toString());
            System.out.println();
            
            doDashboardFeatures(sc, emId, emRole);
        } catch (RoomTypeQueryException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.\n");
            doCreateNewRoomRate(sc, emId, emRole);
        }
    }

    private static void doViewRoomRateDetails(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== View Room Rate Details Interface ====");
            System.out.print("> Room Rate Name: ");
            String rateName = sc.nextLine();
            RoomRate rate = roomManagementSessionBean.getRoomRate(rateName);
            
            System.out.println("Selected Room Rate details:");
            System.out.println("> Name: " + rate.getRoomRateName());
            System.out.println("> Type: " + rate.getRoomRateType());
            System.out.println("> Amount: " + rate.getRatePerNight());
            if (rate.getStartDate() != null) {
                System.out.println("> Validity Period: " + rate.getStartDate().toString() + 
                    " -> " + rate.getEndDate().toString());
            } else {
                System.out.println("> Validity Period: NULL");
            }
            System.out.println();
            
            System.out.println("   Select an action:");
            System.out.println("   > 1. Update Room Rate");
            System.out.println("   > 2. Delete Room Rate");
            System.out.println("   > 3. Back to Dashboard");
            System.out.print("   > ");
            int input = sc.nextInt(); sc.nextLine(); System.out.println();
            
            switch (input) {
                case 1:
                    doUpdateRoomRate(sc, emId, emRole, rate.getRoomRateId());
                    break;
                case 2:
                    doDeleteRoomRate(sc, emId, emRole, rate.getRoomRateId());
                    break;
                case 3:
                    doDashboardFeatures(sc, emId, emRole);
                    break;
                default:
                    System.out.println("Invalid input.");
                    doDashboardFeatures(sc, emId, emRole);
                    break;
            }
        } catch (RoomRateQueryException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        
        
    }

    private static void doUpdateRoomRate(Scanner sc, Long emId, String emRole, Long rateId) {
        System.out.println("==== Update Room Rate Interface ====");
        
        try {
            RoomRate rate = roomManagementSessionBean.getRoomRate(rateId);
            boolean done = false;
            String name = rate.getRoomRateName();
            Double amount = rate.getRatePerNight();
            Date startDate = rate.getStartDate();
            Date endDate = rate.getEndDate();
                        
            while (!done) {
                System.out.println("Select which detail of the rate you want to change:");
                System.out.println("> 1. Name");
                System.out.println("> 2. Amount");
                System.out.println("> 3. Validity Period");
                System.out.print("> ");
                int input = sc.nextInt(); sc.nextLine(); System.out.println();
                
                switch (input) {
                    case 1:
                        System.out.print("> Input new Name: ");
                        name = sc.nextLine();
                        break;
                    case 2:
                        System.out.print("> Input new Amount: ");
                        amount = sc.nextDouble();
                        sc.nextLine();
                        System.out.println();
                        break;
                    case 3:
                        if (rate.getRoomRateType().equals(RoomRateEnum.PeakRate.toString()) ||
                                rate.getRoomRateType().equals(RoomRateEnum.PromotionRate.toString())) {
                            
                            
                            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
                            
                            System.out.print("> Input new Start Date [DD MM YYYY]: ");
                            String start = sc.nextLine();
                            startDate = Date.from(LocalDate.parse(start, dtFormat).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                            System.out.print("> Input new End Date [DD MM YYYY]: ");
                            String end = sc.nextLine(); System.out.println();
                            endDate = Date.from(LocalDate.parse(end, dtFormat).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                        } else {
                            System.out.println("Sorry. your Rate Type do not require a validity period.\n");
                        }   break;
                    default:
                        break;
                }
                
                System.out.println("Finalise changes?");
                System.out.println("> 1. Yes");
                System.out.println("> 2. No");
                System.out.print("> ");
                int answer = sc.nextInt(); sc.nextLine(); System.out.println();
                if (answer == 1) done = true;
            }
            
            roomManagementSessionBean.updateRoomRate(rateId, name, amount, startDate, endDate);
            System.out.println("You have successfully updated the Room Rate.\n");
            
            rate = roomManagementSessionBean.getRoomRate(rateId);
            System.out.println("Updated Room Rate details:");
            System.out.println("> Name: " + rate.getRoomRateName());
            System.out.println("> Type: " + rate.getRoomRateType());
            System.out.println("> Amount: " + rate.getRatePerNight());
            if (rate.getStartDate() != null) {
                System.out.println("> Validity Period: " + rate.getStartDate().toString() + 
                    " -> " + rate.getEndDate().toString());
            } else {
                System.out.println("> Validity Period: NULL");
            }
            System.out.println();
            
            doDashboardFeatures(sc, emId, emRole);
        } catch (FindRoomRateException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void doDeleteRoomRate(Scanner sc, Long emId, String emRole, Long roomRateId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
