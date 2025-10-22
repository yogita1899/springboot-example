package com.integrationninjas.springbootexample.service;

import com.integrationninjas.springbootexample.model.Orders;
import com.integrationninjas.springbootexample.exception.OrderNotFoundException;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {

    private final Map<Long, Orders> orderDb = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    // No preloaded data
    public OrderService() {}

    // ---------------------- GET ALL ----------------------
    @Trace(dispatcher = true)
    public List<Orders> getAllOrders() {
        simulateLatency();
        List<Orders> orders = new ArrayList<>(orderDb.values());
        NewRelic.recordMetric("Custom/Orders/TotalCount", orders.size());
        return orders;
    }

    // ---------------------- GET BY ID ----------------------
    @Trace(dispatcher = true)
    public Orders getOrderById(Long id) {
        simulateLatency();
        Orders order = orderDb.get(id);
        if (order == null) {
            NewRelic.noticeError("Order not found: " + id);
            throw new OrderNotFoundException("Order not found with ID: " + id);
        }
        NewRelic.addCustomParameter("OrderID", id);
        NewRelic.addCustomParameter("CustomerName", order.getCustomerName());
        NewRelic.addCustomParameter("Status", order.getStatus());
        return order;
    }

    // ---------------------- CREATE ----------------------
    @Trace(dispatcher = true)
    public Orders createOrder(Orders order) {
        simulateLatency();
        long id = idGenerator.incrementAndGet();
        order.setId(id);
        orderDb.put(id, order);

        // Record useful metrics
        NewRelic.recordMetric("Custom/Orders/CreatedCount", 1);
        NewRelic.addCustomParameter("OrderID", id);
        NewRelic.addCustomParameter("CustomerName", order.getCustomerName());
        NewRelic.addCustomParameter("Amount", order.getPrice() * order.getQuantity());

        return order;
    }

    // ---------------------- UPDATE ----------------------
    @Trace(dispatcher = true)
    public Orders updateOrder(Long id, Orders updatedOrders) {
        simulateLatency();
        Orders existing = orderDb.get(id);
        if (existing == null) {
            NewRelic.noticeError("Attempt to update non-existent order ID: " + id);
            throw new OrderNotFoundException("Order not found with ID: " + id);
        }

        existing.setStatus(updatedOrders.getStatus());
        existing.setQuantity(updatedOrders.getQuantity());
        existing.setPrice(updatedOrders.getPrice());

        // Record status updates
        NewRelic.recordMetric("Custom/Orders/UpdatedCount", 1);
        NewRelic.addCustomParameter("OrderID", id);
        NewRelic.addCustomParameter("NewStatus", existing.getStatus());
        NewRelic.addCustomParameter("UpdatedValue", existing.getPrice() * existing.getQuantity());

        return existing;
    }

    // ---------------------- DELETE ----------------------
    @Trace(dispatcher = true)
    public void deleteOrder(Long id) {
        simulateLatency();
        if (orderDb.remove(id) == null) {
            NewRelic.noticeError("Attempt to delete non-existent order ID: " + id);
            throw new OrderNotFoundException("Order not found with ID: " + id);
        }
        NewRelic.recordMetric("Custom/Orders/DeletedCount", 1);
        NewRelic.addCustomParameter("DeletedOrderID", id);
    }

    // ---------------------- LATENCY SIMULATION ----------------------
    private void simulateLatency() {
        try {
            Thread.sleep(300 + new Random().nextInt(700)); // 300-1000 ms delay
            for (int i = 0; i < 50000; i++) {
                Math.log(i + 1); // Simulate CPU work
            }
        } catch (InterruptedException ignored) {}
    }
}
