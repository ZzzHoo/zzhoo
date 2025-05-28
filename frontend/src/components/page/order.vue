<template>
  <div class="order-bg">
    <app-head />
    <app-body>
      <div class="order-main">
        <!-- 商品信息卡片 -->
        <div class="card goods-card">
          <el-image
            class="goods-img"
            :src="orderInfo.idleItem.imgUrl"
            fit="cover"
            v-if="orderInfo.idleItem && orderInfo.idleItem.imgUrl"
          />
          <div class="goods-info">
            <div class="goods-title">{{ orderInfo.idleItem.idleName }}</div>
            <div class="goods-meta">
              <span class="goods-price">￥{{ orderInfo.orderPrice }}</span>
              <span class="goods-seller">
                <el-avatar
                  v-if="orderInfo.idleItem.user && orderInfo.idleItem.user.avatar"
                  :src="orderInfo.idleItem.user.avatar"
                  size="small"
                  style="margin-right:6px"
                />
                卖家：{{ orderInfo.idleItem.user && orderInfo.idleItem.user.nickname ? orderInfo.idleItem.user.nickname : orderInfo.idleItem.userId }}
              </span>
            </div>
          </div>
        </div>

        <!-- 订单状态进度条 -->
        <div class="card status-card">
          <el-steps :active="orderInfo.orderStatus" finish-status="success" align-center>
            <el-step title="待付款" icon="el-icon-wallet"></el-step>
            <el-step title="待发货" icon="el-icon-truck"></el-step>
            <el-step title="待收货" icon="el-icon-box"></el-step>
            <el-step title="已完成" icon="el-icon-success"></el-step>
          </el-steps>
          <div class="status-label">
            <el-tag
              :type="orderStatusTag.type"
              size="medium"
              effect="dark"
            >{{ orderStatusTag.text }}</el-tag>
          </div>
        </div>

        <!-- 收货信息 -->
        <div class="card info-card">
          <div class="info-title"><i class="el-icon-location-outline"></i> 收货信息</div>
          <div class="info-content">
            <div><i class="el-icon-user"></i> 收货人：{{ addressInfo.consigneeName }}</div>
            <div><i class="el-icon-phone"></i> 电话：{{ addressInfo.consigneePhone }}</div>
            <div><i class="el-icon-map-location"></i> 地址：{{ addressInfo.detailAddress }}</div>
          </div>
        </div>

        <!-- 订单信息 -->
        <div class="card info-card">
          <div class="info-title"><i class="el-icon-document"></i> 订单信息</div>
          <div class="info-content">
            <div>订单编号：{{ orderInfo.orderNumber }}</div>
            <div>支付状态：<span :class="orderInfo.paymentStatus===0?'text-gray':'text-green'">{{ orderInfo.paymentStatus === 0 ? '未支付' : '已支付' }}</span></div>
            <div>支付方式：{{ orderInfo.paymentWay || '-' }}</div>
            <div>创建时间：{{ formatTime(orderInfo.createTime) }}</div>
            <div v-if="orderInfo.paymentTime">支付时间：{{ formatTime(orderInfo.paymentTime) }}</div>
          </div>
        </div>
      </div>

      <!-- 操作按钮区，移动端吸底 -->
      <div class="order-action-bar" :class="{ 'mobile-action': isMobile }">
        <el-button v-if="userId==orderInfo.userId && orderInfo.orderStatus===0" type="danger" plain @click="changeOrderStatus(4,orderInfo)">取消订单</el-button>
        <el-button v-if="userId==orderInfo.userId && orderInfo.orderStatus===0" type="primary" @click="changeOrderStatus(1,orderInfo)">立即支付</el-button>
        <el-button v-if="userId==orderInfo.idleItem.userId && orderInfo.orderStatus===1" type="primary" @click="changeOrderStatus(2,orderInfo)">发货</el-button>
        <el-button v-if="userId==orderInfo.userId && orderInfo.orderStatus===2" type="success" @click="changeOrderStatus(3,orderInfo)">确认收货</el-button>
      </div>
      <app-foot />
    </app-body>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue';
import AppFoot from '../common/AppFoot.vue';

