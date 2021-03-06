import { request } from "./index.js";

const orderDomin = "http://k4d104.p.ssafy.io:8084/order";

export const order = {
  // crud
  // order from seller

  // 프로덕트 아이디를 가지고 와서 최근 일주일에 관한 7개의 상품을 가져온다
  fetchProductDayOfWeek(productId) {
    return request(orderDomin, "get", `/product/${productId}/dayofweek`);
  },
  //판매자의 id로 로 해당판매자의 상품을 구매한 사람들의 리스트 반환
  fetchSellerid(sellerid, page, size) {
    return request(orderDomin, "get", `/sellerid/${sellerid}/${page}/${size}`);
  },
  // getOrdersBySellerIdBystatus
  fetchSelleridStatus(sellerid, statusnum) {
    return request(orderDomin, "get", `/sellerid/${sellerid}/status/${statusnum}`);
  },
  //getOrdersBySellerIdWithUnchecked
  fetchSelleridUnchecked(sellerid) {
    return request(orderDomin, "get", `/sellerid/${sellerid}/unchecked`);
  },
  //getOrdersBySellerIdWithUnchecked
  fetchSelleridUnchecked(sellerid) {
    return request(orderDomin, "get", `/sellerid/${sellerid}/unchecked`);
  },
  //getOrdersBySellerIdByday
  fetchSellerMonth(sellerid, month) {
    return request(orderDomin, "get", `/sellerid/${sellerid}/${month}`);
  },

  // order from user

  //getOrdersByUserIdBystatus
  fetchUserStatus(userid, statusnum) {
    return request(orderDomin, "get", `/userid/${userid}/status/${statusnum}`);
  },

  //getOrders
  fetchUserAll(userId) {
    return request(orderDomin, "get", `/userid/${userId}/all`);
  },

  //getOrdersByUserId
  fetchUserOrders(userId, page, size) {
    return request(orderDomin, "get", `/userid/${userId}/${page}/${size}`);
  },

  //todayHot
  fetchRecommendTodayHot() {
    return request(orderDomin, "get", `/recommend/todayhot`);
  },

  //steadySeller
  fetchRecommendSteadySeller() {
    return request(orderDomin, "get", `/recommend/steadyseller`);
  },

  //hotProduct
  fetchRecommendHotProduct() {
    return request(orderDomin, "get", `/recommend/hotproduct`);
  },

  //updateOrders
  updateOrder(orderDetailEntity) {
    return request(orderDomin, "put", ``, { orderDetailEntity });
  },

  //AddOrders
  createOrder(orderDetailEntity) {
    return request(orderDomin, "post", ``, { orderDetailEntity });
  },

  //넘겨받은 배송정보와 각 장바구니별 데이터로 주문 저장하고, 장바구니 데이터 갱신까지 한번에!
  saveOrder(orders, shoppings) {
    return request(orderDomin, "post", `/saveorder`, { orders, shoppings });
  },

  //해당 구매자의 모든 주문 정보를 상품, 사진과 함께 반환
  fetchOrderList(userId) {
    return request(orderDomin, "get", `/userid/orders/${userId}`);
  },
};
