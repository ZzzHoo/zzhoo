<template>
    <div class="message-bg">
        <app-head></app-head>
        <app-body>
            <div class="message-main">
                <div class="message-title">我的消息</div>
                <div v-if="meslist.length === 0" class="message-empty">
                    <i class="el-icon-message"></i>
                    <span>暂无消息</span>
                </div>
                <div v-for="(mes,index) in meslist" :key="mes.id" class="message-card" @click="toDetails(mes.idle.id)">
                    <div class="message-card-left">
                        <el-image :src="mes.fromU.avatar" fit="cover" class="message-avatar"></el-image>
                        <div class="message-info">
                            <div class="message-nickname">{{mes.fromU.nickname}}</div>
                            <div class="message-content">{{mes.content}}</div>
                            <div class="message-time">{{mes.createTime}}</div>
                        </div>
                    </div>
                    <div class="message-card-right">
                        <el-image :src="mes.idle.imgUrl" fit="cover" class="message-goods-img"></el-image>
                    </div>
                </div>
            </div>
            <app-foot></app-foot>
        </app-body>
    </div>
</template>

<script>
    import AppHead from '../common/AppHeader.vue';
    import AppBody from '../common/AppPageBody.vue'
    import AppFoot from '../common/AppFoot.vue'

    /*
     * 消息页面（message.vue）
     * 作用：展示用户收到的所有消息，包括系统消息、交易消息等。
     * 主要功能：
     *   - 展示消息列表（带头像、内容、时间、关联商品）
     *   - 支持点击消息跳转到对应商品详情页
     *   - 页面风格与首页、个人中心一致，极简蓝绿渐变风格
     *
     * 主要逻辑说明：
     *   - created() 生命周期钩子自动加载消息列表
     *   - getAllMyMessage() 从后端获取消息数据，处理图片和内容格式
     *   - toDetails() 方法跳转商品详情
     *
     * 组件结构：
     *   - 顶部导航（AppHead）
     *   - 消息卡片列表（message-card）
     *   - 底部（AppFoot）
     */

    export default {
        name: "message",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data(){
            return{
                meslist:[]
            };
        },
        created(){
            this.$api.getAllMyMessage().then(res=>{
                console.log(res);
                if(res.status_code===1){
                    for(let i=0;i<res.data.length;i++){
                        let imgList=JSON.parse(res.data[i].idle.pictureList);
                        res.data[i].idle.imgUrl=imgList?imgList[0]:'';
                        let contentList=res.data[i].content.split('<br>');
                        let contenHtml=contentList[0];
                        for(let i=1;i<contentList.length;i++){
                            contenHtml+='  '+contentList[i];
                        }
                        res.data[i].content=contenHtml;
                    }
                    this.meslist=res.data;
                }
            })
        },
        methods:{
            toDetails(id){
                this.$router.push({path: '/details',query:{id:id}});
            }
        }
    }
</script>

<style scoped>
    .message-bg {
        min-height: 100vh;
        background: linear-gradient(120deg, #e0f7fa 0%, #e3f0ff 100%);
    }
    .message-main {
        max-width: 900px;
        margin: 48px auto 0 auto;
        padding: 32px 0 0 0;
    }
    .message-title {
        font-size: 22px;
        font-weight: 700;
        color: #1a237e;
        margin-bottom: 32px;
        letter-spacing: 1px;
    }
    .message-empty {
        display: flex;
        flex-direction: column;
        align-items: center;
        color: #b0bec5;
        font-size: 18px;
        margin-top: 80px;
    }
    .message-empty i {
        font-size: 48px;
        margin-bottom: 16px;
    }
    .message-card {
        background: #fff;
        border-radius: 22px;
        box-shadow: 0 6px 32px rgba(60,120,200,0.10);
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 24px 32px;
        margin-bottom: 28px;
        cursor: pointer;
        transition: box-shadow 0.22s, transform 0.18s;
    }
    .message-card:hover {
        box-shadow: 0 14px 40px rgba(60,120,200,0.18);
        transform: translateY(-4px) scale(1.02);
    }
    .message-card-left {
        display: flex;
        align-items: center;
        flex: 1;
        min-width: 0;
    }
    .message-avatar {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        margin-right: 18px;
        border: 3px solid #e3f0ff;
        box-shadow: 0 2px 8px rgba(38,198,218,0.10);
    }
    .message-info {
        display: flex;
        flex-direction: column;
        gap: 6px;
        min-width: 0;
    }
    .message-nickname {
        font-weight: 700;
        font-size: 18px;
        color: #26c6da;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        max-width: 220px;
    }
    .message-content {
        font-size: 16px;
        color: #222;
        max-width: 420px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }
    .message-time {
        font-size: 13px;
        color: #78909c;
    }
    .message-card-right {
        margin-left: 32px;
    }
    .message-goods-img {
        width: 120px;
        height: 80px;
        border-radius: 12px;
        object-fit: cover;
        box-shadow: 0 2px 8px rgba(60,120,200,0.10);
        background: #e3f0ff;
    }
</style>