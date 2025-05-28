<template>
  <div class="page-minimal-bg">
    <app-head :nickname-value="userInfo.nickname"
              :avatarValue="userInfo.avatar"></app-head>
    <app-body class="body-minimal">
      <div class="user-dashboard" v-show="!eidtAddress">
        <!-- 个人信息卡片 -->
        <div class="profile-card">
          <div class="profile-header">
            <div class="profile-avatar-container">
              <el-upload
                  action="http://localhost:8080/file/"
                  :on-success="fileHandleSuccess"
                  :file-list="imgFileList"
                  accept="image/*"
              >
                <el-image
                    class="profile-avatar"
                    :src="userInfo.avatar ? userInfo.avatar : require('@/img/默认头像头像.png')"
                    fit="cover">
                </el-image>
                <div class="avatar-upload-hint">点击更换头像</div>
              </el-upload>
            </div>
            <div class="profile-info">
              <h2 class="profile-name">{{userInfo.nickname}}</h2>
              <p class="profile-joined">注册于 {{userInfo.signInTime}}</p>
              <div class="profile-actions">
                <el-button class="action-btn edit-btn" @click="userInfoDialogVisible = true">
                  <i class="el-icon-edit"></i> 编辑个人信息
                </el-button>
                <el-button class="action-btn address-btn" @click="eidtAddress=true">
                  <i class="el-icon-location"></i> 收货地址管理
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 我的内容区域 -->
        <div class="content-section">
          <div class="content-tabs">
            <el-tabs v-model="activeName" @tab-click="handleClick" class="custom-tabs">
              <el-tab-pane label="我的发布" name="1"></el-tab-pane>
              <el-tab-pane label="已下架" name="2"></el-tab-pane>
              <el-tab-pane label="出售记录" name="4"></el-tab-pane>
              <el-tab-pane label="购买记录" name="5"></el-tab-pane>
            </el-tabs>
          </div>
          
          <div class="content-grid">
            <div v-if="dataList[activeName-1].length > 0" class="goods-grid">
              <div class="goods-card-minimal" v-for="(item,index) in dataList[activeName-1]" :key="index" @click="toDetails(activeName,item)">
                <div class="goods-img-wrap">
                  <el-image
                      :src="item.imgUrl"
                      fit="cover"
                      class="goods-img-minimal">
                    <div slot="error" class="image-slot">
                      <i class="el-icon-picture-outline"></i>
                    </div>
                  </el-image>
                  <div class="goods-price-badge">￥{{item.idlePrice}}</div>
                  <div v-if="item.isOffline" class="goods-offline-badge">已下架</div>
                </div>
                <div class="goods-title-minimal">{{item.idleName}}</div>
                <div class="goods-seller-row">
                  <el-image :src="item.user && item.user.avatar ? item.user.avatar : require('@/img/默认头像头像.png')" fit="cover" class="goods-seller-avatar-minimal"></el-image>
                  <span class="goods-seller-name-minimal">{{item.user && item.user.nickname ? item.user.nickname : '未知用户'}}</span>
                  <span v-if="activeName==='4' || activeName==='5'" class="goods-status-tag" :class="'status-'+item.orderStatus">
                    {{orderStatus[item.orderStatus]}}
                  </span>
                  <el-button 
                    v-if="activeName!=='4' && activeName!=='5'" 
                    type="danger" 
                    size="mini" 
                    class="goods-action-btn"
                    @click.stop="handle(activeName,item,index)">
                    {{handleName[activeName-1]}}
                  </el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <i class="el-icon-box"></i>
              <p>暂无内容</p>
            </div>
          </div>
        </div>

        <!-- 编辑个人信息弹窗 -->
        <el-dialog
            @close="finishEdit"
            title="编辑个人信息"
            :visible.sync="userInfoDialogVisible"
            width="520px"
            class="custom-dialog">
          <div class="user-info-form">
            <div class="form-item">
              <div class="edit-tip">昵称</div>
              <el-input
                  v-model="userInfo.nickname"
                  :disabled="notUserNicknameEdit"
                  @change="saveUserInfo"
                  placeholder="请输入昵称">
                <el-button slot="append" type="warning" icon="el-icon-edit"
                           @click="notUserNicknameEdit = false">编辑
                </el-button>
              </el-input>
            </div>

            <div class="form-item">
              <div class="edit-tip">学号</div>
              <el-input
                  v-model="userInfo.studentId"
                  placeholder="请输入学号"
                  @change="saveUserInfo">
              </el-input>
            </div>

            <div class="form-item">
              <div class="edit-tip">手机号</div>
              <el-input
                  v-model="userInfo.phone"
                  placeholder="请输入手机号"
                  maxlength="11"
                  @change="saveUserInfo">
              </el-input>
            </div>

            <div class="form-item">
              <div class="edit-tip">邮箱</div>
              <el-input
                  v-model="userInfo.email"
                  placeholder="请输入邮箱"
                  @change="saveUserInfo">
              </el-input>
            </div>

            <div class="form-item">
              <div class="edit-tip">性别</div>
              <el-radio-group v-model="userInfo.gender" @change="saveUserInfo">
                <el-radio label="male">男</el-radio>
                <el-radio label="female">女</el-radio>
              </el-radio-group>
            </div>

            <div class="form-item">
              <div class="edit-tip">银行账号</div>
              <el-input
                  v-model="userInfo.bankAccount"
                  placeholder="请输入16位银行账号"
                  maxlength="16"
                  @change="saveUserInfo">
              </el-input>
            </div>

            <div class="form-item">
              <div class="edit-tip">个性签名</div>
              <el-input
                  v-model="userInfo.signature"
                  placeholder="请输入个性签名"
                  maxlength="50"
                  @change="saveUserInfo">
              </el-input>
            </div>

            <div class="form-divider"></div>

            <div v-if="userPasswordEdit">
              <div class="edit-tip">原密码</div>
              <el-input v-model="userPassword1" show-password></el-input>
              <div class="edit-tip">新密码</div>
              <el-input v-model="userPassword2" show-password></el-input>
              <div class="edit-tip">确认新密码</div>
              <el-input v-model="userPassword3" show-password></el-input>
              <div class="edit-tip"></div>
              <el-button @click="savePassword" class="minimal-btn">确认修改</el-button>
            </div>
            <div v-else>
              <div class="edit-tip">密码</div>
              <el-input
                  value="123456"
                  :disabled="true"
                  show-password>
                <el-button slot="append" type="warning" icon="el-icon-edit"
                           @click="userPasswordEdit = true">修改密码
                </el-button>
              </el-input>
            </div>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" class="minimal-btn" @click="saveUserInfo">保存</el-button>
            <el-button @click="userInfoDialogVisible=false">关闭</el-button>
          </span>
        </el-dialog>
      </div>

      <!-- 收货地址编辑部分 -->
      <div v-show="eidtAddress" class="address-container">
        <div class="section-header">
          <el-page-header class="address-container-back" @back="eidtAddress=false"
                      content="收货地址管理"></el-page-header>
        </div>
        <div class="address-container-add">
          <div class="address-container-add-title">新增收货地址</div>
          <div class="address-container-add-item">
            <el-input placeholder="请输入收货人姓名" v-model="addressInfo.consigneeName" maxlength="10"
                    show-word-limit>
              <div slot="prepend">收货人姓名</div>
            </el-input>
          </div>
          <div class="address-container-add-item">
            <el-input placeholder="请输入收货人手机号" v-model="addressInfo.consigneePhone"
                    onkeyup="this.value=this.value.replace(/[^\d.]/g,'');" maxlength="11" show-word-limit>
              <div slot="prepend">您的手机号</div>
            </el-input>
          </div>

          <div class="address-container-add-item">
            <span class="demonstration">宿舍类型/楼号/楼层</span>
            <el-cascader
                :options="options"
                v-model="selectedOptions"
                @change="handleAddressChange"
                :separator="' '"
            >
            </el-cascader>
          </div>
          <div class="address-container-add-item">
            <el-input placeholder="宿舍号" v-model="addressInfo.detailAddress"
                    maxlength="50" show-word-limit>
              <div slot="prepend">详细地址</div>
            </el-input>
          </div>
          <el-checkbox v-model="addressInfo.defaultFlag">设置为默认地址</el-checkbox>
          <el-button class="minimal-btn" style="margin-left: 20px;" @click="saveAddress">保存</el-button>
        </div>
        <div class="address-container-list">
          <div class="address-title">已有收货地址</div>
          <el-table
              stripe
              :data="addressData"
              class="address-table"
              style="width: 100%">
            <el-table-column
                prop="consigneeName"
                label="收货人姓名"
                width="100">
            </el-table-column>
            <el-table-column
                prop="consigneePhone"
                label="手机号"
                width="120">
            </el-table-column>
            <el-table-column
                prop="detailAddressText"
                label="地址"
                width="270">
            </el-table-column>
            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button
                    size="mini"
                    class="table-btn edit-btn"
                    @click="handleEdit(scope.$index, scope.row)">编辑
                </el-button>
                <el-button
                    size="mini"
                    class="table-btn delete-btn"
                    @click="handleDelete(scope.$index, scope.row)">删除
                </el-button>
              </template>
            </el-table-column>
            <el-table-column label="是否默认地址" width="110">
              <template slot-scope="scope">
                <el-button v-if="!scope.row.defaultFlag"
                         size="mini"
                         class="table-btn default-btn"
                         @click="handleSetDefault(scope.$index, scope.row)">设为默认
                </el-button>
                <div v-else class="default-tag">
                  <i class="el-icon-star-on"></i> 默认地址
                </div>
              </template>
            </el-table-column>
          </el-table>
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
import options from '../common/country-data.js'

