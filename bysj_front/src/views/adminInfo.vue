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
          <el-button type="primary" icon="el-icon-s-custom" @click="adminLogout">注销</el-button>
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
            <el-form :model="adminInfoForm" label-width="120px">
              <el-form-item label="姓名">
                <el-input v-model="adminInfoForm.name"></el-input>
              </el-form-item>
              <el-form-item label="登录账号">
                <el-input v-model="adminInfoForm.loginName"></el-input>
              </el-form-item>
              <el-form-item label="性别">
                <el-input v-model="adminInfoForm.sex"></el-input>
              </el-form-item>

              <el-form-item label="登陆密码">
                <el-input v-model="password" type="password" :disabled="true">
                  <el-button style="padding-right:10px" slot="suffix" type="text" @click="dialogFormVisible5=true">修改
                  </el-button>
                </el-input>
              </el-form-item>
              <el-form-item label="手机号码">
                <el-input v-model="phone" type="password" :disabled="true">
                  <el-button style="padding-right:10px" slot="suffix" type="text" @click="dialogFormVisible4 = true">
                    更换
                  </el-button>
                </el-input>
              </el-form-item>
              <el-form-item label="身份证号码">
                <el-input v-model="idCard" type="password" :disabled="true">
                  <el-button style="padding-right:10px" slot="suffix" type="text" @click="dialogFormVisible6=true">修改
                  </el-button>
                </el-input>
              </el-form-item>
              <el-form-item>
                <div style="text-align: center">
                  <el-button type="primary" @click="updateAdminInfo">修改</el-button>
                </div>
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
          订单管理
        </span>

          <div style="height: 61px;line-height: 61px;display: flex;justify-content: center;margin-right: 5px">
            <div>
              <el-input v-model="searchOrderId" style="width: 150px;margin-right: 5px;" suffix-icon="el-icon-search"
                        placeholder="订单编号"></el-input>
              <el-input v-model="searchViewName" style="width: 150px;margin-right: 5px;" suffix-icon="el-icon-search"
                        placeholder="观演人"></el-input>
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
                               width="120">
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
                               width="120">
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
                               width="120">
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
                               width="200">
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
                               width="120">
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
                               width="120">
                <el-button type="primary" @click="paiSong">派送</el-button>
              </el-table-column>
            </el-table>
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
          </div>
        </el-tab-pane>


        <el-tab-pane>
          <span slot="label"><i class="el-icon-date"></i>
          票务管理
        </span>


          <div style="text-align: center">
            <el-cascader
                style="width: 200px;margin-right: 10px"
                placeholder="省份 | 城市"
                clearable
                v-model="provinceCity"
                :options="options"
                :props="{ expandTrigger: 'hover'}"
                @change="handleChange">
            </el-cascader>

            <el-input v-model="actor" style="width: 150px;margin-right: 5px;" suffix-icon="el-icon-search"
                      placeholder="相关演出人"></el-input>
            <el-date-picker v-model="orderTimeT"
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
            <el-button style="margin-right: 50px" type="primary" class="search-btn" @click="loadTicket">搜索</el-button>
          </div>

          <div style="margin-top: 50px">
            <el-table
                align="center"
                ref="singleTable"
                :data="tableData"
                highlight-current-row
                @current-change="handleTicketCurrentChange"
                style="width: 100%">
              <el-table-column header-align="center"
                               align="center"
                               type="index"
                               :index="countT"
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
                  <el-image style="width: 70px; height: 50px; border:none;cursor: pointer;"
                            :src="require(`@/static/image/`+ row.image)"
                            :preview-src-list="[ require(`@/static/image/`+ row.image) ]">
                    <div slot="error" class="image-slot">
                      <img src="../static/image/noImg.png" style="width: auto; height: 40px; border:none;">
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
            <div style="text-align: center;margin-top: 20px">
            <el-button  type="submit" @click="toAddTicket" >添加票务</el-button>
            <el-button  type="submit" @click="toUpdateTicket" >修改票务</el-button>
            <el-button  type="submit" @click="toDeleteTicket" >删除票务</el-button>
            </div>
            <div class="block" style="margin-bottom: 30px;margin-top: 30px">
              <el-pagination
                  style="text-align: center;margin-top: 20px"
                  @size-change="handleSizePageTChange"
                  @current-change="handleCurrentPageTChange"
                  :current-page="pageIndexT"
                  :page-sizes="[2, 5, 10, 20]"
                  :page-size="pageSizeT"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="totalT">
              </el-pagination>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>


    <!--修改手机号码-->
    <el-dialog
        title="修改手机号码"
        :visible.sync="dialogFormVisible4"
        width="30%"
        center>
      <el-form label-width="100px">
        <el-form-item label="手机号">
          <el-input v-model="phone" type="password" :disabled="true" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="新手机号">
          <el-input v-model="modifyPhone" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="验证码">
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
        <el-form-item label="密码">
          <el-input v-model="password" type="password" :disabled="true" autocomplete="off"
                    style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="modifyPassword" type="password" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="验证码">
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
        <el-form-item label="身份证">
          <el-input v-model="idCard" type="password" :disabled="true" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="新身份证">
          <el-input v-model="modifyIdCard" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="验证码">
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



    <!--添加票务-->
    <el-dialog
        title="添加票务"
        :visible.sync="dialogFormVisible1"
        width="30%"
        center>
      <el-form :model="ticketForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="ticketForm.title"  autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="演出人">
          <el-input v-model="ticketForm.actor" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="省份城市">
          <el-cascader
              style="width: 200px;margin-right: 10px"
              placeholder="省份 | 城市"
              clearable
              v-model="provinceCityT"
              :options="optionsT"
              :props="{ expandTrigger: 'hover'}"
              @change="handleChangeT">
          </el-cascader>
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="ticketForm.place" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="票品单价">
          <el-input v-model="ticketForm.price" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="演出时间">
          <div class="block">
            <el-date-picker
                v-model="ticketForm.actionTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择日期时间">
            </el-date-picker>
          </div>
        </el-form-item>
        <el-form-item label="描述信息">
          <el-input v-model="ticketForm.describeInfo" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="上传封面">
          <el-upload
              class="avatar-uploader"
              action="http://localhost:8090/common/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload">
            <img v-if="imageUrl" :src="imageUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible1 = false">取 消</el-button>
    <el-button type="primary" @click="addTicket">确 定</el-button>
        </span>
    </el-dialog>

    <!--修改票务-->
    <el-dialog
        title="修改票务"
        :visible.sync="dialogFormVisible2"
        width="30%"
        center>
      <el-form :model="modifyTicketForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="modifyTicketForm.title"  autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="演出人">
          <el-input v-model="modifyTicketForm.actor" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="省份城市">
          <el-cascader
              style="width: 200px;margin-right: 10px"
              placeholder="省份 | 城市"
              clearable
              v-model="provinceCityT"
              :options="optionsT"
              :props="{ expandTrigger: 'hover'}"
              @change="handleChangeT">
          </el-cascader>
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="modifyTicketForm.place" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="票品单价">
          <el-input v-model="modifyTicketForm.price" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="演出时间">
          <div class="block">
            <el-date-picker
                v-model="modifyTicketForm.actionTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择日期时间">
            </el-date-picker>
          </div>
        </el-form-item>
        <el-form-item label="描述信息">
          <el-input v-model="modifyTicketForm.describeInfo" autocomplete="off" style="width: 80%"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible2 = false">取 消</el-button>
    <el-button type="primary" @click="updateTicket">确 定</el-button>
        </span>
    </el-dialog>

    <!--删除票务-->
    <el-dialog
        title="删除票务"
        :visible.sync="dialogFormVisible3"
        width="30%"
        center>
      <h3>是否确定删除该票务信息?</h3>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible3 = false">取 消</el-button>
    <el-button type="primary" @click="deleteTicket">确 定</el-button>
      </span>
    </el-dialog>



  </div>
