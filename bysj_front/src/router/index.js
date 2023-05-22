import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'login',
    component: () =>import('../views/login.vue')
  },
  {
    path: '/userInfo',
    name: 'userInfo',
    component: () =>import('../views/userInfo.vue')
  },
  {
    path: '/adminInfo',
    name: 'adminInfo',
    component: () =>import('../views/adminInfo.vue')
  },
  {
    path: '/index',
    name: 'index',
    component: () =>import('../views/index.vue')
  },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
