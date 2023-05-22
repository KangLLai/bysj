<template>
  <div>
    <div class="head">
      <el-menu  router
                :default-active="this.$router.path"
          class="el-menu-demo"
          mode="horizontal"
          @select="handleSelect"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b">
        <el-menu-item index="/index">首页</el-menu-item>
        <el-menu-item index="/userInfo">用户中心</el-menu-item>
        <el-menu-item index="/adminInfo">管理员页面</el-menu-item>


        <div style="height: 61px;line-height: 61px;display: flex;justify-content: right;margin-right: 5px">
          <img src="../static/image/lbxx.jpg" alt="" style="width: 60px;">
          <span class="title">线上购票系统</span>
          <el-button type="primary" icon="el-icon-s-custom" @click="userLogout">注销</el-button>
        </div>
      </el-menu>
    </div>

    <div style="margin-top: 20px">
    <el-tabs type="border-card">
      <el-tab-pane>
        <span slot="label"><i class="el-icon-user-solid"></i>
          个人信息
        </span>
        <div style="display: flex;justify-content: center;align-items: center;">
        <el-form :model="userInfoForm"  label-width="120px"  >
          <el-form-item  label="姓名">
            <el-input v-model="userInfoForm.name"></el-input>
          </el-form-item>
          <el-form-item  label="登录账号">
            <el-input  v-model="userInfoForm.loginName"></el-input>
          </el-form-item>
          <el-form-item  label="性别">
            <el-input v-model="userInfoForm.sex"></el-input>
          </el-form-item>
          <el-form-item  label="职业">
            <el-input v-model="userInfoForm.unit"></el-input>
          </el-form-item>
          <el-form-item  label="出生日期">
            <div class="block">
              <el-date-picker
                  v-model="userInfoForm.birthdayTime"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  placeholder="选择日期时间">
              </el-date-picker>
            </div>
          </el-form-item>
          <el-form-item>
            <div  style="text-align: center">
            <el-button type="primary"  @click="updateUserInfo" >修改</el-button>
            </div>
          </el-form-item>
        </el-form>
        </div>
      </el-tab-pane>

      <el-tab-pane>
        <span slot="label"><i class="el-icon-place"></i>
          收货地址
        </span>
        <div>
        <el-table
            align="center"
            ref="singleTable"
            :data="addressTableData"
            highlight-current-row
            @current-change="handleAddressCurrentChange"
            style="width: 100%">
          <el-table-column header-align="center" v-if="false"
                           align="center"
                           property="id"
                           label="地址id"
                           width="50">

          </el-table-column>
          <el-table-column header-align="center"
                           align="center"
                           property="name"
                           label="姓名"
                           width="120">

          </el-table-column>
          <el-table-column header-align="center"
                           align="center"
                           property="phone"
                           label="电话"
                           width="200">
          </el-table-column>
          <el-table-column header-align="center"
                           align="center"
                           property="destination"
                           label="地址"
                           width="300">
          </el-table-column>
          <el-table-column header-align="center"
                           align="center"
                           label="操作"
                           width="200">
            <div>
              <el-button type="submit" @click="toUpdateAddress">修改</el-button>
              <el-button type="submit" @click="deleteAddress">删除</el-button>
            </div>
          </el-table-column>
        </el-table>
          <div style="text-align: center;margin-top: 20px">
          <el-button type="primary" @click="toAddAddress">添加收货地址</el-button>
          <el-button type="primary" @click="setDefault">设为默认地址</el-button>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane >
        <span slot="label"><i class="el-icon-s-tools"></i>
          账号设置
        </span>
        <div style="display: flex;justify-content: center;align-items: center;">
          <el-form label-width="120px"  >
            <el-form-item  label="登陆密码">
              <el-input v-model="password" type="password" :disabled="true" >
                <el-button style="padding-right:10px" slot="suffix" type="text" @click="dialogFormVisible5=true" >修改</el-button>
              </el-input>
            </el-form-item>
            <el-form-item  label="手机号码">
              <el-input v-model="phone" type="password"  :disabled="true">
                <el-button style="padding-right:10px" slot="suffix" type="text" @click="dialogFormVisible4 = true" >更换</el-button>
              </el-input>
            </el-form-item>
            <el-form-item  label="身份证号码">
              <el-input v-model="idCard" type="password" :disabled="true">
                <el-button style="padding-right:10px" slot="suffix" type="text"  @click="dialogFormVisible6=true">修改</el-button>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
    </div>

    <div style="margin-top: 30px">
    <el-tabs type="border-card">
      <el-tab-pane>
        <span slot="label"><i class="el-icon-date"></i>
          订单列表
        </span>

        <div style="height: 61px;line-height: 61px;display: flex;justify-content: center;margin-right: 5px">
          <div>

            <el-input v-model="searchOrderId"  style="width: 150px;margin-right: 5px;" suffix-icon="el-icon-search" placeholder="订单编号"></el-input>
            <el-date-picker v-model="orderTime"
                            clearable
                            value-format="yyyy-MM-dd HH:mm:ss"
                            type="datetimerange"
                            placeholder="选择日期"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            :default-time="['00:00:00', '23:59:59']"
                            style="width: 400px;margin-left: 20px;"
            ></el-date-picker>
            <el-button style="margin-left: 20px" type="primary" class="search-btn" @click="loadOrder">搜索</el-button>
          </div>
        </div>
        <!--订单列表-->
        <div style="margin-top: 30px">
          <el-table
              align="center"
              ref="singleTable"
              :data="orderTableData"
              highlight-current-row
              @current-change="handleOrderCurrentChange"
              style="width: 100%">
            <el-table-column header-align="center"
                             align="center"
                             type="index"
                             :index="count"
                             label="序号"
                             width="50">
            </el-table-column>
            <el-table-column header-align="center" v-if="false"
                             align="center"
                             property="orderId"
                             label="订单id"
                             width="50">
            </el-table-column>
            <el-table-column header-align="center"
                             align="center"
                             property="title"
                             label="标题"
                             width="200">
            </el-table-column>
            <el-table-column header-align="center"
                             align="center"
                             property="viewName"
                             label="观演人"
                             width="80">
            </el-table-column>
            <el-table-column header-align="center"
                             align="center"
                             property="idCard"
                             label="身份证"
                             width="120">
            </el-table-column>
            <el-table-column header-align="center"
                             align="center"
                             property="money"
                             label="票价"
                             width="80">
            </el-table-column>
            <el-table-column header-align="center"
                             align="center"
                             property="address"
                             label="收货地址"
                             width="120">
            </el-table-column>
            <el-table-column header-align="center"
                             align="center"
                             property="addressName"
                             label="收货人"
                             width="80">
            </el-table-column>
            <el-table-column header-align="center"
                             align="center"
                             property="phone"
                             label="手机号码"
                             width="120">
            </el-table-column>
            <el-table-column header-align="center"
                             align="center"
                             property="remark"
                             label="备注"
                             width="120">
            </el-table-column>
            <el-table-column header-align="center"
                             align="center"
                             property="createTime"
                             label="下单时间"
                             width="120">
            </el-table-column>
            <el-table-column header-align="center"
                             align="center"
                             property="status"
                             label="订单状态"
                             width="80">
              <template v-slot="scope">
             <span v-if="scope.row.status== 1">
                 待派送
             </span>
              <span v-if="scope.row.status== 2">
                 已派送
             </span>
              <span v-if="scope.row.status== 3">
                 已完成
             </span>
              <span v-if="scope.row.status== 4">
                 已取消
             </span>
              </template>
            </el-table-column>
            <el-table-column header-align="center"
                             align="center"
                             property="createTime"
                             label="操作"
                             width="300">
              <el-button @click="dialogFormVisible7 =true">修改订单</el-button>
              <el-button @click="dialogFormVisible8 =true">确认收货</el-button>
              <el-button @click="dialogFormVisible9 =true">取消订单</el-button>
            </el-table-column>
          </el-table>
          <div class="block">
            <el-pagination
                style="text-align: center;margin-top: 20px"
                @size-change="handleSizePageChange"
                @current-change="handleCurrentPageChange"
                :current-page="pageIndex"
                :page-sizes="[2, 5, 10, 20]"
                :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total">
            </el-pagination>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    </div>



    <!--添加收货地址弹窗-->
    <el-dialog
        title="添加收货地址"
        :visible.sync="dialogFormVisible3"
        width="30%"
        center>
      <el-form :model="addAddressForm" label-width="100px">
        <el-form-item label="收货地址" >
          <el-input v-model="addAddressForm.destination" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="收货人" >
          <el-input v-model="addAddressForm.name" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="收货手机号码" >
          <el-input v-model="addAddressForm.phone" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="是否默认" >
          <div>
            <el-radio v-model="addAddressForm.isDefault" label="1"  border>设为默认</el-radio>
            <el-radio v-model="addAddressForm.isDefault" label="0"  border>不设为默认</el-radio>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible3 = false">取 消</el-button>
    <el-button type="primary" @click="saveAddress">确 定</el-button>
        </span>
    </el-dialog>


    <el-dialog
        title="修改收货地址"
        :visible.sync="dialogFormVisible1"
        width="30%"
        center>
      <el-form :model="addAddressForm" label-width="100px">
        <el-form-item label="收货地址" >
          <el-input v-model="addAddressForm.destination" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="收货人" >
          <el-input v-model="addAddressForm.name" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="收货手机号码" >
          <el-input v-model="addAddressForm.phone" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="是否默认" >
          <div>
            <el-radio v-model="addAddressForm.isDefault" label="1"  border>设为默认</el-radio>
            <el-radio v-model="addAddressForm.isDefault" label="0"  border>不设为默认</el-radio>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible1 = false">取 消</el-button>
    <el-button type="primary" @click="updateAddress">确 定</el-button>
        </span>
    </el-dialog>


    <!--修改手机号码-->
    <el-dialog
        title="修改手机号码"
        :visible.sync="dialogFormVisible4"
        width="30%"
        center>
      <el-form label-width="100px">
        <el-form-item label="手机号" >
          <el-input v-model="phone" type="password"  :disabled="true" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="新手机号" >
          <el-input v-model="modifyPhone" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="验证码" >
          <el-input v-model="code" autocomplete="off" style="width: 80%">
            <el-button style="padding-right:10px" slot="suffix" type="text" @click="getCode">获取验证码</el-button>
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible4 = false">取 消</el-button>
    <el-button type="primary" @click="updatePhone">确 定</el-button>
        </span>
    </el-dialog>

    <!--修改密码-->
    <el-dialog
        title="修改密码"
        :visible.sync="dialogFormVisible5"
        width="30%"
        center>
      <el-form label-width="100px">
        <el-form-item label="密码" >
          <el-input v-model="password"  type="password" :disabled="true" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="新密码" >
          <el-input v-model="modifyPassword" type="password" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="验证码" >
          <el-input v-model="code" autocomplete="off" style="width: 80%">
            <el-button style="padding-right:10px" slot="suffix" type="text" @click="getCode">获取验证码</el-button>
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible5 = false">取 消</el-button>
    <el-button type="primary" @click="updatePassword">确 定</el-button>
        </span>
    </el-dialog>


    <!--修改身份证-->
    <el-dialog
        title="修改身份证"
        :visible.sync="dialogFormVisible6"
        width="30%"
        center>
      <el-form label-width="100px">
        <el-form-item label="身份证" >
          <el-input v-model="idCard"  type="password" :disabled="true" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="新身份证" >
          <el-input v-model="modifyIdCard" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="验证码" >
          <el-input v-model="code" autocomplete="off" style="width: 80%">
            <el-button style="padding-right:10px" slot="suffix" type="text" @click="getCode">获取验证码</el-button>
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible6 = false">取 消</el-button>
    <el-button type="primary" @click="updateIdCard">确 定</el-button>
        </span>
    </el-dialog>



    <!--修改订单-->
    <el-dialog
        title="修改订单"
        :visible.sync="dialogFormVisible7"
        width="30%"
        center>
      <el-form label-width="100px">
        <el-form-item label="修改地址">
          <template style="width: 500px">
            <el-select  :value="addressObject"  value-key="id" placeholder="请选择"  @change="changeAddressSelect" >
              <el-option v-for="addressObject in addressArr" :key="addressObject.id" :label="addressObject.destination"
                         :value="addressObject">
              </el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item label="修改备注信息" >
          <el-input v-model="modifyRemark" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible7 = false">取 消</el-button>
    <el-button type="primary" @click="updateOrder">确 定</el-button>
        </span>
    </el-dialog>


    <!--确认收货-->
    <el-dialog
        title="确认收货"
        :visible.sync="dialogFormVisible8"
        width="30%"
        center>
      <span>是否确认收货???</span>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible8 = false">取 消</el-button>
    <el-button type="primary" @click="sureOrder">确 定</el-button>
        </span>
    </el-dialog>

    <!--取消订单-->
    <el-dialog
        title="取消订单"
        :visible.sync="dialogFormVisible9"
        width="30%"
        center>
      <span>是否取消订单???</span>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible9 = false">取 消</el-button>
    <el-button type="primary" @click="cancelOrder">确 定</el-button>
        </span>
    </el-dialog>



  </div>
