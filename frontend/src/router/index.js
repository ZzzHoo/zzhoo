import Vue from 'vue';
import Router from 'vue-router';

// 解决 vue-router 在3.0以上版本重复点击导航报错的问题
const originalReplace = Router.prototype.replace;
Router.prototype.replace = function replace(location) {
    return originalReplace.call(this, location).catch(err => err);
};
const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
};

// 安装路由插件
Vue.use(Router);

// 导出路由实例，配置所有页面路由
export default new Router({
    routes: [
        {
            path: '/',
            redirect: '/index' // 根路径重定向到首页
        },
        {
            path: '/index',
            // 首页，采用懒加载方式引入组件
            component: () => import('../components/page/index.vue'),
            meta: { title: '校园二手闲置物品交易平台' }
        },
        {
            path: '/search',
            // 搜索结果页
            component: () => import('../components/page/search.vue'),
            meta: { title: '闲置搜索 | 校园二手闲置物品交易平台' }
        },
        {
            path: '/me',
            // 个人中心
            component: () => import('../components/page/me.vue'),
            meta: { title: '个人中心 | 北信科二手市场' }
        },
        {
            path: '/message',
            // 消息中心
            component: () => import('../components/page/message.vue'),
            meta: { title: '消息 | 北信科二手市场' }
        },
        {
            path: '/release',
            // 发布闲置
            component: () => import('../components/page/release.vue'),
            meta: { title: '发布闲置 | 北信科二手市场' }
        },
        {
            path: '/details',
            // 闲置商品详情页
            component: () => import('../components/page/idle-details.vue'),
            meta: { title: '闲置详情 | 北信科二手市场' }
        },
        {
            path: '/order',
            // 订单详情页
            component: () => import('../components/page/order.vue'),
            meta: { title: '订单详情 | 北信科二手市场' }
        },
        {
            path: '/cart',
            // 购物车页面
            component: () => import('../components/page/cart.vue'),
            meta: { title: '我的购物车 | 北信科二手市场' }
        },
        {
            path: '/login',
            // 用户登录页
            component: () => import('../components/page/login.vue'),
            meta: { title: '登录 | 北信科二手市场' }
        },
        {
            path: '/sign-in',
            // 用户注册页
            component: () => import('../components/page/sign-in.vue'),
            meta: { title: '注册 | 北信科二手市场' }
        },
        {
            path: '/login-admin',
            // 管理员登录页
            component: () => import('../components/page/login-admin.vue'),
            meta: { title: '管理员登陆' }
        },
        {
            path: '/platform-admin',
            // 后台管理主页面
            component: () => import('../components/page/platform-admin.vue'),
            meta: { title: '后台管理' }
        },
        {
            path: '*',
            // 兜底路由，未匹配到的路径重定向到首页
            redirect: '/'
        }
    ]
});
