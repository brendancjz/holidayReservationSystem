/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.RoomManagementSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import entity.Room;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.EmployeeEnum;
import util.enumeration.RoomRateEnum;
import util.exception.EmployeeQueryException;
import util.exception.FindEmployeeException;
import util.exception.FindRoomException;
import util.exception.FindRoomRateException;
import util.exception.FindRoomTypeException;
import util.exception.ReservationQueryException;
import util.exception.RoomQueryException;
import util.exception.RoomRateQueryException;
import util.exception.RoomTypeQueryException;

/**
 *
 * @author brend
 */
public class MainApp {
    
    private RoomManagementSessionBeanRemote roomManagementSessionBean;
    private EmployeeSessionBeanRemote employeeSessionBean;
    private PartnerSessionBeanRemote partnerSessionBean;
    private GuestSessionBeanRemote guestSessionBean;
    
    MainApp(RoomManagementSessionBeanRemote roomManagementSessionBean, EmployeeSessionBeanRemote employeeSessionBean, 
            PartnerSessionBeanRemote partnerSessionBean, GuestSessionBeanRemote guestSessionBean) {
        this.roomManagementSessionBean = roomManagementSessionBean;
        this.employeeSessionBean = employeeSessionBean;
        this.partnerSessionBean = partnerSessionBean;
        this.guestSessionBean = guestSessionBean;
    }
    
    public void run() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("=== Welcome to HoRS Management Client. ===");
        System.out.println("Select an action:");
        System.out.println("> 1. Login");
        System.out.println("> 2. Exit");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();
        System.out.println();
        
