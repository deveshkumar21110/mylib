package com.example.mylib.controllers.book;

import com.example.mylib.services.Reservation.ReservationService;
import com.example.mylib.services.User.UserService;
import com.example.mylib.services.borrow.BorrowService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final BorrowService borrowService;
    private final ReservationService reservationService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllReservations() {
        try {
            return ResponseEntity.ok(reservationService.getAllReservations());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to fetch all reservations.");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReservations(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(reservationService.getUsersReservations(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Not able to find reservations.");
        }
    }

    @PutMapping("/user/{reservationId}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long reservationId) {
        try {
            reservationService.cancelReservation(reservationId);
            return ResponseEntity.ok("Reservation CANCELED");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Reservation not deleted.");
        }
    }

    @PostMapping("/user/{userId}/{bookId}")
    public ResponseEntity<?> reserveBook(@PathVariable Long userId, @PathVariable Long bookId) {
        try {
            if(!borrowService.isEligibleToBorrow(userId, bookId)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("You have already borrowed this book"); 
            }
            reservationService.createReservation(userId, bookId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body("Reservation request created.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Can't able to create Reservation request: " + e.getMessage());
        }
    }
}