export default {
  name: "me",
  components: {
    AppHead,
    AppBody,
    AppFoot
  },
  data() {
    return {
      imgFileList: [],
      addressInfo: {
        consigneeName: '',
        consigneePhone: '',
        provinceName: '',
        cityName: '',
        regionName: '',
        detailAddress: '',
        defaultFlag: false
      },
      activeName: '1',
      handleName: ['下架', '删除', '取消购物车', '', ''],
      dataList: [
        [],
        [],
        [],
        [],
        [], // 购买记录
      ],
      orderStatus: ['待付款', '待发货', '待收货', '已完成', '已取消', '已退款'],
      userInfoDialogVisible: false,
      notUserNicknameEdit: true,
      userPasswordEdit: false,
      userPassword1: '',
      userPassword2: '',
      userPassword3: '',
      eidtAddress: false,
      selectedOptions: [],//存放默认值
      options: options,   //存放城市数据,
      userInfo: {
        accountNumber: "",
        avatar: require('@/img/默认头像头像.png'),
        nickname: "",
        signInTime: "",
        studentId: "",     // 学号
        phone: "",         // 手机号
        email: "",         // 邮箱
        gender: "",        // 性别
        bankAccount: "",   // 银行账号
        signature: "",     // 个性签名
      },
      addressData: []
    };
  },
  created() {
    if (!this.$globalData.userInfo.nickname) {
      this.$api.getUserInfo().then(res => {
        if (res.status_code === 1) {
          res.data.signInTime = res.data.signInTime.substring(0, 10);
          if (!res.data.avatar) {
            res.data.avatar = require('@/img/默认头像头像.png');
          }
          this.$globalData.userInfo = res.data;
          this.userInfo = Object.assign({}, this.userInfo, res.data);
        }
      })
    } else {
      if (!this.$globalData.userInfo.avatar) {
        this.$globalData.userInfo.avatar = require('@/img/默认头像头像.png');
      }
      this.userInfo = Object.assign({}, this.userInfo, this.$globalData.userInfo);
      console.log(this.userInfo);
    }
    this.getAddressData();
    this.getIdleItemData();
    this.getMyOrder();
    this.getMySoldIdle();
  },
  methods: {
    getMySoldIdle(){
      this.$api.getMySoldIdle().then(res=>{
        if (res.status_code === 1){
          console.log('getMySoldIdle完整数据:', res.data);
          
          // 收集所有买家ID
          const buyerIds = [];
          for (let i = 0; i < res.data.length; i++) {
            if (res.data[i].userId) {
              if (!buyerIds.includes(res.data[i].userId)) {
                buyerIds.push(res.data[i].userId);
              }
            }
          }
          
          // 获取所有买家信息
          const buyerInfoMap = {};
          const getBuyerPromises = buyerIds.map(buyerId => {
            return this.$api.getUserInfoById({ userId: buyerId }).then(userRes => {
              if (userRes.status_code === 1 && userRes.data) {
                buyerInfoMap[buyerId] = userRes.data;
              }
              return userRes;
            }).catch(err => {
              console.error('获取买家信息失败:', err);
            });
          });
          
          Promise.all(getBuyerPromises).then(() => {
            // 处理所有订单数据
            for (let i = 0; i < res.data.length; i++) {
              try {
                let pictureList = [];
                if (res.data[i].idleItem && res.data[i].idleItem.pictureList) {
                  pictureList = JSON.parse(res.data[i].idleItem.pictureList || '[]');
                }
                
                // 获取买家信息
                const buyerId = res.data[i].userId;
                const buyerInfo = buyerId ? buyerInfoMap[buyerId] : null;
                
                this.dataList[3].push({
                  id: res.data[i].id,
                  imgUrl: pictureList.length > 0 ? pictureList[0] : require('@/img/默认头像头像.png'),
                  idleName: res.data[i].idleItem ? res.data[i].idleItem.idleName : '商品已下架',
                  idleDetails: res.data[i].idleItem ? res.data[i].idleItem.idleDetails : '',
                  timeStr: res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19),
                  idlePrice: res.data[i].orderPrice,
                  orderStatus: res.data[i].orderStatus,
                  isOffline: !res.data[i].idleItem || res.data[i].idleItem.idleStatus === 0 || res.data[i].idleItem.idleStatus === 2,
                  user: {
                    avatar: buyerInfo ? buyerInfo.avatar : require('@/img/默认头像头像.png'),
                    nickname: buyerInfo ? buyerInfo.nickname : '未知买家'
                  }
                });
              } catch (error) {
                console.error('解析订单数据错误:', error);
                this.dataList[3].push({
                  id: res.data[i].id,
                  imgUrl: require('@/img/默认头像头像.png'),
                  idleName: '商品已下架',
                  idleDetails: '',
                  timeStr: res.data[i].createTime ? (res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19)) : '未知时间',
                  idlePrice: res.data[i].orderPrice || 0,
                  orderStatus: res.data[i].orderStatus || 0,
                  isOffline: true,
                  user: {
                    avatar: require('@/img/默认头像头像.png'),
                    nickname: '未知买家'
                  }
                });
              }
            }
          });
        }
      })
    },
    getMyOrder(){
      this.$api.getMyOrder().then(res=>{
        if (res.status_code === 1){
          console.log('getMyOrder完整数据:', res.data);
          
          // 收集所有卖家ID
          const sellerIds = [];
          for (let i = 0; i < res.data.length; i++) {
            if (res.data[i].idleItem && res.data[i].idleItem.userId) {
              if (!sellerIds.includes(res.data[i].idleItem.userId)) {
                sellerIds.push(res.data[i].idleItem.userId);
              }
            }
          }
          
          // 获取所有卖家信息
          const sellerInfoMap = {};
          const getSellerPromises = sellerIds.map(sellerId => {
            return this.$api.getUserInfoById({ userId: sellerId }).then(userRes => {
              if (userRes.status_code === 1 && userRes.data) {
                sellerInfoMap[sellerId] = userRes.data;
              }
              return userRes;
            }).catch(err => {
              console.error('获取卖家信息失败:', err);
            });
          });
          
          Promise.all(getSellerPromises).then(() => {
            // 处理所有订单数据
            for (let i = 0; i < res.data.length; i++) {
              try {
                let pictureList = [];
                if (res.data[i].idleItem && res.data[i].idleItem.pictureList) {
                  pictureList = JSON.parse(res.data[i].idleItem.pictureList || '[]');
                }
                
                // 获取卖家ID并查找对应信息
                const sellerId = res.data[i].idleItem ? res.data[i].idleItem.userId : null;
                const sellerInfo = sellerId ? sellerInfoMap[sellerId] : null;
                
                this.dataList[4].push({
                  id: res.data[i].id,
                  imgUrl: pictureList.length > 0 ? pictureList[0] : require('@/img/默认头像头像.png'),
                  idleName: res.data[i].idleItem ? res.data[i].idleItem.idleName : '商品已下架',
                  idleDetails: res.data[i].idleItem ? res.data[i].idleItem.idleDetails : '',
                  timeStr: res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19),
                  idlePrice: res.data[i].orderPrice,
                  orderStatus: res.data[i].orderStatus,
                  isOffline: !res.data[i].idleItem || res.data[i].idleItem.idleStatus === 0 || res.data[i].idleItem.idleStatus === 2,
                  user: {
                    avatar: sellerInfo ? sellerInfo.avatar : require('@/img/默认头像头像.png'),
                    nickname: sellerInfo ? sellerInfo.nickname : '未知卖家'
                  }
                });
              } catch (error) {
                console.error('解析订单数据错误:', error);
                this.dataList[4].push({
                  id: res.data[i].id,
                  imgUrl: require('@/img/默认头像头像.png'),
                  idleName: '商品已下架',
                  idleDetails: '',
                  timeStr: res.data[i].createTime ? (res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19)) : '未知时间',
                  idlePrice: res.data[i].orderPrice || 0,
                  orderStatus: res.data[i].orderStatus || 0,
                  isOffline: true,
                  user: {
                    avatar: require('@/img/默认头像头像.png'),
                    nickname: '未知卖家'
                  }
                });
              }
            }
          });
        }
      })
    },
    getIdleItemData() {
      this.$api.getAllIdleItem().then(res => {
        console.log(res);
        if (res.status_code === 1) {
          for (let i = 0; i < res.data.length; i++) {
            res.data[i].timeStr = res.data[i].releaseTime.substring(0, 10) + " " + res.data[i].releaseTime.substring(11, 19);
            let pictureList = JSON.parse(res.data[i].pictureList);
            res.data[i].imgUrl = pictureList.length > 0 ? pictureList[0] : '';
            // 新增：补充user字段，优先用后端返回的user，否则用当前登录用户信息
            if (!res.data[i].user) {
              res.data[i].user = {
                avatar: this.userInfo.avatar || require('@/img/默认头像头像.png'),
                nickname: this.userInfo.nickname || '未知用户'
              }
            }
            if (res.data[i].idleStatus === 1) {
              this.dataList[0].push(res.data[i]);
            } else if (res.data[i].idleStatus === 2) {
              this.dataList[1].push(res.data[i]);
            }
          }
        }
      })
    },
    getAddressData() {
      this.$api.getAddress().then(res => {
        if (res.status_code === 1) {
          let data = res.data;
          for (let i = 0; i < data.length; i++) {
            data[i].detailAddressText = data[i].provinceName + data[i].cityName + data[i].regionName + data[i].detailAddress;
            data[i].defaultAddress = data[i].defaultFlag ? '默认地址' : '设为默认';
          }
          console.log(data);
          this.addressData = data;
        }
      })
    },
    handleClick(tab, event) {
      // console.log(tab, event);
      console.log(this.activeName);
    },
    saveUserInfo() {
      this.notUserNicknameEdit = true;
      this.$api.updateUserPublicInfo({
        nickname: this.userInfo.nickname,
        studentId: this.userInfo.studentId,
        phone: this.userInfo.phone,
        email: this.userInfo.email,
        gender: this.userInfo.gender,
        bankAccount: this.userInfo.bankAccount,
        signature: this.userInfo.signature
      }).then(res => {
        if (res.status_code === 1) {
          this.$globalData.userInfo.nickname = this.userInfo.nickname;
          this.$globalData.userInfo.studentId = this.userInfo.studentId;
          this.$globalData.userInfo.phone = this.userInfo.phone;
          this.userInfo.email = this.userInfo.email;
          this.$globalData.userInfo.gender = this.userInfo.gender;
          this.$globalData.userInfo.bankAccount = this.userInfo.bankAccount;
          this.$globalData.userInfo.signature = this.userInfo.signature;
          this.$message.success('保存成功！');
          this.userInfoDialogVisible = false;
        } else {
          this.$message.error('保存失败，请重试！');
        }
      }).catch(() => {
        this.$message.error('网络异常，保存失败！');
      });
    },
    savePassword() {
      if (!this.userPassword1 || !this.userPassword2) {
        this.$message.error('密码为空！');
      } else if (this.userPassword2 === this.userPassword3) {
        this.$api.updatePassword({
          oldPassword: this.userPassword1,
          newPassword: this.userPassword2
        }).then(res => {
          if (res.status_code === 1) {
            this.userPasswordEdit = false;
            this.$message({
              message: '修改成功！',
              type: 'success'
            });
            this.userPassword1 = '';
            this.userPassword2 = '';
            this.userPassword3 = '';
          } else {
            this.$message.error('旧密码错误，修改失败！');
          }
        })
      } else {
        this.$message.error('两次输入的密码不一致！');
      }
    },
    finishEdit() {
      this.notUserNicknameEdit = true;
      this.userInfoDialogVisible = false;
      this.userPasswordEdit = false;
    },
    handleAddressChange(value) {
      console.log(value);
      this.addressInfo.provinceName = value[0];
      this.addressInfo.cityName = value[1];
      this.addressInfo.regionName = value[2];
    },
    handleEdit(index, row) {
      console.log(index, row);
      this.addressInfo = JSON.parse(JSON.stringify(row));
      this.selectedOptions = ['', '', ''];
      this.selectedOptions[0] = row.provinceName;
      this.selectedOptions[1] = row.cityName;
      this.selectedOptions[2] = row.regionName;
    },
    handleDelete(index, row) {
      console.log(index, row);
      this.$confirm('是否确定删除该地址?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$api.deleteAddress(row).then(res => {
          if (res.status_code === 1) {
            this.$message({
              message: '删除成功！',
              type: 'success'
            });
            this.addressData.splice(index, 1);
            if (row.defaultFlag && this.addressData.length > 0) {
              this.addressData[0].defaultFlag = true;
              this.addressData[0].defaultAddress = '默认地址';
              this.update({
                id: this.addressData[0].id,
                defaultFlag: true
              });
            }
          } else {
            this.$message.error('系统异常，删除失败！')
          }
        }).catch(() => {
          this.$message.error('网络异常！')
        });
      }).catch(() => {
      });
    },
    handleSetDefault(index, row) {
      console.log(index, row);
      row.defaultFlag = true;
      this.update(row);
    },
    toDetails(activeName, item) {
      if (activeName === '4'||activeName === '5') {
        this.$router.push({path: '/order', query: {id: item.id}});
      } else {
        if (item.isOffline) {
          this.$message.info('该商品已下架，无法查看详情');
          return;
        }
        this.$router.push({path: '/details', query: {id: item.id}});
      }
    },
    handle(activeName,item,index) {
      console.log(activeName,item,index);
      this.$confirm('是否确认？', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if(activeName==='1'){
          this.$api.updateIdleItem({
            id:item.id,
            idleStatus:2
          }).then(res=>{
            console.log(res);
            if(res.status_code===1){
              this.dataList[0].splice(index,1);
              item.idleStatus=2;
              this.dataList[1].unshift(item);
            }else {
              this.$message.error(res.msg)
            }
          });
        }else if(activeName==='2'){
          this.$api.updateIdleItem({
            id:item.id,
            idleStatus:0
          }).then(res=>{
            console.log(res);
            if(res.status_code===1){
              this.dataList[1].splice(index,1);
            }else {
              this.$message.error(res.msg)
            }
          });
        }else if(activeName==='3'){
          this.$api.deleteFavorite(item.favoriteId).then(res=>{
            console.log(res);
            if(res.status_code===1){
              this.$message({
                message: '已取消购物车！',
                type: 'success'
              });
              this.dataList[2].splice(index,1);
            }else {
              this.$message.error(res.msg)
            }
          }).catch(e=>{
          })
        }
      }).catch(() => {
      });
    },
    fileHandleSuccess(response, file, fileList) {
      console.log("file:", response, file, fileList);
      let imgUrl = response.data;
      this.imgFileList = [];
      this.$api.updateUserPublicInfo({
        avatar: imgUrl
      }).then(res => {
        console.log(res);
        this.userInfo.avatar = imgUrl;
        this.$globalData.userInfo.avatar = imgUrl;
      })
    },
    update(data) {
      this.$api.updateAddress(data).then(res => {
        if (res.status_code === 1) {
          this.getAddressData();
          this.$message({
            message: '修改成功！',
            type: 'success'
          });
        } else {
          this.$message.error('系统异常，修改失败！')
        }
      }).catch(() => {
        this.$message.error('网络异常！')
      })
    },
    saveAddress() {
      if (this.addressInfo.id) {
        console.log('update:', this.addressInfo);
        this.update(this.addressInfo);
        this.addressInfo = {
          consigneeName: '',
          consigneePhone: '',
          provinceName: '',
          cityName: '',
          regionName: '',
          detailAddress: '',
          defaultFlag: false
        };
        this.selectedOptions = [];
      } else {
        if (this.addressData.length >= 5) {
          this.$message.error('已达到最大地址数量！')
        } else {
          console.log(this.addressInfo);
          this.$api.addAddress(this.addressInfo).then(res => {
            if (res.status_code === 1) {
              this.getAddressData();
              this.$message({
                message: '新增成功！',
                type: 'success'
              });
              this.selectedOptions = [];
              this.addressInfo = {
                consigneeName: '',
                consigneePhone: '',
                provinceName: '',
                cityName: '',
                regionName: '',
                detailAddress: '',
                defaultFlag: false
              };
            } else {
              this.$message.error('系统异常，新增失败！')
            }
          }).catch(e => {
            this.$message.error('网络异常！')
          })
        }
      }
    }
  }
}
</script>

