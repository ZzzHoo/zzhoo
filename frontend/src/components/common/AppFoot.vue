<!--
  AppFoot.vue
  作用：全局底部组件，显示在页面底部，通常用于展示版权、站点信息等。
-->

<template>
    <div class="foot-container">
        <!-- 底部作者/站点信息 -->
        <div class="author">北信科二手市场</div>
    </div>
</template>

<script>
    export default {
        name: "Foot" // 组件名称
    }
</script>

<style scoped>
    .foot-container {
        width: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 20px 0;
        margin-top: auto;
        background: transparent;
    }

    .author {
        color: #999999;
        font-size: 14px;
        font-weight: 500;
        letter-spacing: 1px;
    }
</style>