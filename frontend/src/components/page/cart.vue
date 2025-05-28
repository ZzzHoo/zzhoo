/*
 * 购物车页面
 *   - 展示当前用户的购物车商品列表
 *   - 支持分页浏览
 *   - 支持移除购物车商品
 *   - 支持点击商品卡片跳转到商品详情页
 *   - 页面风格与首页、个人中心一致，极简蓝绿渐变风格
 */
<template>
  <div class="page-minimal-bg">
    <!-- 页面头部组件 -->
    <app-head></app-head>
    <app-body class="body-minimal">
      <div class="main-content">
        <div class="page-title-container">
          <h2 class="page-title">我的购物车</h2>
          <!-- 显示购物车商品总数 -->
          <span class="item-count" v-if="idleList.length > 0">共 {{totalItem}} 件商品</span>
        </div>
        
        <!-- 批量操作工具栏，仅在有商品时显示 -->
        <div class="batch-toolbar" v-if="idleList.length > 0">
          <div class="select-all">
            <!-- 全选复选框 -->
            <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
            <span class="selected-count" v-if="selectedItems.length > 0">
              已选择 {{selectedItems.length}} 件商品
            </span>
          </div>
          <div class="batch-actions">
            <!-- 显示已选商品总价 -->
            <div class="total-price" v-if="selectedItems.length > 0">
              合计: <span class="price">￥{{totalPrice}}</span>
            </div>
            <!-- 批量删除按钮 -->
            <el-button 
              type="danger" 
              size="small" 
              :disabled="selectedItems.length === 0"
              @click="batchRemove"
              class="batch-btn delete-btn">
              批量删除
            </el-button>
            <!-- 批量支付按钮 -->
            <el-button 
              type="primary" 
              size="small" 
              :disabled="selectedItems.length === 0"
              @click="batchPay"
              class="batch-btn pay-btn">
              批量支付
            </el-button>
          </div>
        </div>

        <!-- 商品列表区域 -->
        <div class="goods-grid" v-if="idleList.length > 0">
          <div class="goods-card-minimal" v-for="(idle,index) in idleList" :key="index">
            <!-- 商品选择框 -->
            <el-checkbox 
              v-model="idle.selected" 
              @change="handleItemSelect"
              class="item-checkbox">
            </el-checkbox>
            <!-- 商品图片，点击可跳转详情 -->
            <div class="goods-img-wrap" @click="toDetails(idle)">
              <el-image :src="idle.imgUrl" fit="cover" class="goods-img-minimal">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline">无图</i>
                </div>
              </el-image>
              <div class="goods-price-badge">￥{{idle.idlePrice}}</div>
            </div>
            <!-- 商品名称，点击可跳转详情 -->
            <div class="goods-title-minimal" @click="toDetails(idle)">{{idle.idleName}}</div>
            <!-- 卖家信息 -->
            <div class="goods-seller-row">
              <el-image :src="idle.user && idle.user.avatar" fit="cover" class="goods-seller-avatar-minimal"></el-image>
              <span class="goods-seller-name-minimal">{{idle.user && idle.user.nickname}}</span>
            </div>
          </div>
        </div>

        <!-- 空购物车时的提示 -->
        <div class="cart-empty-container" v-else>
          <div class="cart-empty-content">
            <div class="empty-cart-icon">
              <i class="el-icon-shopping-cart-2"></i>
            </div>
            <h3 class="empty-cart-title">购物车还是空的哦~</h3>
            <p class="empty-cart-desc">快去挑选心仪的商品吧！</p>
            <el-button type="primary" class="go-shopping-btn" @click="$router.push('/')">
              <i class="el-icon-shopping-bag-1"></i>
              去逛逛
            </el-button>
          </div>
        </div>
        
        <!-- 分页组件 -->
        <div class="fenye fenye-minimal" v-if="idleList.length > 0">
          <el-pagination
              background
              @current-change="handleCurrentChange"
              :current-page.sync="currentPage"
              :page-size="18"
              layout="prev, pager, next, jumper"
              :total="totalItem">
          </el-pagination>
        </div>

        <!-- 选择收货地址弹窗 -->
        <el-dialog
          title="选择收货地址"
          :visible.sync="addressDialogVisible"
          width="500px"
          class="address-dialog">
          <div class="address-list" v-if="addressList.length > 0">
            <div 
              v-for="(address, index) in addressList" 
              :key="index"
              class="address-item"
              :class="{'selected': selectedAddressId === address.id}"
              @click="selectAddress(address)">
              <div class="address-info">
                <div class="address-header">
                  <span class="name">{{address.consigneeName}}</span>
                  <span class="phone">{{address.consigneePhone}}</span>
                  <span class="default-tag" v-if="address.defaultFlag">默认</span>
                </div>
                <div class="address-detail">
                  {{address.provinceName}}{{address.cityName}}{{address.regionName}}{{address.detailAddress}}
                </div>
              </div>
              <div class="address-check">
                <i class="el-icon-check" v-if="selectedAddressId === address.id"></i>
              </div>
            </div>
          </div>
          <div class="no-address" v-else>
            <i class="el-icon-location-information"></i>
            <p>暂无收货地址，请先添加</p>
            <el-button type="primary" @click="goToAddAddress">去添加</el-button>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button @click="addressDialogVisible = false">取消</el-button>
            <el-button 
              type="primary" 
              @click="confirmOrder"
              :disabled="!selectedAddressId">
              确认下单
            </el-button>
          </span>
        </el-dialog>
      </div>
      <!-- 页面底部组件 -->
      <app-foot></app-foot>
    </app-body>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue'
