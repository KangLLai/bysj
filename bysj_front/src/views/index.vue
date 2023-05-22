<template>
  <div>
    <div class="head">
      <el-menu router
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
          <div>
              <el-cascader
                  style="width: 150px;margin-right: 25px"
                  placeholder="省份 | 城市"
                  clearable
                  v-model="provinceCity"
                  :options="options"
                  :props="{ expandTrigger: 'hover'}"
                  @change="handleChange">
              </el-cascader>

            <el-input v-model="actor"  style="width: 150px;margin-right: 5px;" suffix-icon="el-icon-search" placeholder="相关演出人"></el-input>
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
            <el-button style="margin-right: 50px;margin-left: 10px" type="primary" class="search-btn" @click="load">搜索</el-button>
          </div>
          <img src="../static/image/lbxx.jpg" alt="" style="width: 60px;">
          <span class="title">线上购票系统</span>

          <div style="height: 61px;line-height: 61px;display: flex;justify-content: flex-end;margin-left: 5px;">
          <el-button type="primary" icon="el-icon-s-custom" @click="userLogout">注销</el-button>
        </div>
          </div>
      </el-menu>
    </div>

    <div style="background-color: #8399aa">
      <!--走马灯-->
      <el-carousel :interval="4000" type="card" height="400px">
        <el-carousel-item v-for="(item,index) in images" :key="index">
          <img :src="item.url" class="bannerimg"/>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div style="margin-top: 50px">
      <el-table
          align="center"
          ref="singleTable"
          :data="tableData"
          highlight-current-row
          @current-change="handleCurrentChange"
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
                         property="id"
                         label="票务id"
                         width="50">
        </el-table-column>
        <el-table-column header-align="center"
                         align="center"
                         property="imageName"
                         label="封面"
                         width="120">
          <template v-slot="{ row }">
            <el-image style="width: 70px; height: 50px; border:none;cursor: pointer;" :src="require(`@/static/image/`+ row.image)"
                      :preview-src-list="[ require(`@/static/image/`+ row.image) ]">
              <div slot="error" class="image-slot">
                <img src="../static/image/noImg.png"  style="width: auto; height: 40px; border:none;" >
              </div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column header-align="center"
                         align="center"
                         property="title"
                         label="标题"
                         width="200">
        </el-table-column>
        <el-table-column header-align="center"
                         align="center"
                         property="actor"
                         label="演出人"
                         width="120">
        </el-table-column>
        <el-table-column header-align="center"
                         align="center"
                         property="price"
                         label="价格"
                         width="120">
        </el-table-column>
        <el-table-column header-align="center"
                         align="center"
                         property="actionTime"
                         label="演出时间"
                         width="200">
        </el-table-column>
        <el-table-column header-align="center"
                         align="center"
                         property="describeInfo"
                         label="描述信息"
                         width="200">
        </el-table-column>
        <el-table-column header-align="center"
                         align="center"
                         property="province"
                         label="省份"
                         width="120">
        </el-table-column>
        <el-table-column header-align="center"
                         align="center"
                         property="city"
                         label="城市"
                         width="120">
        </el-table-column>
        <el-table-column header-align="center"
                         align="center"
                         property="place"
                         label="演出地"
                         width="120">
        </el-table-column>
      </el-table>

      <div style="text-align: center;margin-top: 10px">
        <el-button type="primary" icon="el-icon-thumb" @click="handleAdd">去下单</el-button>
      </div>

      <div class="block" style="margin-bottom: 30px;margin-top: 30px">
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








      <!--下单弹窗-->
      <el-dialog title="订单信息" :visible.sync="dialogFormVisible1">
        <el-form :model="orderForm" label-width="100px">
          <el-form-item label="订单标题" >
            <el-input v-model="orderForm.title" :disabled="true" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item v-if="false" label="票品id" >
            <el-input v-model="orderForm.ticketId" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="演出人" >
            <el-input v-model="orderForm.actor" :disabled="true" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="描述信息" >
            <el-input v-model="orderForm.describeInfo" :disabled="true" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="票品价格" >
            <el-input v-model="orderForm.money" :disabled="true" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="演出时间" >
            <el-input v-model="orderForm.actionTime" :disabled="true" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="观演人" >
            <el-input v-model="orderForm.viewName" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="身份证" >
            <el-input v-model="orderForm.idCard" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="手机号" >
            <el-input v-model="orderForm.phone" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>

          <el-form-item label="地址">
            <template style="width: 500px">
              <el-select  :value="addressObject"  value-key="id" placeholder="请选择"  @change="changeAddressSelect" >
                <el-option v-for="addressObject in addressArr" :key="addressObject.id" :label="addressObject.destination"
                           :value="addressObject">
                </el-option>
              </el-select>
              <el-button style="margin-left: 20px" type="submit" @click="toAddAddress">添加收货地址</el-button>
            </template>
          </el-form-item>

          <el-form-item label="备注" >
            <el-input v-model="orderForm.remark" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer" style="text-align: center;">
          <el-button @click="dialogFormVisible1 = false">取 消</el-button>
          <el-button type="primary" @click="saveOrder">确 定</el-button>
        </div>
      </el-dialog>




      <!--添加收货地址弹窗-->
      <el-dialog
          title="添加收货地址"
          :visible.sync="dialogFormVisible3"
          width="30%"
          center>
        <el-form :model="addressForm" label-width="100px">
          <el-form-item label="收货地址" >
            <el-input v-model="addressForm.destination" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="收货人" >
            <el-input v-model="addressForm.name" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="收货手机号码" >
            <el-input v-model="addressForm.phone" autocomplete="off" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="是否默认" >
            <div>
              <el-radio v-model="addressForm.isDefault" label="1"  border>设为默认</el-radio>
              <el-radio v-model="addressForm.isDefault" label="0"  border>不设为默认</el-radio>
            </div>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible3 = false">取 消</el-button>
    <el-button type="primary" @click="saveAddress">确 定</el-button>
        </span>
      </el-dialog>









    </div>

  </div>
