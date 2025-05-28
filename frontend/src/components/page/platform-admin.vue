/*
 * 平台管理后台组件 (platform-admin.vue)
 * 功能：平台管理员后台界面
 * 主要功能：
 *   - 提供管理功能导航菜单
 *   - 展示不同管理模块(闲置/订单/用户)
 *   - 管理员信息显示和退出登录
 * 
 * 组件结构：
 *   - 顶部导航栏
 *   - 左侧菜单(el-menu)
 *   - 右侧内容区域
 *   - 底部信息
 */
<template>
	<div class="admin-bg">
		<el-container>
			<el-header class="admin-header">
				<div class="admin-header-left">
					<router-link to="/" class="admin-logo">北信科二手市场</router-link>
				</div>
				<div class="admin-header-center">后台管理</div>
				<div class="admin-header-right">
					<span class="admin-name">管理员：{{admin.nickname}}</span>
					<el-button class="admin-logout-btn" type="primary" @click="logout">退出登录</el-button>
				</div>
			</el-header>
			<el-container>
				<el-aside width="210px" class="admin-aside">
					<el-menu
						default-active="1"
						class="admin-menu"
						@select="handleSelect"
						background-color="#fff"
						text-color="#222"
						active-text-color="#26c6da">
						<el-menu-item index="1"><i class="el-icon-goods"></i> 闲置管理</el-menu-item>
						<el-menu-item index="2"><i class="el-icon-s-goods"></i> 订单管理</el-menu-item>
						<el-menu-item index="3"><i class="el-icon-s-custom"></i> 用户管理</el-menu-item>
					</el-menu>
				</el-aside>
				<el-main class="admin-main">
					<div class="admin-card">
						<IdleGoods v-if="mode == 1"/>
						<orderList v-if="mode == 2"/>
						<userList v-if="mode == 3"/>
					</div>
				</el-main>
			</el-container>
		</el-container>
		<div class="admin-foot">
			<app-foot></app-foot>
		</div>
	</div>
</template>

<script>
    import AppFoot from '../common/AppFoot.vue'
    import IdleGoods from '../common/IdleGoods.vue'
    import orderList from '../common/orderList.vue'
    import userList from '../common/userList.vue'

    export default {
        name: "platform-admin",
        components: {
            AppFoot,
            IdleGoods,
            orderList,
            userList,
        },
        data() {
            return {
                mode: 1,
                admin: {
                    nickname: '管理员',
                },
            }
        },
        created() {
            this.admin.nickname = this.$sta.adminName;
        },
        methods: {
            logout() {
                this.$api.loginOut({}).then(res => {
                    if (res.status_code === 1) {
                        this.$sta.isLogin = false;
                        this.$sta.adminName = '';
                        this.$router.push({path: '/login-admin'});
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            handleSelect(val) {
                if (this.mode !== val) {
                    this.mode = val
                }
            },
            searchIdle(){
                if (this.mode === 1) {
                    this.$api.adminQueryOrder({
                        page: page,
                        nums: 8,
												searchValue: this.searchValue
                    }).then(res => {
                        console.log(res);
                    }).catch(e => {
                        console.log(e)
                    })
                } else if(this.mode === 2){

                } else {

								}
						}
        },
    }
</script>

<style scoped>
	.admin-bg {
		min-height: 100vh;
		background: linear-gradient(120deg, #e0f7fa 0%, #e3f0ff 100%);
		display: flex;
		flex-direction: column;
	}
	.admin-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		height: 64px;
		background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
		color: #fff;
		box-shadow: 0 2px 12px rgba(60,120,200,0.10);
		border-radius: 0 0 18px 18px;
		padding: 0 32px;
	}
	.admin-header-left .admin-logo {
		font-size: 22px;
		font-weight: 800;
		color: #fff;
		text-decoration: none;
		letter-spacing: 2px;
	}
	.admin-header-center {
		font-size: 20px;
		font-weight: 600;
		letter-spacing: 1px;
	}
	.admin-header-right {
		display: flex;
		align-items: center;
		gap: 18px;
	}
	.admin-name {
		font-size: 16px;
		font-weight: 500;
	}
	.admin-logout-btn {
		border-radius: 16px;
		font-weight: 600;
		background: #fff;
		color: #26c6da;
		border: none;
		transition: background 0.2s, color 0.2s;
	}
	.admin-logout-btn:hover {
		background: #26c6da;
		color: #fff;
	}
	.admin-aside {
		background: transparent;
		padding: 32px 0 0 0;
	}
	.admin-menu {
		border-radius: 18px;
		box-shadow: 0 4px 24px rgba(60,120,200,0.10);
		overflow: hidden;
	}
	.admin-main {
		padding: 48px 32px 32px 32px;
		background: transparent;
		display: flex;
		flex-direction: column;
		min-height: calc(100vh - 64px - 32px);
	}
	.admin-card {
		background: #fff;
		border-radius: 18px;
		box-shadow: 0 6px 32px rgba(60,120,200,0.10);
		padding: 32px 24px;
		min-height: 600px;
		flex: 1;
	}
	.admin-foot {
		padding: 20px 0;
		margin-top: auto;
	}
</style>