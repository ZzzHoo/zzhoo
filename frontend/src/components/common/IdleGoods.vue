/*
 * 闲置商品管理组件 (IdleGoods.vue)
 * 功能：展示和管理平台上的闲置商品
 * 主要功能：
 *   - 分页展示上架/下架商品
 *   - 支持按商品名称搜索
 *   - 支持违规下架操作
 *   - 支持永久删除商品
 * 
 * 组件结构：
 *   - 顶部搜索栏和分类标签
 *   - 商品数据表格(el-table)
 *   - 分页组件(el-pagination)
 */
<template>
	<div class="main-border">
		<el-menu default-active="1" class="el-menu-demo" mode="horizontal" @select="handleSelect">
        <span class="app-title">
						<el-input placeholder="闲置名称..." v-model="findValue" @keyup.enter.native="searchIdle">
							<el-button slot="append" icon="el-icon-search" @click="searchIdle"></el-button>
						</el-input>
				</span>
			<el-menu-item @click="status=1" index="1">上线的闲置</el-menu-item>
			<el-menu-item @click="status =2" index="2">下架的闲置</el-menu-item>
		</el-menu>
		<el-table v-if="this.mode == 1"
							:data="onlineGoods"
							stripe
							style="width: 100%;color: #5a5c61;">
			<el-table-column
				prop="releaseTime"
				label="发布日期"
				width="200">
			</el-table-column>
			<el-table-column
				prop="idleName"
				label="闲置名称"
				show-overflow-tooltip
			>
			</el-table-column>
			<el-table-column
				prop="user.nickname"
				label="发布用户"
				show-overflow-tooltip
				min-width="100"
				width="100">
			</el-table-column>
			<el-table-column
				prop="idlePrice"
				label="价格"
				show-overflow-tooltip
				min-width="100"
				width="100">
			</el-table-column>
			<el-table-column label="操作">
				<template slot-scope="scope">
					<el-button
						size="mini"
						type="danger"
						@click="makeOfflineGoods(scope.$index)">违规下架
					</el-button>
				</template>
			</el-table-column>
		</el-table>

		<el-table v-show="this.mode == 2"
							:data="OfflineGoods"
							stripe
							style="width: 100%;color: #5a5c61;">
			<el-table-column
				prop="releaseTime"
				label="发布日期"
				width="200">
			</el-table-column>
			<el-table-column
				prop="idleName"
				label="闲置名称"
				show-overflow-tooltip
			>
			</el-table-column>
			<el-table-column
				prop="user.nickname"
				label="发布用户"
				show-overflow-tooltip
				min-width="100"
				width="100">
			</el-table-column>
			<el-table-column
				prop="idlePrice"
				label="价格"
				show-overflow-tooltip
				min-width="100"
				width="100">
			</el-table-column>
			<el-table-column label="操作">
				<template slot-scope="scope">
					<el-button
						size="mini"
						type="danger"
						@click="deleteGoods(scope.$index)">永久删除
					</el-button>
				</template>
			</el-table-column>
		</el-table>
		<div class="block">
			<el-pagination
				@current-change="handleCurrentChange"
				:current-page.sync="nowPage"
				:page-size="8"
				background
				layout="prev, pager, next,jumper"
				:total="total">
			</el-pagination>
		</div>
	</div>
</template>

