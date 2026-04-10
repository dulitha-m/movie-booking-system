package com.pgno98.moviebookingsystem11.repository;

import com.pgno98.moviebookingsystem11.entity.Payment;
import com.pgno98.moviebookingsystem11.entity.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
}
