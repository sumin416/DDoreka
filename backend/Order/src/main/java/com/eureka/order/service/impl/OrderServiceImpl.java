package com.eureka.order.service.impl;

import com.eureka.order.Entity.OrderDetailEntity;
import com.eureka.order.Entity.OrderEntity;
import com.eureka.order.Repository.OrderDetailRepositoty;
import com.eureka.order.Repository.OrderRepositoty;
import com.eureka.order.dto.*;
import com.eureka.order.service.OrderService;
import com.eureka.order.service.RestTemplateService;
import com.eureka.order.util.OrderStatus;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * order serviceimpl
 * @author refactored by shimjaehyuk, leesumin
 * @since 1.0
 */
@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    /**
     * OrderRepository Bean created by Spring Boot constructor injection
     * OrderDetailRepository Bean created by Spring Boot constructor injection
     */
    @Autowired
    private OrderRepositoty orderRepositoty;
    @Autowired
    private OrderDetailRepositoty orderDetailRepositoty;
    private RestTemplateService<?> restTemplateProduct;

    /**
     * get orders by user id
     *
     * @param userId
     * @return list of order entity
     */
    @Override
    public List<OrderEntity> getOrdersByUserId(String userId) {
        return orderRepositoty.findAllByUserId(userId);
    }

    private List<ProductOptionInfoDTO> getOptionNamePrice(List<Integer> productIds, List<Integer> optionIds){
        Gson gson = new Gson();

        String reqProducts = gson.toJson(productIds);
        String reqOptions = gson.toJson(optionIds);

        reqProducts = reqProducts.substring(1, reqProducts.length()-1);
        reqOptions = reqOptions.substring(1, reqOptions.length()-1);

        String getProductURL = "http://k4d104.p.ssafy.io:8081/product/name";

        HttpHeaders productHttpHeaders = new HttpHeaders();
        productHttpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        productHttpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        productHttpHeaders.set("product-ids", reqProducts);
        productHttpHeaders.set("option-ids", reqOptions);

        ResponseEntity<?> responseEntityProduct = restTemplateProduct.get(getProductURL, productHttpHeaders);

        System.out.println("rest ?????????!");
        return (List<ProductOptionInfoDTO>)responseEntityProduct.getBody();
    }

    @Override
    public List<UserOrderListDTO> getAllOrdersByUserId(String userId) {

        //????????? result
        List<UserOrderListDTO> result = new ArrayList<>();

        //userId??? ?????? ?????? ????????????
        List<OrderEntity> orderEntities = orderRepositoty.findAllByUserId(userId);

        //?????? ????????? ????????? null ??????
        if(orderEntities.isEmpty() || orderEntities == null){
            System.out.println("?????? ?????? ???????????? ???????????? ????????? ?????????!");
            return null;
        }

        //?????? ?????? ????????? productId??? optionId??? list??? ??????
        List<Integer> productIds = new ArrayList<>();
        List<Integer> optionIds = new ArrayList<>();

        for (int i = 0; i < orderEntities.size(); i++) {
            productIds.add(Integer.parseInt(orderEntities.get(i).getProductId()));
            optionIds.add(Integer.parseInt(orderEntities.get(i).getOptionId()));
        }

        //product ???????????? product??? option??? ?????? ????????? ????????????.
        List<ProductOptionInfoDTO> responseProducts = getOptionNamePrice(productIds,optionIds);

        if( responseProducts == null || responseProducts.isEmpty() ){
            System.out.println("product ?????? ??????????????? ????????? ??? ?????? ??????..!");
            return null;
        }

        Map<Integer,ProductOptionInfoDTO> optionMaps = new HashMap<>();

        //Map
        int getFileId;
        for (int i = 0 ; i < responseProducts.size() ; ++i){
            Map<String,Object> optionMap = (Map<String,Object>)responseProducts.get(i);

            ProductOptionInfoDTO productOptionInfoDTO = ProductOptionInfoDTO.builder()
                    .productId((Integer)optionMap.get("productId"))
                    .productName((String)optionMap.get("productName"))
                    .optionId((Integer)optionMap.get("optionId"))
                    .optionName((String)optionMap.get("optionName"))
                    .price((Integer)optionMap.get("price"))
                    .thumbnail((String)optionMap.get("thumbnail"))
                    .build();

//            System.out.println(productOptionInfoDTO.toString());
            optionMaps.put((Integer)optionMap.get("optionId"),productOptionInfoDTO);
        }


        for (int i = 0; i < orderEntities.size(); i++) {

            int optionId = Integer.parseInt(orderEntities.get(i).getOptionId());
            ProductOptionInfoDTO productOptionInfoDTO = optionMaps.get(optionId);

            int productId = productOptionInfoDTO.getProductId();

            //productId??? ????????? product ?????? ?????? ???????????? ????????? ??????
            String productName = productOptionInfoDTO.getProductName();
            //optionId??? ????????? product ?????? ?????? ????????? ???????????????!
            String optionName = productOptionInfoDTO.getOptionName();
            int price = productOptionInfoDTO.getPrice() * Integer.parseInt(orderEntities.get(i).getQuantity());

            //productId??? ????????? ????????? ??????????????? ?????? ????????????
            String thumbnail = productOptionInfoDTO.getThumbnail();

            UserOrderListDTO userOrderListDTO  = UserOrderListDTO.builder()
                    .orderId(orderEntities.get(i).getId())
                    .userId(orderEntities.get(i).getUserId())
                    .optionId(optionId)
                    .quantity(orderEntities.get(i).getQuantity())
                    .datetime(orderEntities.get(i).getDatetime())
                    .productId(productId)
                    .productName(productName)
                    .optionId(optionId)
                    .optionName(optionName)
                    .price(price)
                    .status(orderEntities.get(i).getOrderDetail().getOrderStatus().name())
                    .thumbnail(thumbnail)
                    .build();
            result.add(userOrderListDTO);
        }

        return result;
    }

    /**
     * update order
     *
     * @param orderDetailEntity
     */
    @Override
    public void updateOrder(OrderDetailEntity orderDetailEntity) {
        orderDetailRepositoty.save(orderDetailEntity);
    }

    /**
     * get list of order count by product id sorted by day
     *
     * @param productId
     * @return list of count
     */
    @Override
    public List<Integer> getOrdersByProductIdSortedByDay(String productId) {
        List<Integer> ls = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            int cnt = 0;
            for (OrderEntity orderEntity : orderRepositoty.findAllByDayOfWeek(Integer.toString(i), productId)) {
                cnt += Integer.parseInt(orderEntity.getQuantity().trim());
            }
            ls.add(cnt);
        }
        return ls;
    }

    /**
     * get orders by seller id in last month
     *
     * @param sellerId
     * @param month
     * @return list of list of order
     */
    @Override
    public List<List<OrderEntity>> getOrdersBySellerIdByDay(String sellerId, String month) {
        List<List<OrderEntity>> ls = new ArrayList<>();
        for (int i = 1; i < 31; i++) {
            ls.add(orderRepositoty.findAllBySelleridofday(Integer.toString(i), sellerId, month));
        }
        return ls;
    }

    /**
     * get list of order by seller id, status
     *
     * @param sellerId
     * @param status
     * @return list of order
     */
    @Override
    public List<OrderEntity> getOrdersBySellerIdByStatus(String sellerId, Integer status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        System.out.println(orderStatus.toString());
        List<OrderDetailEntity> orderDetailEntityList = orderDetailRepositoty.findAllByOrderStatus(orderStatus);
        System.out.println("?????????:"+orderDetailEntityList.size());
        List<OrderEntity> orderEntityList = new ArrayList<>();
        for (OrderDetailEntity o : orderDetailEntityList) {
            System.out.println(o.toString());
            if(o == null || o.getOrderEntity() == null || o.getOrderEntity().getSellerId() == null)   continue;
            if (o.getOrderEntity().getSellerId().equals(sellerId)) orderEntityList.add(o.getOrderEntity());
        }
        return orderEntityList;
    }

    /**
     * get orders by user id, status
     *
     * @param userId
     * @param status
     * @return list of order entity
     */
    @Override
    public List<OrderEntity> getOrdersByUserIdByStatus(String userId, Integer status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        List<OrderDetailEntity> orderDetailEntityList = orderDetailRepositoty.findAllByOrderStatus(orderStatus);
        List<OrderEntity> orderEntityList = new ArrayList<>();
        for (OrderDetailEntity o : orderDetailEntityList) {

            if (o.getOrderEntity().getUserId().equals(userId)) orderEntityList.add(o.getOrderEntity());
        }
        return orderEntityList;
    }

    /**
     * get orders by user id
     *
     * @param userId
     * @param page
     * @param size
     * @return page of order
     */
    @Override
    public Page<OrderEntity> getOrdersByUserId(String userId, Integer page, Integer size) {
        return orderRepositoty.findAllByUserId(userId, PageRequest.of(page, size, Sort.by("id").descending()));
    }

    /**
     * @param sellerId
     * @return list of order
     * @deprecated NOT USED
     * get orders by seller id
     */
    @Override
    public List<OrderEntity> getOrdersBySellerId(String sellerId) {
        return orderRepositoty.findAllBySellerId(sellerId);
    }

    /**
     * get orders by seller id
     *
     * @param sellerId
     * @param page
     * @param size
     * @return page of order
     */
    @Override
    public Page<OrderEntity> getOrdersBySellerId(String sellerId, Integer page, Integer size) {
        return orderRepositoty.findAllBySellerId(sellerId, PageRequest.of(page, size));
    }

    /**
     * get orders by seller id with unchecked
     *
     * @param sellerId
     * @return List<OrderEntity>
     */
    @Override
    public List<OrderEntity> getOrdersBySellerIdWithUnchecked(String sellerId) {
        List<OrderDetailEntity> orderDetailEntityList = orderDetailRepositoty.findAllByCheckDatetime(null);
        List<OrderEntity> orderEntityList = new ArrayList<>();
        for (OrderDetailEntity o : orderDetailEntityList) {
            if(o == null || o.getOrderEntity() == null) continue;
            orderEntityList.add(o.getOrderEntity());
        }
        return orderEntityList;
    }

    /**
     * save order
     *
     * @param orderEntity
     */
    @Override
    @Transactional
    public void saveOrder(OrderEntity orderEntity) {
        try {
            OrderEntity savedOrderEntity = orderRepositoty.save(orderEntity);
//            OrderDetailEntity detail= new OrderDetailEntity();
//            detail.setOrderId(savedOrderEntity.getId());
//            detail.setOrderStatus(OrderStatus.PAY);
            orderDetailRepositoty.save(
                    OrderDetailEntity.builder()
                            .orderId(savedOrderEntity.getId())
                            .orderStatus(OrderStatus.PAY)
                            .build()
            );
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * get steady seller
     *
     * @return List<Integer>
     */
    @Override
    public List<Integer> getSteadySeller() {
        return orderRepositoty.findSteadySeller();
    }

    /**
     * get hot product
     *
     * @return List<Integer>
     */
    @Override
    public List<Integer> getHotProduct() {
        return orderRepositoty.findHotProduct();
    }

    /**
     * get today hot
     *
     * @return List<Map < String, Object>>
     */
    @Override
    public List<Map<String, Object>> getTodayHot() {
        return orderRepositoty.findTodayHot();
    }

    /**
     * get today hot products
     *
     * @return List<String>
     */
    @Override
    public List<String> getTodayHotProducts() {
        return orderRepositoty.todayHotProducts();
    }

    /**
     * get weekHotProducts
     *
     * @return List<String>
     */
    @Override
    public List<String> getWeekHotProducts() {
        return orderRepositoty.weekHotProducts();
    }

    /**
     * get monthHotProducts
     *
     * @return List<String>
     */
    @Override
    public List<String> getMonthHotProducts() {
        return orderRepositoty.monthHotProducts();
    }


    private List<OptionPriceDTO> getOptionPrice(String reqProduct){
        String getProductURL = "http://localhost:8081/product/prices";

        HttpHeaders productHttpHeaders = new HttpHeaders();
        productHttpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        productHttpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        productHttpHeaders.set("option-ids", reqProduct);
        System.out.println(reqProduct);


        ResponseEntity<?> responseEntityFile = restTemplateProduct.get(getProductURL, productHttpHeaders);

        System.out.println("rest ?????????!");
        return (List<OptionPriceDTO>)responseEntityFile.getBody();
    }

    private void saveOrderAndDetail(OrderDTO orders, List<ShoppingDTO> shoppings){

    }

    @Override
    public void saveOrderAll(OrderDTO orders, List<ShoppingDTO> shoppings) {

        String reqProduct = getOptionIds(shoppings);

        if(reqProduct == null){
            return;
        }

        //Product ???????????? option??? ???????????? ?????? ?????? ????????????
        List<OptionPriceDTO> optionPriceDTOS = getOptionPrice(reqProduct);

        if(optionPriceDTOS.isEmpty() || optionPriceDTOS == null){
            System.out.println("shopping??? ???????????? option ????????? productOption ???????????? ??????!");
            return;
        }

        //???????????? option - price ???????????? order ??????
        List<OrderEntity> orderEntities = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = new Date();

        String curTime = format.format(time);
        System.out.println("???????????? " + curTime);
        for (int i = 0; i < shoppings.size(); ++i) {

            String optionId = shoppings.get(i).getOptionId();
            int price = 0;

            for (int j = 0; j < optionPriceDTOS.size(); j++) {
                Map<String,Object> optionPriceMap = (Map<String, Object>)optionPriceDTOS.get(j);

                if(optionPriceMap.get("optionId").equals(optionId)){
                    price = Integer.parseInt(shoppings.get(i).getQuantity()) * (Integer)optionPriceMap.get("price");
                    break;
                }
            }


            OrderEntity orderEntity = OrderEntity.builder()
                    .userId(orders.getUserId())
                    .productId(shoppings.get(i).getProductId())
                    .optionId(shoppings.get(i).getOptionId())
                    .sellerId(shoppings.get(i).getSellerId())
                    .addressMain(orders.getAddressMain())
                    .addressSub(orders.getAddressSub())
                    .recipientName(orders.getRecipientName())
                    .zipcode(orders.getZipcode())
                    .deliveryMsg(orders.getDeliveryMsg())
                    .recipientPhone(orders.getRecipientPhone())
                    .quantity(shoppings.get(i).getQuantity())
                    .price(Integer.toString(price))
//                    .datetime(curTime)
                    .paymentMethod(orders.getPaymentMethod())
                    .build();

            System.out.println("????????? ?????????!!");
            orderEntities.add(
                    orderEntity
            );
        }
        System.out.println("orderEntities ??????????????? ???????????? " + orderEntities.size());

        List<OrderEntity> saveOrderEntities = orderRepositoty.saveAll(orderEntities);

        System.out.println("??????????????? ????????? " + saveOrderEntities.size());

        List<OrderDetailEntity> orderDetailEntities = new ArrayList<>();

        for (int i = 0; i < saveOrderEntities.size(); ++i) {
            System.out.println(i + "??????");
            OrderDetailEntity orderDetailEntity = OrderDetailEntity.builder()
                    .orderId(saveOrderEntities.get(i).getId())

                    .orderStatus(OrderStatus.PAY)
                    .build();

            orderDetailEntities.add(
                    orderDetailEntity
            );
        }

        System.out.println("??????????????? ???????????? ?????? " + orderDetailEntities.size());

        List<OrderDetailEntity> orderDetailEntities1 = orderDetailRepositoty.saveAll(orderDetailEntities);

        System.out.println("??????????????? ????????????!");
        //???????????? ??????
        List<Integer> cartIds = new ArrayList<>();

        for (int i = 0; i < shoppings.size(); ++i) {
            cartIds.add(shoppings.get(i).getCartId());
        }

        Gson gson = new Gson();

        String cartIdStr = gson.toJson(cartIds);

        String deleteCartURL = "http://localhost:8085/user/cart/deletes";

        HttpHeaders cartHttpHeaders = new HttpHeaders();
        cartHttpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        cartHttpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        cartHttpHeaders.set("cartIdsParam", cartIdStr);


        restTemplateProduct.delete(deleteCartURL, cartHttpHeaders);

        System.out.println("?????? ??????!");


    }

    private String getOptionIds(List<ShoppingDTO> shoppings){

        if (shoppings == null || shoppings.isEmpty()) {
            System.out.println("shoppings ????????? ??????!!");
            return null;
        }

        List<Integer> optionIds = new ArrayList<>();
        for (int i = 0; i < shoppings.size(); ++i) {
            optionIds.add(Integer.parseInt(shoppings.get(i).getOptionId()));
        }

        Gson gson = new Gson();

        return gson.toJson(optionIds);
    }

    /*
    @Override
    public void saveOrderAll( List<ShoppingDTO> shoppings) {

        //List<String>???  ?????? ????????? map<"option-id",List<option_id,price = OptionPriceDTO> :

        if(shoppings.isEmpty() || shoppings == null){
            System.out.println("shoppings ????????? ??????!!");
            return;
        }

//        List<String> optionIds = new ArrayList<>();
//        for (int i = 0 ; i < shoppings.size() ; ++i){
//            optionIds.add(shoppings.get(i).getOptionId());
//        }

        List<Integer> optionIds = new ArrayList<>();
        for (int i = 0 ; i < shoppings.size() ; ++i){
            optionIds.add(Integer.parseInt(shoppings.get(i).getOptionId()));
        }

        Gson gson = new Gson();
        String reqProduct = gson.toJson(optionIds);

        //set??? list??? ???????????? ??????!!!
//        Set<String> optionIdSet = new HashSet<>(); // ?????????
//        shoppings.forEach((shopping)-> optionIdSet.add(shopping.getOptionId()));
//        List<String> optionIds = new ArrayList<>(optionIdSet);
//        ???????????? ????????? ??????
//        Iterator<String> iter = optionIdSet.iterator(); // set??? Iterator ?????? ??????
//        while(iter.hasNext()) { // iterator??? ?????? ?????? ?????????
//            optionIds.add(iter.next());
//        }

        String getProductURL = "http://k4d104.p.ssafy.io:8081/product/prices";

        HttpHeaders productHttpHeaders = new HttpHeaders();
        productHttpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        productHttpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        reqProduct = reqProduct.toString().substring(1, reqProduct.toString().length()-1);
        System.out.println(reqProduct);

        productHttpHeaders.set("option-ids",reqProduct);

        ResponseEntity<?> responseEntityFile = restTemplateProduct.get(getProductURL, productHttpHeaders);

        System.out.println("rest ?????????!");
        List<OptionPriceDTO> optionPriceDTOS = (List<OptionPriceDTO>)responseEntityFile.getBody();

        if(optionPriceDTOS.size() == 0 || optionPriceDTOS == null){
            System.out.println("shopping??? ???????????? option ????????? productOption ???????????? ??????!");
            return;
        }

        System.out.println("????????? ?????? ????????? ??????????????? ??? ?????????!"+optionPriceDTOS.size());



    }
     */
}