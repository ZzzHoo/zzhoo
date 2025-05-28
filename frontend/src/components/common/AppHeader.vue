/*
 * 顶部导航组件 (AppHeader.vue)
 * 功能：提供网站主要导航功能
 * 主要功能：
 *   - 显示网站logo和名称
 *   - 提供全局搜索功能
 *   - 主要功能入口(发布、消息、购物车)
 *   - 用户头像和下拉菜单
 * 
 * 组件结构：
 *   - 网站logo和名称
 *   - 搜索框(el-input)
 *   - 功能按钮(el-button)
 *   - 用户头像和下拉菜单(el-dropdown)
 */
<template>
    <div class="header">
        <div class="header-container minimal-header">
            <div class="app-name minimal-app-name">
                <router-link to="/">北信科二手市场</router-link>
            </div>
            <div class="search-container minimal-search">
                <el-input placeholder="搜闲置..." v-model="searchValue" @keyup.enter.native="searchIdle" class="minimal-search-input">
                    <el-button slot="append" icon="el-icon-search" @click="searchIdle" class="minimal-search-btn"></el-button>
                </el-input>
            </div>
            <el-button class="minimal-header-btn" icon="el-icon-plus"  @click="toRelease">发布闲置/求购</el-button>
            <el-button class="minimal-header-btn" icon="el-icon-chat-dot-round" @click="toMessage">消息</el-button>
            <el-button class="minimal-header-btn" icon="el-icon-shopping-cart-full" @click="toCart">购物车</el-button>
            <router-link v-if="!isLogin" class="user-name-text minimal-login-link" to="/login">登录</router-link>
            <el-dropdown trigger="click" v-else>
                <div class="minimal-avatar-wrap" style="cursor:pointer;display: flex;align-items: center;">
                    <div class="minimal-nickname">{{nicknameValue?nicknameValue:nickname}}</div>
                    <el-avatar :src="avatarValue?avatarValue:avatar" class="minimal-avatar"></el-avatar>
                </div>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item><div @click="toMe">个人中心</div></el-dropdown-item>
                    <el-dropdown-item divided style="color: red;"><div @click="loginOut">退出登录</div></el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</template>
<script>

    export default {
        name: 'Header',
        props: ['searchInput','nicknameValue','avatarValue'],
        data() {
            return {
                searchValue: this.searchInput,
                nickname:'登录',
                avatar:'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
                isLogin:false
            };
        },
        created(){
            // console.log("header");
            if(! this.$globalData.userInfo.nickname){
                this.$api.getUserInfo().then(res=>{
                    console.log('Header getUserInfo:',res);
                    if(res.status_code===1){
                        this.nickname=res.data.nickname;
                        this.avatar=res.data.avatar;
                        res.data.signInTime=res.data.signInTime.substring(0,10);
                        this.$globalData.userInfo=res.data;
                        this.isLogin=true;
                    }
                })
            }else {
                this.nickname=this.$globalData.userInfo.nickname;
                this.avatar=this.$globalData.userInfo.avatar;
                this.isLogin=true;
            }
        },
        methods: {
            searchIdle() {
                if ('/search' !== this.$route.path) {
                    this.$router.push({path: '/search', query: {searchValue: this.searchValue}});
                } else {
                    this.$router.replace({path: '/search', query: {searchValue: this.searchValue}});
                    this.$router.go(0);
                }

            },
            toMe() {
                if ('/me' !== this.$route.path) {
                    this.$router.push({path: '/me'});
                }
            },
            toMessage(){
                if ('/message' !== this.$route.path) {
                    this.$router.push({path: '/message'});
                }
            },
            toRelease(){
                if ('/release' !== this.$route.path) {
                    this.$router.push({path: '/release'});
                }
            },
            toCart() {
                if ('/cart' !== this.$route.path) {
                    this.$router.push({path: '/cart'});
                }
            },
            loginOut(){
                this.$api.logout().then(res=>{
                    if(res.status_code===1){
                        this.$globalData.userInfo={};
                        console.log("login out");
                        if ('/index' === this.$route.path) {
                            this.$router.go(0);
                        }else {
                            this.$router.push({path: '/index'});
                        }
                    }else {
                        this.$message.error('网络或系统异常，退出登录失败！');
                    }
                });

            }
        }
    };
