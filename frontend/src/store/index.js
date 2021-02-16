import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    login: false,
    userId: null,
    wishlist: [],
    name: null,
    currentItem: {},
    email: '',
    phone: '',
    seller: {},
    sellerstore: {},
    searchData: [],
  },
  mutations: {
    logout(state) {
      state.login = false;
      state.userId = null;
      state.wishlist = [];
      state.name = null;
      state.email = null;
      state.phone = null;

    },
    login(state, data) {
      state.login = true;
      state.userId = data.id
      state.name = data.name;
      state.email = data.email;
      state.phone = data.phone
    },
    selectItem(state, item) {
      state.currentItem = item
    },
    setwishlist(state, data) {
      state.wishlist = data
    },
    deletewishlist(state, idx) {
      state.wishlist.splice(idx, 1)
    },
    setsellerinfo(state, data) {
      state.seller = data
    },
    setsellerstore(state, data) {
      state.sellerstore = data
    },
    searchData(state, data) {
      state.searchData = data
    },
    paywishlist(state, data) {
      for (let i=0; i<state.wishlist.length; i++) {
        if (state.wishlist[i].id == data) {
          state.wishlist.splice(i, 1);
          break;
        }
      }
    },
  },
  actions: {
    LOGOUT({ commit }) {
      commit("logout");
      localStorage.removeItem("eureka-authorization")
    },
    LOGIN({ commit }, data) {
      commit("login", data);

    },
    SELECTITEM({ commit }, item) {
      commit("selectItem", item)
    },
    SETWISHLIST({ commit }, data) {
      commit("setwishlist", data)
    },
    DELETEWISHLIST({ commit }, idx) {
      commit("deletewishlist", idx)
    },
    SETSELLERINFO({ commit }, data) {
      commit("setsellerinfo", data)
    },
    SETSELLERSTORE({ commit }, data) {
      commit("setsellerstore", data)
    },
    SEARCHDATA({ commit }, data) {
      commit("searchData", data)
    },
    PAYWISHLIST({ commit }, data) {
      commit("paywishlist", data)
    },
  },
  modules: {
  },
  plugins: [
    createPersistedState()
  ]
})
