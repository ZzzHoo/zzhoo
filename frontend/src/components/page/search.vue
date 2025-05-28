<template>
  <div class="page-minimal-bg">
    <!-- 头部组件，带搜索输入回显 -->
    <app-head :searchInput="searchValue"></app-head>
    <app-body class="body-minimal">
      <div class="main-content">
        <!-- 搜索结果提示栏 -->
        <div class="tabs-minimal">
          <div class="search-indicator">
            <span class="search-tag">搜索结果:</span>
            <span class="search-query">{{searchValue}}</span>
            <span class="search-count">(共 {{totalItem}} 个)</span>
          </div>
        </div>
        
        <!-- 搜索结果商品列表 -->
        <div class="goods-grid" v-if="idleList.length > 0">
          <div class="goods-card-minimal" v-for="(idle,index) in idleList" :key="index" @click="toDetails(idle)">
            <div class="goods-img-wrap">
              <el-image :src="idle.imgUrl" fit="cover" class="goods-img-minimal">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline">无图</i>
                </div>
              </el-image>
              <div class="goods-price-badge">￥{{idle.idlePrice}}</div>
            </div>
            <div class="goods-title-minimal">{{idle.idleName}}</div>
            <div class="goods-seller-row">
              <el-image :src="idle.user && idle.user.avatar ? idle.user.avatar : require('@/img/默认头像头像.png')" fit="cover" class="goods-seller-avatar-minimal"></el-image>
              <span class="goods-seller-name-minimal">{{idle.user && idle.user.nickname ? idle.user.nickname : '未知用户'}}</span>
            </div>
            <div class="goods-time-badge">{{idle.timeStr}}</div>
          </div>
        </div>
        
        <!-- 无搜索结果时的提示 -->
        <div class="empty-result" v-else>
          <div class="empty-container">
            <i class="el-icon-search empty-icon"></i>
            <p class="empty-text">未找到"{{searchValue}}"相关商品</p>
            <el-button class="tab-btn active" @click="$router.push('/')">返回首页</el-button>
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
      </div>
      <app-foot></app-foot>
    </app-body>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue'
import AppFoot from '../common/AppFoot.vue'

export default {
  name: "search",
  components: {
    AppHead,
    AppBody,
    AppFoot
  },
  data() {
    return {
      idleList: [],      // 当前页展示的商品列表
      allGoodsList: [],  // 所有搜索到的商品
      currentPage: 1,    // 当前页码
      searchValue: '',   // 搜索关键词
      totalItem: 0,      // 搜索结果总数
      pageSize: 18       // 每页显示商品数
    };
  },
  created() {
    // 页面创建时根据路由参数进行搜索
    this.fetchGoods(1, this.$route.query.searchValue);
    this.searchValue = this.$route.query.searchValue;
  },
  watch: {
    // 监听路由变化，自动刷新搜索结果
    $route(to, from) {
      this.searchValue = to.query.searchValue;
      this.fetchGoods(to.query.page, to.query.searchValue);
    }
  },
  methods: {
    // 获取搜索结果
    fetchGoods(page, findValue) {
      const nums = this.pageSize * 2;
      const loading = this.$loading({
        lock: true,
        text: '加载数据中',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0)'
      });
      
      this.$api.findIdleTiem({
        page: 1,
        nums,
        findValue: findValue
      }).then(res => {
        let list = res.data.list;
        // 处理每个商品的图片和时间
        for (let i = 0; i < list.length; i++) {
          list[i].timeStr = list[i].releaseTime.substring(0, 10) + " " + list[i].releaseTime.substring(11, 19);
          let pictureList = JSON.parse(list[i].pictureList);
          list[i].imgUrl = pictureList.length > 0 ? pictureList[0] : '';
        }
        this.allGoodsList = list;
        this.totalItem = res.data.count;
        this.sliceGoods(page);
      }).catch(e => {
        console.log(e);
        this.$message.error('搜索失败，请重试');
      }).finally(() => {
        loading.close();
      });
    },
    // 分页切换
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
    // 分页组件切换页码
    handleCurrentChange(val) {
      this.$router.replace({query: {page: val, searchValue: this.searchValue}});
    },
    // 跳转到商品详情页
    toDetails(idle) {
      this.$router.push({path: '/details', query: {id: idle.id}});
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
  padding: 72px 0 0 0;
}
.tabs-minimal {
  display: flex;
  gap: 18px;
  margin-bottom: 32px;
  justify-content: center;
}
.search-indicator {
  background: #f4f8fb;
  padding: 10px 32px;
  border-radius: 22px;
  display: inline-flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}
.search-tag {
  color: #1a237e;
  font-size: 16px;
  margin-right: 8px;
}
.search-query {
  color: #26c6da;
  font-size: 18px;
  font-weight: 600;
  margin-right: 8px;
}
.search-count {
  color: #78909c;
  font-size: 14px;
}
.tab-btn {
  border: none;
  outline: none;
  background: #f4f8fb;
  color: #1a237e;
  font-size: 18px;
  font-weight: 500;
  border-radius: 22px;
  padding: 8px 32px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.tab-btn.active, .tab-btn:hover {
  background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
  color: #fff;
}
.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 32px 28px;
  width: 100%;
  margin-bottom: 40px;
}
.goods-card-minimal {
  background: rgba(255,255,255,0.95);
  border-radius: 28px;
  box-shadow: 0 6px 32px rgba(60,120,200,0.10);
  padding: 0 0 18px 0;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  cursor: pointer;
  transition: box-shadow 0.22s, transform 0.18s;
  position: relative;
  min-height: 320px;
  overflow: hidden;
  box-sizing: border-box;
}
.goods-card-minimal:hover {
  box-shadow: 0 12px 40px rgba(60,120,200,0.18);
  transform: translateY(-6px) scale(1.03);
}
.goods-img-wrap {
  width: 100%;
  aspect-ratio: 1.2/1;
  border-radius: 28px 28px 0 0;
  overflow: hidden;
  position: relative;
  background: #e3f0ff;
}
.goods-img-minimal {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.goods-price-badge {
  position: absolute;
  left: 18px;
  top: 18px;
  background: linear-gradient(90deg, #42a5f5 0%, #26c6da 100%);
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  border-radius: 16px;
  padding: 4px 18px;
  box-shadow: 0 2px 8px rgba(60,120,200,0.10);
}
.goods-time-badge {
  font-size: 12px;
  color: #90a4ae;
  margin: 6px 18px 0;
  align-self: flex-end;
}
.goods-title-minimal {
  font-size: 20px;
  font-weight: 700;
  color: #222;
  margin: 18px 0 10px 0;
  padding: 0 18px;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  width: 100%;
}
.goods-seller-row {
  display: flex;
  align-items: center;
  margin: 8px 0 0 18px;
  width: calc(100% - 36px);
}
.goods-seller-avatar-minimal {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 10px;
  border: 2px solid #e3f0ff;
}
.goods-seller-name-minimal {
  font-size: 16px;
  color: #1a237e;
  font-weight: 600;
  max-width: 120px;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.empty-result {
  width: 100%;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
}
.empty-container {
  background: rgba(255,255,255,0.95);
  border-radius: 28px;
  box-shadow: 0 6px 32px rgba(60,120,200,0.10);
  padding: 60px 80px;
  text-align: center;
}
.empty-icon {
  font-size: 64px;
  color: #b0bec5;
  margin-bottom: 24px;
}
.empty-text {
  font-size: 22px;
  color: #78909c;
  margin-bottom: 32px;
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
</style>