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
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        try {
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
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.\n");
            run();
        }
        
    }

    private void doExit() {
        System.out.println("You have exited. Goodbye.");
    }

    private void doLogin(Scanner sc) {
        try {
            System.out.println("==== Login Interface ====");
            System.out.println("Enter login details:");
            System.out.print("> Employee ID: ");
            Long emId = sc.nextLong();
            sc.nextLine();
            System.out.print("> Password: ");
            String password = sc.nextLine();
            
            if (employeeSessionBean.verifyLoginDetails(emId, password) && 
                    employeeSessionBean.checkEmployeeExists(emId, password)) {

                Employee currEm = employeeSessionBean.getEmployeeById(emId);
                System.out.println("Welcome " + currEm.getEmployeeRole() + " " + currEm.getFirstName() + "\n");

                doDashboardFeatures(sc, currEm);
            } else {
                System.out.println("No account match or wrong login details. Try again.\n");
                doLogin(sc);
            }
        } catch (FindEmployeeException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e ) {
            System.out.println("Login Error. Try again.\n");
            run();
        }
            
    }
    
    private void doDashboardFeatures(Scanner sc, Employee em) {
        
        String emRole = em.getEmployeeRole();
        Long emId = em.getEmployeeId();
        if (emRole.equals(EmployeeEnum.SYSTEMADMIN.toString())) {
            SystemAdminModule module = new SystemAdminModule(partnerSessionBean, employeeSessionBean);
            module.doSystemAdminDashboardFeatures(sc, emId);
            run();
        } else if (emRole.equals(EmployeeEnum.OPSMANAGER.toString())) {
            OpsManagerModule module = new OpsManagerModule(roomManagementSessionBean);
            module.doOpsManagerDashboardFeatures(sc, emId);
            run();
        } else if (emRole.equals(EmployeeEnum.SALESMANAGER.toString())) {
            SalesManagerModule module = new SalesManagerModule(roomManagementSessionBean);
            module.doSalesManagerDashboardFeatures(sc, emId);
            run();
        } else if (emRole.equals(EmployeeEnum.GRELMANAGER.toString())) {
            doGRelManagerDashboardFeatures(sc, emId, emRole);
        }
        
        
    }

 

    private void doGRelManagerDashboardFeatures(Scanner sc, Long emId, String emRole) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    

  
}