<style scoped>
.page-minimal-bg {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(120deg, #e0f7fa 0%, #e3f0ff 100%);
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.body-minimal {
  width: 100%;
  padding: 0;
  margin: 0;
  background: transparent;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.user-dashboard {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 0;
}

/* 个人信息卡片 */
.profile-card {
  width: 100%;
  background: rgba(255,255,255,0.95);
  border-radius: 24px;
  box-shadow: 0 6px 32px rgba(60,120,200,0.10);
  padding: 40px;
  margin-bottom: 40px;
  overflow: hidden;
  box-sizing: border-box;
}

.profile-header {
  display: flex;
  align-items: flex-start;
}

.profile-avatar-container {
  margin-right: 36px;
  position: relative;
}

.profile-avatar {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  box-shadow: 0 8px 24px rgba(60,120,200,0.15);
  border: 4px solid #fff;
  background: #fff;
  transition: all 0.3s ease;
}

.profile-avatar:hover {
  transform: scale(1.03);
  box-shadow: 0 12px 28px rgba(60,120,200,0.25);
}

.avatar-upload-hint {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
  padding: 6px;
  font-size: 12px;
  color: #fff;
  background: rgba(38,198,218,0.7);
  border-radius: 0 0 20px 20px;
  opacity: 0;
  transition: opacity 0.3s;
}

.profile-avatar-container:hover .avatar-upload-hint {
  opacity: 1;
}

.profile-info {
  flex: 1;
}

.profile-name {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #1a237e;
  letter-spacing: 0.5px;
}

.profile-joined {
  color: #78909c;
  font-size: 14px;
  margin-bottom: 24px;
}

.profile-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.action-btn {
  padding: 10px 24px;
  border-radius: 18px;
  font-weight: 500;
  transition: all 0.25s;
  border: none;
}

.action-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.1);
}

.edit-btn {
  background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
  color: white;
}

.address-btn {
  background: rgba(255,255,255,0.8);
  color: #26c6da;
  border: 1px solid #e0f7fa;
}

/* 内容区域 */
.content-section {
  width: 100%;
  background: rgba(255,255,255,0.95);
  border-radius: 24px;
  box-shadow: 0 6px 32px rgba(60,120,200,0.10);
  padding: 30px;
  margin-bottom: 40px;
  box-sizing: border-box;
}

.content-tabs {
  margin-bottom: 30px;
}

.custom-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.custom-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
  transition: all 0.3s;
}

