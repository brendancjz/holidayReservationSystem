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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import util.enumeration.EmployeeEnum;
import util.enumeration.RoomRateEnum;
import util.exception.EmployeeQueryException;
import util.exception.FindEmployeeException;
import util.exception.FindRoomRateException;
import util.exception.ReservationQueryException;
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
        MainApp app = new MainApp(roomManagementSessionBean, employeeSessionBean);
        app.run();
    } 
}
