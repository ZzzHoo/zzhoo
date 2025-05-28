// bus.js
// 作用：全局事件总线，用于实现 Vue 组件之间的跨层级通信（发布/订阅模式）

import Vue from 'vue';

// 创建一个全局事件总线实例
const bus = new Vue();

export default bus; // 导出事件总线，供其他组件使用