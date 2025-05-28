<template>
    <div id="app">
        <!-- 路由出口，所有页面组件都会在这里切换显示 -->
        <router-view></router-view>
    </div>
</template>

<style>
    /* 让页面始终显示纵向滚动条，避免内容变化时页面抖动 */
    html {
        overflow-y: scroll;
        width: 100vw;
        padding-right: 0; /* 防止滚动条出现时的抖动 */
    }
    
    /* 去除默认边距，设置全局背景色，禁止横向滚动 */
    html, body {
        margin: 0;
        padding: 0;
        background-color: #f6f6f6;
        overflow-x: hidden;
    }
    
    /* 根容器样式，保证全局背景和最小高度 */
    #app {
        background-color: #f6f6f6;
        width: 100%;
        min-height: 100vh;
        position: relative;
        overflow-x: hidden;
    }
    
    /* 自定义滚动条样式 */
    ::-webkit-scrollbar {
        width: 8px !important;
        height: 8px !important;
        background-color: transparent;
    }
    
    ::-webkit-scrollbar-thumb {
        background: rgba(38, 198, 218, 0.2);
        border-radius: 4px;
    }
    
    ::-webkit-scrollbar-thumb:hover {
        background: rgba(38, 198, 218, 0.4);
    }
    
    /* 为滚动条预留空间，防止内容被遮挡 */
    body {
        padding-right: 8px; /* 与滚动条宽度相同 */
        box-sizing: border-box;
    }

    /* 页面主背景容器的通用样式 */
    .page-minimal-bg {
        box-sizing: border-box;
        width: 100%;
        overflow-x: hidden;
    }
</style>