import AppFoot from '../common/AppFoot.vue'

export default {
  name: "cart",
  components: {
    AppHead,
    AppBody,
    AppFoot
  },
  data() {
    return {
      idleList: [], // 当前页购物车商品列表
      allGoodsList: [], // 所有商品列表（如有需要）
      currentPage: 1, // 当前页码
      totalItem: 1, // 商品总数
      pageSize: 18, // 每页显示商品数
      selectAll: false, // 全选状态
      selectedItems: [], // 已选中的商品
      addressDialogVisible: false, // 收货地址弹窗显示状态
      addressList: [], // 收货地址列表
      selectedAddressId: null // 当前选中的收货地址ID
    };
  },
  computed: {
    // 计算已选商品总价
    totalPrice() {
      return this.selectedItems.reduce((total, item) => {
        return total + Number(item.idlePrice);
      }, 0).toFixed(2);
    }
  },
  watch: {
    // 监听购物车商品列表变化，自动同步已选商品和全选状态
    idleList: {
      handler(newList) {
        this.selectedItems = newList.filter(item => item.selected);
        this.selectAll = newList.length > 0 && newList.every(item => item.selected);
      },
      deep: true
    }
  },
  created() {
    // 组件创建时加载购物车商品
    this.fetchCartGoods(1);
  },
  methods: {
    // 获取购物车商品数据
    fetchCartGoods(page) {
      const nums = this.pageSize * 2;
      this.$api.getMyFavorite().then(res => {
        console.log('购物车接口返回', res);
        if (res.status_code === 1) {
          let list = res.data
            .map(item => {
              let idle = item.idleItem;
              idle.favoriteId = item.id;
              idle.selected = false; // 添加选择状态
              if (!idle.user) {
                idle.user = { avatar: '', nickname: '未知用户' };
              }
              let pictureList = [];
              try {
                pictureList = JSON.parse(idle.pictureList);
              } catch (e) {}
              idle.imgUrl = pictureList.length > 0 ? pictureList[0] : '';
              return idle;
            })
            .filter(idle => idle.idleStatus === 1);
          this.allGoodsList = list;
          this.totalItem = list.length;
          this.sliceGoods(page);
        }
      }).catch(e => {
        console.log(e)
      })
    },
    sliceGoods(page) {
      const start = (page - 1) * this.pageSize;
      const end = start + this.pageSize;
      let pageGoods = this.allGoodsList.slice(start, end);
      if (pageGoods.length < this.pageSize && end < this.allGoodsList.length) {
        let need = this.pageSize - pageGoods.length;
        pageGoods = pageGoods.concat(this.allGoodsList.slice(end, end + need));
      }
      this.idleList = pageGoods;
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchCartGoods(val);
    },
    toDetails(idle) {
      this.$router.push({path: '/details', query: {id: idle.id}});
    },
    removeFromCart(idle, index) {
      this.$api.deleteFavorite(idle.favoriteId).then(res => {
        if (res.status_code === 1) {
          this.idleList.splice(index, 1);
          this.allGoodsList = this.allGoodsList.filter(item => item.favoriteId !== idle.favoriteId);
          this.totalItem--;
          this.$message.success('已移除该商品');
        } else {
          this.$message.error('移除失败');
        }
      }).catch(() => {
        this.$message.error('网络异常，移除失败');
      });
    },
    // 处理全选操作
    handleSelectAll(val) {
      this.idleList.forEach(item => {
        item.selected = val;
      });
    },
    // 处理单个商品选择
    handleItemSelect() {
      this.selectAll = this.idleList.length > 0 && this.idleList.every(item => item.selected);
    },
    // 批量删除已选商品
    batchRemove() {
      if (this.selectedItems.length === 0) return;
      
      this.$confirm('确定要删除选中的商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const promises = this.selectedItems.map(item => 
          this.$api.deleteFavorite(item.favoriteId)
        );
        
        Promise.all(promises).then(results => {
          const successCount = results.filter(res => res.status_code === 1).length;
          
          if (successCount > 0) {
            // 更新列表
            this.allGoodsList = this.allGoodsList.filter(item => !item.selected);
            this.totalItem = this.allGoodsList.length;
            this.sliceGoods(this.currentPage);
            
            this.$message.success(`成功删除${successCount}件商品`);
          }
          
          if (successCount < this.selectedItems.length) {
            this.$message.warning('部分商品删除失败，请重试');
          }
        }).catch(() => {
          this.$message.error('网络异常，删除失败');
        });
      }).catch(() => {});
    },
    // 批量支付已选商品
    async batchPay() {
      if (this.selectedItems.length === 0) return;
      
      // 获取收货地址列表
      try {
        const res = await this.$api.getAddress();
        if (res.status_code === 1) {
          this.addressList = res.data;
          // 默认选中默认地址
          const defaultAddress = this.addressList.find(addr => addr.defaultFlag);
          if (defaultAddress) {
            this.selectedAddressId = defaultAddress.id;
          }
          this.addressDialogVisible = true;
        } else {
          this.$message.error('获取地址列表失败');
        }
      } catch (error) {
        this.$message.error('网络异常，请稍后重试');
      }
    },
    // 选择收货地址
    selectAddress(address) {
      this.selectedAddressId = address.id;
    },
    // 跳转到添加地址页面
    goToAddAddress() {
      this.addressDialogVisible = false;
      this.$router.push('/me');
      // 通知用户跳转到个人中心添加地址
      this.$message({
        message: '请在个人中心添加收货地址',
        type: 'info'
      });
    },
    // 确认下单
    async confirmOrder() {
      if (!this.selectedAddressId) {
        this.$message.warning('请选择收货地址');
        return;
      }

      try {
        // 显示加载提示
        const loading = this.$loading({
          lock: true,
          text: '订单处理中...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });

        // 创建订单
        const orderPromises = this.selectedItems.map(item => {
          return this.$api.addOrder({
            idleId: item.id,
            addressId: this.selectedAddressId,
            orderPrice: item.idlePrice
          });
        });

        const results = await Promise.all(orderPromises);
        const successOrders = results.filter(res => res.status_code === 1);

        if (successOrders.length > 0) {
          // 获取成功创建的订单ID列表
          const orderIds = successOrders.map(res => res.data.id);

          try {
            // 先更新购物车列表
            this.allGoodsList = this.allGoodsList.filter(item => !item.selected);
            this.totalItem = this.allGoodsList.length;
            this.sliceGoods(this.currentPage);
            
            // 关闭地址选择弹窗
            this.addressDialogVisible = false;
            
            // 关闭加载提示
            loading.close();

            // 构建支付表单
            const form = document.createElement('form');
            form.method = 'get';
            form.action = 'http://localhost:8080/alipay/pay';  // 直接使用完整的后端URL
            form.target = '_blank';

            // 添加订单ID参数
            const orderIdsInput = document.createElement('input');
            orderIdsInput.type = 'hidden';
            orderIdsInput.name = 'orderIds';
            orderIdsInput.value = orderIds.join(',');
            form.appendChild(orderIdsInput);

            // 添加表单到页面并提交
            document.body.appendChild(form);
            form.submit();
            document.body.removeChild(form);

            this.$message.success('订单创建成功，正在跳转支付页面');

          } catch (error) {
            console.error('支付处理失败:', error);
            this.$message.error('支付处理失败，请到订单中心重新支付');
            
            // 跳转到订单中心
            this.$router.push({
              path: '/me',
              query: { 
                activeName: '5' // 切换到购买记录标签
              }
            });
          }
        }

        if (successOrders.length < this.selectedItems.length) {
          this.$message.warning('部分商品下单失败，请重试');
        }

      } catch (error) {
        console.error('下单失败:', error);
        this.$message.error('网络异常，下单失败');
        if (loading) loading.close();
      }
    }
  }
}
</script>

