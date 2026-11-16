package org.example.krutishe_za_vsih.service.jpa;

import org.example.krutishe_za_vsih.dto.CreateOrderRequest;
import org.example.krutishe_za_vsih.dto.OrderDto;
import org.example.krutishe_za_vsih.dto.ReassignResult;
import org.example.krutishe_za_vsih.entity.OrderStatus;

import java.time.LocalDate;
import java.util.List;

public interface JpaOrderService {

    List<OrderDto> findAll(Long animatorId);

    OrderDto findById(long id);

    List<OrderDto> findByStatusAndPeriod(OrderStatus status, LocalDate from, LocalDate to);

    long countByStatus(OrderStatus status);

    OrderDto create(CreateOrderRequest request);

    OrderDto updateStatus(long id, OrderStatus status);

    void delete(long id);

    /** Транзакційна операція над кількома записами: або все, або нічого. */
    ReassignResult reassignOrders(long fromAnimatorId, long toAnimatorId);
}