</template>


<script>
import request from "@/utils/request";

export default {
  name:"userInfo",
  data(){
    return{
      activeIndex: '3-1',
      dialogFormVisible1:false,
      dialogFormVisible2:false,
      dialogFormVisible3:false,
      dialogFormVisible4:false,
      dialogFormVisible5:false,
      dialogFormVisible6:false,
      dialogFormVisible7:false,
      dialogFormVisible8:false,
      dialogFormVisible9:false,
      addressForm:{},
      addAddressForm:{},
      OrderForm:{},
      userForm :{},
      userInfoForm :{},
      adminForm :{},
      addressTableData:[],
      orderTableData:[],
      pageIndex: 1,
      pageSize: 5,
      total : 0,
      password : "",
      phone : "",
      modifyPhone : "",
      modifyPassword : "",
      modifyIdCard : "",
      modifyLoginName : "",
      code : "",
      idCard : "",
      searchOrderId:"",
      orderTime :'',
      beginTime: '',
      endTime: '',
      modifyOrderForm:  {},
      addressObject:{},
      addressArr:[],
      modifyRemark:'',
      orderStatus : '',
    }
  },
  computed: {},
  watch: {
    orderTime(val) {
      if (val && val.length >= 2) {
        this.beginTime = val[0]
        this.endTime = val[1]
      } else {
        this.beginTime = ''
        this.endTime = ''
      }
    }
  },
  created() {
    this.getUserInfo()
    this.getAllAddress()
    this.loadOrder()
  },
  methods : {
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
    },
    handleAddressCurrentChange(val) {
      if (val){
        this.currentRow = val;
        this.addAddressForm =this.currentRow
        console.log("当前行地址数据👇")
        console.log(this.addAddressForm)
      }
    },
    handleOrderCurrentChange(val) {
      if (val){
        this.currentRow = val;
        this.OrderForm =this.currentRow
        console.log("当前行订单数据👇")
        console.log(this.OrderForm)
      }
    },
    handleSizePageChange(val){
      this.pageSize = val
      this.orderTableData = []
      this.loadOrder()
    },
    handleCurrentPageChange(val){
      this.pageIndex = val
      this.orderTableData = []
      this.loadOrder()
    },
    changeAddressSelect(addressObject){
      this.addressObject = addressObject
      this.modifyOrderForm.addressId = addressObject.id
      this.modifyOrderForm.address = addressObject.destination
      this.modifyOrderForm.addressName = addressObject.name
      console.log(this.modifyOrderForm)
    },
    toAddAddress(){
      this.dialogFormVisible3 = true
      this.addAddressForm = {}
    },
    toUpdateAddress(){
      this.dialogFormVisible1 = true
    },
    /*让序号不受分页影响,分页后不从1开始*/
    count(index) {
      return (this.pageIndex - 1) * this.pageSize + index + 1
    },
    userLogout(){
      this.$router.push('/')
      request.post("/user/logout").then(res =>{
        if (res.success === true){
          this.$message.success(res.message)
        }else {
          this.$message.error(res.message)
        }
      })
    },
    getUserInfo(){
      request.get("/user/queryUser").then(res => {
        if (res.success === true){
          this.userInfoForm = res.data
          this.password = res.data.password
          this.phone = res.data.phone
          this.idCard = res.data.idCard
          this.modifyLoginName = res.data.loginName
          this.userInfoForm.phone = null
          this.userInfoForm.idCard = null
          this.userInfoForm.password = null
        }else {
          this.$message.error(res.message)
        }
      })
    },
    updateUserInfo(){
      if (this.userInfoForm.loginName === this.modifyLoginName){
        //说明登录名没修改
        this.userInfoForm.loginName = null
      }
      request.post("/user/updateUser",this.userInfoForm).then(res =>{
        if (res.success === true){
          this.$message.success(res.message)
          this.getUserInfo()
        }else {
          this.$message.error(res.message)
        }
      })
    },
    getAllAddress(){
      request.post("/address/queryAddress").then(res =>{
        if (res.success === true){
          this.addressTableData = res.data
          this.addressArr = res.data
          console.log(this.tableData)
        }else {
          this.$message.error(res.message)
        }

      })
    },
    saveAddress(){
      console.log("收货=========")
      console.log(this.addAddressForm)
      request.post("/address/saveAddress",this.addAddressForm).then(res =>{
        if (res.success === true){
          this.$message.success(res.message)
          this.dialogFormVisible3 = false
          this.addAddressForm = {}
          this.getAllAddress()
        }else {
          this.$message.error(res.message)
        }
      })
    },
    setDefault(){
      request.post("/address/default/" + this.addAddressForm.id).then(res => {
        if (res.success === true){
          this.$message.success(res.message)
        }else {
          this.$message.error(res.message)
        }
      })
    },
    updateAddress(){
      request.post("/address/updateAddress",this.addAddressForm).then(res =>{
        if (res.success === true){
          this.$message.success(res.message)
          this.dialogFormVisible1 = false
          this.getAllAddress()
        }else {
          this.$message.error(res.message)
        }
      })
    },
    deleteAddress(){
      request.post("/address/deleteAddress/" + this.addAddressForm.id).then(res =>{
        if (res.success === true){
          this.getAllAddress()
          this.$message.success(res.message)
        }else {
          this.$message.error(res.message)
        }
      })

    },
    loadOrder(){
      if (this.searchOrderId) {
        this.pageIndex = 1
        this.pageSize = 5
      }
      request.get("/order/queryUserOrders",{
        params:{
          pageIndex : this.pageIndex,
          pageSize : this.pageSize,
          orderId : this.searchOrderId,
          beginTime : this.beginTime,
          endTime : this.endTime,
        }
      }).then(res =>{
        console.log(res)
        this.orderTableData=[]
        this.orderTableData = res.data
        this.total =res.total
      })
    },
    getCode(){ //获取验证码
      request.post("/user/sendMsg").then(res=>{
        if (res.success === true){
          this.$message.success(res.message)
        }else {
          this.$message.error(res.message)
        }
      })
    },
    updatePhone(){
    request.get("/user/updatePhone", {
      params : {
        phone : this.modifyPhone,
        code : this.code
      }
    }).then(res =>{
      if (res.success === true){
        this.modifyPhone = null
        this.code = null
        this.getUserInfo()
        this.dialogFormVisible4 = false
        this.$message.success(res.message)
      }else {
        this.$message.error(res.message)
      }
    })
    },
    updatePassword(){
      request.get("/user/updatePassword",{
        params : {
          password : this.modifyPassword,
          code : this.code
        }
      }).then(res =>{
        if (res.success === true){
          this.modifyPassword = null,
          this.code = null
          this.getUserInfo()
          this.dialogFormVisible5 =false
          this.$message.success(res.message)
        }else {
          this.$message.error(res.message)
        }
      })
    },
    updateIdCard(){
      request.get("/user/updateIdCard",{
        params : {
          idCard : this.modifyIdCard,
          code : this.code
        }
      }).then(res =>{
        if (res.success === true){
          this.modifyIdCard = null,
          this.code = null
          this.getUserInfo()
          this.dialogFormVisible6 =false
          this.$message.success(res.message)
        }else {
          this.$message.error(res.message)
        }
      })
    },
    updateOrder() {
      this.modifyOrderForm.orderId = this.OrderForm.orderId
      this.modifyOrderForm.remark = this.modifyRemark
      this.modifyOrderForm.status = this.OrderForm.status
      request.post("/order/updateOrder",this.modifyOrderForm).then(res =>{
        if (res.success === true){
          this.dialogFormVisible7 = false
          this.modifyOrderForm = {}
          this.loadOrder()
          this.$message.success(res.message)
        }else {
          this.$message.error(res.message)
        }
      })
    },
    sureOrder() {
      this.modifyOrderForm = {}
      this.modifyOrderForm.orderId = this.OrderForm.orderId
      this.modifyOrderForm.status = "3"
      request.post("/order/updateOrder",this.modifyOrderForm).then(res =>{
        if (res.success === true){
          this.dialogFormVisible8 = false
          this.modifyOrderForm = {}
          this.loadOrder()
          this.$message.success(res.message)
        }else {
          this.$message.error(res.message)
        }
      })
    },
    cancelOrder() {
      if (this.OrderForm.status === "1"){
        this.modifyOrderForm = {}
        this.modifyOrderForm.orderId = this.OrderForm.orderId
        this.modifyOrderForm.status = "4"
        request.post("/order/updateOrder",this.modifyOrderForm).then(res =>{
          if (res.success === true){
            this.dialogFormVisible9 = false
            this.modifyOrderForm = {}
            this.loadOrder()
            this.$message.success(res.message)
          }else {
            this.$message.error(res.message)
          }
        })
      }else {
        if (this.OrderForm.status === "2"){
          this.$message.error("订单正在派送阶段,不能取消")
          this.dialogFormVisible9 =false
        }
        if (this.OrderForm.status === "3"){
          this.$message.error("订单已完成")
          this.dialogFormVisible9 =false
        }
        if (this.OrderForm.status === "4"){
          this.$message.error("订单已取消")
          this.dialogFormVisible9 =false
        }
      }
    },
  }
}
</script>


<style scoped>
/*文字标题*/
.title {
  color: #8399aa;
  font-size: 25px;
  position: relative;
  line-height: 61px;
  margin-left: 10px;
  text-align: center;
}
a {
  text-decoration: none;
  color: white;
}

</style>
