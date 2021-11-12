/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.RoomManagementSessionBeanRemote;
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
import util.enumeration.RoomRateEnum;
import util.exception.EmptyListException;
import util.exception.InvalidInputException;
import util.exception.RoomRateExistException;

/**
 *
 * @author brend
 */
public class SalesManagerModule {

    private RoomManagementSessionBeanRemote roomManagementSessionBean;

    public SalesManagerModule(RoomManagementSessionBeanRemote roomManagementSessionBean) {
        this.roomManagementSessionBean = roomManagementSessionBean;
    }

    public void doSalesManagerDashboardFeatures(Scanner sc) {
        System.out.println("==== Sales Manager Dashboard Interface ====");
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
                doCreateNewRoomRate(sc);
                break;
            case 2:
                System.out.println("You have selected 'View Room Rate Details'\n");
                doViewRoomRateDetails(sc);
                break;
            case 3:
                System.out.println("You have selected 'View All Room Rates'\n");
                doViewAllRoomRates(sc);
                break;
            case 4:
                System.out.println("You have logged out.\n");

                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doSalesManagerDashboardFeatures(sc);
                break;
        }
    }

    private void doCancelledEntry(Scanner sc) {
        System.out.println("\n You have cancelled entry. Taking you back to dashboard.\n");

        doSalesManagerDashboardFeatures(sc);
    }

    private void doCreateNewRoomRate(Scanner sc) {
        try {
            System.out.println("==== Create New Room Rate Interface ====");
            System.out.println("Enter details to create new room rate. To cancel at anything, enter 'q'.");
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            System.out.println("Select Room Type to have the new Room Rate:");
            int idx = 1;
            for (RoomType type : types) {
                if (!type.getIsDisabled()) {
                    System.out.println("> " + idx++ + ". " + type.getRoomTypeName());
                }
            }
            System.out.print("> ");
            String tInput = sc.next();
            sc.nextLine();
            if (tInput.equals("q")) {
                doCancelledEntry(sc);
                return;
            }
            int typeInput = Integer.parseInt(tInput);

            System.out.println("** You have selected: " + types.get(typeInput - 1).getRoomTypeName() + "\n");
            System.out.println("Select Room Rate Type:");
            RoomRateEnum[] rateEnums = new RoomRateEnum[]{RoomRateEnum.PublishedRate,
                RoomRateEnum.NormalRate,
                RoomRateEnum.PeakRate,
                RoomRateEnum.PromotionRate};
            for (int i = 0; i < rateEnums.length; i++) {
                System.out.println("> " + (i + 1) + ". " + rateEnums[i]);
            }
            System.out.print("> ");
            String rInput = sc.next();
            sc.nextLine();
            if (rInput.equals("q")) {
                doCancelledEntry(sc);
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
                    doCancelledEntry(sc);
                    return;
                }
                startDate = LocalDate.parse(start, dtFormat).atStartOfDay();
                System.out.print("> End Date [DD MM YYYY]: ");
                String end = sc.nextLine();
                if (end.equals("q")) {
                    doCancelledEntry(sc);
                    return;
                }
                endDate = LocalDate.parse(end, dtFormat).atStartOfDay();

                if (endDate.isBefore(startDate)) {
                    throw new InvalidInputException("Invalid dates input.\n");
                }
                
                System.out.println("** You have selected the period of " + (ChronoUnit.DAYS.between(startDate, endDate) + 1)
                        + " day(s): " + start + " -> " + end + "\n");
            }

            System.out.print("> Rate Per Night: ");
            String inputRate = sc.next();
            sc.nextLine();
            if (inputRate.equals("q")) {
                doCancelledEntry(sc);
                return;
            }
            double rateAmount = Double.parseDouble(inputRate);
            System.out.println("** You have selected: $" + rateAmount + "\n");

            String rateName = rateEnums[rateInput - 1].toString() + types.get(typeInput - 1).getRoomTypeName();
            RoomRate checkRateExist = roomManagementSessionBean.getRoomRate(rateName);
            if (checkRateExist != null) {
                throw new RoomRateExistException("Room Rate already exists.\n");
            }

            RoomRate rate = roomManagementSessionBean.createNewRoomRate(types.get(typeInput - 1).getRoomTypeId(), rateEnums[rateInput - 1], startDate, endDate, rateAmount);
            System.out.println("You have successfully created a new Room Rate.");
            System.out.println("> Name: " + rate.getRoomRateName());
            System.out.println("> Type: " + rate.getRoomRateType());
            System.out.println("> Amount: " + rate.getRatePerNight());
            if (rate.getStartDate() != null) {
                System.out.println("> Validity Period: " + rate.getStartDate().toString()
                        + " -> " + rate.getEndDate().toString());
            }
            System.out.println();

            doSalesManagerDashboardFeatures(sc);
        } catch (NumberFormatException | EmptyListException | RoomRateExistException e) {
            System.out.println("\nInvalid input. Try again.\n");
            System.out.println(e.toString() + "\n");
            doCreateNewRoomRate(sc);
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
            doSalesManagerDashboardFeatures(sc);
        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            doSalesManagerDashboardFeatures(sc);
        }
    }

    private void doViewRoomRateDetails(Scanner sc) {
        try {
            System.out.println("==== View Room Rate Details Interface ====");
            System.out.println("Viewing room rate. To cancel entry, enter 'q'.");
            System.out.print("> Room Rate Name: ");
            String rateName = sc.nextLine();
            if (rateName.equals("q")) {
                doCancelledEntry(sc);
                return;
            }
            RoomRate rate = roomManagementSessionBean.getRoomRate(rateName);
            if (rate == null) {
                throw new RoomRateExistException("Room Rate does not exist.\n");
            }

            System.out.println();
            System.out.println("Selected Room Rate details:");
            System.out.println("> Name: " + rate.getRoomRateName());
            System.out.println("> Type: " + rate.getRoomRateType());
            System.out.println("> Amount: " + rate.getRatePerNight());
            System.out.println("> Is Disabled: " + rate.getIsDisabled());
            if (rate.getStartDate() != null) {
                System.out.println("> Validity Period: " + rate.getStartDate().toString()
                        + " -> " + rate.getEndDate().toString());
            } else {
                System.out.println("> Validity Period: NULL");
            }
            System.out.println();

            System.out.println("   Select an action:");
            System.out.println("   > 1. Update Room Rate");
            System.out.println("   > 2. Delete Room Rate");
            System.out.println("   > 3. Back to Dashboard");
            System.out.print("   > ");
            int input = sc.nextInt();
            sc.nextLine();
            System.out.println();

            switch (input) {
                case 1:
                    doUpdateRoomRate(sc, rate.getRoomRateId());
                    break;
                case 2:
                    doDeleteRoomRate(sc, rate.getRoomRateId());
                    break;
                case 3:
                    doSalesManagerDashboardFeatures(sc);
                    break;
                default:
                    System.out.println("Invalid input.");
                    doSalesManagerDashboardFeatures(sc);
                    break;
            }
        } catch (RoomRateExistException ex) {
            System.out.println("Error: " + ex.getMessage() + "\n");
            doViewRoomRateDetails(sc);
        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            doSalesManagerDashboardFeatures(sc);
        }

    }

    private void doUpdateRoomRate(Scanner sc, Long rateId) {
        System.out.println("==== Update Room Rate Interface ====");
        System.out.println("Updating a room rate. To cancel entry, enter 'q'");
        try {
            RoomRate rate = roomManagementSessionBean.getRoomRate(rateId);
            if (rate == null) {
                throw new RoomRateExistException("Room Rate does not exist.\n");
            }
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
                String inputStr = sc.next();
                sc.nextLine();
                if (inputStr.equals("q")) {
                    doCancelledEntry(sc);
                    return;
                }
                int input = Integer.parseInt(inputStr);
                System.out.println();

                switch (input) {
                    case 1:
                        System.out.print("> Input new Name: ");
                        name = sc.nextLine().trim();
                        break;
                    case 2:
                        System.out.print("> Input new Amount: ");
                        amount = sc.nextDouble();
                        sc.nextLine();
                        System.out.println();
                        break;
                    case 3:
                        if (rate.getRoomRateType().equals(RoomRateEnum.PeakRate.toString())
                                || rate.getRoomRateType().equals(RoomRateEnum.PromotionRate.toString())) {

                            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");

                            System.out.print("> Input new Start Date [DD MM YYYY]: ");
                            String start = sc.nextLine().trim();

                            System.out.print("> Input new End Date [DD MM YYYY]: ");
                            String end = sc.nextLine().trim();
                            System.out.println();

                            LocalDate checkInDate = LocalDate.parse(start, dtFormat);
                            LocalDate checkOutDate = LocalDate.parse(end, dtFormat);

                            if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
                                throw new InvalidInputException("Invalid dates input.\n");
                            }

                            startDate = Date.from(checkInDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                            endDate = Date.from(checkOutDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                        } else {
                            System.out.println("Sorry. your Rate Type do not require a validity period.\n");
                        }
                        break;
                    default:
                        System.out.println("Invalid input.");
                        doUpdateRoomRate(sc, rateId);
                        break;
                }

                System.out.println("Finalise changes?");
                System.out.println("> 1. Yes");
                System.out.println("> 2. No");
                System.out.print("> ");
                int answer = sc.nextInt();
                sc.nextLine();
                System.out.println();
                if (answer == 1) {
                    done = true;
                }
            }

            roomManagementSessionBean.updateRoomRate(rateId, name, amount, startDate, endDate);
            System.out.println("You have successfully updated the Room Rate.\n");

            rate = roomManagementSessionBean.getRoomRate(rateId);
            System.out.println("Updated Room Rate details:");
            System.out.println("> Name: " + rate.getRoomRateName());
            System.out.println("> Type: " + rate.getRoomRateType());
            System.out.println("> Amount: " + rate.getRatePerNight());
            if (rate.getStartDate() != null) {
                System.out.println("> Validity Period: " + rate.getStartDate().toString()
                        + " -> " + rate.getEndDate().toString());
            } else {
                System.out.println("> Validity Period: NULL");
            }
            System.out.println();

            doSalesManagerDashboardFeatures(sc);
        } catch (InvalidInputException | NumberFormatException | RoomRateExistException ex) {
            System.out.println("Error: " + ex.getMessage());
            doSalesManagerDashboardFeatures(sc);
        } catch (Exception e) {
            doSalesManagerDashboardFeatures(sc);
        }
    }

    private void doDeleteRoomRate(Scanner sc, Long roomRateId) {
        System.out.println("=== Delete Room Rate Interface ====");
        System.out.println("Confirm Deletion?");
        System.out.println("> 1. Yes");
        System.out.println("> 2. No");
        System.out.print("> ");
        try {
            int input = sc.nextInt();
            sc.nextLine();
            System.out.println();
            switch (input) {
                case 1:
                    roomManagementSessionBean.deleteRoomRate(roomRateId);
                    System.out.println("You have successfully deleted/disabled the Room Rate.\n");
                    break;

                case 2:
                    doSalesManagerDashboardFeatures(sc);
                    break;
                default:
                    System.out.println("Invalid input. Try again.\n");
                    doDeleteRoomRate(sc, roomRateId);
                    break;
            }
            doSalesManagerDashboardFeatures(sc);
        } catch (Exception ex) {
            System.out.println("Uh oh.. Something went wrong.\n");
            doSalesManagerDashboardFeatures(sc);
        }
    }

    private void doViewAllRoomRates(Scanner sc) {
        System.out.println("==== View All Room Rates Interface ====");
        try {
            List<RoomRate> list = roomManagementSessionBean.getAllRoomRates();

            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            System.out.printf("\n%3s%40s%15s%30s%10s%15s%30s", "ID", "Rate Name", "Rate Type", "Room Type", "Amount", "Is Disabled", "Validty Period");

            for (RoomRate rate : list) {
                String validityPeriod;
                if (rate.getStartDate() != null) {
                    validityPeriod = outputFormat.format(rate.getStartDate())
                            + " -> " + outputFormat.format(rate.getEndDate());
                } else {
                    validityPeriod = "NULL";
                }

                System.out.printf("\n%3s%40s%15s%30s%10s%15s%30s", rate.getRoomRateId(),
                        rate.getRoomRateName(), rate.getRoomRateType(), rate.getRoomType().getRoomTypeName(),
                        rate.getRatePerNight(), rate.getIsDisabled(), validityPeriod);

            }

            System.out.println();
            System.out.println();
            doSalesManagerDashboardFeatures(sc);
        } catch (EmptyListException e) {
            System.out.println("Error: " + e.getMessage());
            doSalesManagerDashboardFeatures(sc);
        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            doSalesManagerDashboardFeatures(sc);
        }
    }
}
