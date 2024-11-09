<script setup>

import LightCard from "@/views/components/LightCard.vue";
import {
  Calendar,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen,
  Link,
  Picture,
  Microphone, CircleCheck, Star, FolderOpened, ArrowRightBold
} from "@element-plus/icons-vue";
import Weather from "@/views/components/Weather.vue";
import {computed, reactive, ref, watch} from "vue";
import {get} from "@/net";
import {ElMessage} from "element-plus";
import TopicEditor from "@/views/components/TopicEditor.vue";

const weather = reactive({
  location: {},
  now: {},
  hourly: [],
  success: false
})
const editor = ref(false);
const today = computed( () => {
        const new_today = new Date()
        return `${new_today.getFullYear()}-${new_today.getMonth() + 1}-${new_today.getDate()}`
    }
)

navigator.geolocation.getCurrentPosition(position => {
  const longitude = position.coords.longitude
  const latitude = position.coords.latitude
  get(`/api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data => {
    Object.assign(weather, data)
    weather.success = true
  })
}, error => {
  console.info(error)
  ElMessage.warning('位置信息获取超时，请检测网络设置')
  get(`/api/forum/weather?longitude=116.40529&latitude=39.90499`, data => {
    Object.assign(weather, data)
    weather.success = true
  })
}, {
  timeout: 3000,
  enableHighAccuracy: true
})

</script>
<template>
    <div style="display: flex;margin: 20px auto;gap: 20px; max-width: 900px;">
        <div style="flex: 1">
            <light-card class="create-topic" @click="editor=true">
                 <el-icon><EditPen/></el-icon>点击发表主题...
            </light-card>
            <div style="margin-top: 10px; display: flex;flex-direction: column;gap: 10px">
                <light-card style="height: 100px" v-for="item in 10">

                </light-card>
            </div>
        </div>
        <div style="width: 280px">
            <div style="position: sticky;top:20px">
               <light-card>
                   <div style="font-weight: bold">
                        论坛公告
                   </div>
                   <el-divider style="margin: 10px 0"/>
                   <div style="font-size: 14px;margin: 10px;color: grey">
                       <span>
                           1.欢迎来到我的论坛，请遵守论坛规则，禁止发布违法内容。
                       </span>
                   </div>
               </light-card>
                <light-card style="margin-top: 20px">
                    <div style="font-weight: bold">
                        天气信息
                    </div>
                    <el-divider style="margin: 10px 0"/>
                    <div>
                        <weather :data="weather"/>
                    </div>
                </light-card>
                <light-card style="margin-top: 10px">
                    <div class="info-text">
                        <div>当今日期</div>
                        <div>{{today}}</div>
                    </div>
                    <!---->
                    <div class="info-text">
                        <div>当前IP地址</div>
                        <div>127.0.0.1</div>
                    </div>
                </light-card>

            </div>
        </div>
        <topic-editor :show='editor' @close="editor = false"/>
    </div>
</template>

<style scoped>
.info-text {
    display:flex;
    justify-content: space-between;
    color: grey;
    font-size: 14px;
}
.create-topic {
    background-color: #efefef;
    border-radius: 5px;
    height: 40px;
    color: grey;
    font-size: 14px;
    line-height: 40px;
    padding: 0 10px;
    &:hover {
        cursor: pointer;
    }
}
</style>