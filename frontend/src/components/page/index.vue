<template>
  <div class="page-minimal-bg">
    <app-head></app-head>
    <div class="carousel-wrapper">
      <el-carousel :interval="3000" arrow="always" height="240px">
        <el-carousel-item v-for="i in 4" :key="i">
          <img :src="require(`@/img/btu${i}.jpg`)" class="carousel-img" />
        </el-carousel-item>
      </el-carousel>
    </div>
    <app-body class="body-minimal">
      <div class="main-content">
        <div class="tabs-minimal">
          <button v-for="tab in tabs" :key="tab.name" :class="['tab-btn', labelName===tab.name?'active':'']" @click="switchTab(tab.name)">{{tab.label}}</button>
        </div>
        <div v-if="!loading && idleList.length === 0" class="empty-state">
          <i class="el-icon-goods"></i>
          <p class="empty-text">暂无相关商品</p>
          <p class="empty-desc">该分类下还没有商品，请浏览其他分类</p>
        </div>
        <div v-else class="goods-grid">
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
              <el-image :src="idle.user.avatar" fit="cover" class="goods-seller-avatar-minimal"></el-image>
              <span class="goods-seller-name-minimal">{{idle.user.nickname}}</span>
            </div>
           
          </div>
        </div>
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
  name: "index",
  components: {
    AppHead,
    AppBody,
    AppFoot
  },
  data() {
    return {
      loading: false,
      labelName: '0',
      idleList: [],
      allGoodsList: [],
      currentPage: 1,
      totalItem: 1,
      pageSize: 18,
      tabs: [
        {label: '全部', name: '0'},
        {label: '数码产品', name: '1'},
        {label: '生活用品', name: '2'},
        {label: '运动相关', name: '3'},
        {label: '图书笔记', name: '4'},
        {label: '求购信息', name: '5'}
      ]
    };
  },
  created() {
    this.fetchGoods(1);
  },
  watch:{
    $route(to,from){
      this.labelName=to.query.labelName;
      let val=parseInt(to.query.page)?parseInt(to.query.page):1;
      this.currentPage=parseInt(to.query.page)?parseInt(to.query.page):1;
      this.fetchGoods(val);
    }
  },
  methods: {
    fetchGoods(page) {
      this.loading = true;
      const nums = this.pageSize * 2;
      const loading = this.$loading({
        lock: true,
        text: '加载数据中',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0)'
      });
      const apiFn = this.labelName > 0 ? this.$api.findIdleTiemByLable : this.$api.findIdleTiem;
      const params = this.labelName > 0 ? {
        idleLabel: this.labelName,
        page: 1,
        nums
      } : {
        page: 1,
        nums
      };
      apiFn(params).then(res => {
        let list = res.data.list;
        for (let i = 0; i < list.length; i++) {
          let pictureList = JSON.parse(list[i].pictureList);
          list[i].imgUrl = pictureList.length > 0 ? pictureList[0] : '';
        }
        this.allGoodsList = list;
        this.totalItem = res.data.count;
        this.sliceGoods(page);
      }).catch(e => {
        console.log(e);
        this.$message.error('加载失败，请重试');
      }).finally(() => {
        loading.close();
        this.loading = false;
      });
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
    handleClick(tab, event) {
      this.$router.replace({query: {page: 1,labelName:this.labelName}});
    },
    handleCurrentChange(val) {
      this.$router.replace({query: {page: val,labelName:this.labelName}});
    },
    toDetails(idle) {
      this.$router.push({path: '/details', query: {id: idle.id}});
    },
    switchTab(tabName) {
      this.labelName = tabName;
      this.$router.replace({query: {page: 1, labelName: tabName}});
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
  overflow-x: hidden;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.body-minimal {
  width: 100%;
  padding: 0;
  margin: 0;
  background: transparent;
  box-sizing: border-box;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.main-content {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 72px 0 0 0;
  box-sizing: border-box;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.tabs-minimal {
  width: 100%;
  display: flex;
  gap: 18px;
  margin-bottom: 32px;
  justify-content: center;
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
  grid-template-columns: repeat(4, 1fr);
  gap: 32px 28px;
  width: 100%;
  margin-bottom: 40px;
  box-sizing: border-box;
}
.goods-card-minimal {
  width: 100%;
  height: 360px;
  box-sizing: border-box;
  background: rgba(255,255,255,0.95);
  border-radius: 28px;
  box-shadow: 0 6px 32px rgba(60,120,200,0.10);
  padding: 0 0 18px 0;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  cursor: pointer;
  transition: box-shadow 0.22s, transform 0.18s;
  position: relative;
  overflow: hidden;
}
.goods-card-minimal:hover {
  box-shadow: 0 12px 40px rgba(60,120,200,0.18);
  transform: translateY(-6px) scale(1.03);
}
.goods-img-wrap {
  width: 100%;
  height: 240px;
  border-radius: 28px 28px 0 0;
  overflow: hidden;
  position: relative;
  background: #e3f0ff;
  flex-shrink: 0;
}
.goods-img-minimal {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}
.image-slot {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
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
  z-index: 1;
}
.goods-title-minimal {
  font-size: 16px;
  font-weight: 700;
  color: #222;
  margin: 12px 0 8px 0;
  padding: 0 18px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}
.goods-seller-row {
  display: flex;
  align-items: center;
  padding: 0 18px;
  margin-top: auto;
  height: 40px;
}
.goods-seller-avatar-minimal {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  margin-right: 8px;
  border: 2px solid #e3f0ff;
  flex-shrink: 0;
}
.goods-seller-name-minimal {
  font-size: 14px;
  color: #1a237e;
  font-weight: 600;
  max-width: 120px;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
.carousel-wrapper {
  width: 90%;
  max-width: 1200px;
  margin: 56px auto 32px auto;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(60,120,200,0.10);
  box-sizing: border-box;
}
.carousel-img {
  width: 100%;
  height: 240px;
  object-fit: cover;
  border-radius: 18px;
}
@media screen and (max-width: 1400px) {
  .main-content {
    max-width: 1000px;
  }
  .goods-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media screen and (max-width: 1100px) {
  .main-content {
    max-width: 800px;
  }
  .goods-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media screen and (max-width: 768px) {
  .main-content {
    width: 95%;
  }
  .goods-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px 16px;
  }
  .tabs-minimal {
    gap: 12px;
  }
  .tab-btn {
    padding: 6px 20px;
    font-size: 14px;
  }
  
  .goods-card-minimal {
    height: 280px;
    border-radius: 20px;
  }
  
  .goods-img-wrap {
    height: 180px;
    border-radius: 20px 20px 0 0;
  }
  
  .goods-title-minimal {
    font-size: 14px;
    margin: 10px 0 6px 0;
    padding: 0 12px;
    -webkit-line-clamp: 2;
  }
  
  .goods-price-badge {
    font-size: 14px;
    padding: 3px 12px;
    left: 12px;
    top: 12px;
  }
  
  .goods-seller-row {
    padding: 0 12px;
    height: 32px;
  }
  
  .goods-seller-avatar-minimal {
    width: 24px;
    height: 24px;
  }
  
  .goods-seller-name-minimal {
    font-size: 12px;
  }
}
@media screen and (max-width: 480px) {
  .main-content {
    width: 92%;
  }
  .goods-grid {
    grid-template-columns: repeat(1, 1fr);
    gap: 16px;
  }
  
  .goods-card-minimal {
    height: 320px;
  }
  
  .goods-img-wrap {
    height: 200px;
  }
  
  .tabs-minimal {
    flex-wrap: wrap;
    justify-content: center;
    gap: 8px;
  }
  
  .tab-btn {
    padding: 4px 16px;
    font-size: 13px;
  }
}
.empty-state {
  width: 100%;
  padding: 60px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 28px;
  box-shadow: 0 6px 32px rgba(60,120,200,0.10);
}

.empty-state i {
  font-size: 64px;
  color: #b0bec5;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 20px;
  color: #455a64;
  font-weight: 600;
  margin-bottom: 8px;
}

.empty-desc {
  font-size: 16px;
  color: #90a4ae;
  margin-top: 0;
}
</style>
