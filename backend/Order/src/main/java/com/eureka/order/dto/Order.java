package com.eureka.order.dto;

import com.eureka.order.Entity.OrderDetailEntity;
import lombok.Data;

import javax.persistence.*;

/**
 * Order Dto
 * @author refactored by shimjaehyuk, leesumin
 * @since 1.0
 */
@Data
public class Order {
    private String id;

    private String userId;
    private String productId;
    private String optionId;
    private String sellerId;
    private String addressMain;
    private String addressSub;
    private String recipientName;
    private String zipCode;
    private String deliveryMsg;
    private String recipientPhone;
    private String quantity;
    private String price;
    private String datetime;
    private String paymentMethod;

    private OrderDetailEntity orderDetailEntity;



}