export default {
  name: "order",
  components: { AppHead, AppBody, AppFoot },
  data() {
    return {
      addressDialogVisible: false,
      addressData: [],
      orderInfo: {
        createTime: "",
        id: 0,
        idleId: 0,
        idleItem: {
          id: '',
          idleName: '',
          idleDetails: '',
          pictureList: [],
          idlePrice: 0,
          idlePlace: '',
          idleLabel: '',
          idleStatus: -1,
          userId: '',
          userNickname: '',
          userAvatar: '',
          imgUrl: ''
        },
        orderNumber: "",
        orderPrice: 0,
        orderStatus: 0,
        paymentStatus: 0,
        paymentTime: "",
        paymentWay: "",
        userId: 0
      },
      addressInfo: {
        id: '',
        update: false,
        consigneeName: '',
        consigneePhone: '',
        detailAddress: ''
      },
      userId: '',
      isMobile: false
    };
  },
  computed: {
    orderStatusTag() {
      const map = [
        { text: '待付款', type: 'info' },
        { text: '待发货', type: 'warning' },
        { text: '待收货', type: 'primary' },
        { text: '已完成', type: 'success' }
      ];
      return map[this.orderInfo.orderStatus] || { text: '未知', type: 'info' };
    }
  },
  mounted() {
    this.isMobile = window.innerWidth < 600;
    this.$nextTick(() => {
      this.userId = this.getCookie('shUserId');
      let orderId = this.$route.query.id;
      this.$api.getOrder({ id: orderId }).then(res => {
        if (res.status_code === 1) {
          if (res.data.idleItem) {
            let imgList = JSON.parse(res.data.idleItem.pictureList);
            if (imgList.length > 0) {
              res.data.idleItem.imgUrl = imgList[0];
            } else {
              res.data.idleItem.imgUrl = '';
            }
            // 假设后端已返回 userNickname/userAvatar，否则需补充
            res.data.idleItem.userNickname = res.data.idleItem.userNickname || '';
            res.data.idleItem.userAvatar = res.data.idleItem.userAvatar || '';
          } else {
            res.data.idleItem = { idleName: '', imgUrl: '' }
          }
          this.orderInfo = res.data;
          this.$api.getOrderAddress({ orderId: this.orderInfo.id }).then(res => {
            if (res.data) {
              this.addressInfo = res.data;
              this.addressInfo.update = true;
            } else {
              this.getAddressData();
            }
          })
        }
      })
    })
  },
  methods: {
    getCookie(cname) {
      var name = cname + "=";
      var ca = document.cookie.split(';');
      for (var i = 0; i < ca.length; i++) {
        var c = ca[i].trim();
        if (c.indexOf(name) === 0) return c.substring(name.length, c.length);
      }
      return "0";
    },
    getAddressData() {
      this.$api.getAddress().then(res => {
        if (res.status_code === 1) {
          let data = res.data;
          for (let i = 0; i < data.length; i++) {
            data[i].detailAddressText = data[i].provinceName + data[i].cityName + data[i].regionName + data[i].detailAddress;
          }
          this.addressData = data;
          if (!this.addressInfo.update) {
            for (let i = 0; i < data.length; i++) {
              if (data[i].defaultFlag) {
                this.selectAddress(i, data[i]);
              }
            }
          }
        }
      })
    },
    selectAddress(i, item) {
      this.addressDialogVisible = false;
      this.addressInfo.consigneeName = item.consigneeName;
      this.addressInfo.consigneePhone = item.consigneePhone;
      this.addressInfo.detailAddress = item.detailAddressText;
      if (this.addressInfo.update) {
        this.$api.updateOrderAddress({
          id: this.addressInfo.id,
          consigneeName: item.consigneeName,
          consigneePhone: item.consigneePhone,
          detailAddress: item.detailAddressText
        })
      } else {
        this.$api.addOrderAddress({
          orderId: this.orderInfo.id,
          consigneeName: item.consigneeName,
          consigneePhone: item.consigneePhone,
          detailAddress: item.detailAddressText
        }).then(res => {
          if (res.status_code === 1) {
            this.addressInfo.update = true;
            this.addressInfo.id = res.data.id;
          } else {
            this.$message.error(res.msg)
          }
        })
      }
    },
    changeOrderStatus(orderStatus, orderInfo) {
      if (orderStatus === 1) {
        if (!this.addressInfo.detailAddress) {
          this.$message.error('请选择地址！')
        } else {
          this.$confirm('确认支付订单吗？', '支付提示', {
            confirmButtonText: '确认支付',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            // 构建支付表单
            const form = document.createElement('form');
            form.method = 'get';
            form.action = 'http://localhost:8080/alipay/pay';  // 直接使用完整的后端URL
            form.target = '_blank';

            // 添加订单ID参数
            const orderIdsInput = document.createElement('input');
            orderIdsInput.type = 'hidden';
            orderIdsInput.name = 'orderIds';
            orderIdsInput.value = orderInfo.id;
            form.appendChild(orderIdsInput);

            // 添加表单到页面并提交
            document.body.appendChild(form);
            form.submit();
            document.body.removeChild(form);

            // 更新订单状态
            this.$api.updateOrder({
              id: orderInfo.id,
              orderStatus: orderStatus,
              paymentStatus: 1,
              paymentWay: '支付宝',
            }).then(res => {
              if (res.status_code === 1) {
                this.$message.success('订单支付已提交，请在新窗口完成支付');
                this.orderInfo.orderStatus = orderStatus;
                this.orderInfo.paymentStatus = 1;
                this.orderInfo.paymentWay = '支付宝';
                this.orderInfo.paymentTime = res.data.paymentTime;
              }
            }).catch(err => {
              console.error('更新订单状态失败:', err);
              this.$message.error('订单状态更新失败，请刷新页面重试');
            });
          }).catch(() => {
            this.$message.info('已取消支付');
          });
        }
      } else {
        this.$api.updateOrder({
          id: orderInfo.id,
          orderStatus: orderStatus,
        }).then(res => {
          if (res.status_code === 1) {
            this.$message.success('操作成功！');
            this.orderInfo.orderStatus = orderStatus;
          }
        })
      }
    },
    formatTime(time) {
      if (!time) return '';
      if (typeof time === 'string') return time.replace('T', ' ').substring(0, 19);
      const d = new Date(time);
      return d.getFullYear() + '-' + (d.getMonth() + 1).toString().padStart(2, '0') + '-' + d.getDate().toString().padStart(2, '0') + ' ' + d.getHours().toString().padStart(2, '0') + ':' + d.getMinutes().toString().padStart(2, '0') + ':' + d.getSeconds().toString().padStart(2, '0');
    }
  }
}
</script>