</template>


<script>
import request from "@/utils/request";
import router from "@/router";
export default {
  name: "index",
  data() {
    return {
      activeName: 'userLogin',
      images: [
        {url: require("../static/image/zjl.jpg")},
        {url: require("../static/image/ljj.jpg")},
        {url: require("../static/image/bp.jpg")},
        {url: require("../static/image/cyx.jpg")},
        {url: require("../static/image/iu.jpg")},
        {url: require("../static/image/wyt.jpg")}
      ],
      provinceCity:[],
      options: [{value: '北京', label: '北京'},{value: '上海', label: '上海'},{value: '天津', label: '天津'},{value: '重庆', label: '重庆'},{value: '澳门', label: '澳门'},
        {value: '香港', label: '香港'},

        {value: '广东省', label: '广东省', children: [{value: '广州', label: '广州',},{value: '深圳', label: '深圳',},{value: '东莞', label: '东莞',},{value: '中山', label: '中山',},
            {value: '惠州', label: '惠州',},{value: '江门', label: '江门',},{value: '珠海', label: '珠海',},{value: '汕头', label: '汕头',},{value: '佛山', label: '佛山',},
            {value: '湛江', label: '湛江',},{value: '河源', label: '河源',},{value: '肇庆', label: '肇庆',},{value: '潮州', label: '潮州',},{value: '清远', label: '清远',},
            {value: '韶关', label: '韶关',},{value: '揭阳', label: '揭阳',},{value: '阳江', label: '阳江',},{value: '云浮', label: '云浮',},{value: '茂名', label: '茂名',},
            {value: '梅州', label: '梅州',},{value: '汕尾', label: '汕尾',},]},

        {value: '山东省', label: '山东省', children: [{value: '济南', label: '济南',},{value: '青岛', label: '青岛',},{value: '临沂', label: '临沂',},{value: '济宁', label: '济宁',},
            {value: '菏泽', label: '菏泽',},{value: '烟台', label: '烟台',},{value: '泰安', label: '泰安',},{value: '淄博', label: '淄博',},{value: '潍坊', label: '潍坊',},
            {value: '日照', label: '日照',},{value: '威海', label: '威海',},{value: '滨州', label: '滨州',},{value: '东营', label: '东营',},{value: '聊城', label: '聊城',},
            {value: '德州', label: '德州',},{value: '莱芜', label: '莱芜',},{value: '枣庄', label: '枣庄',}]},

        {value: '江苏省', label: '江苏省', children: [{value: '苏州', label: '苏州',},{value: '徐州', label: '徐州',},{value: '盐城', label: '盐城',},{value: '无锡', label: '无锡',},
            {value: '南京', label: '南京',},{value: '南通', label: '南通',},{value: '连云港', label: '连云港',},{value: '常州', label: '常州',},{value: '扬州', label: '扬州',},
            {value: '镇江', label: '镇江',},{value: '淮安', label: '淮安',},{value: '泰州', label: '泰州',},{value: '宿迁', label: '宿迁',}]},

        {value: '浙江省', label: '浙江省', children: [{value: '温州', label: '温州',},{value: '宁波', label: '宁波',},{value: '杭州', label: '杭州',},{value: '台州', label: '台州',},
            {value: '嘉兴', label: '嘉兴',},{value: '金华', label: '金华',},{value: '湖州', label: '湖州',},{value: '绍兴', label: '绍兴',},{value: '舟山', label: '舟山',},
            {value: '丽水', label: '丽水',},{value: '衢州', label: '衢州',}]},

        {value: '湖南省', label: '湖南省', children: [{value: '长沙', label: '长沙',},{value: '邵阳', label: '邵阳',},{value: '常德', label: '常德',},{value: '衡阳', label: '衡阳',},
            {value: '株洲', label: '株洲',},{value: '湘潭', label: '湘潭',},{value: '永州', label: '永州',},{value: '岳阳', label: '岳阳',},{value: '怀化', label: '怀化',},
            {value: '郴州', label: '郴州',},{value: '娄底', label: '娄底',},{value: '张家界', label: '张家界',},{value: '湘西州', label: '湘西州',}]},

        {value: '山西省', label: '山西省', children: [{value: '太原', label: '太原',},{value: '大同', label: '大同',},{value: '运城', label: '运城',},{value: '长治', label: '长治',},
            {value: '晋城', label: '晋城',},{value: '忻州', label: '忻州',},{value: '临汾', label: '临汾',},{value: '吕梁', label: '吕梁',},{value: '晋中', label: '晋中',},
            {value: '阳泉', label: '阳泉',},{value: '朔州', label: '朔州',}]},

        {value: '海南省', label: '海南省', children: [{value: '三亚', label: '三亚',},{value: '海口', label: '海口',},{value: '琼海', label: '琼海',},{value: '文昌', label: '文昌',},
            {value: '东方', label: '东方',},{value: '昌江县', label: '昌江县',},{value: '陵水县', label: '陵水县',},{value: '乐东县', label: '乐东县',},{value: '万宁', label: '万宁',}]},

      ],
      addressForm:{},
      addressObject:{},
      addressArr:[],
      addressId:'',
      actor: "",
      activeIndex: '1',
      orderTime: '',
      beginTime: '',
      endTime: '',
      province: '',
      city : '',
      pageIndex:1,
      pageSize:5,
      total : null,
      dialogFormVisible1:false,
      dialogFormVisible2:false,
      dialogFormVisible3:false,
      dialogFormVisible4:false,
      dialogFormVisible5:false,
      form :{},
      orderForm:{},
      userForm :{},
      adminForm :{},
      tableData: []
    }
  },
  computed: {},
  watch:{
    orderTime (val) {
      if (val && val.length >= 2) {
        this.beginTime = val[0]
        this.endTime = val[1]
      } else {
        this.beginTime = ''
        this.endTime = ''
      }
    },
    provinceCity(val){
      if (val.length === 1){
        this.province = null
        this.city = val[0]
      }else if (val.length === 2){
        this.province = val[0]
        this.city = val[1]
      }else if (val.length === 0){
        this.province = null
        this.city = null
      }
    }
  },
  created() {
    this.load()
  },
  methods: {
    handleSelect(key, keyPath) {

      console.log(key, keyPath);
    },
    //改变地址
    changeAddressSelect(addressObject){
      this.addressObject = addressObject
      this.orderForm.addressId = addressObject.id
      this.orderForm.address = addressObject.destination
      this.orderForm.addressName = addressObject.name
      console.log(this.orderForm.address)
      console.log(this.orderForm.addressName)
    },

    handleSizePageChange(val){
      this.pageSize = val
      this.form = {}
      this.load()
    },
    handleCurrentPageChange(val){
      this.pageIndex = val
      this.form = {}
      this.load()
    },
    handleChange(value) {
      this.value =value
      console.log(value);
    },
    handleCurrentChange(val) {
      if (val){
        this.currentRow = val;
        this.form =this.currentRow
        console.log("当前行数据👇")
        console.log(this.form)
      }
    },
    /*让序号不受分页影响,分页后不从1开始*/
    count(index) {
      return (this.pageIndex - 1) * this.pageSize + index + 1
    },
    load(){
      request.get("/ticket/queryTicket",{
        params:{
          pageIndex : this.pageIndex,
          pageSize : this.pageSize,
          province: this.province,
          city: this.city,
          beginTime : this.beginTime,
          endTime : this.endTime,
          actor: this.actor
        }
      }).then(res =>{
        console.log(res)
        console.log(this.actor)
        this.tableData = res.data
        this.total =res.total
      })
    },
    handleAdd(){
      if (!this.form.id){
        this.$message.error("请选择订单")
      }else {
        //下单弹窗
        this.dialogFormVisible1 = true;
        this.getDefaultAddress()
        this.getAllAddress()
        //给下单弹窗的表单赋值
        this.orderForm.title = this.form.title
        this.orderForm.ticketId = this.form.id
        this.orderForm.actor = this.form.actor
        this.orderForm.describeInfo = this.form.describeInfo
        this.orderForm.money = this.form.price
        this.orderForm.actionTime = this.form.actionTime
        console.log(this.orderForm)
      }

    },
    saveOrder(){
      console.log("orderForm数据")
      console.log(this.orderForm)
      request.post("/order/saveOrder",this.orderForm).then(res =>{
        if (res.success === true){
          this.$message.success(res.message)
          this.dialogFormVisible1 = false
          this.orderForm = {}
        }else {
          this.$message.error(res.message)
        }
      })
    },
    userLogout() {
      this.$router.push('/')
      request.post("/user/logout").then(res => {
        if (res.success === true) {
          this.$message.success(res.message)
        } else {
          this.$message.error(res.message)
        }
      })
    },
    getAllAddress(){
      request.post("/address/queryAddress").then(res =>{
        if (res.success === true){
            this.addressArr = res.data
          console.log(this.addressArr)
        }else {
          this.$message.error(res.message)
        }

      })
    },
    getDefaultAddress(){   //获取默认地址
      request.post("/address/getDefault").then(res =>{
        this.addressObject = res.data
        this.orderForm.addressId = this.addressObject.id
        this.orderForm.address = this.addressObject.destination
        this.orderForm.addressName = this.addressObject.name
      })
    },
    toAddAddress(){
      this.dialogFormVisible3 = true
      this.addressForm = {}
    },
    saveAddress(){
      console.log("收货=========")
      console.log(this.addressForm)
      request.post("/address/saveAddress",this.addressForm).then(res =>{
        if (res.success === true){
          this.$message.success(res.message)
          this.dialogFormVisible3 = false
          this.addressForm = {}
          this.getAllAddress()
        }else {
          this.$message.error(res.message)
        }
      })
    }
  }
}



</script>

<style scoped>
.head {

}

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

.el-menu-item.is-active {
  color: #fff !important;
  background: #8399aa !important;
}


.el-carousel__item h3 {
  color: #475669;
  font-size: 14px;
  opacity: 0.75;
  line-height: 200px;
  margin: 0;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n+1) {
  background-color: #d3dce6;
}

/*图片自适应填充*/
.bannerimg {
  width: 100%;
  height: inherit;
}








</style>