<style scoped>
.page-minimal-bg {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #e3f2fd 0%, #f5f5f5 100%);
  margin: 0;
  padding: 0;
}
.body-minimal {
  width: 100%;
  padding: 0;
  margin: 0;
  background: transparent;
}
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 72px 20px 0;
  animation: fadeIn 0.5s ease-out;
}
.page-title-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
  padding-bottom: 15px;
  border-bottom: 2px solid rgba(33, 150, 243, 0.1);
}
.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #1a237e;
  margin: 0;
  position: relative;
  padding-left: 15px;
}
.page-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(to bottom, #2196f3, #00bcd4);
  border-radius: 2px;
}
.item-count {
  font-size: 15px;
  color: #78909c;
  font-weight: 500;
  background: rgba(236, 239, 241, 0.5);
  padding: 8px 16px;
  border-radius: 20px;
}
.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 25px;
  margin-bottom: 40px;
}
.goods-card-minimal {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  padding: 0 0 15px 0;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 280px;
}
.goods-card-minimal:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}
.goods-img-wrap {
  width: 100%;
  aspect-ratio: 1.2/1;
  position: relative;
  overflow: hidden;
}
.goods-img-minimal {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}
.goods-card-minimal:hover .goods-img-minimal {
  transform: scale(1.08);
}
.goods-price-badge {
  position: absolute;
  left: 12px;
  top: 12px;
  background: linear-gradient(45deg, #2196f3, #00bcd4);
  color: white;
  font-size: 16px;
  font-weight: 700;
  padding: 6px 16px;
  border-radius: 20px;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.3);
  z-index: 2;
}
.goods-title-minimal {
  font-size: 16px;
  font-weight: 600;
  color: #1a237e;
  margin: 15px 15px 10px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.goods-seller-row {
  display: flex;
  align-items: center;
  margin: 8px 15px;
  padding: 8px 12px;
  background: rgba(236, 239, 241, 0.5);
  border-radius: 10px;
}
.goods-seller-avatar-minimal {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 10px;
  border: 2px solid #e3f2fd;
}
.goods-seller-name-minimal {
  font-size: 14px;
  color: #455a64;
  font-weight: 500;
  max-width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.item-checkbox {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 2;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  padding: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}
.item-checkbox:hover {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 0.95);
}
.cart-empty-container {
  width: 100%;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
  margin: 20px 0;
  animation: fadeIn 0.5s ease-out;
}
.cart-empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  text-align: center;
}
.empty-cart-icon {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  animation: pulse 2s ease-in-out infinite;
}
.empty-cart-icon i {
  font-size: 60px;
  color: #2196f3;
}
.empty-cart-title {
  font-size: 24px;
  color: #1a237e;
  font-weight: 600;
  margin-bottom: 15px;
}
.empty-cart-desc {
  font-size: 16px;
  color: #78909c;
  margin-bottom: 30px;
}
.go-shopping-btn {
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 25px;
  background: linear-gradient(45deg, #2196f3, #00bcd4);
  border: none;
  color: white;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}
.go-shopping-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(33, 150, 243, 0.3);
}
.go-shopping-btn i {
  font-size: 20px;
}
@keyframes pulse {
  0% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(33, 150, 243, 0.4);
  }
  70% {
    transform: scale(1.05);
    box-shadow: 0 0 0 15px rgba(33, 150, 243, 0);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(33, 150, 243, 0);
  }
}
.fenye-minimal {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 50%;
  background: #f4f8fb;
  color: #26c6da;
  font-weight: 600;
  margin: 0 4px;
  transition: background 0.2s, color 0.2s;
}
:deep(.el-pagination.is-background .el-pager li.active) {
  background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
  color: #fff;
}
:deep(.el-pagination.is-background .el-pager li:hover) {
  background: #26c6da;
  color: #fff;
}
.batch-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 25px;
  margin-bottom: 30px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  position: sticky;
  top: 72px;
  z-index: 10;
}
.batch-toolbar:hover {
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.12);
}
.select-all {
  display: flex;
  align-items: center;
  gap: 15px;
}
.selected-count {
  color: #1a237e;
  font-size: 15px;
  font-weight: 500;
  background: rgba(33, 150, 243, 0.1);
  padding: 6px 12px;
  border-radius: 20px;
}
.batch-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}
.total-price {
  font-size: 15px;
  color: #455a64;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
}
.total-price .price {
  font-size: 22px;
  font-weight: 700;
  color: #f44336;
  min-width: 80px;
  text-align: right;
}
.batch-btn {
  border: none;
  padding: 10px 24px;
  border-radius: 25px;
  font-weight: 600;
  font-size: 15px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}
