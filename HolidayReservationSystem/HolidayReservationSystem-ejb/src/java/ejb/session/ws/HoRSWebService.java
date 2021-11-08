/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomManagementSessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@WebService(serviceName = "HoRSWebService")
@Stateless()
public class HoRSWebService {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    @EJB
    private RoomManagementSessionBeanLocal roomManagementSessionBean;

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "retrieveAllPartners")
    public List<Partner> retrieveAllPartners() {
        try {
            List<Partner> partners = partnerSessionBean.retrieveAllPartners();

            for (Partner partner : partners) {

                for (Reservation reservation : partner.getReservations()) {
                    em.detach(reservation);
                    reservation.setCustomer(null);
                }

                em.detach(partner);
            }

            return partners;
        } catch (EmptyListException ex) {
            return null;
        }
    }

    @WebMethod(operationName = "checkPartnerExists")
    public Boolean checkPartnerExists(@WebParam(name = "email") String email) {
        return partnerSessionBean.checkPartnerExists(email);
    }

    @WebMethod(operationName = "getPartnerByEmail")
    public Partner getPartnerByEmail(@WebParam(name = "email") String email) {
        Partner partner = partnerSessionBean.getPartnerByEmail(email);
        if (partner != null) {
            em.detach(partner);
            partner.setReservations(null);
        }

        return partner;
    }

    @WebMethod(operationName = "getReservationByReservationId")
    public Reservation getReservationByReservationId(@WebParam(name = "id") Long reservationId) {
        Reservation reservation = reservationSessionBean.getReservationByReservationId(reservationId);
        if (reservation != null) {
            em.detach(reservation);
            reservation.setCustomer(null);
            reservation.setRoomType(null);

            for (RoomRate rate : reservation.getRoomRates()) {
                em.detach(rate);
                rate.setRoomType(null);
            }
        }

        return reservation;
    }

    @WebMethod(operationName = "getAllRoomTypes")
    public List<RoomType> getAllRoomTypes() {
        try {
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();

            for (RoomType type : types) {
                em.detach(type);

                for (RoomRate rate : type.getRates()) {
                    em.detach(rate);
                    rate.setRoomType(null);
                }

                for (Room room : type.getRooms()) {
                    em.detach(room);
                    room.setRoomType(null);
                }

            }

            return types;
        } catch (EmptyListException ex) {
            return null;
        }
    }

    @WebMethod(operationName = "getAllPartnerReservations")
    public List<Reservation> getAllPartnerReservations(@WebParam(name = "partnerId") Long partnerId) {
        try {
            List<Reservation> reservations = reservationSessionBean.getReservationsByPartnerId(partnerId);

            for (Reservation reservation : reservations) {
                em.detach(reservation);
                reservation.setCustomer(null);
                reservation.setRoomType(null);

                for (RoomRate rate : reservation.getRoomRates()) {
                    em.detach(rate);
                    rate.setRoomType(null);
                }

            }

            return reservations;
        } catch (EmptyListException ex) {
            return null;
        }
    }

    @WebMethod(operationName = "getRoomTypeFromReservationId")
    public RoomType getRoomTypeFromReservationId(@WebParam(name = "reservationId") Long id) {
        try {
            Reservation reservation = em.find(Reservation.class, id);
            RoomType type = reservation.getRoomType();

            em.detach(type);

            for (RoomRate rate : type.getRates()) {
                em.detach(rate);
                rate.setRoomType(null);
            }

            for (Room room : type.getRooms()) {
                em.detach(room);
                room.setRoomType(null);
            }

            return type;
        } catch (Exception ex) {
            return null;
        }
    }

    @WebMethod(operationName = "getPartnerReservation")
    public Reservation getPartnerReservation(@WebParam(name = "checkIn") String checkIn,
            @WebParam(name = "checkOut") String checkOut,
            @WebParam(name = "partnerId") Long partnerId) {

        DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
        LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
        LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);
        Reservation reservation = reservationSessionBean.getReservationsByDuration(checkInDate, checkOutDate, partnerId);
        if (reservation != null) {
            em.detach(reservation);
            reservation.setCustomer(null);
            reservation.setRoomType(null);

            for (RoomRate rate : reservation.getRoomRates()) {
                em.detach(rate);
                rate.setRoomType(null);
            }
        }

        return reservation;

    }

    @WebMethod(operationName = "isRoomTypeAvailableForReservation")
    public Boolean isRoomTypeAvailableForReservation(@WebParam(name = "roomTypeId") Long typeId,
            @WebParam(name = "checkIn") String checkIn,
            @WebParam(name = "checkOut") String checkOut,
            @WebParam(name = "numOfRooms") Integer numOfRooms) {

        DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
        LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
        LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);

        RoomType type = em.find(RoomType.class, typeId);
        type.getRooms().size();

        int count = 0;
        for (Room room : type.getRooms()) {
            if (room.getIsAvailable()) {

                count++;
            }
        }
        int countOfRoomsRequired = 0;

        try {
            List<Reservation> reservationsOfRoomType = reservationSessionBean.getReservationsByRoomTypeId(typeId);

            for (Reservation reservation : reservationsOfRoomType) {
                Date start = reservation.getStartDate();
                Date end = reservation.getEndDate();
                if (isCollided(start, end, checkInDate, checkOutDate)) {
                    System.out.println("Reservation ID: " + reservation.getReservationId() + " collides with this new reservation.");
                    countOfRoomsRequired += reservation.getNumOfRooms();
                    //countOfRoomsRequired++;
                }
            }
        } catch (EmptyListException e) {
            //no reservations, no problems;
            countOfRoomsRequired = 0;
        }

        return (count - countOfRoomsRequired - numOfRooms) >= 0;
    }

    private boolean isCollided(Date start, Date end, LocalDate startDate, LocalDate endDate) {
        LocalDate start1 = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end1 = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean lowerBound = start1.isEqual(startDate) || (start1.isAfter(startDate) && start1.isBefore(endDate));
        boolean upperBound = (end1.isBefore(endDate) && end1.isAfter(startDate)) || end1.isEqual(endDate);
        return lowerBound || upperBound;
    }

    @WebMethod(operationName = "getRoomRates")
    public List<RoomRate> getRoomRateUsed(@WebParam(name = "typeId") Long typeId) {
        try {
            List<RoomRate> rates = roomManagementSessionBean.getRoomRates(typeId);

            for (RoomRate r : rates) {
                r.setRoomType(null);
                em.detach(r);
            }

            return rates;
        } catch (EmptyListException ex) {
            return null;
        }
    }

    @WebMethod(operationName = "createNewReservationWithOneRateUsed")
    public Long createNewReservationWithOneRateUsed(@WebParam(name = "checkIn") String checkIn,
            @WebParam(name = "checkOut") String checkOut,
            @WebParam(name = "numOfRooms") Integer numOfRooms,
            @WebParam(name = "fee") Double fee,
            @WebParam(name = "guestId") Long guestId,
            @WebParam(name = "typeId") Long typeId,
            @WebParam(name = "rate1") Long rateId) {

        try {
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
            LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);

            Date startDate = Date.from(checkInDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(checkOutDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

            //CREATE RESERVATION OBJECT
            RoomType type = em.find(RoomType.class, typeId);
            Reservation reservation = new Reservation(startDate, endDate, numOfRooms, getTotalReservationFee(checkInDate, checkOutDate, type) * numOfRooms);

            Long reservationId = reservationSessionBean.createNewReservation(reservation, guestId, typeId, rateId);

            //ASSOCIATE RESERVATION TO PARTNER
            reservation = em.find(Reservation.class, reservationId);
            Partner partner = em.find(Partner.class, guestId);
            partner.getReservations().add(reservation);

            return reservationId;
        } catch (EmptyListException ex) {
            return null;
        }
    }

    @WebMethod(operationName = "createNewReservationWithTwoRatesUsed")
    public Long createNewReservationWithTwoRatesUsed(@WebParam(name = "checkIn") String checkIn,
            @WebParam(name = "checkOut") String checkOut,
            @WebParam(name = "numOfRooms") Integer numOfRooms,
            @WebParam(name = "fee") Double fee,
            @WebParam(name = "guestId") Long guestId,
            @WebParam(name = "typeId") Long typeId,
            @WebParam(name = "rate1") Long rateId,
            @WebParam(name = "rate2") Long rate2Id) {

        try {
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
            LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);

            Date startDate = Date.from(checkInDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(checkOutDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

            //CREATE RESERVATION OBJECT
            RoomType type = em.find(RoomType.class, typeId);
            Reservation reservation = new Reservation(startDate, endDate, numOfRooms, getTotalReservationFee(checkInDate, checkOutDate, type) * numOfRooms);

            List<RoomRate> ratesUsed = new ArrayList<>();
            ratesUsed.add(em.find(RoomRate.class, rateId));
            ratesUsed.add(em.find(RoomRate.class, rate2Id));
            Long reservationId = reservationSessionBean.createNewReservation(reservation, guestId, typeId, ratesUsed);

            //ASSOCIATE RESERVATION TO PARTNER
            reservation = em.find(Reservation.class, reservationId);
            Partner partner = em.find(Partner.class, guestId);
            partner.getReservations().add(reservation);

            return reservationId;
        } catch (EmptyListException ex) {
            return null;
        }
    }

    @WebMethod(operationName = "createNewReservationWithThreeRatesUsed")
    public Long createNewReservationWithThreeRatesUsed(@WebParam(name = "checkIn") String checkIn,
            @WebParam(name = "checkOut") String checkOut,
            @WebParam(name = "numOfRooms") Integer numOfRooms,
            @WebParam(name = "fee") Double fee,
            @WebParam(name = "guestId") Long guestId,
            @WebParam(name = "typeId") Long typeId,
            @WebParam(name = "rate1") Long rateId,
            @WebParam(name = "rate2") Long rate2Id,
            @WebParam(name = "rate3") Long rate3Id) {

        try {
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
            LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);

            Date startDate = Date.from(checkInDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(checkOutDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

            //CREATE RESERVATION OBJECT
            RoomType type = em.find(RoomType.class, typeId);
            Reservation reservation = new Reservation(startDate, endDate, numOfRooms, getTotalReservationFee(checkInDate, checkOutDate, type) * numOfRooms);

            List<RoomRate> ratesUsed = new ArrayList<>();
            ratesUsed.add(em.find(RoomRate.class, rateId));
            ratesUsed.add(em.find(RoomRate.class, rate2Id));
            ratesUsed.add(em.find(RoomRate.class, rate3Id));
            Long reservationId = reservationSessionBean.createNewReservation(reservation, guestId, typeId, ratesUsed);

            //ASSOCIATE RESERVATION TO PARTNER
            reservation = em.find(Reservation.class, reservationId);
            Partner partner = em.find(Partner.class, guestId);
            partner.getReservations().add(reservation);

            return reservationId;
        } catch (EmptyListException ex) {
            return null;
        }
    }

    @WebMethod(operationName = "createNewReservationWithFourRatesUsed")
    public Long createNewReservationWithFourRatesUsed(@WebParam(name = "checkIn") String checkIn,
            @WebParam(name = "checkOut") String checkOut,
            @WebParam(name = "numOfRooms") Integer numOfRooms,
            @WebParam(name = "fee") Double fee,
            @WebParam(name = "guestId") Long guestId,
            @WebParam(name = "typeId") Long typeId,
            @WebParam(name = "rate1") Long rateId,
            @WebParam(name = "rate2") Long rate2Id,
            @WebParam(name = "rate3") Long rate3Id,
            @WebParam(name = "rate4") Long rate4Id) {

        try {
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
            LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);

            Date startDate = Date.from(checkInDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(checkOutDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

            //CREATE RESERVATION OBJECT
            RoomType type = em.find(RoomType.class, typeId);
            Reservation reservation = new Reservation(startDate, endDate, numOfRooms, getTotalReservationFee(checkInDate, checkOutDate, type) * numOfRooms);

            List<RoomRate> ratesUsed = new ArrayList<>();
            ratesUsed.add(em.find(RoomRate.class, rateId));
            ratesUsed.add(em.find(RoomRate.class, rate2Id));
            ratesUsed.add(em.find(RoomRate.class, rate3Id));
            ratesUsed.add(em.find(RoomRate.class, rate4Id));
            Long reservationId = reservationSessionBean.createNewReservation(reservation, guestId, typeId, ratesUsed);

            //ASSOCIATE RESERVATION TO PARTNER
            reservation = em.find(Reservation.class, reservationId);
            Partner partner = em.find(Partner.class, guestId);
            partner.getReservations().add(reservation);

            return reservationId;
        } catch (EmptyListException ex) {
            return null;
        }
    }

    private double getTotalReservationFee(LocalDate checkInDate, LocalDate checkOutDate, RoomType selectedRoomType) throws EmptyListException {
        double totalReservation = 0;
        long numOfDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        List<RoomRate> rates = roomManagementSessionBean.getRoomRates(selectedRoomType.getRoomTypeId());

        for (int i = 0; i < numOfDays; i++) {
            //get the rate Per night for each night
            boolean foundRate = false;
            for (int j = rates.size() - 1; j >= 0; j--) {
                RoomRate rate = rates.get(j);
                if (((rate.getStartDate() == null) || isCurrentDateWithinRange(checkInDate, rate.getStartDate(), rate.getEndDate())) && !foundRate) {
                    totalReservation += rate.getRatePerNight();
                    checkInDate = checkInDate.plusDays(1);
                    foundRate = true;
                }
            }
        }
        return totalReservation;
    }

    private boolean isCurrentDateWithinRange(LocalDate currDate, Date startDate, Date endDate) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean lowerBound = currDate.isAfter(start) || currDate.isEqual(start);
        boolean upperBound = currDate.isBefore(end) || currDate.isEqual(end);
        return lowerBound && upperBound;
    }

}
