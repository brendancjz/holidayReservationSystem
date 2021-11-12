/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Allocation;
import entity.AllocationException;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Stateless
public class AllocationSessionBean implements AllocationSessionBeanRemote, AllocationSessionBeanLocal {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    @EJB
    private RoomManagementSessionBeanLocal roomManagementSessionBean;

    @EJB
    private AllocationExceptionSessionBeanLocal allocationExceptionSessionBean;

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewAllocation(Allocation allocation) {

        em.persist(allocation);
        em.flush();

        return allocation.getAllocationId();
    }

    @Override
    public Allocation getAllocationByAllocationId(Long allocationId) {
        Allocation allocation = em.find(Allocation.class, allocationId);
        allocation.getRooms().size();
        return allocation;
    }

    @Override
    public List<Allocation> retrieveAllAllocations() throws EmptyListException {
        List<Allocation> list;
        Query query = em.createQuery("SELECT a FROM Allocation a");
        list = query.getResultList();
        if (list.isEmpty()) {
            throw new EmptyListException("Allocation List is empty.\n");
        }

        return list;
    }

    @Override
    public List<Allocation> getAllocationsForGuestForCurrentDay(Long guestId, LocalDate currDate) throws EmptyListException {

        Date curr = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        Query query = em.createQuery("SELECT a FROM Allocation a WHERE a.reservation.customer.customerId = :guestId AND a.currentDate = :currDate");
        query.setParameter("guestId", guestId);
        query.setParameter("currDate", curr);

        List<Allocation> list = query.getResultList();
        
        if (list.isEmpty()) throw new EmptyListException("Allocation list is empty.\n");
        for (Allocation allocation : list) {
            allocation.getRooms().size();
            allocation.getReservation();
        }

        return list;

    }

    @Override
    public void associateAllocationWithRoom(Allocation allocation, Long roomId) {
        allocation = em.merge(allocation);
        Room room = em.find(Room.class, roomId);
        room.setIsVacant(Boolean.FALSE);

        allocation.getRooms().add(room);
    }

    @Override
    public void associateAllocationWithReservation(Allocation allocation, Long reservationId) {
        Reservation r = em.find(Reservation.class, reservationId);

        allocation.setReservation(r);
    }

    @Override
    public List<Allocation> getAllocationsForGuestForCheckOutDay(Long customerId, LocalDate currDate) throws EmptyListException {
        Date curr = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        System.out.println("curr Day " + curr.toString());
        Query query = em.createQuery("SELECT a FROM Allocation a WHERE a.reservation.customer.customerId = :customerId AND a.reservation.endDate = :endDate");
        query.setParameter("customerId", customerId);
        query.setParameter("endDate", curr);

        List<Allocation> list = query.getResultList();
        
        if (list.isEmpty()) throw new EmptyListException("Allocation list is empty.\n");
        for (Allocation allocation : list) {
            allocation.getRooms().size();
            allocation.getReservation();
        }

        return list;
    }

    @Override
    public void removeAllocation(Allocation newAllocation) {
        newAllocation = em.merge(newAllocation);

        em.remove(newAllocation);
    }

    @Override
    public void dissociateAllocationWithRoomsAndReservation(Allocation newAllocation) {
        newAllocation = em.merge(newAllocation);
        int size = newAllocation.getRooms().size();
        for (int i = size - 1; i >= 0; i--) {
            newAllocation.getRooms().remove(i);
        }
        newAllocation.setReservation(null);
        newAllocation.setRoom(null);

        em.remove(newAllocation);
    }

