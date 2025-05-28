<template>
  <!-- 模板部分完全保持不变 -->
  <div class="container">
    <div class="login-box">
      <div class="inner-box">
        <div class="main-title">校园二手交易商城</div>
        <el-form ref="form" :model="userForm">
          <el-input
              placeholder="请输入账号..."
              v-model="userForm.accountNumber"
              class="enhanced-input"
          >
            <template slot="prepend">
              <i class="el-icon-user-solid icon-style"></i>
            </template>
          </el-input>

          <el-input
              placeholder="请输入密码..."
              v-model="userForm.userPassword"
              class="enhanced-input"
              @keyup.enter.native="login"
              show-password
          >
            <template slot="prepend">
              <i class="el-icon-lock icon-style"></i>
            </template>
          </el-input>

          <div class="btn-group">
            <el-button
                type="primary"
                class="custom-btn"
                @click="login"
            >
              登录
            </el-button>
          </div>

          <div class="link-group">
            <router-link to="/sign-in" class="nav-link">注册账号</router-link>
            <router-link to="/login-admin" class="nav-link">管理员入口</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "login",
  data() {
    return {
      userForm: {
        accountNumber: '',
        userPassword: ''
      }
    };
  },
  methods: {
    login() {
      this.$api.userLogin({
        accountNumber: this.userForm.accountNumber,
        userPassword: this.userForm.userPassword
      }).then(res => {
        if (res.status_code === 1) {
          res.data.signInTime=res.data.signInTime.substring(0,10);
          this.$globalData.userInfo = res.data;
          this.$router.replace({path: '/index'});
        } else {
          this.$message.error(res.msg);
        }
      });
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-box {
  width: 380px;
  background: rgba(255,255,255,0.95);
  border-radius: 20px;
  box-shadow:
      0 10px 30px rgba(0,0,0,0.12),
      0 2px 6px rgba(0,0,0,0.04);
  border: 1px solid rgba(255,255,255,0.5);
  backdrop-filter: blur(8px);
}

.inner-box {
  padding: 40px 35px;
}

.main-title {
  text-align: center;
  font-size: 24px;
  color: #2d3436;
  margin-bottom: 35px;
  font-weight: 600;
  letter-spacing: 1px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.enhanced-input {
  margin-bottom: 25px;
}

.enhanced-input >>> .el-input__inner {
  height: 46px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.enhanced-input >>> .el-input__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.icon-style {
  font-size: 18px;
  color: #409EFF;
  padding-right: 10px;
}

.btn-group {
  margin-top: 15px;
}

.custom-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  border-radius: 8px;
  background: #409EFF;
  border: none;
  transition: all 0.3s ease;
}

.custom-btn:hover {
  background: #66b1ff;
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(64, 158, 255, 0.3);
}

.link-group {
  margin-top: 25px;
  display: flex;
  justify-content: space-between;
}

.nav-link {
  color: #636e72;
  font-size: 14px;
  position: relative;
  padding: 6px 10px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.nav-link:hover {
  color: #409EFF;
  background: rgba(64, 158, 255, 0.05);
}

.nav-link::after {
  content: "";
  position: absolute;
  left: 10px;
  right: 10px;
  bottom: 0;
  height: 1px;
  background: currentColor;
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.nav-link:hover::after {
  transform: scaleX(1);
}
</style>