package com.integrationninjas.springbootexample.controller;

import com.integrationninjas.springbootexample.model.Orders;
import com.integrationninjas.springbootexample.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Orders> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Orders getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public Orders createOrder(@RequestBody Orders orders) {
        return orderService.createOrder(orders);
    }

    @PutMapping("/{id}")
    public Orders updateOrder(@PathVariable Long id, @RequestBody Orders orders) {
        return orderService.updateOrder(id, orders);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

}
