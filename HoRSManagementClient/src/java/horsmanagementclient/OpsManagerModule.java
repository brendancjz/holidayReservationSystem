/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.AllocationExceptionSessionBeanRemote;
import ejb.session.stateless.RoomManagementSessionBeanRemote;
import entity.AllocationException;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;
import javax.ejb.EJBTransactionRolledbackException;
import util.exception.EmptyListException;
import util.exception.InvalidInputException;
import util.exception.RoomExistException;
import util.exception.RoomTypeExistException;
import util.exception.RoomTypeRankException;

/**
 *
 * @author brend
 */
public class OpsManagerModule {

    private RoomManagementSessionBeanRemote roomManagementSessionBean;
    private AllocationExceptionSessionBeanRemote allocationExceptionSessionBean;

    public OpsManagerModule(RoomManagementSessionBeanRemote roomManagementSessionBean, AllocationExceptionSessionBeanRemote allocationExceptionSessionBean) {
        this.roomManagementSessionBean = roomManagementSessionBean;
        this.allocationExceptionSessionBean = allocationExceptionSessionBean;
    }

    public void doOpsManagerDashboardFeatures(Scanner sc, Long emId) {
        System.out.println("==== Ops Manager Dashboard Interface ====");
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
                doCreateNewRoomType(sc, emId);
                break;
            case 2:
                System.out.println("You have selected 'View Room Type Details'\n");
                doViewRoomTypeDetails(sc, emId);
                break;
            case 3:
                System.out.println("You have selected 'View All Room Types'\n");
                doViewAllRoomTypes(sc, emId);
                break;
            case 4:
                System.out.println("You have selected 'Create New Room'\n");
                doCreateNewRoom(sc, emId);
                break;
            case 5:
                System.out.println("You have selected 'Update Room'\n");
                doUpdateRoom(sc, emId);
                break;
            case 6:
                System.out.println("You have selected 'Delete Room'\n");
                doDeleteRoom(sc, emId);
                break;
            case 7:
                System.out.println("You have selected 'View All Rooms'\n");
                doViewAllRooms(sc, emId);
                break;
            case 8:
                System.out.println("You have selected 'View Room Allocation Exception Report'\n");
                doViewRoomAllocationExceptionReport(sc, emId);
                break;
            case 9:
                System.out.println("You have logged out.\n");

                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doOpsManagerDashboardFeatures(sc, emId);
                break;
        }
    }

    private void doCreateNewRoomType(Scanner sc, Long emId) {
        try {
            System.out.println("==== Create New Room Type Interface ====");
            //String roomTypeName, String roomTypeDesc, Integer roomSize, Integer numOfBeds, Integer capacity, String amenities
            System.out.println("Creating new room type. To cancel anytime, enter 'q'.");
            System.out.print("> Room Type Name [5 - 50 Characters]: ");
            String typeName = sc.nextLine();
            if (typeName.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
 
            //Check if exists
            RoomType checkTypeExist = roomManagementSessionBean.getRoomType(typeName);
            if (checkTypeExist != null) {
                throw new RoomTypeExistException("Room Type Name already exist. Try again.\n");
            }

            System.out.print("> Room Type Description [MIN 5 CHAR]: ");
            String typeDesc = sc.nextLine();
            if (typeDesc.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            System.out.print("> Room Size: ");
            String roomInput = sc.next();
            sc.nextLine();
            if (roomInput.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            Integer roomSize = Integer.parseInt(roomInput);
            System.out.print("> Number Of Beds: ");
            String bedInput = sc.next();
            sc.nextLine();
            if (bedInput.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            Integer numOfBeds = Integer.parseInt(bedInput);
            System.out.print("> Room Capacity: ");
            String capInput = sc.next();
            sc.nextLine();
            if (capInput.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            Integer cap = Integer.parseInt(capInput);

            int numOfRoomTypes = roomManagementSessionBean.getAllNonDisabledRoomTypes().size() + 1;
            System.out.print("> Select Rank [1 - " + (numOfRoomTypes) + "]: ");
            String rankInput = sc.next();
            sc.nextLine();
            if (rankInput.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            } else if ((Integer.parseInt(rankInput) <= 0 || Integer.parseInt(rankInput) > numOfRoomTypes)) {
                throw new RoomTypeRankException("Rank is out of range.");
            }
            Integer rank = Integer.parseInt(rankInput);
            System.out.print("> Room Amenities [MIN 5 CHAR]: ");
            String amenities = sc.nextLine();
            if (amenities.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            System.out.println();

            roomManagementSessionBean.updateRoomTypeRankingsCreation(rank);
            RoomType newRoomType = new RoomType(typeName, typeDesc, roomSize, numOfBeds, cap, rank, amenities);

            Long newRoomTypeId = roomManagementSessionBean.createNewRoomType(newRoomType);
            
            RoomType type = roomManagementSessionBean.getRoomType(newRoomTypeId);

            System.out.println("You have successfully created a new Room Type.");
            System.out.println("> Name: " + type.getRoomTypeName());
            System.out.println("> Description: " + type.getRoomTypeDesc());
            System.out.println("> Size: " + type.getRoomSize());
            System.out.println("> Number Of Beds: " + type.getNumOfBeds());
            System.out.println("> Capacity: " + type.getCapacity());
            System.out.println("> Ranking: " + type.getTypeRank());
            System.out.println("> Amenities: " + type.getAmenities());
            System.out.println();

            doOpsManagerDashboardFeatures(sc, emId);
        } catch (RoomTypeRankException | RoomTypeExistException | EmptyListException ex) {
            System.out.println(ex.getMessage());
            doOpsManagerDashboardFeatures(sc, emId);
        } catch (EJBTransactionRolledbackException e) {
            System.out.println("Sorry. You have inputted invalid values. Try again.\n");
            doOpsManagerDashboardFeatures(sc, emId);
        } catch (Exception e) {
            System.out.println("Something went wrong.");
            System.out.println(e.toString());
            doCreateNewRoomType(sc, emId);
        }
    }

    private void doViewRoomTypeDetails(Scanner sc, Long emId) {
        System.out.println("==== View Room Type Details Interface ====");
        System.out.println("Viewing room type. To cancel entry anytime, enter 'q'.");
        try {

            System.out.print("> Room Type Name: ");
            String typeName = sc.nextLine();
            System.out.println();
            if (typeName.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            RoomType type = roomManagementSessionBean.getRoomType(typeName);
            if (type == null) {
                throw new RoomTypeExistException("Room Type does not exist.\n");
            }
            System.out.println("Selected Room Type details:");
            System.out.println("> Name: " + type.getRoomTypeName());
            System.out.println("> Description: " + type.getRoomTypeDesc());
            System.out.println("> Size: " + type.getRoomSize());
            System.out.println("> Number Of Beds: " + type.getNumOfBeds());
            System.out.println("> Capacity: " + type.getCapacity());
            System.out.println("> Ranking: " + type.getTypeRank());
            System.out.println("> Amenities: " + type.getAmenities());
            System.out.println("> Number of Rooms: " + type.getRooms().size());
            System.out.println("> Is Disabled: " + type.getIsDisabled());
            System.out.println("> Room Rates:");
            
            try {
                List<RoomRate> rates = roomManagementSessionBean.getRoomRates(type.getRoomTypeId());
                for (RoomRate rate : rates) {
                    System.out.println("  > " + rate.getRoomRateName());
                }
                System.out.println();
            } catch (EmptyListException e) {
                System.out.println("     NULL");
            }

            System.out.println("\n   Select an action:");
            System.out.println("   > 1. Update Room Type");
            System.out.println("   > 2. Delete Room Type");
            System.out.println("   > 3. Back to Dashboard");
            System.out.print("   > ");
            int input = sc.nextInt();
            sc.nextLine();
            System.out.println();

            switch (input) {
                case 1:
                    doUpdateRoomType(sc, emId, type.getRoomTypeId());
                    break;
                case 2:
                    doDeleteRoomType(sc, emId, type.getRoomTypeId());
                    break;
                case 3:
                    doOpsManagerDashboardFeatures(sc, emId);
                    break;
                default:
                    System.out.println("Invalid input.");
                    doOpsManagerDashboardFeatures(sc, emId);
                    break;
            }
        } catch (RoomTypeExistException e) {
            System.out.println(e.getMessage());
            doOpsManagerDashboardFeatures(sc, emId);
        }
    }

    private void doViewAllRoomTypes(Scanner sc, Long emId) {
        try {
            System.out.println("==== View All Room Types Interface");
            System.out.printf("\n%3s%30s%25s%15s%15s%15s%15s%15s%12s%50s", "ID", "Type Name", "Description", "Room Size", "No. of Beds", "Capacity", "No. of Rooms", "Is Disabled", "Rank", "Amenities");

            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            for (RoomType type : types) {

                System.out.printf("\n%3s%30s%25s%15s%15s%15s%15s%15s%12s%50s", type.getRoomTypeId(), type.getRoomTypeName(), type.getRoomTypeDesc(),
                        type.getRoomSize(), type.getNumOfBeds(), type.getCapacity(), type.getRooms().size(),
                        type.getIsDisabled(), type.getTypeRank(), type.getAmenities());

            }

            System.out.println();
            System.out.println();

        } catch (EmptyListException e) {
            System.out.println("Main exception: " + e.toString() + "\n");

        }
        doOpsManagerDashboardFeatures(sc, emId);
    }

    private void doCreateNewRoom(Scanner sc, Long emId) {
        try {
            System.out.println("==== Create New Room Interface");
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            System.out.println("Select Room Type to have the new Room Rate:");
            int idx = 1;
            for (RoomType type : types) {
                if (!type.getIsDisabled()) {
                    System.out.println("> " + idx++ + ". " + type.getRoomTypeName());
                }
            }
            System.out.print("> ");
            String tInput = sc.next().trim();
            sc.nextLine();
            if (tInput.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            int typeInput = Integer.parseInt(tInput);
            if (typeInput <= 0 || typeInput > types.size()) {
                throw new InvalidInputException("Sorry, invalid type input. Try again.\n");
            }
            System.out.println("** You have selected: " + types.get(typeInput - 1).getRoomTypeName() + "\n");

            System.out.println("Creating new Room:");
            System.out.print("> Room Level: ");
            String roomInput = sc.next().trim();
            sc.nextLine();
            if (roomInput.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            int level = Integer.parseInt(roomInput);
            System.out.print("> Room Number: ");
            String numInput = sc.next().trim();
            sc.nextLine();
            if (numInput.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            int num = Integer.parseInt(numInput);
            System.out.println();
            
            Room r = roomManagementSessionBean.getRoom(level, num);
            if (r != null) throw new RoomExistException("Room already exist. Try another room level and number.\n");

            Room newRoom = new Room(level, num);
            newRoom = roomManagementSessionBean.createNewRoom(newRoom, types.get(typeInput - 1).getRoomTypeId());

            System.out.println("You have successfully created a new Room.");
            System.out.println("> Room Level: " + newRoom.getRoomLevel());
            System.out.println("> Room Number: " + newRoom.getRoomNum());
            System.out.println("> Room Type: " + newRoom.getRoomType().getRoomTypeName());
            System.out.println("> Is Available: " + newRoom.getIsAvailable());
            System.out.println("> Is Disabled: " + newRoom.getIsDisabled());
            System.out.println();

            doOpsManagerDashboardFeatures(sc, emId);

        } catch (RoomExistException | InvalidInputException | NumberFormatException | EmptyListException e) {
            System.out.println(e.getMessage());
            doOpsManagerDashboardFeatures(sc, emId);
        } catch (EJBTransactionRolledbackException e) {
            System.out.println("Sorry. You have inputted invalid values. Try again.\n");
            doOpsManagerDashboardFeatures(sc, emId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doUpdateRoom(Scanner sc, Long emId) {
        try {
            System.out.println("==== Update Room Interface ====");
            System.out.println("Updating room. To cancel entry at anytime, enter 'q'.");
            System.out.print("> Input existing Room Level: ");
            String roomInput = sc.next().trim();
            sc.nextLine();
            if (roomInput.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            int level = Integer.parseInt(roomInput);
            System.out.print("> Input existing Room Number: ");
            String numInput = sc.next().trim();
            sc.nextLine();
            if (numInput.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            int number = Integer.parseInt(numInput);
            System.out.println();

            Room room = roomManagementSessionBean.getRoom(level, number);
            if (room == null) {
                throw new RoomExistException("Room does not exist.\n");
            }

            boolean isAvail = room.getIsAvailable();
            level = room.getRoomLevel();
            number = room.getRoomNum();
            RoomType type = room.getRoomType();

            if (room.getIsDisabled()) {
                System.out.println("Sorry, you selected a disabled Room. Try again with another room.\n");
                doUpdateRoom(sc, emId);
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
                int input = sc.nextInt();
                sc.nextLine();
                System.out.println();

                switch (input) {
                    case 1:
                        System.out.print("> Input new level: ");
                        level = sc.nextInt();
                        sc.nextLine();
                        break;
                    case 2:
                        System.out.print("> Input new number: ");
                        number = sc.nextInt();
                        sc.nextLine();
                        break;
                    case 3:
                        List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
                        System.out.println("Select Room Type to change to:");
                        int idx = 1;
                        for (RoomType t : types) {
                            if (!t.getIsDisabled()) {
                                System.out.println("> " + idx++ + ". " + t.getRoomTypeName());
                            }
                        }

                        System.out.print("> ");
                        int typeInput = sc.nextInt();
                        sc.nextLine();
                        if (typeInput <= 0 || typeInput >= types.size()) {
                            throw new InvalidInputException("Sorry, invalid type input. Restarting.\n");
                        }

                        type = types.get(typeInput - 1);
                        break;
                    case 4:
                        System.out.println("> Select True or False: ");
                        System.out.println("  > 1. True");
                        System.out.println("  > 2. False");
                        System.out.print("  > ");
                        int input2 = sc.nextInt();
                        sc.nextLine();

                        isAvail = (input2 == 1);
                        break;
                    default:
                        continue;

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

            roomManagementSessionBean.updateRoom(room.getRoomId(), level, number, isAvail, type);

            System.out.println("You have successfully updated the Room.\n");

            room = roomManagementSessionBean.getRoom(room.getRoomId());

            System.out.println("Updated Room details:");
            System.out.println("> Room Level: " + room.getRoomLevel());
            System.out.println("> Room Number: " + room.getRoomNum());
            System.out.println("> IsAvailable: " + room.getIsAvailable());
            System.out.println("> Room Type: " + room.getRoomType().getRoomTypeName());
            System.out.println();

            doOpsManagerDashboardFeatures(sc, emId);
        } catch (InvalidInputException | NumberFormatException | EmptyListException | RoomExistException e) {
            System.out.println(e.getMessage());
            doOpsManagerDashboardFeatures(sc, emId);
        }
    }

    private void doDeleteRoom(Scanner sc, Long emId) {
        try {
            System.out.println("==== Delete Room Interface ====");
            System.out.print("> Input existing Room Level: ");
            String roomInput = sc.next();
            sc.nextLine();
            if (roomInput.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            int level = Integer.parseInt(roomInput);
            System.out.print("> Input existing Room Number: ");
            String numInput = sc.next();
            sc.nextLine();
            if (numInput.equals("q")) {
                doCancelledEntry(sc, emId);
                return;
            }
            int number = Integer.parseInt(numInput);
            System.out.println();

            Room room = roomManagementSessionBean.getRoom(level, number);
            if (room == null) {
                throw new RoomExistException("Room does not exist.\n");
            }

            if (room.getIsDisabled()) {
                System.out.println("Sorry, you selected a disabled Room. Try again with another room.\n");
                doOpsManagerDashboardFeatures(sc, emId);
                return;
            } else if (!room.getIsAvailable()) {
                System.out.println("Sorry, you selected an unavailable Room. Try again with another room.\n");
                doOpsManagerDashboardFeatures(sc, emId);
                return;
            }

            System.out.println("You have selected Room ID: " + room.getRoomId());
            System.out.println("Confirm Deletion?");
            System.out.println("> 1. Yes");
            System.out.println("> 2. No");
            System.out.print("> ");

            int input = sc.nextInt();
            sc.nextLine();
            System.out.println();
            switch (input) {
                case 1:
                    roomManagementSessionBean.deleteRoom(room.getRoomId());
                    System.out.println("You have successfully deleted/disabled the Room Rate.\n");
                    break;

                case 2:
                    doOpsManagerDashboardFeatures(sc, emId);
                    break;
                default:
                    System.out.println("Invalid input. Try again.\n");
                    doDeleteRoom(sc, emId);
                    break;
            }

            doOpsManagerDashboardFeatures(sc, emId);
        } catch (NumberFormatException | RoomExistException e) {
            System.out.println(e.getMessage());
            doOpsManagerDashboardFeatures(sc, emId);
        }
    }

    private void doViewAllRooms(Scanner sc, Long emId) {
        try {
            System.out.println("==== View All Rooms Interface ====");
            List<Room> rooms = roomManagementSessionBean.retrieveAllRooms();
            System.out.printf("\n%3s%10s%10s%30s%15s", "ID", "Level", "Number", "Room Type", "Is Available");

            for (Room room : rooms) {
                if (!room.getIsDisabled()) {
                    System.out.printf("\n%3s%10s%10s%30s%15s", room.getRoomId(), room.getRoomLevel(),
                            room.getRoomNum(), room.getRoomType().getRoomTypeName(), room.getIsAvailable());

                }

            }
            System.out.println();
            System.out.println();
            doOpsManagerDashboardFeatures(sc, emId);
        } catch (EmptyListException ex) {
            System.out.println(ex.getMessage());
            doOpsManagerDashboardFeatures(sc, emId);
        }
    }

    private void doUpdateRoomType(Scanner sc, Long emId, Long roomTypeId) {
        System.out.println("==== Update Room Type Interface ====");
        System.out.println("Updating room type. To cancel entry at anytime, enter 'q'.");
        try {
            RoomType type = roomManagementSessionBean.getRoomType(roomTypeId);
            if (type == null) {
                throw new RoomTypeExistException("Room Type does not exist.\n");
            }

            boolean done = false;
            String name = type.getRoomTypeName();
            String desc = type.getRoomTypeDesc();
            Integer size = type.getRoomSize();
            Integer beds = type.getNumOfBeds();
            Integer cap = type.getCapacity();
            Integer rank = type.getTypeRank();
            String amenities = type.getAmenities();

            List<RoomType> typeList = roomManagementSessionBean.getAllNonDisabledRoomTypes();

            while (!done) {
                System.out.println("Select which detail of the type you want to change:");
                System.out.println("> 1. Name");
                System.out.println("> 2. Description");
                System.out.println("> 3. Room Size");
                System.out.println("> 4. Number Of Beds");
                System.out.println("> 5. Room Capacity");
                System.out.println("> 6. Ranking");
                System.out.println("> 7. Amenities");
                System.out.print("> ");
                String inputStr = sc.next();
                sc.nextLine();
                if (inputStr.equals("q")) {
                    doCancelledEntry(sc, emId);
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
                        System.out.print("> Input new Description: ");
                        desc = sc.nextLine().trim();
                        break;
                    case 3:
                        System.out.print("> Input new Room Size: ");
                        size = sc.nextInt();
                        sc.nextLine();
                        break;
                    case 4:
                        System.out.print("> Input new Number Of Beds: ");
                        beds = sc.nextInt();
                        sc.nextLine();
                        break;
                    case 5:
                        System.out.print("> Input new Room Capacity: ");
                        cap = sc.nextInt();
                        sc.nextLine();
                        break;
                    case 6:
                        System.out.print("> Input new Ranking [1-" + typeList.size() + "]: ");
                        rank = sc.nextInt();
                        sc.nextLine();
                        if (rank <= 0 || rank >= typeList.size()) {
                            throw new InvalidInputException("Invalid rank input.\n");
                        }
                        
                        break;
                    case 7:
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
                int answer = sc.nextInt();
                sc.nextLine();
                System.out.println();
                if (answer == 1) {
                    done = true;
                }
            }
            roomManagementSessionBean.updateRoomTypeRankingsUpdate(type.getTypeRank(), rank);
            roomManagementSessionBean.updateRoomType(roomTypeId, name, desc, size, beds, cap, rank, amenities);
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

            doOpsManagerDashboardFeatures(sc, emId);
        } catch (RoomTypeExistException | EmptyListException ex) {
            System.out.println(ex.getMessage());
            doOpsManagerDashboardFeatures(sc, emId);
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
            doUpdateRoomType(sc, emId, roomTypeId);
        } catch (Exception e) {
            System.out.println("Invalid Input. Going back to dashboard.\n");
            doOpsManagerDashboardFeatures(sc, emId);
        }
    }

    private void doDeleteRoomType(Scanner sc, Long emId, Long roomTypeId) {
        try {
            System.out.println("=== Delete Room Type Interface ====");
            System.out.println("Confirm Deletion?");
            System.out.println("> 1. Yes");
            System.out.println("> 2. No");
            System.out.print("> ");

            int input = sc.nextInt();
            sc.nextLine();
            System.out.println();
            switch (input) {
                case 1:
                    roomManagementSessionBean.deleteRoomType(roomTypeId);
                    System.out.println("You have successfully deleted/disabled the Room Type.\n");
                    break;

                case 2:
                    doOpsManagerDashboardFeatures(sc, emId);
                    break;
                default:
                    System.out.println("Invalid input. Try again.\n");
                    doDeleteRoomType(sc, emId, roomTypeId);
                    break;
            }

            doOpsManagerDashboardFeatures(sc, emId);
        } catch (EmptyListException ex) {
            System.out.println(ex.getMessage());
            doOpsManagerDashboardFeatures(sc, emId);
        }

    }

    private void doCancelledEntry(Scanner sc, Long emId) {
        System.out.println("\n You have cancelled entry. Taking you back to dashboard.\n");

        doOpsManagerDashboardFeatures(sc, emId);
    }

    private void doViewRoomAllocationExceptionReport(Scanner sc, Long emId) {
        try {
            System.out.println("==== View Room Allocation Exception Report ====");
            List<AllocationException> list = allocationExceptionSessionBean.retrieveAllExceptions();

            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (AllocationException a : list) {
                System.out.println(":: AllocationException ID: " + a.getExceptionId());
                System.out.println("   > Exception Type: " + a.getExceptionType());
                System.out.println("   > Date: " + outputFormat.format(a.getCurrentDate()));
                System.out.println("   > Reservation ID: " + a.getReservation().getReservationId());
            }

            System.out.println();
            doOpsManagerDashboardFeatures(sc, emId);
        } catch (EmptyListException e) {
            System.out.println(e.getMessage());
            doOpsManagerDashboardFeatures(sc, emId);
        }

    }
}
