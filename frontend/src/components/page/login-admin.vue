<template>
  <div class="login-container">
    <!-- 管理员登录卡片 -->
    <el-card class="admin-login-card">
      <div class="login-body">
        <div class="admin-title">管理后台系统</div>
        <!-- 登录表单 -->
        <el-form ref="form" :model="userForm">
          <!-- 管理员账号输入框 -->
          <el-input
              placeholder="请输入管理员账号"
              v-model="userForm.accountNumber"
              class="admin-input"
          >
            <template slot="prepend">
              <i class="el-icon-user-solid input-icon"></i>
            </template>
          </el-input>

          <!-- 管理员密码输入框 -->
          <el-input
              placeholder="请输入管理员密码"
              v-model="userForm.adminPassword"
              class="admin-input"
              @keyup.enter.native="login"
              show-password
          >
            <template slot="prepend">
              <i class="el-icon-lock input-icon"></i>
            </template>
          </el-input>

          <!-- 登录按钮 -->
          <div class="login-submit">
            <el-button type="primary" class="admin-btn" @click="login">登录</el-button>
          </div>
          <!-- 切换到学生登录的链接 -->
          <div class="other-submit">
            <router-link to="/login" class="admin-link">学生登录</router-link>
          </div>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "login",
  data() {
    return {
      // 管理员登录表单数据
      userForm: {
        accountNumber: '',
        adminPassword: ''
      }
    };
  },
  methods: {
    // 管理员登录方法
    login() {
      this.$api.adminLogin({
        accountNumber: this.userForm.accountNumber,
        adminPassword: this.userForm.adminPassword
      }).then(res => {
        if (res.status_code === 1) {
          // 登录成功，设置全局状态并跳转后台首页
          this.$sta.isLogin = true;
          this.$sta.adminName = res.data.adminName;
          this.$router.replace({path:'/platform-admin'});
        } else {
          // 登录失败提示
          this.$message.error('登录失败，账号或密码错误！');
        }
      });
    }
  }
}
</script>

<style scoped>
/* 页面整体居中和背景 */
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
      linear-gradient(rgba(255,255,255,0.8), rgba(245,245,245,0.9)),
      url("../../img/img.png") center/cover fixed;
}

/* 登录卡片样式 */
.admin-login-card {
  width: 420px;
  border-radius: 12px;
  box-shadow:
      0 12px 24px rgba(0, 0, 0, 0.15),
      0 6px 12px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(255,255,255,0.3);
  backdrop-filter: blur(8px);
  background: rgba(255,255,255,0.92) !important;
}

.admin-login-card:hover {
  transform: translateY(-2px);
  transition: transform 0.3s ease;
}

.login-body {
  padding: 40px 35px;
}

/* 标题样式 */
.admin-title {
  text-align: center;
  font-size: 24px;
  color: #2c3e50;
  margin-bottom: 35px;
  font-weight: 700;
  letter-spacing: 1px;
  position: relative;
  padding-bottom: 15px;
}

.admin-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 50px;
  height: 3px;
  background: #409EFF;
  border-radius: 2px;
}

/* 输入框样式 */
.admin-input >>> .el-input-group__prepend {
  background: #f8f9fa;
  border-right: 1px solid #e9ecef !important;
  padding: 0 15px;
}

.admin-input >>> .el-input__inner {
  height: 46px;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.admin-input >>> .el-input__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.15);
}

.input-icon {
  color: #409EFF;
  font-size: 18px;
}

/* 按钮样式 */
.admin-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  letter-spacing: 1px;
  border-radius: 8px;
  background: #409EFF;
  border: none;
  transition: all 0.3s ease;
}

.admin-btn:hover {
  background: #66b1ff;
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(64, 158, 255, 0.3);
}

/* 链接样式 */
.admin-link {
  color: #666;
  font-size: 14px;
  text-decoration: none;
  position: relative;
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.admin-link:hover {
  color: #409EFF;
  background: rgba(64, 158, 255, 0.05);
}

.admin-link::before {
  content: "";
  position: absolute;
  width: 0;
  height: 1px;
  bottom: 0;
  left: 50%;
  background: currentColor;
  transition: all 0.3s ease;
}

.admin-link:hover::before {
  width: 100%;
  left: 0;
}

.other-submit {
  margin-top: 25px;
  justify-content: center;
}
</style>