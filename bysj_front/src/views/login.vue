<template>
  <div>
    <div :xl="6" :lg="7" class="bg-login">

      <!--标题-->
      <el-row type="flex" class="row-bg row-two" justify="center" align="middle">
        <el-col :span="6"></el-col>
        <el-col :span="6">
          <!--标题-->
          <h1 class="title"><a style="text-decoration:none;color: #41b9a6;" href="index">线上售票系统</a></h1>
        </el-col>
        <el-col :span="6"></el-col>
      </el-row>
      <!--form表单-->
      <el-row type="flex" class="row-bg card" justify="center" align="bottom">
        <el-col :span="7" class="login-card">
          <template>
            <el-tabs  v-model="activeName" >
              <el-tab-pane label="用户登录" name="userLogin" style="text-align: center">
                <el-form   :model="userLoginForm" label-width="21%" class="loginForm">
                  <el-form-item label="登录名"  style="width: 380px">
                    <el-input v-model="userLoginForm.loginName" placeholder="用户登录名" ></el-input>
                  </el-form-item>
                  <el-form-item label="密码" prop="password" style="width: 380px">
                    <el-input type="password" show-password v-model="userLoginForm.password" placeholder="密码" ></el-input>
                  </el-form-item>
                  <el-form-item class="btn-ground">
                    <el-button type="primary" @click="userLogin" icon="el-icon-success">立即登陆</el-button>
                    <el-button @click="userRegister" icon="el-icon-circle-plus">注册</el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>
              <el-tab-pane label="管理员登录" name="administratorLogin" style="text-align: center">
                <el-form   :model="adminLoginForm" label-width="21%" class="loginForm">
                  <el-form-item label="登录名"  style="width: 380px">
                    <el-input v-model="adminLoginForm.loginName" placeholder="管理员登录名" ></el-input>
                  </el-form-item>
                  <el-form-item label="密码" prop="password" style="width: 380px">
                    <el-input type="password" show-password v-model="adminLoginForm.password" placeholder="密码" ></el-input>
                  </el-form-item>
                  <el-form-item class="btn-ground">
                    <el-button type="primary" @click="adminLogin" icon="el-icon-success">立即登陆</el-button>
                    <el-button @click="adminRegister" icon="el-icon-circle-plus">注册</el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>

            </el-tabs>
          </template>
        </el-col>
      </el-row>
    </div>


    <!--用户注册窗口-->
    <el-dialog
        title="用户注册"
        :visible.sync="dialogFormVisible4"
        width="30%"
        center>
      <el-form :model="userForm" label-width="100px">
        <el-form-item label="姓名" >
          <el-input v-model="userForm.name" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="登录用户名" >
          <el-input v-model="userForm.loginName" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="登录密码" >
          <el-input type="password" show-password v-model="userForm.password" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="性别" >
          <div>
            <el-radio v-model="userForm.sex" label="1"  border>男</el-radio>
            <el-radio v-model="userForm.sex" label="0"  border>女</el-radio>
          </div>
        </el-form-item>
        <el-form-item label="手机号码" >
          <el-input  v-model="userForm.phone" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="身份证" >
          <el-input  v-model="userForm.idCard" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="职业" >
          <el-input  v-model="userForm.unit" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>

        <el-form-item label="出生日期" >
          <div class="block">
            <el-date-picker
                v-model="userForm.birthdayTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择日期时间">
            </el-date-picker>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible4 = false">取 消</el-button>
    <el-button type="primary" @click="saveUser">确 定</el-button>
        </span>
    </el-dialog>



    <!--管理员注册窗口-->
    <el-dialog
        title="管理员注册"
        :visible.sync="dialogFormVisible5"
        width="30%"
        center>
      <el-form :model="adminForm" label-width="100px">
        <el-form-item label="姓名" >
          <el-input v-model="adminForm.name" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="登录名" >
          <el-input v-model="adminForm.loginName" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="登录密码" >
          <el-input type="password" show-password v-model="adminForm.password" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="性别" >
          <div>
            <el-radio v-model="adminForm.sex" label="1"  border>男</el-radio>
            <el-radio v-model="adminForm.sex" label="0"  border>女</el-radio>
          </div>
        </el-form-item>
        <el-form-item label="手机号码" >
          <el-input  v-model="adminForm.phone" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="身份证" >
          <el-input  v-model="adminForm.idCard" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible5 = false">取 消</el-button>
    <el-button type="primary" @click="saveAdmin">确 定</el-button>
        </span>
    </el-dialog>



  </div>
</template>



<script>
import request from "@/utils/request";

export default {
  name : "login",
  data(){
    return{
      activeName: 'userLogin',
      userLoginForm : {},
      adminLoginForm : {},
      dialogFormVisible5 : false,
      dialogFormVisible4 : false,
      userForm : {},
      adminForm : {},
    }
  },
  computed : {},
  created() {

  },
  methods : {
    userLogin(){
      request.post("/user/login",this.userLoginForm).then(res =>{
        if (res.success === true){
          this.$router.push('/index')
          this.$message.success(res.message)
          this.dialogFormVisible2 = false
          this.userLoginForm={}
        }else {
          this.$message.error(res.message)
        }
      })
    },
    userRegister(){
      this.dialogFormVisible4 = true
      this.userForm={}
    },
    adminLogin(){
      request.post("/administrator/login",this.adminLoginForm).then(res =>{
        if (res.success === true){
          this.$router.push('/adminInfo')
          this.$message.success(res.message)
          this.dialogFormVisible2 = false
          this.adminLoginForm={}
        }else {
          this.$message.error(res.message)
        }
      })
    },
    adminRegister(){
      this.dialogFormVisible5 = true
      this.adminForm={}
    },
    saveUser(){
      console.log(this.userForm)
      request.post("/user/saveUser",this.userForm).then(res =>{
        if (res.success === true){
          this.$message.success(res.message)
          this.dialogFormVisible4 = false
          this.userForm = {}
        }else {
          this.$message.error(res.message)
        }
      })
    },
    saveAdmin(){
      console.log(this.adminForm)
      request.post("/administrator/saveAdministrator",this.adminForm).then(res =>{
        if (res.success === true){
          this.$message.success(res.message)
          this.dialogFormVisible5 = false
          this.adminForm = {}
        }else {
          this.$message.error(res.message)
        }
      })
    },
  }
}
</script>

<style scoped>

.bg-login {
  min-height: 100vh;
  background-image: url("../static/image/background.jpg");
  background-size: 100% 100%;
  width: 100%;

}

.btn-ground {
  text-align: center;
}

.logo {
  margin: 30px;
  height: 70px;
  width: 70px;
  position: fixed;
}

.title {
  text-shadow: -3px 3px 1px #5f565e;
  text-align: center;
  margin-top: 50%;
  color: #41b9a6;
  font-size: 40px;
}

.login-card {
  background-color: #ffffff;
  opacity: 0.85;
  box-shadow: 0 0 20px #ffffff;
  border-radius: 10px;
  padding: 40px 40px 30px 15px;
  width: auto;
}


</style>