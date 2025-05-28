<!--
  userList.vue
  作用：用户管理页面组件，支持普通用户、违规用户、管理员的切换与管理。
-->

<template>
    <div class="main-border">
        <!-- 顶部菜单栏，切换不同用户类型 -->
        <el-menu default-active="1" class="el-menu-demo" mode="horizontal" @select="handleSelect">
          <!-- 搜索框，仅在非管理员模式下显示 -->
          <span v-show="this.mode != 3" class="app-title">
						<el-input placeholder="用户账号..." v-model="searchValue" @keyup.enter.native="searchIdle">
							<el-button slot="append" icon="el-icon-search" @click="searchIdle"></el-button>
						</el-input>
					</span>
            <el-menu-item index="1">正常用户</el-menu-item>
            <el-menu-item index="2">违规用户</el-menu-item>
            <el-menu-item index="3">管理员</el-menu-item>
            <!-- 管理员模式下显示添加管理员按钮 -->
            <div v-show="this.mode ==3" class="addAdminButton">
                <el-button size="mini" type="success" @click="adminRegVisible = true">添加管理员</el-button>
                <!-- 添加管理员弹窗 -->
                <el-dialog
                        title="添加管理员"
                        :visible.sync="adminRegVisible"
                        width="25%"
                       >
                    <span style="margin-left: 10px">新增管理员名称</span>
                    <el-input v-model="adminName"  maxlength="8" placeholder="请输入管理员名称" style="padding: 10px 0" clearable required></el-input>
                    <span style="margin-left: 10px">新增管理员账户</span>
                    <el-input v-model="adminAccount" minlength="8" maxlength="10" placeholder="请输入管理员账户" style="padding: 10px 0"
                               clearable required></el-input>
                    <span style="margin-left: 10px">新增管理员密码</span>
                    <el-input v-model="adminPassword" minlength="8" placeholder="请输入管理员密码" style="padding: 10px 0" show-password required></el-input>
                    <span style="margin-left: 10px">确认管理员密码</span>
                    <el-input v-model="adminRePassword" minlength="10" placeholder="请再次输入管理员密码" style="padding: 10px 0" show-password required></el-input>
                    <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="regAdmin">添加</el-button>
                    </span>
                </el-dialog>
            </div>
        </el-menu>
        <!-- 正常用户表格 -->
        <el-table v-show="this.mode == 1"
                  :data="userData"
                  stripe
                  style="width: 100%;color: #5a5c61;">
            <el-table-column label="头像" width="50">
                <template slot-scope="scope">
                    <el-avatar shape="square" :size="23" :src="scope.row.avatar"></el-avatar>
                </template>
            </el-table-column>
            <el-table-column
                    prop="accountNumber"
                    label="用户账号"
                    show-overflow-tooltip
                    min-width="150"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="nickname"
                    label="用户昵称"
                    show-overflow-tooltip
                    min-width="150"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="signInTime"
                    label="注册时间"
                    show-overflow-tooltip
                    width="200">
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <!-- 封号操作按钮 -->
                    <el-button
                            size="mini"
                            type="danger"
                            @click="sealUser(scope.$index)">封号</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 违规用户表格 -->
        <el-table v-show="this.mode == 2"
                  :data="badUserData"
                  stripe
                  style="width: 100%;color: #5a5c61;">
            <el-table-column
                    label="头像"
                    width="50">
                <template slot-scope="scope">
                    <el-avatar shape="square" :size="23" :src="scope.row.avatar"></el-avatar>
                </template>
            </el-table-column>
            <el-table-column
                    prop="accountNumber"
                    label="用户账号"
                    show-overflow-tooltip
                    min-width="150"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="nickname"
                    label="用户昵称"
                    show-overflow-tooltip
                    width="150"
            >
            </el-table-column>
            <el-table-column
                    prop="signInTime"
                    label="注册时间"
                    show-overflow-tooltip
                    width="200">
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <!-- 解封操作按钮 -->
                    <el-button
                            size="mini"
                            type="success"
                            @click="unsealUser(scope.$index)">解封</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 管理员表格 -->
        <el-table  v-show="this.mode == 3"
                :data="userManage"
                stripe
                style="width: 100%;color: #5a5c61;">
            <el-table-column
                    prop="accountNumber"
                    label="管理员账号"
                    show-overflow-tooltip
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="adminName"
                    label="管理名称"
                    >
            </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <div class="block">
            <el-pagination
                    @current-change="handleCurrentChange"
                    :current-page.sync="nowPage"
                    :page-size="7"
                    background
                    layout="prev, pager, next,jumper"
                    :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "userList",
        created() {
            // 组件创建时默认加载用户数据
            this.getUserData();
        },
        methods: {
            // 分页切换
            handleCurrentChange(val) {
                this.nowPage = val;
                if(this.mode == 1){
                    this.getUserData();
                }
                if(this.mode == 2){
                    this.getBadUserData();
                }
                if(this.mode == 3){
                    this.getUserManage();
                }
            },
            // 菜单切换
            handleSelect(val){
                if(this.mode !== val){
                    this.mode = val
                    if(val == 1){
                        this.nowPage = 1;
                        this.getUserData();
                    }
                    if(val == 2){
                        this.nowPage = 1;
                        this.getBadUserData();
                    }
                    if(val == 3){
                        this.nowPage = 1;
                        this.getUserManage();
                    }
                }
            },
            // 获取正常用户数据
            getUserData(){
                //正常普通用户
                this.$api.getUserData({
                    page: this.nowPage,
                    nums:8,
                    status:0
                }).then(res => {
                    if(res.status_code==1){
                        this.userData = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            // 获取违规用户数据
            getBadUserData(){
                //违规用户
                this.$api.getUserData({
                    page: this.nowPage,
                    nums:8,
                    status:1
                }).then(res => {
                    if(res.status_code==1){
                        this.badUserData = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            // 获取管理员数据
            getUserManage(){
                this.$api.getUserManage({
                    page: this.nowPage,
                    nums:8
                }).then(res => {
                    if(res.status_code==1){
                        this.userManage = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            // 搜索用户
            searchIdle(){
                if(this.searchValue == ""){
                    this.$message.warning("请输入用户账号");
                    return;
                }
                this.$api.searchUser({
                    accountNumber: this.searchValue
                }).then(res => {
                    if(res.status_code==1){
                        this.userData = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            // 封号操作
            sealUser(index){
                let user = this.userData[index];
                this.$api.sealUser({
                    id: user.id
                }).then(res => {
                    if(res.status_code==1){
                        this.$message.success("封号成功");
                        this.getUserData();
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            // 解封操作
            unsealUser(index){
                let user = this.badUserData[index];
                this.$api.unsealUser({
                    id: user.id
                }).then(res => {
                    if(res.status_code==1){
                        this.$message.success("解封成功");
                        this.getBadUserData();
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            // 添加管理员
            regAdmin(){
                if(this.adminPassword !== this.adminRePassword){
                    this.$message.error("两次输入密码不一致");
                    return;
                }
                this.$api.regAdmin({
                    adminName: this.adminName,
                    adminAccount: this.adminAccount,
                    adminPassword: this.adminPassword
                }).then(res => {
                    if(res.status_code==1){
                        this.$message.success("添加成功");
                        this.adminRegVisible = false;
                        this.getUserManage();
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            }
        },
        data() {
            return {
                mode: 1, // 当前显示的用户类型，1-正常用户，2-违规用户，3-管理员
                nowPage: 1, // 当前页码
                total: 0, // 总数据条数
                userData: [], // 正常用户数据
                badUserData: [], // 违规用户数据
                userManage: [], // 管理员数据
                searchValue: "", // 搜索框内容
                adminRegVisible: false, // 添加管理员弹窗显示状态
                adminName: "", // 新管理员名称
                adminAccount: "", // 新管理员账号
                adminPassword: "", // 新管理员密码
                adminRePassword: "" // 新管理员确认密码
            }
        }
    }
</script>

<style scoped>
    .main-border{
        background-color: #FFF;
        padding: 10px 30px;
        box-shadow: 0 1px 15px -6px rgba(0,0,0,.5);
        border-radius: 5px;
    }
    .block {
        display: flex;
        justify-content:center;
        padding-top: 15px;
        padding-bottom: 10px;
        width: 100%;
    }
    .addAdminButton{
        display:flex;
        justify-content: flex-end;
        align-items: center;
        height: 60px;
        outline: none;
    }
</style>

