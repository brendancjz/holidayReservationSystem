/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemjavaseclient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import ws.client.HoRSWebService_Service;
import ws.client.Partner;
import ws.client.Reservation;
import ws.client.RoomRate;
import ws.client.RoomType;

/**
 *
 * @author brend
 */
public class HolidayReservationSystemJavaSeClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        run();

    }

    private static void run() {
        try {
            Scanner sc = new Scanner(System.in);

            System.out.println("=== Welcome to Holiday Reservation System JavaSE Client ===");
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
                    System.out.println("You have exited. Goodbye.");
                    break;
                default:
                    System.out.println("Invalid input. Try again.\n");
                    run();
                    break;
            }
        } catch (Exception e) {
            System.out.println("Invalid Input. Please try again.\n");
            run();
        }

    }

    private static void doLogin(Scanner sc) {
        System.out.println("==== Login Interface ====");
        System.out.println("Enter login details:");
        System.out.print("> Email: ");
        String email = sc.nextLine();
        try {
            HoRSWebService_Service service = new HoRSWebService_Service();

            if (service.getHoRSWebServicePort().checkPartnerExists(email)) {

                Partner currPartner = service.getHoRSWebServicePort().getPartnerByEmail(email);
                System.out.println("Welcome " + currPartner.getFirstName() + ", you're in!\n");

                doDashboardFeatures(sc, currPartner.getCustomerId());

            } else {
                System.out.println("No account match or wrong login details. Try again.\n");
                doLogin(sc);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private static void doDashboardFeatures(Scanner sc, Long customerId) {
        System.out.println("==== Dashboard Interface ====");
        System.out.println("> 1. Partner Search Room");
        System.out.println("> 2. View Partner Reservation Details");
        System.out.println("> 3. View All Partner Reservations");
        System.out.println("> 4. Logout");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();

        switch (input) {
            case 1:
                System.out.println("You have selected 'Search Hotel Room'\n");
                doPartnerSearchRoom(sc, customerId);
                break;
            case 2:
                System.out.println("You have selected 'View My Reservation Details'\n");
                doViewPartnerReservationDetails(sc, customerId);
                break;
            case 3:
                System.out.println("You have selected 'View All My Reservations'\n");
                doViewAllPartnerReservations(sc, customerId);
                break;
            case 4:
                System.out.println("You have logged out.\n");
                run();
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doDashboardFeatures(sc, customerId);
                break;
        }
    }

    private static void doViewAllPartnerReservations(Scanner sc, Long customerId) {
        try {
            System.out.println("==== View All Partner Reservations Interface ====");

            HoRSWebService_Service service = new HoRSWebService_Service();
            List<Reservation> reservations = service.getHoRSWebServicePort().getAllPartnerReservations(customerId);
            if (reservations == null) {
                System.out.println("Partner has not made any reservations.\n");
                doDashboardFeatures(sc, customerId);
            }
            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            System.out.printf("\n%3s%20s%15s%15s%30s", "ID", "Room Type", "No. of Rooms", "Total Fees", "Duration");
            for (Reservation reservation : reservations) {
                Date start = Date.from(reservation.getStartDate().toGregorianCalendar().toInstant());
                Date end = Date.from(reservation.getEndDate().toGregorianCalendar().toInstant());
                RoomType type = service.getHoRSWebServicePort().getRoomTypeFromReservationId(reservation.getReservationId());
                System.out.printf("\n%3s%20s%15s%15s%30s", reservation.getReservationId(),
                        type.getRoomTypeName(), reservation.getReservationFee(),
                        reservation.getNumOfRooms(), outputFormat.format(start)
                        + " -> " + outputFormat.format(end));

            }
            System.out.println();
            System.out.println();
            doDashboardFeatures(sc, customerId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void doViewPartnerReservationDetails(Scanner sc, Long customerId) {
        try {
            System.out.println("==== View Partner Reservation Details Interface ====");
            System.out.println("Enter your reservation's details:");
            System.out.print("> Check-In Date [DD MM YYYY]: ");
            String checkIn = sc.nextLine();
            System.out.print("> Check-Out Date [DD MM YYYY]: ");
            String checkOut = sc.nextLine();
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
            LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);
            if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
                System.out.println("Invalid dates input.\n");
                doDashboardFeatures(sc, customerId);
            }

            HoRSWebService_Service service = new HoRSWebService_Service();
            Reservation reservation = service.getHoRSWebServicePort().getPartnerReservation(checkIn, checkOut, customerId);

            if (reservation == null) {
                System.out.println("Reservation does not exist.\n");
                doDashboardFeatures(sc, customerId);
            }

            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date start = Date.from(reservation.getStartDate().toGregorianCalendar().toInstant());
            Date end = Date.from(reservation.getEndDate().toGregorianCalendar().toInstant());
            String duration = outputFormat.format(start)
                    + " -> " + outputFormat.format(end);
            RoomType type = service.getHoRSWebServicePort().getRoomTypeFromReservationId(reservation.getReservationId());

            System.out.println("Selected Reservation details:");
            System.out.println(":: Reservation ID: " + reservation.getReservationId());
            System.out.println("   > Reservation Fee: " + reservation.getReservationFee());

            System.out.println("   > Room Type: " + type.getRoomTypeName());
            System.out.println("   > Num of Rooms: " + reservation.getNumOfRooms());
            System.out.println("   > Duration: " + duration);
            System.out.println();
            doDashboardFeatures(sc, customerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void doPartnerSearchRoom(Scanner sc, Long customerId) {
        try {
            System.out.println("==== Partner Search Room Interface ====");
            System.out.println("Please input your check-in and check-out dates. Follow the format 'DD MM YYYY'.");
            System.out.print("> Check-In Date: ");
            String checkIn = sc.nextLine();
            System.out.print("> Check-Out Date: ");
            String checkOut = sc.nextLine();
            System.out.println();
            
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
            LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);
            if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
                System.out.println("Invalid dates input.\n");
                doDashboardFeatures(sc, customerId);
                return;
            }
            System.out.println("How many number rooms are you looking to reserve?");
            System.out.print("> Number of Rooms: ");
            int numOfRooms = sc.nextInt();
            sc.nextLine();
            

            long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

            //Output all the room types, give guest option to select the room he wants to search
            System.out.println("Here are all available Room Types for your " + daysBetween + " night(s) stay"
                    + ". Which Hotel Room would you like to reserve?");

            HoRSWebService_Service service = new HoRSWebService_Service();

            List<RoomType> types = service.getHoRSWebServicePort().getAllRoomTypes();

            if (types == null) {
                System.out.println("No room types.\n");
                doDashboardFeatures(sc, customerId);
            }

            int count = 1;

            for (int i = 0; i < types.size(); i++) {
                RoomType type = types.get(i);
                //Check if room type is available first. If available then display
                boolean isRoomTypeAvail = service.getHoRSWebServicePort().isRoomTypeAvailableForReservation(type.getRoomTypeId(), checkIn, checkOut, numOfRooms);

                if (isRoomTypeAvail) {
                    //Derive the total reservation fee
                    double totalReservation = getTotalReservationFee(checkInDate, checkOutDate, type);
                    if (totalReservation == -1) {
                        System.out.println("No Room Rates available.\n");
                        doDashboardFeatures(sc, customerId);
                        return;
                    }
                    System.out.println("> " + count++ + ". " + type.getRoomTypeDesc()
                            + "\n     ** Amenities: " + type.getAmenities()
                            + "\n     ** Total reservation fee is " + totalReservation * numOfRooms);
                } else {
                    types.remove(type);
                    i--;

                }
            }
            System.out.print("> ");
            int input = sc.nextInt();
            sc.nextLine();
            System.out.println();

            RoomType selectedRoomType = types.get(input - 1);
            System.out.println("** You have selected " + selectedRoomType.getRoomTypeName() + "\n");
            System.out.println("Do you want to reserve the room?");
            System.out.println("> 1. Yes");
            System.out.println("> 2. No");
            System.out.print("> ");
            int reserveInput = sc.nextInt();
            sc.nextLine();
            System.out.println();
            if (reserveInput == 1) {
                doPartnerReserveRoom(sc, customerId, checkInDate, checkOutDate, numOfRooms, selectedRoomType);
            } else {
                System.out.println("Going back to dashboard.\n");
                doDashboardFeatures(sc, customerId);
            }

        } catch (Exception e) {
            System.out.println("Something went wrong.\n");
            doPartnerSearchRoom(sc, customerId);

        }
    }

    private static void doPartnerReserveRoom(Scanner sc, Long customerId, LocalDate checkInDate, LocalDate checkOutDate, int numOfRooms, RoomType selectedRoomType) {
        try {
            HoRSWebService_Service service = new HoRSWebService_Service();

            List<RoomRate> ratesUsed = getRoomRateUsed(checkInDate, checkOutDate, selectedRoomType, sc, customerId);

            System.out.println("Confirm reservation?");
            System.out.println("> 1. Yes");
            System.out.println("> 2. No");
            System.out.print("> ");
            int confirmationInput = sc.nextInt();
            sc.nextLine();
            System.out.println();

            if (confirmationInput == 1) {

                Date startDate = Date.from(checkInDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                Date endDate = Date.from(checkOutDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

                DateFormat outputFormat = new SimpleDateFormat("dd MM yyyy");

                String checkIn = outputFormat.format(startDate);
                String checkOut = outputFormat.format(endDate);
                Long reservationId;
                switch (ratesUsed.size()) {
                    case 1:
                        reservationId = service.getHoRSWebServicePort().createNewReservationWithOneRateUsed(checkIn, checkOut, numOfRooms,
                                getTotalReservationFee(checkInDate, checkOutDate, selectedRoomType) * numOfRooms, customerId, selectedRoomType.getRoomTypeId(), ratesUsed.get(0).getRoomRateId());
                        break;
                    case 2:
                        reservationId = service.getHoRSWebServicePort().createNewReservationWithTwoRatesUsed(checkIn, checkOut, numOfRooms,
                                getTotalReservationFee(checkInDate, checkOutDate, selectedRoomType) * numOfRooms, customerId, selectedRoomType.getRoomTypeId(), ratesUsed.get(0).getRoomRateId(), ratesUsed.get(1).getRoomRateId());
                        break;
                    case 3:
                        reservationId = service.getHoRSWebServicePort().createNewReservationWithThreeRatesUsed(checkIn, checkOut, numOfRooms,
                                getTotalReservationFee(checkInDate, checkOutDate, selectedRoomType) * numOfRooms, customerId, selectedRoomType.getRoomTypeId(), ratesUsed.get(0).getRoomRateId(), ratesUsed.get(1).getRoomRateId(), ratesUsed.get(2).getRoomRateId());
                        break;
                    default:
                        reservationId = service.getHoRSWebServicePort().createNewReservationWithFourRatesUsed(checkIn, checkOut, numOfRooms,
                                getTotalReservationFee(checkInDate, checkOutDate, selectedRoomType) * numOfRooms, customerId, selectedRoomType.getRoomTypeId(), ratesUsed.get(0).getRoomRateId(), ratesUsed.get(1).getRoomRateId(), ratesUsed.get(2).getRoomRateId(), ratesUsed.get(3).getRoomRateId());
                        break;
                }

                Reservation reservation = service.getHoRSWebServicePort().getReservationByReservationId(reservationId);

                Date start = Date.from(reservation.getStartDate().toGregorianCalendar().toInstant());
                Date end = Date.from(reservation.getEndDate().toGregorianCalendar().toInstant());
                System.out.println("You have made a reservation:");
                System.out.println(":: Reservation ID: " + reservation.getReservationId());
                System.out.println("> Number Of Rooms: " + reservation.getNumOfRooms());
                System.out.println("> Reservation Fee: " + reservation.getReservationFee());
                System.out.println("> Start Date: " + outputFormat.format(start));
                System.out.println("> End Date: " + outputFormat.format(end));
                System.out.println();

            } else {
                System.out.println("Going back to dashboard.\n");

            }
            doDashboardFeatures(sc, customerId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static double getTotalReservationFee(LocalDate checkInDate, LocalDate checkOutDate, RoomType selectedRoomType) {
        double totalReservation = 0;
        long numOfDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        HoRSWebService_Service service = new HoRSWebService_Service();
        List<RoomRate> rates = service.getHoRSWebServicePort().getRoomRates(selectedRoomType.getRoomTypeId());
        if (rates == null) {

            return -1;
        }
        for (int i = 0; i < numOfDays; i++) {
            //get the rate Per night for each night
            boolean foundRate = false;
            for (int j = rates.size() - 1; j >= 0; j--) {
                RoomRate rate = rates.get(j);
                boolean isCurrentDateWithinRange = true;
                if (rate.getStartDate() != null || rate.getEndDate() != null) {
                    LocalDate start = rate.getStartDate().toGregorianCalendar().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate end = rate.getEndDate().toGregorianCalendar().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    boolean lowerBound = checkInDate.isAfter(start) || checkInDate.isEqual(start);
                    boolean upperBound = checkInDate.isBefore(end) || checkInDate.isEqual(end);
                    isCurrentDateWithinRange = lowerBound && upperBound;
                }

                if ((isCurrentDateWithinRange) && !foundRate) {
                    totalReservation += rate.getRatePerNight();
                    checkInDate = checkInDate.plusDays(1);
                    foundRate = true;
                }
            }
        }
        return totalReservation;
    }

    private static List<RoomRate> getRoomRateUsed(LocalDate checkInDate, LocalDate checkOutDate, RoomType selectedRoomType, Scanner sc, Long customerId) {
        HoRSWebService_Service service = new HoRSWebService_Service();

        long numOfDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        List<RoomRate> rates = service.getHoRSWebServicePort().getRoomRates(selectedRoomType.getRoomTypeId());
        if (rates == null) {
            System.out.println("No room rates available.\n");
            doDashboardFeatures(sc, customerId);
        }
        List<RoomRate> ratesUsed = new ArrayList<>();
        for (int i = 0; i < numOfDays; i++) {
            //get the rate Per night for each night
            boolean foundRate = false;
            for (int j = rates.size() - 1; j >= 0; j--) {
                RoomRate rate = rates.get(j);

                boolean isCurrentDateWithinRange = true;
                if (rate.getStartDate() != null || rate.getEndDate() != null) {
                    LocalDate start = rate.getStartDate().toGregorianCalendar().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate end = rate.getEndDate().toGregorianCalendar().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    boolean lowerBound = checkInDate.isAfter(start) || checkInDate.isEqual(start);
                    boolean upperBound = checkInDate.isBefore(end) || checkInDate.isEqual(end);
                    isCurrentDateWithinRange = lowerBound && upperBound;
                }

                if ((isCurrentDateWithinRange) && !foundRate) {
                    if (!ratesUsed.contains(rate)) {
                        ratesUsed.add(rate); //Adding unique room rates
                    }
                    checkInDate = checkInDate.plusDays(1);
                    foundRate = true;
                }
            }
        }
        return ratesUsed;
    }
}