</template>


<script>
import request from "@/utils/request";

export default {
  name: "adminInfo",
  data() {
    return {
      activeIndex: '4',
      provinceCity: [],
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
      provinceCityT: [],
      optionsT: [{value: '北京', label: '北京'},{value: '上海', label: '上海'},{value: '天津', label: '天津'},{value: '重庆', label: '重庆'},{value: '澳门', label: '澳门'},
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
      dialogFormVisible4: false,
      dialogFormVisible5: false,
      dialogFormVisible6: false,
      dialogFormVisible1: false,  //添加票务
      dialogFormVisible2: false,  //修改票务
      dialogFormVisible3: false,  //删除票务
      OrderForm: {},
      adminInfoForm: {},
      orderTableData: [],
      pageIndex: 1,
      pageSize: 5,
      total: 0,
      password: "",
      phone: "",
      modifyPhone: "",
      modifyPassword: "",
      modifyIdCard: "",
      modifyLoginName: "",
      modifyOrderForm: {},
      code: "",
      idCard: "",
      searchOrderId: "",
      searchViewName: "",
      orderTime: '',
      beginTime: '',
      endTime: '',
      tableData: [],
      beginTimeT: '',
      endTimeT: '',
      province: '',
      city: '',
      provinceT: '',
      cityT: '',
      pageIndexT: 1,
      pageSizeT: 5,
      totalT: null,
      actor: '',
      orderTimeT: '',
      ticketForm : {},
      imageUrl : '',
      modifyTicketForm : {},
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
    },
    orderTimeT(val) {
      if (val && val.length >= 2) {
        this.beginTimeT = val[0]
        this.endTimeT = val[1]
      } else {
        this.beginTimeT = ''
        this.endTimeT = ''
      }
    },
    provinceCity(val) {
      if (val.length === 1) {
        this.province = null
        this.city = val[0]
      } else if (val.length === 2) {
        this.province = val[0]
        this.city = val[1]
      } else if (val.length === 0) {
        this.province = null
        this.city = null
      }
    },
    provinceCityT(val) {  //添加订单使用
      if (val.length === 1) {
        this.provinceT = null
        this.cityT = val[0]
        this.ticketForm.city = this.cityT
        this.modifyTicketForm.city = this.cityT
      } else if (val.length === 2) {
        this.provinceT = val[0]
        this.cityT = val[1]
        this.ticketForm.province = this.provinceT
        this.ticketForm.city = this.cityT
        this.modifyTicketForm.province = this.provinceT
        this.modifyTicketForm.city = this.cityT
      } else if (val.length === 0) {
        this.provinceT = null
        this.cityT = null
      }
    }
  },
  created() {
    this.getAdminInfo()
    this.loadOrder()
    this.loadTicket()
  },
  methods: {

    // 上传成功后的回显
    handleAvatarSuccess(res,file) {
      function sleep (time) {//图片上传需要时间,休眠2秒在赋值给url让图片回显
        return new Promise((resolve) => setTimeout(resolve, time));
      }
      sleep(2000).then(() =>{
        //this.imageUrl = require(`@/static/image/${res.data}`)
        this.imageUrl = URL.createObjectURL(file.raw);
        this.ticketForm.image = res.data
        console.log("image-------" + this.ticketForm.image)
      })
      this.dialogFormVisible1=true
    },
    // 上传前对类型大小的验证
    beforeAvatarUpload(file,fileList) {
      const isJPG = file.type === 'image/jpeg';
      const isPNG = file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 10;

      if (!isJPG && !isPNG) {
        this.$message.error('上传图片只能是 JPG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 10MB!');
      }
      return file;
    },
    handleChange(value) {
      this.value = value
      console.log(value);
    },handleChangeT(value) {
      this.value = value
      console.log(value);
    },
    handleSizePageTChange(val) {
      this.pageSizeT = val
      this.loadTicket()
    },
    handleCurrentPageTChange(val) {
      this.pageIndexT = val
      this.loadTicket()
    },
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
    },
    handleTicketCurrentChange(val) {
      if (val) {
        this.currentRow = val;
        this.modifyTicketForm = this.currentRow
        this.provinceCityT = []
        if (this.modifyTicketForm.province){
          this.provinceCityT[0] = this.modifyTicketForm.province
          this.provinceCityT[1] = this.modifyTicketForm.city
        }else{
          this.provinceCityT[0] = this.modifyTicketForm.city
        }
        console.log("provincityT-----"  + this.provinceCityT)
        console.log("当前行数据👇")
        console.log(this.modifyTicketForm)
      }
    },
    handleOrderCurrentChange(val) {
      if (val) {
        this.currentRow = val;
        this.OrderForm = this.currentRow
        console.log("当前行数据👇")
        console.log(this.OrderForm)
        console.log(this.OrderForm.orderId)
      }
    },
    handleSizePageChange(val) {
      this.pageSize = val
      this.orderTableData = []
      this.loadOrder()
    },
    handleCurrentPageChange(val) {
      this.pageIndex = val
      this.orderTableData = []
      this.loadOrder()
    },
    /*让序号不受分页影响,分页后不从1开始*/
    count(index) {
      return (this.pageIndex - 1) * this.pageSize + index + 1
    },
    countT(index) {
      return (this.pageIndexT - 1) * this.pageSizeT + index + 1
    },
    adminLogout() {
      this.$router.push('/')
      request.post("/administrator/logout").then(res => {
        if (res.success === true) {
          this.$message.success(res.message)
        } else {
          this.$message.error(res.message)
        }
      })
    },
    getAdminInfo() {
      request.get("/administrator/queryAdministrator").then(res => {
        if (res.success === true) {
          this.adminInfoForm = res.data
          this.password = res.data.password
          this.phone = res.data.phone
          this.idCard = res.data.idCard
          this.modifyLoginName = res.data.loginName
          this.adminInfoForm.phone = null
          this.adminInfoForm.idCard = null
          this.adminInfoForm.password = null
        } else {
          this.$message.error(res.message)
        }
      })
    },
    updateAdminInfo() {
      if (this.adminInfoForm.loginName === this.modifyLoginName) {
        //说明登录名没修改
        this.adminInfoForm.loginName = null
      }
      request.post("/administrator/updateAdministrator", this.adminInfoForm).then(res => {
        if (res.success === true) {
          this.$message.success(res.message)
          this.getAdminInfo()
        } else {
          this.$message.error(res.message)
        }
      })
    },
    loadOrder() {
      if (this.searchOrderId) {
        this.pageIndex = 1
        this.pageSize = 5
      }
      request.get("/order/queryAllOrders", {
        params: {
          pageIndex: this.pageIndex,
          pageSize: this.pageSize,
          orderId: this.searchOrderId,
          viewName: this.searchViewName,
          beginTime: this.beginTime,
          endTime: this.endTime,
        }
      }).then(res => {
        console.log(res)
        this.orderTableData = []
        this.orderTableData = res.data
        this.total = res.total
      })
    },
    getCode() { //获取验证码
      request.post("/administrator/sendMsg").then(res => {
        if (res.success === true) {
          this.$message.success(res.message)
        } else {
          this.$message.error(res.message)
        }
      })
    },
    updatePhone() {
      request.get("/administrator/updatePhone", {
        params: {
          phone: this.modifyPhone,
          code: this.code
        }
      }).then(res => {
        if (res.success === true) {
          this.modifyPhone = null
          this.code = null
          this.getAdminInfo()
          this.dialogFormVisible4 = false
          this.$message.success(res.message)
        } else {
          this.$message.error(res.message)
        }
      })
    },
    updatePassword() {
      request.get("/administrator/updatePassword", {
        params: {
          password: this.modifyPassword,
          code: this.code
        }
      }).then(res => {
        if (res.success === true) {
          this.modifyPassword = null,
              this.code = null
          this.getAdminInfo()
          this.dialogFormVisible5 = false
          this.$message.success(res.message)
        } else {
          this.$message.error(res.message)
        }
      })
    },
    updateIdCard() {
      request.get("/administrator/updateIdCard", {
        params: {
          idCard: this.modifyIdCard,
          code: this.code
        }
      }).then(res => {
        if (res.success === true) {
          this.modifyIdCard = null,
              this.code = null
          this.getAdminInfo()
          this.dialogFormVisible6 = false
          this.$message.success(res.message)
        } else {
          this.$message.error(res.message)
        }
      })
    },
    loadTicket() {
      request.get("/ticket/queryTicket", {
        params: {
          pageIndex: this.pageIndexT,
          pageSize: this.pageSizeT,
          province: this.province,
          city: this.city,
          beginTime: this.beginTimeT,
          endTime: this.endTimeT,
          actor: this.actor
        }
      }).then(res => {
        console.log(res)
        console.log(this.actor)
        this.tableData = res.data
        this.totalT = res.total
      })
    },
    toAddTicket(){
      this.ticketForm = {}
      this.provinceCityT = []
      this.imageUrl = null
      this.dialogFormVisible1 =true;
    },
    toUpdateTicket(){
      this.imageUrl = require(`@/static/image/${this.modifyTicketForm.image}`)
      this.dialogFormVisible2 =true;
    },
    toDeleteTicket(){
      this.dialogFormVisible3 =true;
    },
    addTicket(){
      request.post("/ticket/saveTicket",this.ticketForm).then(res => {
        if (res.success === true){
          this.$message.success(res.message)
          this.dialogFormVisible1 = false
          this.ticketForm = {}
          this.imageUrl = null
          this.provinceCityT = null

          this.loadTicket()
        }else {
          this.$message.error(res.message)
        }
      })
    },
    updateTicket(){
      request.post("/ticket/updateTicket",this.modifyTicketForm).then(res => {
        if (res.success === true){
          this.loadTicket()
          this.$message.success(res.message)
          this.dialogFormVisible2 = false
          this.modifyTicketForm = {}
          this.imageUrl = null
          this.provinceCityT = []
        }else {
          this.$message.error(res.message)
        }
      })
    },
    deleteTicket(){
      request.post("/ticket/deleteTicket/" +this.modifyTicketForm.id).then(res =>{
        if (res.success === true){
          this.loadTicket()
          this.$message.success(res.message)
          this.dialogFormVisible3 = false
        }else {
          this.$message.error(res.message)
        }
      })
    },
    paiSong(){
      if (this.OrderForm.status === "1"){
        this.modifyOrderForm.orderId = this.OrderForm.orderId
        this.modifyOrderForm.status = "2"
        request.post("/order/updateOrder",this.modifyOrderForm).then(res =>{
          if (res.success === true){
            this.modifyOrderForm = {}
            this.loadOrder()
            this.$message.success(res.message)
          }else {
            this.$message.error(res.message)
          }
        })
      }else {
        this.$message("该订单不处于待派送阶段")
      }

    }
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

/deep/ .avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}


</style>