    public void doRoomAllocation(LocalDate currDate) {
        try {
            System.out.println("==== Allocating Rooms To Current Day Reservations ====");

            Date curr = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            List<Reservation> reservations = reservationSessionBean.getReservationsToAllocate(currDate);
            if (reservations.isEmpty()) {
                System.out.println("No room to allocate today.\n");

                return;
            }

            for (Reservation reservation : reservations) {
                System.out.println("Allocating for Reservation ID: " + reservation.getReservationId());

                //Update reservation to the latest db
                reservation = reservationSessionBean.getReservationByReservationId(reservation.getReservationId());

                RoomType typeReserved = reservation.getRoomType();

                int numOfRoomsToAllocate = reservation.getNumOfRooms();
                List<Room> rooms = typeReserved.getRooms();

                List<Room> vacantRooms = new ArrayList<>();
                for (Room room : rooms) {
                    if (room.getIsVacant()) {
                        vacantRooms.add(room);
                    }

                }

                if (vacantRooms.size() >= numOfRoomsToAllocate) {
                    System.out.println("> Number of Rooms to allocate: " + numOfRoomsToAllocate);
                    System.out.println("> Number of Vacant Rooms: " + vacantRooms.size());

                    List<Room> allocatedRooms = vacantRooms.subList(0, numOfRoomsToAllocate);

                    //CREATE
                    Allocation newAllocation = new Allocation(curr);

                    //PERSIST
                    Long newAllocationId = this.createNewAllocation(newAllocation, reservation.getReservationId());

                    List<Long> roomList = new ArrayList<>();
                    for (Room room : allocatedRooms) {
                        roomList.add(room.getRoomId());
                    }
                    this.associateAllocationWithRooms(newAllocationId, roomList);

                    newAllocation = this.getAllocationByAllocationId(newAllocationId);
                    System.out.println("Successfully created an Allocation.");
                    DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                    System.out.println(":: Allocation ID: " + newAllocation.getAllocationId());
                    System.out.println("   > Reservation ID: " + newAllocation.getAllocationId());
                    System.out.println("   > Current Date:" + outputFormat.format(newAllocation.getCurrentDate()));
                    for (Room room : newAllocation.getRooms()) {
                        System.out.println("   > Room ID: " + room.getRoomId());
                    }
                    System.out.println();
                } else {

                    int rankOfRoomType = typeReserved.getTypeRank();

                    if (rankOfRoomType == 1) { //This is the highest rank. Confirm cannot allocate a rank higher. Throw typ2 exception
                        //CREATE
                        AllocationException exception = new AllocationException(curr, 2);
                        //ASSOCIATE

                        //PERSIST
                        allocationExceptionSessionBean.createNewAllocationException(exception, reservation.getReservationId());
                        System.out.println("Sorry. Type 2 Allocation Exception occurred.\n");

                        continue;
                    }

                    List<Room> allocatedRooms = vacantRooms;

                    //Type 1 Exception
                    //Allocate all the rooms of the current RoomType into this allocation
                    //CREATE
                    Allocation newAllocation = new Allocation(curr);

                    //Get the remaining rooms from other RoomTypes, while loop
                    int numOfRoomsNeedToUpgrade = numOfRoomsToAllocate - vacantRooms.size();
                    while (numOfRoomsNeedToUpgrade > 0) {
                        //get a higher rank RoomType
                        rankOfRoomType = rankOfRoomType - 1;

                        if (rankOfRoomType <= 0) {
                            //CREATE
                            AllocationException exception = new AllocationException(curr, 2);
                            //ASSOCIATE
                            allocationExceptionSessionBean.associateAllocationExceptionWithReservation(exception, reservation.getReservationId());
                            //PERSIST
                            allocationExceptionSessionBean.createNewAllocationException(exception);
                            System.out.println("Sorry. Type 2 Allocation Exception occurred.\n");

                            break;
                        }

                        RoomType higherRankedType = roomManagementSessionBean.getRoomTypeByRank(rankOfRoomType);

                        List<Room> higherRankedRooms = higherRankedType.getRooms();
                        List<Room> higherRankedVacantRooms = new ArrayList<>();
                        for (Room room : higherRankedRooms) {
                            if (room.getIsVacant()) {
                                higherRankedVacantRooms.add(room);

                            }

                        }

                        if (higherRankedVacantRooms.size() >= numOfRoomsNeedToUpgrade) {

                            List<Room> higherRankedAllocatedRooms = higherRankedVacantRooms.subList(0, numOfRoomsNeedToUpgrade);

                            for (Room room : higherRankedAllocatedRooms) {

                                allocatedRooms.add(room);
                            }

                            numOfRoomsNeedToUpgrade = 0;
                        } else {

                            if (!higherRankedVacantRooms.isEmpty()) {
                                for (Room room : higherRankedVacantRooms) {

                                    allocatedRooms.add(room);
                                }
                            }
                            numOfRoomsNeedToUpgrade -= higherRankedVacantRooms.size();
                        }

                    }

                    //PERSIST
                    Long newAllocationId = this.createNewAllocation(newAllocation, reservation.getReservationId());

                    //ASSOCIATING 
                    List<Long> roomList = new ArrayList<>();
                    for (Room room : allocatedRooms) {
                        roomList.add(room.getRoomId());
                    }
                    this.associateAllocationWithRooms(newAllocationId, roomList);
                    newAllocation = this.getAllocationByAllocationId(newAllocationId);
                    System.out.println("Successfully created an Allocation.");
                    DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                    System.out.println(":: Allocation ID: " + newAllocation.getAllocationId());
                    System.out.println("   > Reservation ID: " + newAllocation.getAllocationId());
                    System.out.println("   > Current Date:" + outputFormat.format(newAllocation.getCurrentDate()));
                    for (Room room : newAllocation.getRooms()) {
                        System.out.println("   > Room Type: " + room.getRoomType().getRoomTypeName() + " Room ID: " + room.getRoomId());
                    }
                    System.out.println();

                    //Create Type 1 Exception
                    //CREATE
                    AllocationException exception = new AllocationException(curr, 1);
                    //PERSIST
                    allocationExceptionSessionBean.createNewAllocationException(exception, reservation.getReservationId());

                    System.out.println("Type 1 Allocation Exception occurred.\n");

                }

            }

        } catch (Exception e) {
            System.out.println("Invalid input. Try again. " + e.toString());

        }
    }

    @Override
    public Long createNewAllocation(Allocation newAllocation, Long reservationId) {
        System.out.println("Creating new allocation with reservation.");
        this.associateAllocationWithReservation(newAllocation, reservationId);
        em.persist(newAllocation);
        em.flush();

        return newAllocation.getAllocationId();
    }

    @Override
    public void associateAllocationWithRooms(Long newAllocationId, List<Long> allocatedRoomIds) {
        Allocation allocation = this.getAllocationByAllocationId(newAllocationId);
        //ASSOCIATE
        for (Long roomId : allocatedRoomIds) {
            Room room = em.find(Room.class, roomId);
            allocation.getRooms().add(room);
            room.setIsVacant(Boolean.FALSE);
        }
    }

}