        switch (input) {
            case 1:
                doLogin(sc);
                break;
            case 2:
                doExit();
                break;
            default:
                run();
                break;
        }
    }

    private void doExit() {
        System.out.println("You have exited. Goodbye.");
    }

    private void doLogin(Scanner sc) {
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
                System.out.println("Login Error: " + e.toString());
            }
            
    }
    
    private void doDashboardFeatures(Scanner sc, Long emId, String emRole) {
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

    private void doSystemAdminDashboardFeatures(Scanner sc, Long emId, String emRole) {
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
                doCreateNewPartner(sc, emId, emRole);
                break;
            case 4:
                System.out.println("You have selected 'View All Partners'\n");
                doViewAllPartners(sc, emId, emRole);
                break;
            case 5:
                System.out.println("You have logged out.\n");
                run();
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doDashboardFeatures(sc, emId, emRole);
                break;
        }
    }

    private void doSalesManagerDashboardFeatures(Scanner sc, Long emId, String emRole) {
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
                doViewAllRoomRates(sc, emId, emRole);
                break;
            case 4:
                System.out.println("You have logged out.\n");
                run();
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doDashboardFeatures(sc, emId, emRole);
                break;
        }
    }

    private void doGRelManagerDashboardFeatures(Scanner sc, Long emId, String emRole) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doOpsManagerDashboardFeatures(Scanner sc, Long emId, String emRole) {
        System.out.println("> 1. Create New Room Type");
        System.out.println("> 2. View Room Type Details");
        System.out.println("> 3. View All Room Types");
        System.out.println("> 4. Create New Room");
        System.out.println("> 5. Update Room");
        System.out.println("> 6. Delete Room");
        System.out.println("> 7. View All Rooms");
        System.out.println("> 8. View Room Allocation Exception Report");
        System.out.println("> 9. Logout");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();
        
        switch (input) {
            case 1:
                System.out.println("You have selected 'Create New Room Type'\n");
                doCreateNewRoomType(sc, emId, emRole);
                break;
            case 2:
                System.out.println("You have selected 'View Room Type Details'\n");
                doViewRoomTypeDeatails(sc, emId, emRole);
                break;
            case 3:
                System.out.println("You have selected 'View All Room Types'\n");
                doViewAllRoomTypes(sc, emId, emRole);
                break;
            case 4:
                System.out.println("You have selected 'Create New Room'\n");
                doCreateNewRoom(sc, emId, emRole);
                break;
            case 5:
                System.out.println("You have selected 'Update Room'\n");
                doUpdateRoom(sc, emId, emRole);
                break;
            case 6:
                System.out.println("You have selected 'Delete Room'\n");
                doDeleteRoom(sc, emId, emRole);
                break;
            case 7:
                System.out.println("You have selected 'View All Rooms'\n");
                doViewAllRooms(sc, emId, emRole);
                break;
            case 8:
                System.out.println("You have selected 'View Room Allocation Exception Report'\n");
                //doViewAllMyReservations(sc, guestId);
                break;
            case 9:
                System.out.println("You have logged out.\n");
                run();
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doDashboardFeatures(sc, emId, emRole);
                break;
        }
    }

    private void doCreateNewEmployee(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== Create New Employee Interface ====");
            System.out.println("Please input the following details. To cancel creation at anytime, enter 'q'.");
            System.out.print("> First Name: ");
            String firstName = sc.nextLine();
            if (firstName.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            System.out.print("> Last Name: ");
            String lastName = sc.nextLine();
            if (lastName.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            System.out.print("> Password: ");
            String password = sc.nextLine();
            if (password.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            System.out.println("> Employee Role:\n   > 1. System Administrator"
                    + "\n   > 2. Operation Manager\n   > 3. Sales Manager"
                    + "\n   > 4. Guest Relation Manager");
            System.out.print("> ");
            String inputR = sc.next(); sc.nextLine();
            if (inputR.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
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
            System.out.println("Invalid input. Try again.");
            doCreateNewEmployee(sc, emId, emRole);
        }
    }

    private void doViewAllEmployees(Scanner sc, Long emId, String emRole) {
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
    
    private void doCreateNewPartner(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== Create New Partner Interface ====");
            System.out.println("Enter partner details. To cancel creation at anytime, enter 'q'.");
            System.out.print("> First Name: ");
            String firstName = sc.nextLine();
            if (firstName.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            System.out.print("> Last Name: ");
            String lastName = sc.nextLine();
            if (lastName.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            System.out.print("> Email: ");
            String email = sc.nextLine();
            if (email.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            System.out.print("> Contact Number: ");
            String numberInput = sc.nextLine();
            if (numberInput.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
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
                doDashboardFeatures(sc, emId, emRole);
            } else {
                System.out.println("You have inputted wrong details. Please try again.\n");
                
                doCreateNewPartner(sc, emId, emRole);
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.");
            doCreateNewPartner(sc, emId, emRole);
        }
        
    }
    private void doCancelledEntry(Scanner sc, Long emId, String emRole) {
        System.out.println("\n You have cancelled entry. Taking you back to dashboard.\n");
        
        doDashboardFeatures(sc, emId, emRole);
    }
    
    private void doViewAllPartners(Scanner sc, Long emId, String emRole) {
        System.out.println("==== View All Partners Interface ====");
        List<Partner> partners = partnerSessionBean.retrieveAllPartners();
        for (Partner partner : partners) {
            System.out.println(":: Partner ID: " + partner.getCustomerId());
            System.out.println("   > Name: " + partner.getFirstName() + " " + partner.getLastName());
            System.out.println("   > Email: " + partner.getEmail());
            System.out.println("   > Contact Number: " + partner.getContactNumber());
            System.out.println();
        }
        
        doDashboardFeatures(sc, emId, emRole);
    }

    private void doCreateNewRoomRate(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== Create New Room Rate Interface ====");
            System.out.println("Enter details to create new room rate. To cancel at anything, enter 'q'.");
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            System.out.println("Select Room Type to have the new Room Rate:");
            int idx = 1;
            for (RoomType type : types ) { 
                if (!type.getIsDisabled()){
                    System.out.println("> " + idx++ + ". " + type.getRoomTypeName());
                }
            }
            System.out.print("> ");
            String tInput = sc.next(); sc.nextLine();
            if (tInput.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            int typeInput = Integer.parseInt(tInput);
            
            System.out.println("** You have selected: " + types.get(typeInput - 1).getRoomTypeName() + "\n");
            System.out.println("Select Room Rate Type:");
            RoomRateEnum[] rateEnums = new RoomRateEnum[] {RoomRateEnum.PublishedRate, 
                                                    RoomRateEnum.NormalRate, 
                                                    RoomRateEnum.PeakRate, 
                                                    RoomRateEnum.PromotionRate};
            for (int i = 0; i < rateEnums.length; i++) {
                System.out.println("> " + (i+1) + ". " + rateEnums[i]);
            }
            System.out.print("> ");
            String rInput = sc.next(); sc.nextLine();
            if (rInput.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            int rateInput = Integer.parseInt(rInput);
            System.out.println("** You have selected: " + rateEnums[rateInput - 1].toString() + "\n");
            
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            
            if (rateInput == 3 || rateInput == 4) {
                System.out.println("Input validity period of selected room rate:");
                System.out.print("> Start Date [DD MM YYYY]: ");
                String start = sc.nextLine();
                if (start.equals("q")) {
                    doCancelledEntry(sc, emId, emRole);
                    return;
                }
                startDate = LocalDate.parse(start, dtFormat).atStartOfDay();
                System.out.print("> End Date [DD MM YYYY]: ");
                String end = sc.nextLine();
                if (end.equals("q")) {
                    doCancelledEntry(sc, emId, emRole);
                    return;
                }
                endDate = LocalDate.parse(end, dtFormat).atStartOfDay();
                
                System.out.println("** You have selected the period of " + (ChronoUnit.DAYS.between(startDate, endDate) + 1) +
                        " day(s): " + start + " -> " + end + "\n");
            }
            
            
            System.out.print("> Rate Per Night: "); 
            String inputRate = sc.next(); sc.nextLine(); 
            if (inputRate.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            double rateAmount = Double.parseDouble(inputRate);
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
            System.out.println("\nInvalid input. Try again.\n");
            System.out.println(e.toString() + "\n");
            doCreateNewRoomRate(sc, emId, emRole);
        }
    }

    private void doViewRoomRateDetails(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== View Room Rate Details Interface ====");
            System.out.println("Viewing room rate. To cancel entry, enter 'q'.");
            System.out.print("> Room Rate Name: ");
            String rateName = sc.nextLine();
            if (rateName.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            RoomRate rate = roomManagementSessionBean.getRoomRate(rateName);
            
            System.out.println("Selected Room Rate details:");
            System.out.println("> Name: " + rate.getRoomRateName());
            System.out.println("> Type: " + rate.getRoomRateType());
            System.out.println("> Amount: " + rate.getRatePerNight());
            System.out.println("> Is Disabled: " + rate.getIsDisabled());
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
            System.out.println("Error: " + ex.getMessage() + "\n");
            doViewRoomRateDetails(sc, emId, emRole);
        }
        
        
    }

    private void doUpdateRoomRate(Scanner sc, Long emId, String emRole, Long rateId) {
        System.out.println("==== Update Room Rate Interface ====");
        System.out.println("Updating a room rate. To cancel entry, enter 'q'");
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
                String inputStr = sc.next(); sc.nextLine();
                if (inputStr.equals("q")) {
                    doCancelledEntry(sc, emId, emRole);
                    return;
                }
                int input = Integer.parseInt(inputStr); System.out.println();
               
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
                        System.out.println("Invalid input.");
                        doUpdateRoomRate(sc, emId, emRole, rateId);
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

    private void doDeleteRoomRate(Scanner sc, Long emId, String emRole, Long roomRateId) {
        System.out.println("=== Delete Room Rate Interface ====");
        System.out.println("Confirm Deletion?");
        System.out.println("> 1. Yes");
        System.out.println("> 2. No");
        System.out.print("> ");
        try {
            int input = sc.nextInt(); sc.nextLine(); System.out.println();
            switch (input) {
                case 1:
                    roomManagementSessionBean.deleteRoomRate(roomRateId);
                    System.out.println("You have successfully deleted/disabled the Room Rate.\n");
                    break;

                case 2:
                    doDashboardFeatures(sc, emId, emRole);
                    break;
                default:
                    System.out.println("Invalid input. Try again.\n");
                    doDeleteRoomRate(sc, emId, emRole, roomRateId);
                    break;
            }
            doDashboardFeatures(sc, emId, emRole);
        } catch (FindRoomRateException | ReservationQueryException ex) {
                    System.out.println("Error: " + ex.getMessage());
        }
    }

    private void doViewAllRoomRates(Scanner sc, Long emId, String emRole) {
        System.out.println("==== View All Room Rates Interface ====");
        try {
            List<RoomRate> list = roomManagementSessionBean.getAllRoomRates();
            
            int count = 0;
            
            for (RoomRate rate : list) {
                System.out.println(":: Room Rate ID: " + rate.getRoomRateId());
                System.out.println("> Name: " + rate.getRoomRateName());
                System.out.println("> Room Type: " + rate.getRoomType().getRoomTypeName());
                System.out.println("> Amount: " + rate.getRatePerNight());
                System.out.println("> Is Disabled: " + rate.getIsDisabled());
                if (rate.getStartDate() != null) {
                    System.out.println("> Validity Period: " + rate.getStartDate().toString() + 
                        " -> " + rate.getEndDate().toString());
                } else {
                    System.out.println("> Validity Period: NULL");
                }
                System.out.println();
                
                count++;
            } 
            System.out.println("Total Number of Room Rates: " + count + "\n");
            doDashboardFeatures(sc, emId, emRole);
        } catch (RoomRateQueryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    } 

    private void doCreateNewRoomType(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== Create New Room Type Interface ====");
        //String roomTypeName, String roomTypeDesc, Integer roomSize, Integer numOfBeds, Integer capacity, String amenities
            System.out.println("Creating new room type. To cancel anytime, enter 'q'.");
            System.out.print("> Room Type Name [MIN 5 CHAR]: ");
            String typeName = sc.nextLine();
            if (typeName.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            System.out.print("> Room Type Description [MIN 5 CHAR]: ");
            String typeDesc = sc.nextLine();
            if (typeDesc.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            System.out.print("> Room Size: ");
            String roomInput = sc.next(); sc.nextLine();
            if (roomInput.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            Integer roomSize = Integer.parseInt(roomInput); 
            System.out.print("> Number Of Beds: ");
            String bedInput = sc.next(); sc.nextLine();
            if (bedInput.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            Integer numOfBeds = Integer.parseInt(bedInput); 
            System.out.print("> Room Capacity: ");
            String capInput = sc.next(); sc.nextLine();
            if (capInput.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            Integer cap = Integer.parseInt(capInput);
            System.out.print("> Room Amenities [MIN 5 CHAR]: ");
            String amenities = sc.nextLine();
            if (amenities.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            System.out.println();
            RoomType newRoomType = new RoomType(typeName, typeDesc, roomSize, numOfBeds, cap, amenities);
            
            Long newRoomTypeId = roomManagementSessionBean.createNewRoomType(newRoomType);

            RoomType type = roomManagementSessionBean.getRoomType(newRoomTypeId);
            
            System.out.println("You have successfully created a new Room Type.");
            System.out.println("> Name: " + type.getRoomTypeName());
            System.out.println("> Description: " + type.getRoomTypeDesc());
            System.out.println("> Size: " + type.getRoomSize());
            System.out.println("> Number Of Beds: " + type.getNumOfBeds());
            System.out.println("> Capacity: " + type.getCapacity());
            System.out.println("> Amenities: " + type.getAmenities());
            System.out.println();
            
            doDashboardFeatures(sc, emId, emRole);
        } catch (FindRoomTypeException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.\n");
            doCreateNewRoomType(sc, emId, emRole);
        }
    }

    private void doViewRoomTypeDeatails(Scanner sc, Long emId, String emRole) {
        System.out.println("==== View Room Type Details Interface ====");
        System.out.println("Viewing room type. To cancel entry anytime, enter 'q'.");
        try {
            
            System.out.print("> Room Type Name: ");
            String typeName = sc.nextLine();System.out.println();
            if (typeName.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            RoomType type = roomManagementSessionBean.getRoomType(typeName);
            
            System.out.println("Selected Room Type details:");
            System.out.println("> Name: " + type.getRoomTypeName());
            System.out.println("> Description: " + type.getRoomTypeDesc());
            System.out.println("> Size: " + type.getRoomSize());
            System.out.println("> Number Of Beds: " + type.getNumOfBeds());
            System.out.println("> Capacity: " + type.getCapacity());
            System.out.println("> Amenities: " + type.getAmenities());
            System.out.println("> Number of Rooms: " + type.getRooms().size());
            System.out.println("> Is Disabled: " + type.getIsDisabled());
            System.out.println("> Room Rates:");
            List<RoomRate> rates = roomManagementSessionBean.getRoomRates(type.getRoomTypeId());
            if (!rates.isEmpty()) {
                for (RoomRate rate : rates) {
                System.out.println("  > " + rate.getRoomRateName());
                }
                System.out.println();
            } else {
                System.out.println("     NULL");
            }
            
            
            
            System.out.println("   Select an action:");
            System.out.println("   > 1. Update Room Type");
            System.out.println("   > 2. Delete Room Type");
            System.out.println("   > 3. Back to Dashboard");
            System.out.print("   > ");
            int input = sc.nextInt(); sc.nextLine(); System.out.println();
            
            switch (input) {
                case 1:
                    doUpdateRoomType(sc, emId, emRole, type.getRoomTypeId());
                    break;
                case 2:
                    doDeleteRoomType(sc, emId, emRole, type.getRoomTypeId());
                    break;
                case 3:
                    doDashboardFeatures(sc, emId, emRole);
                    break;
                default:
                    System.out.println("Invalid input.");
                    doDashboardFeatures(sc, emId, emRole);
                    break;
            }
        } catch (RoomTypeQueryException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("General Exception: " + e.toString());
            doDashboardFeatures(sc, emId, emRole);
        }
    }

    private void doViewAllRoomTypes(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== View All Room Types Interface");
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            for (RoomType type : types ) {
                System.out.println(":: Room Type ID: " + type.getRoomTypeId());
                System.out.println("> Name: " + type.getRoomTypeName());
                System.out.println("> Description: " + type.getRoomTypeDesc());
                System.out.println("> Size: " + type.getRoomSize());
                System.out.println("> Number Of Beds: " + type.getNumOfBeds());
                System.out.println("> Capacity: " + type.getCapacity());
                System.out.println("> Amenities: " + type.getAmenities());
                System.out.println("> Number of Rooms: " + type.getRooms().size());
                System.out.println("> Is Disabled: " + type.getIsDisabled());
                System.out.println();
            }
            
        } catch (RoomTypeQueryException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Main exception: " + e.toString() + "\n");
            
        }
        doDashboardFeatures(sc, emId, emRole);
    }

    private void doUpdateRoomType(Scanner sc, Long emId, String emRole, Long roomTypeId) {
        System.out.println("==== Update Room Type Interface ====");
        System.out.println("Updating room type. To cancel entry at anytime, enter 'q'.");
        try {
            RoomType type = roomManagementSessionBean.getRoomType(roomTypeId);
            boolean done = false;
            String name = type.getRoomTypeName();
            String desc = type.getRoomTypeDesc();
            Integer size = type.getRoomSize();
            Integer beds = type.getNumOfBeds();
            Integer cap = type.getCapacity();
            String amenities = type.getAmenities();
                        
            while (!done) {
                System.out.println("Select which detail of the type you want to change:");
                System.out.println("> 1. Name");
                System.out.println("> 2. Description");
                System.out.println("> 3. Room Size");
                System.out.println("> 4. Number Of Beds");
                System.out.println("> 5. Room Capacity");
                System.out.println("> 6. Amenities");
                System.out.print("> ");
                String inputStr = sc.next(); sc.nextLine(); 
                if (inputStr.equals("q")) {
                    doCancelledEntry(sc, emId, emRole);
                    return;
                }
                int input = Integer.parseInt(inputStr); System.out.println();
                
                switch (input) {
                    case 1:
                        System.out.print("> Input new Name: ");
                        name = sc.nextLine();
                        break;
                    case 2:
                        System.out.print("> Input new Description: ");
                        desc = sc.nextLine();
                        break;
                    case 3:
                        System.out.print("> Input new Room Size: ");
                        size = sc.nextInt(); sc.nextLine();
                        break;
                    case 4:
                        System.out.print("> Input new Number Of Beds: ");
                        beds = sc.nextInt(); sc.nextLine();
                        break;
                    case 5:
                        System.out.print("> Input new Room Capacity: ");
                        cap = sc.nextInt(); sc.nextLine();
                        break;
                    case 6:
                        System.out.print("> Input new Amenities: ");
                        amenities = sc.nextLine();
                        break;
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
            
            roomManagementSessionBean.updateRoomType(roomTypeId, name, desc, size, beds, cap, amenities);
            System.out.println("You have successfully updated the Room Type.\n");
            
            type = roomManagementSessionBean.getRoomType(roomTypeId);
            System.out.println("Updated Room Rate details:");
            System.out.println("> Name: " + type.getRoomTypeName());
            System.out.println("> Description: " + type.getRoomTypeDesc());
            System.out.println("> Size: " + type.getRoomSize());
            System.out.println("> Number Of Beds: " + type.getNumOfBeds());
            System.out.println("> Capacity: " + type.getCapacity());
            System.out.println("> Amenities: " + type.getAmenities());
            System.out.println();
            
            doDashboardFeatures(sc, emId, emRole);
        } catch (FindRoomTypeException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void doDeleteRoomType(Scanner sc, Long emId, String emRole, Long roomTypeId) {
        System.out.println("=== Delete Room Type Interface ====");
        System.out.println("Confirm Deletion?");
        System.out.println("> 1. Yes");
        System.out.println("> 2. No");
        System.out.print("> ");
        try { 
            int input = sc.nextInt(); sc.nextLine(); System.out.println();
            switch (input) {
                case 1:
                    roomManagementSessionBean.deleteRoomType(roomTypeId);
                    System.out.println("You have successfully deleted/disabled the Room Type.\n");
                    break;

                case 2:
                    doDashboardFeatures(sc, emId, emRole);
                    break;
                default:
                    System.out.println("Invalid input. Try again.\n");
                    doDeleteRoomType(sc, emId, emRole, roomTypeId);
                    break;
            }
            
            doDashboardFeatures(sc, emId, emRole);
        } catch (FindRoomRateException | ReservationQueryException | FindRoomTypeException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Unspecified Error " + e.getMessage());
        }
    }

    private void doCreateNewRoom(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== Create New Room Interface");
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            System.out.println("Select Room Type to have the new Room Rate:");
            int idx = 1;
            for (RoomType type : types ) { 
                if (!type.getIsDisabled()){
                    System.out.println("> " + idx++ + ". " + type.getRoomTypeName());
                }
            }
            System.out.print("> ");
            String tInput = sc.next(); sc.nextLine();
            if (tInput.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            int typeInput = Integer.parseInt(tInput);
            
            System.out.println("** You have selected: " + types.get(typeInput - 1).getRoomTypeName() + "\n");
            
            System.out.println("Creating new Room:");
            System.out.print("> Room Level: ");
            String roomInput = sc.next();  sc.nextLine();
            if (roomInput.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            int level = Integer.parseInt(roomInput);
            System.out.print("> Room Number: ");
            String numInput = sc.next(); sc.nextLine();
            if (numInput.equals("q")) {
                    doCancelledEntry(sc, emId, emRole);
                    return;
                }
            int num = Integer.parseInt(numInput); System.out.println();
            
            Room newRoom = new Room(level, num);
            newRoom = roomManagementSessionBean.createNewRoom(newRoom, types.get(typeInput - 1).getRoomTypeId());
            
            System.out.println("You have successfully created a new Room.");
            System.out.println("> Room Level: " + newRoom.getRoomLevel());
            System.out.println("> Room Number: " + newRoom.getRoomNum());
            System.out.println("> Room Type: " + newRoom.getRoomType().getRoomTypeName());
            System.out.println("> Is Available: " + newRoom.getIsAvailable());
            System.out.println("> Is Disabled: " + newRoom.getIsDisabled());
            System.out.println();
            
            doDashboardFeatures(sc, emId, emRole);
            
        } catch (RoomTypeQueryException e) {
            System.out.println("Error: " + e.toString());
        } catch (Exception e) {
            System.out.println("General Error: " + e.toString());
        }
    }

    private void doUpdateRoom(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== Update Room Interface ====");
            System.out.println("Updating room. To cancel entry at anytime, enter 'q'.");
            System.out.print("> Input existing Room Level: ");
            String roomInput = sc.next(); sc.nextLine();
            if (roomInput.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            int level = Integer.parseInt(roomInput); 
            System.out.print("> Input existing Room Number: ");
            String numInput = sc.next(); sc.nextLine();
            if (numInput.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            int number = Integer.parseInt(numInput); System.out.println();
            
            Room room = roomManagementSessionBean.getRoom(level, number);
            boolean isAvail = room.getIsAvailable();
            level = room.getRoomLevel();
            number = room.getRoomNum();
            RoomType type = room.getRoomType();
            
            if (room.getIsDisabled()) {
                System.out.println("Sorry, you selected a disabled Room. Try again with another room.\n");
                doUpdateRoom(sc, emId, emRole);
                return;
            }

            System.out.println("You have selected Room ID: " + room.getRoomId());
            boolean done = false;
            while (!done) {
                System.out.println("Select which detail of the type you want to change:");
                System.out.println("> 1. Room Level");
                System.out.println("> 2. Room Number");
                System.out.println("> 3. Room Type");
                System.out.println("> 4. Availability");
                System.out.print("> ");
                int input = sc.nextInt(); sc.nextLine(); System.out.println();
                
                switch (input) {
                    case 1:
                        System.out.print("> Input new level: ");
                        level = sc.nextInt(); sc.nextLine();
                        break;
                    case 2:
                        System.out.print("> Input new number: ");
                        number = sc.nextInt(); sc.nextLine();
                        break;
                    case 3:
                        List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
                        System.out.println("Select Room Type to change to:");
                        int idx = 1;
                        for (RoomType t : types ) { 
                            if (!t.getIsDisabled()){
                                System.out.println("> " + idx++ + ". " + t.getRoomTypeName());
                            }
                        }
                        
                        System.out.print("> ");
                        int typeInput = sc.nextInt(); sc.nextLine();
                        
                        type = types.get(typeInput - 1);
                        break;
                    case 4:
                        System.out.println("> Select True or False: ");
                        System.out.println("  > 1. True");
                        System.out.println("  > 2. False");
                        System.out.print("  > ");
                        int input2 = sc.nextInt(); sc.nextLine();
                        
                        isAvail = (input2 == 1);
                        break;
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
            
            roomManagementSessionBean.updateRoom(room.getRoomId(), level, number, isAvail, type);
            
            System.out.println("You have successfully updated the Room.\n");
            
            room = roomManagementSessionBean.getRoom(room.getRoomId());
            System.out.println("Updated Room details:");
            System.out.println("> Room Level: " + room.getRoomLevel());
            System.out.println("> Room Number: " + room.getRoomNum());
            System.out.println("> IsAvailable: " + room.getIsAvailable());
            System.out.println("> Room Type: " + room.getRoomType().getRoomTypeName());
            System.out.println();
            
            doDashboardFeatures(sc, emId, emRole);
        } catch (RoomQueryException | RoomTypeQueryException | FindRoomException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    doUpdateRoom(sc, emId, emRole);
        } catch (Exception ex) {
            System.out.println("General Exception: " + ex.toString());
        }
    }

    private void doDeleteRoom(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== Delete Room Interface ====");
            System.out.print("> Input existing Room Level: ");
            String roomInput = sc.next(); sc.nextLine();
            if (roomInput.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            int level = Integer.parseInt(roomInput);
            System.out.print("> Input existing Room Number: ");
            String numInput = sc.next(); sc.nextLine();
            if (numInput.equals("q")) {
                doCancelledEntry(sc, emId, emRole);
                return;
            }
            int number = Integer.parseInt(numInput); System.out.println();
            
            Room room = roomManagementSessionBean.getRoom(level, number);
            if (room.getIsDisabled()) {
                System.out.println("Sorry, you selected a disabled Room. Try again with another room.\n");
                doDashboardFeatures(sc, emId, emRole);
                return;
            }
            
            System.out.println("You have selected Room ID: " + room.getRoomId());
            System.out.println("Confirm Deletion?");
            System.out.println("> 1. Yes");
            System.out.println("> 2. No");
            System.out.print("> ");
            
            int input = sc.nextInt(); sc.nextLine(); System.out.println();
            switch (input) {
                case 1:
                    roomManagementSessionBean.deleteRoom(room.getRoomId());
                    System.out.println("You have successfully deleted/disabled the Room Rate.\n");
                    break;

                case 2:
                    doDashboardFeatures(sc, emId, emRole);
                    break;
                default:
                    System.out.println("Invalid input. Try again.\n");
                    doDeleteRoom(sc, emId, emRole);
                    break;
            }
            
            doDashboardFeatures(sc, emId, emRole);
        } catch (RoomQueryException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (FindRoomException | ReservationQueryException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doViewAllRooms(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== View All Rooms Interface ====");
            List<Room> rooms = roomManagementSessionBean.retrieveAllRooms();
            for (Room room : rooms) {
                if (!room.getIsDisabled()) {
                    System.out.println("::Room ID: " + room.getRoomId());
                    System.out.println("  > Level: " + room.getRoomLevel());
                    System.out.println("  > Number: " + room.getRoomNum());
                    System.out.println("  > Room Type: " + room.getRoomType().getRoomTypeName());
                    System.out.println("  > Is Available: " + room.getIsAvailable());
                    System.out.println();
                }
                
            }
            
            doDashboardFeatures(sc, emId, emRole);
        } catch (RoomQueryException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
