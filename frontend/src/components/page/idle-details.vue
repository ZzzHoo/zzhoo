/*
 * 商品详情组件 (idle-details.vue)
 * 功能：展示商品详情和留言互动
 * 主要功能：
 *   - 展示商品完整信息
 *   - 显示卖家信息
 *   - 购买/加入购物车功能
 *   - 商品留言功能
 * 
 * 组件结构：
 *   - 商品信息区域
 *   - 卖家信息区域
 *   - 操作按钮区域
 *   - 留言区域
 */
<template>
  <div class="idle-details-new">
    <app-head />
    <app-body>
      <div class="idle-details-main">
        <!-- 商品图片轮播 -->
        <el-card class="idle-details-card pic-card">
          <el-carousel :interval="4000" arrow="always" height="350px" v-if="idleItemInfo.pictureList && idleItemInfo.pictureList.length">
            <el-carousel-item v-for="(img, idx) in idleItemInfo.pictureList" :key="idx">
              <el-image :src="img" fit="cover" style="width:100%;height:100%" />
            </el-carousel-item>
          </el-carousel>
          <div v-else class="no-pic">暂无图片</div>
        </el-card>

        <!-- 商品信息与卖家信息 -->
        <div class="info-seller-row">
          <!-- 商品信息卡片 -->
          <el-card class="idle-details-card info-card">
            <div class="title-row">
              <span class="item-title">{{ idleItemInfo.idleName }}</span>
            </div>
            <div class="item-price">￥{{ idleItemInfo.idlePrice }}</div>
            <div class="item-desc" v-html="idleItemInfo.idleDetails"></div>
            <div class="item-place">发布地点：{{ idleItemInfo.idlePlace }}</div>
            <div class="item-status">
              <el-tag v-if="idleItemInfo.idleStatus===1" type="success">在售</el-tag>
              <el-tag v-else type="info">已下架</el-tag>
            </div>
            <div class="item-btns">
              <el-button v-if="!isMaster && idleItemInfo.idleStatus===1" type="danger" @click="buyButton(idleItemInfo)">立即购买</el-button>
              <el-button 
                v-if="!isMaster && idleItemInfo.idleStatus===1" 
                :type="isFavorite ? 'success' : 'primary'" 
                @click="favoriteButton(idleItemInfo)">
                {{isFavorite ? '已加入购物车' : '加入购物车'}}
              </el-button>
              <el-button v-if="isMaster && idleItemInfo.idleStatus===1" type="warning" @click="changeStatus(idleItemInfo,2)">下架</el-button>
              <el-button v-if="isMaster && idleItemInfo.idleStatus===2" type="primary" @click="changeStatus(idleItemInfo,1)">重新上架</el-button>
            </div>
          </el-card>

          <!-- 卖家信息卡片 -->
          <el-card class="idle-details-card seller-card">
            <div class="seller-info">
              <el-avatar :src="idleItemInfo.user.avatar" size="large" />
              <div class="seller-meta">
                <div class="seller-nickname">{{ idleItemInfo.user.nickname }}</div>
                <div class="seller-signature" v-if="idleItemInfo.user.signature">{{ idleItemInfo.user.signature }}</div>
                <div class="seller-time">{{ idleItemInfo.user.signInTime ? idleItemInfo.user.signInTime.substring(0,10) : '' }} 加入平台</div>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 留言区 -->
        <el-card class="idle-details-card message-card" id="replyMessageLocation">
          <div class="message-title">留言互动</div>
          <div class="message-send">
            <div v-if="isReply" class="reply-tip">
              <el-tag type="info" closable @close="cancelReply">回复：{{replyData.toMessage}} @{{replyData.toUserNickname}}</el-tag>
            </div>
            <el-input type="textarea" autosize placeholder="留言提问..." v-model="messageContent" maxlength="200" show-word-limit />
            <div class="message-send-button">
              <el-button type="primary" @click="sendMessage">发送留言</el-button>
            </div>
          </div>
          <div class="message-list">
            <el-card v-for="(mes,idx) in messageList" :key="mes.id" class="message-item">
              <div class="msg-user">
                <el-avatar :src="mes.fromU.avatar" size="small" />
                <span class="msg-nickname">{{ mes.fromU.nickname }}</span>
                <span v-if="mes.toU.nickname" class="msg-reply">@{{ mes.toU.nickname }}: {{ mes.toM.content.substring(0,10) }}{{ mes.toM.content.length>10?'...':'' }}</span>
                <span class="msg-time">{{ mes.createTime }}</span>
              </div>
              <div class="msg-content" v-html="mes.content"></div>
              <div class="msg-actions">
                <el-button size="mini" @click="replyMessage(idx)">回复</el-button>
              </div>
            </el-card>
            <div v-if="!messageList.length" class="no-message">暂无留言</div>
          </div>
        </el-card>
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
  name: 'idle-details',
  components: { AppHead, AppBody, AppFoot },
  data() {
    return {
      messageContent: '',
      toUser: null,
      toMessage: null,
      isReply: false,
      replyData: { toUserNickname: '', toMessage: '' },
      messageList: [],
      idleItemInfo: {
        id: '', idleName: '', idleDetails: '', pictureList: [], idlePrice: 0, idlePlace: '', idleLabel: '', idleStatus: -1, userId: '',
        user: { avatar: '', nickname: '', signInTime: '', signature: '' },
      },
      isMaster: false,
      isFavorite: false,
      favoriteId: null
    };
  },
  created() {
    this.getIdleItemInfo();
  },
  methods: {
    getIdleItemInfo() {
      this.$api.getIdleItem({id: this.$route.query.id}).then(res => {
        if (res.status_code === 1) {
          this.idleItemInfo = res.data;
          if (this.idleItemInfo.pictureList) {
            try {
              this.idleItemInfo.pictureList = JSON.parse(this.idleItemInfo.pictureList);
            } catch (e) {
              this.idleItemInfo.pictureList = [];
            }
          }
          this.isMaster = this.idleItemInfo.userId === Number(this.getCookie('shUserId'));
          this.toUser = this.idleItemInfo.userId;
          this.getAllIdleMessage();
          // 检查是否已加入购物车
          this.checkFavorite();
        }
      });
    },
    getAllIdleMessage() {
      this.$api.getAllIdleMessage({ idleId: this.idleItemInfo.id }).then(res => {
        if (res.status_code === 1) {
          this.messageList = res.data;
        }
      });
    },
    checkFavorite() {
      this.$api.checkFavorite({ idleId: this.idleItemInfo.id }).then(res => {
        if (res.status_code === 1) {
          if (!res.data) {
            this.isFavorite = false;
            this.favoriteId = null;
          } else {
            this.isFavorite = true;
            this.favoriteId = res.data;
          }
        }
      }).catch(err => {
        console.error('检查购物车状态失败:', err);
      });
    },
    getCookie(cname) {
      var name = cname + '=';
      var ca = document.cookie.split(';');
      for (var i = 0; i < ca.length; i++) {
        var c = ca[i].trim();
        if (c.indexOf(name) === 0) return c.substring(name.length, c.length);
      }
      return '';
    },
    replyMessage(index) {
      $('html,body').animate({ scrollTop: (document.getElementById('replyMessageLocation') ? document.getElementById('replyMessageLocation').offsetTop : 0) - 600 }, { duration: 500, easing: 'swing' });
      this.isReply = true;
      this.replyData.toUserNickname = this.messageList[index].fromU.nickname;
      this.replyData.toMessage = this.messageList[index].content.substring(0, 10) + (this.messageList[index].content.length > 10 ? '...' : '');
      this.toUser = this.messageList[index].userId;
      this.toMessage = this.messageList[index].id;
    },
    changeStatus(idle, status) {
      this.$api.updateIdleItem({ id: idle.id, idleStatus: status }).then(res => {
        if (res.status_code === 1) {
          this.idleItemInfo.idleStatus = status;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    buyButton(idleItemInfo) {
      this.$api.addOrder({ idleId: idleItemInfo.id, orderPrice: idleItemInfo.idlePrice }).then(res => {
        if (res.status_code === 1) {
          this.$router.push({ path: '/order', query: { id: res.data.id } });
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    favoriteButton(idleItemInfo) {
      if (this.isFavorite) {
        this.$api.deleteFavorite(this.favoriteId).then(res => {
          if (res.status_code === 1) {
            this.$message({ message: '已取消购物车！', type: 'success' });
            this.isFavorite = false;
            this.favoriteId = null;
          } else {
            this.$message.error(res.msg);
          }
        });
      } else {
        this.$api.addFavorite({ idleId: idleItemInfo.id }).then(res => {
          if (res.status_code === 1) {
            this.$message({ message: '已加入购物车！', type: 'success' });
            this.isFavorite = true;
            this.favoriteId = res.data;
          } else {
            this.$message.error(res.msg);
          }
        });
      }
    },
    cancelReply() {
      this.isReply = false;
      this.toUser = this.idleItemInfo.userId;
      this.toMessage = null;
      this.replyData.toUserNickname = '';
      this.replyData.toMessage = '';
    },
    sendMessage() {
      let content = this.messageContent.trim();
      if (this.toUser == null) {
        this.toUser = this.idleItemInfo.userId;
      }
      if (content) {
        let contentList = content.split(/\r?\n/);
        let contenHtml = contentList[0];
        for (let i = 1; i < contentList.length; i++) {
          contenHtml += '<br>' + contentList[i];
        }
        this.$api.sendMessage({ idleId: this.idleItemInfo.id, content: contenHtml, toUser: this.toUser, toMessage: this.toMessage }).then(res => {
          if (res.status_code === 1) {
            this.$message({ message: '留言成功！', type: 'success' });
            this.messageContent = '';
            this.cancelReply();
            this.getAllIdleMessage();
          } else {
            this.$message.error('留言失败！' + res.msg);
          }
        }).catch(() => {
          this.$message.error('留言失败！');
        });
      } else {
        this.$message.error('留言为空！');
      }
    }
  }
};
</script>

<style scoped>
.idle-details-new {
  background: #f7f9fa;
  min-height: 100vh;
}
.idle-details-main {
  max-width: 1100px;
  margin: 0 auto;
  padding: 30px 0 60px 0;
}
.idle-details-card {
  margin-bottom: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 12px #f0f1f2;
}
.pic-card {
  padding: 0;
  overflow: hidden;
}
.no-pic {
  height: 350px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #bbb;
  font-size: 20px;
}
.info-seller-row {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}
.info-card {
  flex: 2;
  min-width: 350px;
}
.seller-card {
  flex: 1;
  min-width: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.seller-info {
  display: flex;
  align-items: center;
  gap: 18px;
}
.seller-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.seller-nickname {
  font-weight: bold;
  font-size: 18px;
}
.seller-signature {
  color: #26c6da;
  font-style: italic;
  font-size: 14px;
}
.seller-time {
  color: #888;
  font-size: 12px;
}
.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
.item-title {
  font-size: 22px;
  font-weight: 600;
}
.item-label {
  margin-left: 8px;
}
.item-price {
  color: #e53935;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}
.item-desc {
  font-size: 16px;
  color: #222;
  margin-bottom: 10px;
  line-height: 1.7;
}
.item-place {
  color: #888;
  font-size: 14px;
  margin-bottom: 10px;
}
.item-status {
  margin-bottom: 15px;
}
.item-btns {
  display: flex;
  gap: 12px;
  margin-top: 10px;
}
.message-card {
  padding-bottom: 20px;
}
.message-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 18px;
}
.message-send {
  margin-bottom: 18px;
}
.reply-tip {
  margin-bottom: 8px;
}
.message-send-button {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
}
.message-list {
  margin-top: 10px;
}
.message-item {
  margin-bottom: 16px;
  border-radius: 8px;
  background: #fff;
}
.msg-user {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}
.msg-nickname {
  font-weight: 600;
  font-size: 15px;
}
.msg-reply {
  color: #26c6da;
  margin-left: 8px;
  font-size: 13px;
}
.msg-time {
  color: #aaa;
  font-size: 12px;
  margin-left: 10px;
}
.msg-content {
  font-size: 15px;
  color: #333;
  margin-bottom: 6px;
  margin-left: 36px;
}
.msg-actions {
  display: flex;
  justify-content: flex-end;
}
.no-message {
  color: #bbb;
  text-align: center;
  margin-top: 20px;
  font-size: 16px;
}
@media (max-width: 900px) {
  .idle-details-main {
    padding: 10px 0 30px 0;
  }
  .info-seller-row {
    flex-direction: column;
    gap: 16px;
  }
}
</style>