<script>
    export default {
        name: "IdleGoods",
        data() {
            return {
                mode: 1,
                nowPage: 1,
                total: 0,
                onlineGoods: [],
                OfflineGoods: [],
                findValue: '',
								status: 1
            }
        },
        created() {
            this.getOnlineGoods();
        },
        methods: {
            searchIdle(){
                this.$api.queryIdle({
                    findValue: this.findValue,
                    page: this.nowPage,
                    nums: 8,
										status: this.status
                }).then(res => {
                    console.log(res);
                    if (res.status_code == 1 && res.data.list != null) {
                            if(res.data.list[0].idleStatus == 1){
                                this.onlineGoods = res.data.list;
                                this.total = res.data.count;
														}else {
                                this.OfflineGoods = res.data.list;
                                this.total = res.data.count;
														}
                    } else{
                        this.$message.error(res.msg)
										}
                }).catch(e => {
                    console.log(e)
                })
            },
            handleCurrentChange(val) {
                this.nowPage = val;
                if (this.mode == 1) {
                    this.getOnlineGoods();
                }
                if (this.mode == 2) {
                    this.getOfflineGoods();
                }
            },
            handleSelect(val) {
                if (this.mode !== val) {
                    this.mode = val;
                    if (val == 1) {
                        this.nowPage = 1;
                        this.getOnlineGoods();
                    }
                    if (val == 2) {
                        this.nowPage = 1;
                        this.getOfflineGoods();
                    }
                }
            },
            makeOfflineGoods(i) {
                this.$api.updateGoods({
                    id: this.onlineGoods[i].id,
                    status: 2
                }).then(res => {
                    if (res.status_code == 1) {
                        this.getOnlineGoods();
                    } else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            deleteGoods(i) {
                this.$api.updateGoods({
                    id: this.OfflineGoods[i].id,
                    status: 0
                }).then(res => {
                    if (res.status_code == 1) {
                        this.getOfflineGoods();
                    } else {
                        this.$message.error(res.msg)
                    }

                }).catch(e => {
                    console.log(e)
                })
            },
            getOnlineGoods() {
                this.$api.getGoods({
                    status: 1,
                    page: this.nowPage,
                    nums: 8
                }).then(res => {
                    if (res.status_code == 1) {
                        this.onlineGoods = res.data.list;
                        this.total = res.data.count;
                    } else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            getOfflineGoods() {
                this.$api.getGoods({
                    status: 2,
                    page: this.nowPage,
                    nums: 8
                }).then(res => {
                    if (res.status_code == 1) {
                        this.OfflineGoods = res.data.list;
                        this.total = res.data.count;
                    } else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            }
        }

    }
</script>

<style scoped>
	.main-border {
		background-color: #FFF;
		padding: 24px 36px;
		box-shadow: 0 6px 32px rgba(60,120,200,0.10);
		border-radius: 18px;
		margin: 32px 0;
	}

	.block {
		display: flex;
		justify-content: center;
		padding-top: 15px;
		padding-bottom: 10px;
		width: 100%;
	}

	.el-table {
		border-radius: 14px;
		box-shadow: 0 2px 12px rgba(60,120,200,0.10);
		overflow: hidden;
	}

	.el-input {
		border-radius: 12px;
		background: #f4f8fb;
	}

	.el-button {
		border-radius: 14px;
		background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
		color: #fff;
		font-weight: 600;
		border: none;
		transition: background 0.2s, color 0.2s;
	}

	.el-button:hover {
		background: linear-gradient(90deg, #26c6da 0%, #42a5f5 100%);
		color: #fff;
	}

	.order-list-bg {
		min-height: 100vh;
		background: linear-gradient(120deg, #e0f7fa 0%, #e3f0ff 100%);
		padding: 0;
	}
	.order-list-main {
		max-width: 1200px;
		margin: 48px auto 0 auto;
		padding: 32px 0 0 0;
	}
	.order-list-header {
		display: flex;
		justify-content: flex-end;
		margin-bottom: 24px;
	}
	.order-search-input {
		width: 320px;
	}
	.order-table-card {
		border-radius: 22px;
		box-shadow: 0 6px 32px rgba(60,120,200,0.10);
		padding: 0 0 18px 0;
		margin-bottom: 32px;
		background: #fff;
	}
	.order-table-minimal {
		border-radius: 18px;
		overflow: hidden;
	}
	.order-status-tag {
		font-size: 14px;
		font-weight: 600;
		padding: 2px 10px;
		border-radius: 12px;
		display: inline-block;
	}
	.status-0 { background: #ffecb3; color: #ff9800; }
	.status-1 { background: #e1f5fe; color: #03a9f4; }
	.status-2 { background: #e8f5e9; color: #4caf50; }
	.status-3 { background: #e0f2f1; color: #009688; }
	.status-4 { background: #f5f5f5; color: #9e9e9e; }
	.order-pagination {
		display: flex;
		justify-content: center;
		margin-top: 32px;
	}
</style>