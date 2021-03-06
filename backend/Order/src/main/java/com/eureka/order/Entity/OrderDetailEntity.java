package com.eureka.order.Entity;

import com.eureka.order.dto.Order;
import com.eureka.order.util.OrderStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;

/**
 * order detail entity
 * @author refactored by shimjaehyuk, leesumin
 * @since 1.0
 */
@Entity
@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orderdetail")
public class OrderDetailEntity {

    @Id
    @Column(name = "order_id")
    private int orderId;
    @Column(name = "check_datetime")
    private String checkDatetime;
    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private String courier;
    @Column(name = "invoice_num")
    private String invoiceNum;
    @Column(name = "delivery_start_datatime")
    private String deliveryStartDatatime;
    @Column(name = "delivery_completion_datatime")
    private String deliveryCompletionDatatime;
    @Column(name = "cancel_msg")
    private String cancelMsg;

    @OneToOne(mappedBy = "orderDetail")
    @JsonBackReference
    private OrderEntity orderEntity;
}
