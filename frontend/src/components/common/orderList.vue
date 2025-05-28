/*管理员界面
 * 管理平台订单
 * 主要功能：
 *   - 分页展示订单列表
 *   - 支持按订单号搜索
 *   - 显示订单状态和支付状态
 *   - 支持删除订单操作
 */
<template>
  <div class="order-list-bg">
    <div class="order-list-main">
      <div class="order-list-header">
        <el-input placeholder="订单编号..." v-model="searchValue" @keyup.enter.native="searchIdle" class="order-search-input">
          <el-button slot="append" icon="el-icon-search" @click="searchIdle"></el-button>
        </el-input>
      </div>
      <el-card class="order-table-card">
        <el-table
          :data="Order"
          stripe
          style="width: 100%;color: #5a5c61;"
          class="order-table-minimal">
          <el-table-column prop="orderNumber" label="订单号" show-overflow-tooltip width="200"></el-table-column>
          <el-table-column prop="idleItem.idleName" label="闲置名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="orderPrice" label="金额" show-overflow-tooltip min-width="100" width="100"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" show-overflow-tooltip min-width="150" width="200"></el-table-column>
          <el-table-column label="订单状态" width="100" show-overflow-tooltip>
            <template slot-scope="scope">
              <div :class="'order-status-tag status-'+scope.row.orderStatus">{{orderStatus[scope.row.orderStatus]}}</div>
            </template>
          </el-table-column>
          <el-table-column label="支付状态" width="100" show-overflow-tooltip class="cell el-tooltip">
            <template slot-scope="scope">
              <div>{{paymentStatus[scope.row.paymentStatus]}}</div>
            </template>
          </el-table-column>
          <el-table-column prop="paymentWay" label="支付方式" width="100" show-overflow-tooltip></el-table-column>
          <el-table-column prop="paymentTime" label="支付时间" show-overflow-tooltip></el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button size="mini" type="danger" @click="deleteOrder(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      <div class="block order-pagination">
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
  </div>
</template>

<script>
    export default {
        name: "orderList",
        created() {
            this.getOrder();
        },
        methods:{
            getOrder(){
                this.$api.getOrderList({
                    page: this.nowPage,
                    nums:8
                }).then(res => {
                    if(res.status_code==1){
                        this.Order = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }

                }).catch(e => {
                    console.log(e)
                })
            },
            deleteOrder(index){
                this.$api.deleteOrder({
                    id:this.Order[index].id
                }).then(res=>{
                    if(res.status_code==1){
                        this.getOrder();
                    }else {
                        this.$message.error(res.msg)
                    }

                }).catch(e => {
                    console.log(e)
                })
            },
            handleCurrentChange(val) {
                this.nowPage = val;
                this.getOrder();
            },
            searchIdle(){
                this.$api.queryOrder({
                    page: this.nowPage,
                    nums: 8,
                    searchValue: this.searchValue
                }).then(res => {
                    console.log(res);
                    if (res.status_code == 1) {
                        this.Order = res.data.list;
                        this.total = res.data.count;
                    } else{
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            }
        },
        data(){
            return {
                mode:1,
                nowPage: 1,
                total: 0,
                paymentStatus:['未支付','已支付'],
                orderStatus:['待付款','待发货','待收货','已完成','已取消'],
                Order: [],
                searchValue: ''
            }
        },
    }
</script>

<style scoped>
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
.order-pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>