.batch-btn i {
  font-size: 18px;
}
.batch-btn.delete-btn {
  background: linear-gradient(45deg, #ff5252, #f44336);
  color: white;
}
.batch-btn.pay-btn {
  background: linear-gradient(45deg, #2196f3, #00bcd4);
  color: white;
}
.batch-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}
.batch-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
.address-dialog {
  border-radius: 20px;
  overflow: hidden;
}
.address-list {
  max-height: 450px;
  overflow-y: auto;
  padding: 10px;
}
.address-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 15px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8f9fa;
  border: 2px solid transparent;
}
.address-item:hover {
  background: #e3f2fd;
  transform: translateY(-2px);
}
.address-item.selected {
  background: #e3f2fd;
  border-color: #2196f3;
}
.address-info {
  flex: 1;
}
.address-header {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}
.address-header .name {
  font-size: 17px;
  font-weight: 600;
  color: #1a237e;
}
.address-header .phone {
  color: #546e7a;
  font-size: 15px;
}
.address-header .default-tag {
  background: #fff3e0;
  color: #ff9800;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}
.address-detail {
  color: #607d8b;
  font-size: 15px;
  line-height: 1.5;
}
.address-check {
  width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.address-check i {
  color: #2196f3;
  font-size: 24px;
  transition: transform 0.3s ease;
}
.address-item.selected .address-check i {
  transform: scale(1.2);
}
.no-address {
  text-align: center;
  padding: 50px 0;
}
.no-address i {
  font-size: 64px;
  color: #b0bec5;
  margin-bottom: 20px;
}
.no-address p {
  color: #78909c;
  font-size: 18px;
  margin-bottom: 25px;
}
@media screen and (max-width: 768px) {
  .main-content {
    padding: 72px 15px 0;
  }
  .batch-toolbar {
    padding: 12px 15px;
    flex-direction: column;
    gap: 12px;
    top: 60px;
  }
  .select-all {
    width: 100%;
    justify-content: space-between;
  }
  .batch-actions {
    width: 100%;
    flex-wrap: wrap;
    gap: 10px;
  }
  .total-price {
    width: 100%;
    justify-content: space-between;
  }
  .batch-btn {
    flex: 1;
    padding: 8px 15px;
    font-size: 14px;
    justify-content: center;
  }
  .item-checkbox {
    padding: 6px;
  }
  .goods-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 15px;
  }
  .goods-card-minimal {
    min-height: 240px;
  }
  .goods-title-minimal {
    font-size: 14px;
    margin: 12px 12px 8px;
  }
  .goods-price-badge {
    font-size: 14px;
    padding: 4px 12px;
  }
  .goods-seller-row {
    margin: 6px 12px;
    padding: 6px 10px;
  }
  .goods-seller-avatar-minimal {
    width: 28px;
    height: 28px;
  }
  .goods-seller-name-minimal {
    font-size: 12px;
  }
  .cart-empty-container {
    min-height: 300px;
  }
  .empty-cart-icon {
    width: 90px;
    height: 90px;
    margin-bottom: 20px;
  }
  .empty-cart-icon i {
    font-size: 45px;
  }
  .empty-cart-title {
    font-size: 20px;
  }
  .empty-cart-desc {
    font-size: 14px;
    margin-bottom: 25px;
  }
  .go-shopping-btn {
    padding: 10px 25px;
    font-size: 14px;
  }
  
  .page-title-container {
    margin-bottom: 20px;
    padding-bottom: 12px;
  }

  .page-title {
    font-size: 22px;
  }

  .page-title::before {
    height: 20px;
  }

  .item-count {
    font-size: 14px;
    padding: 6px 12px;
  }
}
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.goods-card-minimal {
  animation: slideIn 0.5s ease-out forwards;
  animation-delay: calc(var(--index) * 0.1s);
}
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style> 