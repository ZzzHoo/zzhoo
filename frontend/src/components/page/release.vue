/*
 * 商品发布组件 (release.vue)
 * 功能：发布新的闲置商品或公告
 * 主要功能：
 *   - 填写商品/公告信息
 *   - 上传商品图片
 *   - 选择商品类别和位置
 *   - 设置价格(公告除外)
 * 
 * 组件结构：
 *   - 表单区域(el-form)
 *   - 图片上传组件(el-upload)
 *   - 位置选择组件(el-cascader)
 *   - 发布按钮(el-button)
 */
<template>
  <div class="release-bg">
    <app-head />
    <app-body>
      <div class="release-main">
        <div class="release-title">发布闲置/公告</div>
        <el-form class="release-form" label-width="90px" :model="idleItemInfo">
          <el-form-item label="标题">
            <el-input placeholder="请输入标题" v-model="idleItemInfo.idleName" maxlength="30" show-word-limit />
          </el-form-item>
          <el-form-item label="详细介绍">
            <el-input type="textarea" :autosize="{minRows:3,maxRows:6}" placeholder="请输入详细介绍..." v-model="idleItemInfo.idleDetails" maxlength="1000" show-word-limit />
          </el-form-item>
          <el-form-item label="您的位置">
            <el-cascader :options="options" v-model="selectedOptions" @change="handleChange" :separator="' '" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="类别">
            <el-select v-model="idleItemInfo.idleLabel" placeholder="请选择类别" style="width: 100%;">
              <el-option v-for="item in options2" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="价格" v-if="idleItemInfo.idleLabel !== 5">
            <el-input-number v-model="idleItemInfo.idlePrice" :precision="2" :step="10" :max="10000000" style="width: 100%;">
              <template #prepend>￥</template>
            </el-input-number>
          </el-form-item>
          <el-form-item label="上传图片">
            <el-upload
              class="release-upload"
              action="http://localhost:8080/file/"
              :on-preview="fileHandlePreview"
              :on-remove="fileHandleRemove"
              :on-success="fileHandleSuccess"
              :show-file-list="showFileList"
              :limit="10"
              :on-exceed="handleExceed"
              accept="image/*"
              drag
              multiple>
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">将图片拖到此处，或<em>点击上传</em></div>
            </el-upload>
            <div class="picture-list">
              <el-image v-for="(img,index) in imgList" :key="index" :src="img" fit="contain" style="width: 120px; margin: 6px; border-radius: 8px; box-shadow: 0 2px 8px #e3f0ff;" :preview-src-list="imgList" />
            </div>
            <el-dialog :visible.sync="imgDialogVisible">
              <img width="100%" :src="dialogImageUrl" alt="" />
            </el-dialog>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" plain @click="releaseButton" style="width: 100%;font-size:18px;">确认发布</el-button>
          </el-form-item>
        </el-form>
      </div>
      <app-foot />
    </app-body>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue';
import AppFoot from '../common/AppFoot.vue';
import options from '../common/country-data.js';

export default {
  name: "release",
  components: { AppHead, AppBody, AppFoot },
  data() {
    return {
      imgDialogVisible: false,
      dialogImageUrl: '',
      showFileList: true,
      options: options,
      selectedOptions: [],
      options2: [
        { value: 1, label: '数码科技' },
        { value: 2, label: '生活用品' },
        { value: 3, label: '运动相关' },
        { value: 4, label: '图书笔记' },
        { value: 5, label: '公告展示' }
      ],
      imgList: [],
      idleItemInfo: {
        idleName: '',
        idleDetails: '',
        pictureList: '',
        idlePrice: 0,
        idlePlace: '',
        idleLabel: ''
      }
    };
  },
  methods: {
    handleChange(value) {
      this.idleItemInfo.idlePlace = value.join(' ');
    },
    fileHandlePreview(file) {
      this.dialogImageUrl = file.url;
      this.imgDialogVisible = true;
    },
    fileHandleRemove(file, fileList) {
      this.imgList = fileList.map(f => f.url || f.response.data);
    },
    fileHandleSuccess(response, file, fileList) {
      this.imgList = fileList.map(f => f.url || f.response.data);
    },
    handleExceed(files, fileList) {
      this.$message.warning(`限制10张图片，本次选择了 ${files.length} 张图，共选择了 ${files.length + fileList.length} 张图`);
    },
    releaseButton() {
      this.idleItemInfo.pictureList = JSON.stringify(this.imgList);
      if (
        this.idleItemInfo.idleName &&
        this.idleItemInfo.idleDetails &&
        this.idleItemInfo.idlePlace &&
        this.idleItemInfo.idleLabel &&
        (this.idleItemInfo.idlePrice || this.idleItemInfo.idleLabel === 5)
      ) {
        this.$api.addIdleItem(this.idleItemInfo).then(res => {
          if (res.status_code === 1) {
            this.$message({ message: '发布成功！', type: 'success' });
            this.$router.replace({ path: '/details', query: { id: res.data.id } });
          } else {
            this.$message.error('发布失败！' + res.msg);
          }
        }).catch(() => {
          this.$message.error('请填写完整信息');
        });
      } else {
        this.$message.error('请填写完整信息！');
      }
    }
  }
};
</script>

<style scoped>
.release-bg {
  min-height: 100vh;
  background: linear-gradient(120deg, #e3f0ff 0%, #f8fdff 100%);
}
.release-main {
  max-width: 520px;
  margin: 40px auto 0 auto;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(60,120,200,0.10);
  padding: 36px 28px 32px 28px;
}
.release-title {
  font-size: 26px;
  font-weight: 700;
  color: #1a237e;
  text-align: center;
  margin-bottom: 32px;
  letter-spacing: 2px;
}
.release-form {
  margin-top: 0;
}
.release-upload {
  width: 100%;
  margin-bottom: 12px;
}
.picture-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}
@media (max-width: 600px) {
  .release-main {
    padding: 18px 4vw 24px 4vw;
    max-width: 98vw;
  }
  .release-title {
    font-size: 20px;
    margin-bottom: 18px;
  }
}
</style>