.custom-tabs :deep(.el-tabs__item.is-active) {
  color: #26c6da;
  font-weight: 600;
}

.custom-tabs :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
  height: 3px;
  border-radius: 3px;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
  width: 100%;
  margin-bottom: 20px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  color: #b0bec5;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
}

/* 商品卡片样式 */
.goods-card-minimal {
  background: rgba(255,255,255,0.95);
  border-radius: 22px;
  box-shadow: 0 6px 20px rgba(60,120,200,0.08);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  cursor: pointer;
  transition: all 0.25s ease;
  position: relative;
  overflow: hidden;
  min-height: 280px;
}

.goods-card-minimal:hover {
  box-shadow: 0 14px 40px rgba(60,120,200,0.15);
  transform: translateY(-8px);
}

.goods-img-wrap {
  width: 100%;
  aspect-ratio: 1.2/1;
  border-radius: 22px 22px 0 0;
  overflow: hidden;
  position: relative;
  background: #e3f0ff;
}

.goods-img-minimal {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.goods-card-minimal:hover .goods-img-minimal {
  transform: scale(1.05);
}

.goods-price-badge {
  position: absolute;
  left: 16px;
  top: 16px;
  background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  border-radius: 14px;
  padding: 4px 16px;
  box-shadow: 0 4px 12px rgba(38,198,218,0.25);
  z-index: 2;
}

.goods-title-minimal {
  font-size: 16px;
  font-weight: 600;
  color: #263238;
  margin: 16px 16px 10px 16px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  width: calc(100% - 32px);
}

.goods-seller-row {
  display: flex;
  align-items: center;
  padding: 0 16px 16px 16px;
  width: calc(100% - 32px);
  position: relative;
}

.goods-seller-avatar-minimal {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  margin-right: 8px;
  border: 2px solid #e3f0ff;
}

.goods-seller-name-minimal {
  font-size: 14px;
  color: #455a64;
  max-width: 100px;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-status-tag {
  position: absolute;
  right: 16px;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
}

.status-0 {
  background-color: #ffecb3;
  color: #ff6f00;
}

.status-1 {
  background-color: #e3f2fd;
  color: #1976d2;
}

.status-2 {
  background-color: #e0f2f1;
  color: #00897b;
}

.status-3 {
  background-color: #f1f8e9;
  color: #558b2f;
}

.status-4 {
  background-color: #f5f5f5;
  color: #757575;
}

.status-5 {
  background-color: #eceff1;
  color: #546e7a;
}

.goods-action-btn {
  position: absolute;
  right: 16px;
  background: rgba(244,67,54,0.1);
  color: #f44336;
  font-size: 12px;
  border: none;
  padding: 2px 10px;
  border-radius: 12px;
  transition: all 0.2s;
}

.goods-action-btn:hover {
  background: rgba(244,67,54,0.2);
  transform: scale(1.05);
}

/* 地址管理部分 */
.address-container {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 0;
}

.section-header {
  margin-bottom: 24px;
}

.address-container-add {
  background: rgba(255,255,255,0.95);
  border-radius: 24px;
  box-shadow: 0 6px 32px rgba(60,120,200,0.10);
  padding: 30px;
  margin-bottom: 30px;
}

.address-container-add-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a237e;
  margin-bottom: 24px;
  position: relative;
}

.address-container-add-title:after {
  content: '';
  display: block;
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
  margin-top: 8px;
  border-radius: 2px;
}

.address-container-add-item {
  margin-bottom: 20px;
}

.demonstration {
  display: block;
  font-size: 14px;
  color: #455a64;
  margin-bottom: 8px;
}

.address-container-list {
  width: 100%;
  background: rgba(255,255,255,0.95);
  border-radius: 24px;
  box-shadow: 0 6px 32px rgba(60,120,200,0.10);
  padding: 30px;
  margin-bottom: 40px;
  box-sizing: border-box;
}

.address-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a237e;
  margin-bottom: 24px;
  position: relative;
}

