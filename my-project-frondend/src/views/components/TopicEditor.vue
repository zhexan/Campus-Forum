<script setup>
import {Check, Document} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {Quill, QuillEditor} from "@vueup/vue-quill";
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {ElMessage} from "element-plus";
import axios from "axios";
import ImageResize from "quill-image-resize-vue";
import { ImageExtend, QuillWatch } from "quill-image-super-solution-module";
import {accessHeader, get} from "@/net";
defineProps({
      show: Boolean
    }
)
const emit = defineEmits(['close'])

const editor = reactive({
  type: null,
  title:'',
  text: '',
  uploading:false,
  types:[]
})
get('api/forum/type', data => editor.types = data)

Quill.register('modules/imageResize', ImageResize)
Quill.register('modules/ImageExtend', ImageExtend)

const editorOption = {
  modules: {
    toolbar: {
      container: [
        "bold", "italic", "underline", "strike","clean",
        {color: []}, {'background': []},
        {size: ["small", false, "large", "huge"]},
        { header: [1, 2, 3, 4, 5, 6, false] },
        {list: "ordered"}, {list: "bullet"}, {align: []},
        "blockquote", "code-block", "link", "image",
        { indent: '-1' }, { indent: '+1' }
      ],
      handlers: {
        'image': function () {
          QuillWatch.emit(this.quill.id)
        }
      }
    },
    imageResize: {
      modules: [ 'Resize', 'DisplaySize' ]
    },
    ImageExtend: {
      action:  axios.defaults.baseURL + '/api/image/cache',
      name: 'file',
      size: 5,
      loading: true,
      accept: 'image/png, image/jpeg',
      response: (resp) => {
        if(resp.data) {
          return axios.defaults.baseURL + '/images' + resp.data
        } else {
          return null
        }
      },
      methods: 'POST',
      headers: xhr => {
        xhr.setRequestHeader('Authorization', accessHeader().Authorization);
      },
      start: () => editor.uploading = true,
      success: () => {
        ElMessage.success('图片上传成功!')
        editor.uploading = false
      },
      error: () => {
        ElMessage.warning('图片上传失败，请联系管理员!')
        editor.uploading = false
      }
    }
  }
}

</script>

<template>
  <div>
    <el-drawer :model-value="show"
               direction="btt"
               :size="650"
               @close="emit('close')"
               :close-on-click-modal="false">
      <template #header>
        <div>
          <div style="font-weight: bold">
            发表新的帖子
          </div>
          <div style="font-size: 13px">
            发表内容之前，请遵守相关法律法规。
          </div>
        </div>
      </template>
      <div style="display:flex;gap: 10px">
        <div style="width: 155px">
          <el-select placeholder="请选择主题类型..." v-model="editor.type" :disabled="!editor.types.length">
            <el-option v-for="item in editor.types" :label="item.name" :value="item.id"/>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input v-model="editor.title" placeholder="请输入帖子标题..." :prefix-icon="Document"
                    style="height: 100%"/>
        </div>
      </div>
      <div style="margin-top: 10px; height: 460px; overflow: hidden; border-radius: 5px"
           v-loading="editor.uploading"
           element-loading-text="正在上传图片,请稍等...">
        <quill-editor v-model:content="editor.text"
                      style="height: calc(100% - 45px)"
                      content-type="delta"
                      placeholder="今天分享点什么..."
                      :options="editorOption"
        />
      </div>
      <div style="display: flex;justify-content: space-between; margin-top: 5px">
        <div style="color: grey;font-size: 13px">
         当前字数 666 (最大支持20000字)
        </div>
        <div>
          <el-button type="success" :icon="Check" plain>立即发布帖子</el-button>
        </div>
      </div>

    </el-drawer>
  </div>

</template>

<style scoped>
:deep(.el-drawer) {
  width: 800px;
  margin: auto;
  border-radius: 10px 10px 0 0;
}

:deep(.el-drawer__header) {
  margin: 0;
}
:deep(.ql-toolbar) {
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}
:deep(.ql-container) {
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color);
}
:deep(.ql-editor.ql-blank::before) {
  color: var(--el-text-color-placeholder);
  font-style: normal;
}
:deep(.ql-editor){
  font-size: 15px;
}

</style>