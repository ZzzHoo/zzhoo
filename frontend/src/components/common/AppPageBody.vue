<!--
 全局页面主体容器组件，用于统一页面内容区域的布局和样式。
-->

<template>
    <div class="main-container">
        <div class="main-content">
            <slot></slot>
        </div>
    </div>
</template>

<script>
    export default {
        name: "PageBody"
    }
</script>

<style scoped>
    .main-container {
        margin-top: 67px;
        display: flex;
        justify-content: center;
        min-height: calc(100vh - 67px);
    }
    .main-content {
        width: 90%;
        max-width: 1000px;
        background-color: #ffffff;
        min-height: calc(100vh - 67px);
        display: flex;
        flex-direction: column;
    }
</style>