.address-title:after {
  content: '';
  display: block;
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
  margin-top: 8px;
  border-radius: 2px;
}

.address-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.address-table :deep(.el-table__header) {
  background: #f5f7fa;
}

.address-table :deep(.el-table__header th) {
  background: #f5f7fa;
  color: #455a64;
  font-weight: 600;
}

.address-table :deep(.el-table__row:hover) {
  background-color: #f1f8ff;
}

.table-btn {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  border: none;
  transition: all 0.2s;
}

.table-btn.edit-btn {
  background: rgba(66,165,245,0.1);
  color: #42a5f5;
}

.table-btn.delete-btn {
  background: rgba(244,67,54,0.1);
  color: #f44336;
}

.table-btn.default-btn {
  background: rgba(38,198,218,0.1);
  color: #26c6da;
}

.default-tag {
  color: #26c6da;
  font-size: 14px;
}

.default-tag i {
  color: #ffc107;
  margin-right: 4px;
}

/* 弹窗样式 */
.custom-dialog :deep(.el-dialog__title) {
  font-size: 22px;
  font-weight: 600;
  color: #1a237e;
}

.custom-dialog :deep(.el-dialog__header) {
  padding: 24px 24px 10px 24px;
  text-align: center;
  position: relative;
}

.custom-dialog :deep(.el-dialog__header):after {
  content: '';
  display: block;
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
  margin: 10px auto 0;
  border-radius: 2px;
}

