import {request} from './index.js';

const productDomin = "http://k4d104.p.ssafy.io/:8081/product/";
const categoryDomin = "http://k4d104.p.ssafy.io/:8081/category/";

export const product = { // crud
    //상품 등록
    create(product, option, image) {
    return request(productDomin ,"post", "add", { product, option, image });
    },
    //전체 상품조회
    fetch() {
        return request(productDomin ,"get", "all");
    },
    //상품 상세조회
    find(productId) {
        return request(productDomin, 'get', `detail/${productId}`);
    },
    //카테고리별 상품 조회
    fetchFindByCategory(categoryId, depth) {
        return request(productDomin, 'get', `findByCategory/${categoryId}/${depth}`);
    },
    //최근 등록된 상품 조회
    fetchRecommendLatestproduct() {
        return request(productDomin, 'get', `recommend/latestproduct`);
    },
    //추천 상품 조회
    fetchRecommendProductids() {
        return request(productDomin, 'get', `recommend/productids`);
    },
    //실시간 검색어 조회
    fetchRecommendRealtimesearchword() {
        return request(productDomin, 'get', `recommend/realtimesearchword`);
    },
    //검색 상품 조회
    fetchSearch(category1Id,keyword ) {
        return request(productDomin, 'get', `search/,${category1Id}/${keyword}`);
    },
    //판매자 상품 조회
    fetchSellerAll(storeId) {
        return request(productDomin, 'get', `seller/all${storeId}`);
    },



};
// 전체는 fetch , 
export const category = {
    //카테고리 등록
    create(product, option, image) {
      return request(categoryDomin ,"post", "add", { product, option, image });
    },
    //메인 카테고리 조회
    fetchMainCategory() {
        return request(categoryDomin, 'get', `mainCategory`);
    },
    //하위 카테고리 조회
    fetchSubCategory(categoryId) {
        return request(categoryDomin, 'get', `subCategory${categoryId}`);
    },

  };
  