<style scoped>
.order-bg {
  min-height: 100vh;
  background: linear-gradient(120deg, #e3f0ff 0%, #f8fdff 100%);
}
.order-main {
  max-width: 700px;
  margin: 32px auto 0 auto;
  padding: 0 12px 80px 12px;
}
.card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(60,120,200,0.10);
  margin-bottom: 28px;
  padding: 24px 20px;
}
.goods-card {
  display: flex;
  align-items: center;
  gap: 24px;
}
.goods-img {
  width: 110px;
  height: 110px;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(60,120,200,0.10);
}
.goods-info {
  flex: 1;
}
.goods-title {
  font-size: 22px;
  font-weight: 700;
  color: #1a237e;
  margin-bottom: 10px;
}
.goods-meta {
  display: flex;
  align-items: center;
  gap: 18px;
}
.goods-price {
  font-size: 20px;
  color: #e74c3c;
  font-weight: 600;
}
.goods-seller {
  color: #78909c;
  font-size: 15px;
  display: flex;
  align-items: center;
}
.status-card {
  padding-bottom: 12px;
}
.status-label {
  margin-top: 18px;
  text-align: right;
}
.info-card {
  padding-bottom: 12px;
}
.info-title {
  font-size: 17px;
  font-weight: 600;
  color: #1976d2;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.info-content {
  color: #263238;
  font-size: 15px;
  line-height: 2.1;
  padding-left: 4px;
}
.text-gray { color: #b0b0b0; }
.text-green { color: #43a047; }
.order-action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin: 32px auto 0 auto;
  max-width: 700px;
  background: transparent;
}
.mobile-action {
  position: fixed;
  left: 0; right: 0; bottom: 0;
  background: #fff;
  box-shadow: 0 -2px 12px rgba(60,120,200,0.10);
  padding: 12px 10vw 12px 10vw;
  z-index: 99;
  border-radius: 18px 18px 0 0;
  margin: 0;
  width: 100vw;
  justify-content: center;
}
@media (max-width: 600px) {
  .order-main { padding: 0 2vw 80px 2vw; }
  .goods-card { flex-direction: column; align-items: flex-start; gap: 10px; }
  .goods-img { width: 80px; height: 80px; }
  .order-action-bar { max-width: 100vw; }
}
</style>