.custom-dialog :deep(.el-dialog__body) {
  padding: 20px 24px;
}

.custom-dialog :deep(.el-dialog__footer) {
  padding: 10px 24px 24px;
  text-align: center;
}

.user-info-form {
  padding: 10px 0;
}

.form-item {
  margin-bottom: 16px;
}

.form-divider {
  height: 1px;
  background: #eceff1;
  margin: 24px 0;
}

.edit-tip {
  font-size: 14px;
  color: #455a64;
  margin-bottom: 8px;
}

.minimal-btn {
  background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 18px;
  transition: all 0.25s;
}

.minimal-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(66,165,245,0.25);
}

.goods-offline-badge {
  position: absolute;
  right: 18px;
  top: 18px;
  background: linear-gradient(90deg, #f44336 0%, #e53935 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  border-radius: 16px;
  padding: 4px 18px;
  box-shadow: 0 2px 8px rgba(220,60,60,0.18);
}

.goods-status-tag {
  font-size: 14px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 12px;
  margin-left: auto;
}
.status-0 {
  background: #ffecb3;
  color: #ff9800;
}
.status-1 {
  background: #e1f5fe;
  color: #03a9f4;
}
.status-2 {
  background: #e8f5e9;
  color: #4caf50;
}
.status-3 {
  background: #e0f2f1;
  color: #009688;
}
.status-4 {
  background: #f5f5f5;
  color: #9e9e9e;
}
.status-5 {
  background-color: #eceff1;
  color: #546e7a;
}

@media screen and (max-width: 1400px) {
  .goods-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (max-width: 1100px) {
  .goods-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .user-dashboard,
  .address-container {
    width: 95%;
  }
}

@media screen and (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .profile-avatar-container {
    margin: 0 0 24px 0;
  }
  
  .profile-actions {
    justify-content: center;
  }
  
  .goods-grid {
    gap: 20px;
  }
  
  .content-section,
  .address-container-list {
    padding: 20px;
  }
}

@media screen and (max-width: 480px) {
  .goods-grid {
    grid-template-columns: repeat(1, 1fr);
  }
  
  .user-dashboard,
  .address-container {
    width: 92%;
    padding: 20px 0;
  }
  
  .profile-card {
    padding: 30px 20px;
  }
}
</style>