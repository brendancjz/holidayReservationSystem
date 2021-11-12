/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.AllocationExceptionSessionBeanRemote;
import ejb.session.stateless.AllocationSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomManagementSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import util.enumeration.EmployeeEnum;

/**
 *
 * @author brend
 */
public class MainApp {

    private RoomManagementSessionBeanRemote roomManagementSessionBean;
    private EmployeeSessionBeanRemote employeeSessionBean;
    private PartnerSessionBeanRemote partnerSessionBean;
    private GuestSessionBeanRemote guestSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private AllocationSessionBeanRemote allocationSessionBean;
    private AllocationExceptionSessionBeanRemote allocationExceptionSessionBean;

    MainApp(RoomManagementSessionBeanRemote roomManagementSessionBean, EmployeeSessionBeanRemote employeeSessionBean,
            PartnerSessionBeanRemote partnerSessionBean, GuestSessionBeanRemote guestSessionBean, ReservationSessionBeanRemote reservationSessionBean,
            AllocationSessionBeanRemote allocationSessionBean, AllocationExceptionSessionBeanRemote allocationExceptionSessionBean) {
        this.roomManagementSessionBean = roomManagementSessionBean;
        this.employeeSessionBean = employeeSessionBean;
        this.partnerSessionBean = partnerSessionBean;
        this.guestSessionBean = guestSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.allocationSessionBean = allocationSessionBean;
        this.allocationExceptionSessionBean = allocationExceptionSessionBean;
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
            System.out.print("> Employee Username: ");
            String username = sc.nextLine();
            System.out.print("> Password: ");
            String password = sc.nextLine();

            if (employeeSessionBean.checkEmployeeExists(username, password)) {

                Employee currEm = employeeSessionBean.getEmployeeByUsername(username);
                System.out.println("Welcome " + currEm.getEmployeeRole() + " " + currEm.getFirstName() + "\n");

                doDashboardFeatures(sc, currEm);
            } else {
                System.out.println("No account match or wrong login details. Try again.\n");
                doLogin(sc);
            }
        } catch (Exception e) {
            System.out.println("Login Error. Try again.\n");
            run();
        }

    }

    private void doDashboardFeatures(Scanner sc, Employee em) {

        String emRole = em.getEmployeeRole();
        Long emId = em.getEmployeeId();
        if (emRole.equals(EmployeeEnum.SYSTEMADMIN.toString())) {
            SystemAdminModule module = new SystemAdminModule(partnerSessionBean, employeeSessionBean, 
                    allocationSessionBean, allocationExceptionSessionBean, reservationSessionBean, roomManagementSessionBean);
            module.doSystemAdminDashboardFeatures(sc);
            run();
        } else if (emRole.equals(EmployeeEnum.OPSMANAGER.toString())) {
            OpsManagerModule module = new OpsManagerModule(roomManagementSessionBean, allocationExceptionSessionBean);
            module.doOpsManagerDashboardFeatures(sc);
            run();
        } else if (emRole.equals(EmployeeEnum.SALESMANAGER.toString())) {
            SalesManagerModule module = new SalesManagerModule(roomManagementSessionBean);
            module.doSalesManagerDashboardFeatures(sc);
            run();
        } else if (emRole.equals(EmployeeEnum.GRELMANAGER.toString())) {
            GRelManagerModule module = new GRelManagerModule(allocationSessionBean, allocationExceptionSessionBean, guestSessionBean, reservationSessionBean, roomManagementSessionBean);
            module.doGRelManagerDashboardFeatures(sc);
            run();
        }

    }

}
