<script setup>

import Card from "@/views/components/Card.vue";
import {Select, User, Message, Refresh} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {computed, reactive, ref} from "vue";
import {accessHeader, get, post} from "@/net";
import {ElMessage} from "element-plus";
import axios from "axios";

const store = useStore()
const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())
const desc = ref('')
const baseFormRef = ref()
const emailFormRef = ref()
const baseForm = reactive({
    username: '',
    gender: 1,
    phone: '',
    qq: '',
    wechat: '',
    desc: ''
})
const emailForm = reactive({
    email: '',
    code: ''
})
const validateUsername = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请输入用户名'))
    } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
        callback(new Error('用户名不能包含特殊字符，只能是中文/英文'))
    } else {
        callback()
    }
}
const rules = {
    username: [
        {validator: validateUsername, trigger: ['blur', 'change']},
        {min: 2, max: 8, message: '用户名的长度必须在2-8个字符之间', trigger: ['blur', 'change']},
    ],
    email: [
        {required: true, message: '请输入邮件地址', trigger: 'blur'},
        {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
    ],
    code: [
        {required: true, message: '请输入获取的验证码', trigger: 'blur'},
    ]
}

const loading = reactive({
    form: true,
    base: false
})

function saveDetails() {
    baseFormRef.value.validate(isValid => {
        if (isValid) {
            loading.form = true
            post('api/user/save-details', baseForm, () => {
                ElMessage.success('用户信息成功')
                store.user.username = baseForm.username
                desc.value = baseForm.desc
                loading.form = false
            }, (message) => {
                ElMessage.warning(message)
                loading.base = false
            })
        }
    })
}

get("api/user/details", data => {
    baseForm.username = store.user.username
    baseForm.gender = data.gender
    baseForm.phone = data.phone
    baseForm.qq = data.qq
    baseForm.wechat = data.wechat
    baseForm.desc = desc.value = data.desc
    emailForm.email = store.user.email
    loading.form = false
})

const coldTime = ref(0)
const isEmailValid = ref(false)
const onValidate = (prop, isValid) => {
    if (prop === 'email')
        isEmailValid.value = isValid
}

function sendEmailCode() {
    emailFormRef.value.validate(isValid => {
        if (isValid) {
            coldTime.value = 60
            get(`/api/auth/ask-code?email=${emailForm.email}&type=modify`, () => {
                ElMessage.success(`验证码已发送到邮箱: ${emailForm.email}，请注意查收`)
                const handle = setInterval(() => {
                    coldTime.value--
                    if (coldTime.value === 0) {
                        clearInterval(handle)
                    }
                }, 1000)
            }, (message) => {
                ElMessage.warning(message)
                coldTime.value = 0
            })
        }
    })
}

function modifyEmail() {
    emailFormRef.value.validate(isValid => {
        if (isValid) {
            post('/api/user/modify-email', emailForm, () => {
                ElMessage.success('修改成功')
                store.user.email = emailForm.email
                emailForm.code = ''
            })
        }
    })
}

function beforeAvatarUpload(rawFile) {
    if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
        ElMessage.error('头像只能是JPG/PNG格式的'
        )
        return false
    } else if (rawFile.size / 1024 > 100) {
        ElMessage.error('头像大小不能大于100KB')
        return false
    }
    return true
}
function uploadSuccess(response) {
    ElMessage.success('上传成功')
    store.user.avatar = response.data
}
</script>

<template>
    <div style="display: flex">
        <div class="settings-left">
            <card :icon="User" title="账号信息设置" desc="在这里编辑你的个人信息，你可以在隐私设置中选择是否展示这些信息"
                  v-loading="loading.form">
                <el-form :model="baseForm" :rules="rules" ref="baseFormRef" label-position="top"
                         style="margin: 0 10px 10px 10px">
                    <el-form-item label="用户名" prop="username">
                        <el-input v-model="baseForm.username" maxlength="20"/>
                    </el-form-item>
                    <el-form-item label="性别" prop="gender">
                        <el-radio-group v-model="baseForm.gender">
                            <el-radio :label="0">男</el-radio>
                            <el-radio :label="1">女</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="手机号" prop="phone">
                        <el-input v-model="baseForm.phone" maxlength="11"/>
                    </el-form-item>
                    <el-form-item label="微信号" prop="wechat">
                        <el-input v-model="baseForm.wechat" maxlength="13"/>
                    </el-form-item>
                    <el-form-item label="QQ号" prop="qq">
                        <el-input v-model="baseForm.qq" maxlength="20"/>
                    </el-form-item>
                    <el-form-item label="个人简介" prop="desc">
                        <el-input v-model="baseForm.desc" type="textarea" :rows="6" maxlength="200"/>
                    </el-form-item>
                    <div>
                        <el-button :icon="Select" @click="saveDetails" type="success">保存用户信息</el-button>
                    </div>
                </el-form>
            </card>
            <card style="margin-top: 20px" :icon="Message" title="电子邮件设置" desc="在这里修改默认绑定的邮件地址">
                <el-form @validate="onValidate" :model="emailForm" :rules="rules" ref="emailFormRef"
                         label-position="top"
                         style="margin: 0 10px 10px 10px">
                    <el-form-item label="电子邮件" prop="email">
                        <el-input v-model="emailForm.email"/>
                    </el-form-item>
                    <el-form-item prop="code">
                        <el-row style="width:100%" :gutter="10">
                            <el-col :span="18">
                                <el-input placeholder="请获取验证码" v-model="emailForm.code"/>
                            </el-col>
                            <el-col :span="6">
                                <el-button type="success" style="width:100%" :disabled="!isEmailValid || coldTime > 0"
                                           @click="sendEmailCode" plain>
                                    {{ coldTime > 0 ? `请稍等 ${coldTime} 秒` : '获取验证码' }}
                                </el-button>
                            </el-col>
                        </el-row>
                    </el-form-item>
                    <div>
                        <el-button :icon="Refresh" type="success" @click="modifyEmail">更新电子邮件</el-button>
                    </div>
                </el-form>
            </card>
        </div>
        <div class="settings-right">
            <div style="position: sticky; top: 20px">
                <card>
                    <div style="text-align: center; padding: 5px 15px 0 15px">
                        <div>
                            <el-avatar :size="120"
                                       :src="store.avatarUrl"/>
                            <div style="margin: 5px 0">
                                <el-upload
                                    :action="axios.defaults.baseURL + '/api/image/avatar'"
                                    :show-file-list="false"
                                    :before-upload="beforeAvatarUpload"
                                    :on-success="uploadSuccess"
                                    :headers="accessHeader()"
                                >
                                    <el-button size="small" round>修改头像</el-button>
                                </el-upload>
                            </div>
                            <div style="font-weight: bold">你好，{{ store.user.username }}</div>
                        </div>
                    </div>
                    <el-divider style="margin: 10px 0"/>
                    <div style="font-size: 14px;color: grey; padding: 10px">
                        {{ desc || "这个用户很懒，没有写简介~" }}
                    </div>
                </card>
                <card style="margin-top: 20px; font-size: 14px">
                    <div>账号注册时间：{{ registerTime }}</div>
                    <div style="color: grey">欢迎加入我们的论坛！</div>
                </card>
            </div>
        </div>
    </div>
</template>

<style scoped>
.settings-left {
    flex: 1;
    margin-top: 20px;
    margin-left: 100px;
}

.settings-right {
    width: 300px;
    margin: 20px 30px 20px 100px;
}
</style>