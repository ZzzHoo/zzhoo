<template>
    <div class="sign-in-container">
        <el-card class="box-card">
            <div class="sign-in-body">
                <div class="sign-in-title">注册</div>
                <el-input placeholder="请输入昵称..." maxlength="30"  v-model="userInfo.nickname" class="sign-in-input" clearable>
                    <template slot="prepend">
                        <div class="el-icon-user-solid"></div>
                    </template>
                </el-input>
                <el-input placeholder="请输入账号..." maxlength="30" v-model="userInfo.accountNumber" class="sign-in-input" clearable>
                    <template slot="prepend">
                        <div class="el-icon-user"></div>
                    </template>
                </el-input>
                <el-input placeholder="请输入手机号..." maxlength="11" v-model="userInfo.phone" class="sign-in-input" clearable>
                    <template slot="prepend">
                        <div class="el-icon-phone"></div>
                    </template>
                </el-input>
                <el-input placeholder="请输入密码..." show-password maxlength="16" v-model="userInfo.userPassword" class="sign-in-input" clearable>
                    <template slot="prepend">
                        <div class="el-icon-lock"></div>
                    </template>
                </el-input>
                <el-input placeholder="请再次输入密码..." show-password maxlength="16" v-model="userPassword2" @keyup.enter.native="signIn" class="sign-in-input" clearable>
                    <template slot="prepend">
                        <div class="el-icon-lock"></div>
                    </template>
                </el-input>
                <el-input placeholder="请输入邮箱..." maxlength="64" v-model="userInfo.email" class="sign-in-input" clearable>
                    <template slot="prepend">
                        <div class="el-icon-message"></div>
                    </template>
                </el-input>
                <el-radio-group v-model="userInfo.gender" class="sign-in-input">
                    <el-radio label="male">男</el-radio>
                    <el-radio label="female">女</el-radio>
                </el-radio-group>
                <el-input placeholder="请输入16位银行账号..." maxlength="16" v-model="userInfo.bankAccount" class="sign-in-input" clearable>
                    <template slot="prepend">
                        <div class="el-icon-bank-card"></div>
                    </template>
                </el-input>
                <div class="sign-in-submit">
                    <el-button type="primary" @click="signIn">提交</el-button>
                </div>
                <div class="login-container">
                    <span @click="toLogin" class="login-text">登录</span>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script>
    export default {
        name: "sign-in",
        data(){
            return{
                userPassword2:'',
                userInfo:{
                    accountNumber:'',
                    userPassword:'',
                    nickname:''
                }
            };
        },
        methods:{
            toLogin(){
                this.$router.replace({path: '/login'});
            },
            signIn(){
                console.log(this.userInfo.nickname);
                if (!this.userInfo.avatar) {
                  this.userInfo.avatar = require('@/img/默认头像头像.png');
                }
                if(this.userInfo.accountNumber&&this.userInfo.phone&&this.userInfo.userPassword&&this.userInfo.nickname&&this.userInfo.email&&this.userInfo.gender&&this.userInfo.bankAccount){
                    if(this.userInfo.phone.length !== 11){
                        this.$message.error('手机号长度不符合要求!')
                    }else if(this.userInfo.accountNumber.length < 4){
                        this.$message.error('账号长度不能小于4位!')
                    }else if(this.userInfo.userPassword!==this.userPassword2){
                        this.$message.error('两次输入的密码不相同！');
                    }else if(!/^([A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+)$/.test(this.userInfo.email)){
                        this.$message.error('邮箱格式不正确！');
                    }else if(!(this.userInfo.gender==='male'||this.userInfo.gender==='female')){
                        this.$message.error('性别必须选择！');
                    }else if(!/^\d{16}$/.test(this.userInfo.bankAccount)){
                        this.$message.error('银行账号必须为16位数字！');
                    }else {
                        this.$api.signIn(this.userInfo).then(res=>{
                            if(res.status_code===1){
                                this.$message({
                                    message: '注册成功！',
                                    type: 'success'
                                });
                                this.$router.replace({path: '/login'});
                            }else {
                                this.$message.error(res.msg||'注册失败，用户已存！');
                            }
                        }).catch(e=>{
                            console.log(e);
                            this.$message.error('注册失败，网络异常！');
                        })
                    }
                }else{
                    this.$message.error('注册信息未填写完整！');
                }
            }
        }
    }
</script>

<style scoped>
    .sign-in-container {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        width: 100%;
        background-color: #f1f1f1;
    }

    .sign-in-body {
        padding: 30px;
        width: 400px;
        height: 100%;
    }

    .sign-in-title {
        padding-bottom: 30px;
        text-align: center;
        font-weight: 600;
        font-size: 20px;
        color: #409EFF;
    }

    .sign-in-input {
        margin-bottom: 20px;
    }
    .sign-in-submit{
        display: flex;
        justify-content: center;
    }
    .login-container{
        padding: 0 10px;
    }
    .login-text{
        color: #409EFF;
        font-size: 16px;
        cursor:pointer;
    }
</style>