<script setup>

import Card from "@/views/components/Card.vue";
import {Setting, Lock} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";
// 修改密码表单
const form = reactive({
  password: '',
  new_password: '',
  new_password_repeat: ''
})

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.new_password) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}

const rules = {
  password: [
    {required: true, message: '请输入旧密码', trigger: 'blur'},
  ],
  new_password: [
    {required: true, message: '请输入新密码', trigger: 'blur'},
    {min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change']}
  ],
  new_password_repeat: [
    {required: true, message: '请输入新密码', trigger: 'blur'},
    {validator: validatePassword, trigger: ['blur', 'change']},
  ],
}

const formRef = ref()
const valid = ref(false)
const onValidate = (prop, isValid) => valid.value = isValid

function resetPassword() {
  formRef.value.validate(valid => {
    if (valid) {
      post("api/user/change_password", form, () => {
        ElMessage.success("修改密码成功")
        formRef.value.resetFields();
      })
    }
  })
}
const saving = ref(true)
const privacy = reactive({
  phone: false,
  email: false,
  qq: false,
  wechat: false,
  gender : false
})
get('/api/user/privacy', data => {
  privacy.phone = data.phone
  privacy.email = data.email
  privacy.qq = data.qq
  privacy.wechat = data.wechat
  privacy.gender = data.gender
  saving.value = false
})
function savePrivacy(type, value) {
  saving.value = true
  post('/api/user/privacy',{
    type: type,
    value: value
  }, () => {
    ElMessage.success("修改成功")
    saving.value = false
  })
}
</script>

<template>
  <div style="margin: auto;max-width: 600px">
      <div style="margin-top: 20px">
        <card :icon="Setting" title="隐私设置" desc="在这里设置公开内容，请用户注意自己的隐私" v-loading="saving">
            <div class="checkbox-list">
                <el-checkbox @change="savePrivacy('phone', privacy.phone)" v-model="privacy.phone">公开我的手机号</el-checkbox>
                <el-checkbox @change="savePrivacy('email', privacy.email)" v-model="privacy.email">公开我的电子邮件</el-checkbox>
                <el-checkbox @change="savePrivacy('wechat', privacy.wechat)" v-model="privacy.wechat">公开我的微信号</el-checkbox>
                <el-checkbox @change="savePrivacy('pp', privacy.qq)" v-model="privacy.qq">公开我的QQ号</el-checkbox>
                <el-checkbox @change="savePrivacy('gender', privacy.gender)" v-model="privacy.gender">公开我的性别</el-checkbox>
            </div>
        </card>
        <card style="margin: 20px 0" :icon="Setting" title="修改密码" desc="修改你的密码，请务必牢记你的密码">
              <el-form :model="form" :rules="rules" ref="formRef" @validate="onValidate" label-width="100px" style="margin: 20px">
                <el-form-item label="旧密码">
                  <el-input type="password" :prefix-icon="Lock" v-model="form.password" placeholder="请输入旧密码" maxlength="16"/>
                </el-form-item>
                <el-form-item label="新密码">
                  <el-input type="password" :prefix-icon="Lock" v-model="form.new_password" placeholder="请输入新密码" maxlength="16"/>
                </el-form-item>
                <el-form-item label="确认密码">
                  <el-input type="password" :prefix-icon="Lock" v-model="form.new_password_repeat" placeholder="确认密码" maxlength="16"/>
                </el-form-item>
                <div style="text-align: center">
                  <el-button @click="resetPassword" type="success">立即重置密码</el-button>
                </div>
              </el-form>
        </card>
      </div>
  </div>
</template>

<style scoped>
.checkbox-list {
  margin: 10px 0 0 10px;
  display: flex;
  flex-direction: column;
}
</style>