</script>
<style scoped>
    .header {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        width: 100%;
        height: 68px;
        background: #fff;
        display: flex;
        justify-content: center;
        border-bottom: 1.5px solid #e0f0ff;
        z-index: 1000;
        box-shadow: 0 2px 16px rgba(60,180,220,0.04);
        box-sizing: border-box;
    }
    .minimal-header {
        width: 90%;
        max-width: 1380px;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 32px;
        box-sizing: border-box;
        padding: 0 16px;
    }
    .minimal-app-name a {
        font-size: 30px;
        font-weight: 800;
        background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        text-fill-color: transparent;
        letter-spacing: 2px;
        text-decoration: none;
        transition: opacity 0.18s;
        opacity: 0.96;
    }
    .minimal-app-name a:hover {
        opacity: 1;
    }
    .minimal-search {
        width: 340px;
        margin: 0 18px;
    }
    .minimal-search-input >>> .el-input__inner {
        border-radius: 22px 0 0 22px;
        box-shadow: 0 2px 8px rgba(60,180,220,0.08);
        font-size: 16px;
        padding-left: 18px;
        height: 40px;
    }
    .minimal-search-btn {
        border-radius: 0 22px 22px 0 !important;
        background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%) !important;
        color: #fff !important;
        font-weight: 700;
        border: none !important;
        height: 40px;
        transition: box-shadow 0.18s, background 0.18s;
    }
    .minimal-search-btn:hover {
        box-shadow: 0 4px 16px rgba(60,180,220,0.13);
        background: linear-gradient(90deg, #26c6da 0%, #42a5f5 100%) !important;
    }
    .minimal-header-btn {
        border-radius: 18px !important;
        background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%) !important;
        color: #fff !important;
        font-weight: 700;
        font-size: 16px;
        border: none !important;
        margin: 0 8px;
        padding: 0 26px;
        height: 40px;
        transition: box-shadow 0.18s, background 0.18s, transform 0.16s;
    }
    .minimal-header-btn:hover {
        box-shadow: 0 4px 16px rgba(60,180,220,0.13);
        background: linear-gradient(90deg, #26c6da 0%, #42a5f5 100%) !important;
        transform: translateY(-2px) scale(1.04);
    }
    .minimal-login-link {
        font-size: 16px;
        font-weight: 700;
        color: #26c6da;
        border-radius: 16px;
        padding: 6px 22px;
        background: #e0f7fa;
        margin-left: 10px;
        transition: background 0.18s, color 0.18s;
    }
    .minimal-login-link:hover {
        background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
        color: #fff;
    }
    .minimal-avatar-wrap {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 2px 10px 2px 2px;
        border-radius: 22px;
        background: #f4f8fb;
        box-shadow: 0 2px 8px rgba(60,180,220,0.08);
        margin-left: 10px;
    }
    .minimal-nickname {
        font-size: 16px;
        color: #26c6da;
        font-weight: 700;
        max-width: 90px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
    .minimal-avatar {
        width: 38px !important;
        height: 38px !important;
        border-radius: 50%;
        box-shadow: 0 2px 8px rgba(60,180,220,0.13);
        border: 2px solid #e0f7fa;
    }
    @media (max-width: 1100px) {
        .minimal-header {
            width: 95%;
            gap: 10px;
            padding: 0 8px;
        }
        .minimal-search {
            width: 180px;
        }
        .minimal-app-name a {
            font-size: 20px;
        }
        .minimal-header-btn {
            padding: 0 16px;
        }
    }
</style>
