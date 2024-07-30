<template>
  <div class="app-container">
    <!--工具栏-->
    <div class="head-container">
      <!--如果想在工具栏加入更多按钮，可以使用插槽方式， slot = 'left' or 'right'-->
<!--      <crudOperation :permission="permission" />-->
      <!--表单组件-->
      <el-dialog :close-on-click-modal="false" :append-to-body="true" :before-close="crud.cancelCU" :visible.sync="crud.status.cu > 0" :title="crud.status.title" width="800px">
        <el-form ref="form" :model="form" :rules="rules" size="small" label-width="180px">
          <el-form-item label="serviceName" prop="serviceName">
            <el-input v-model="form.serviceName" :disabled="true" style="width: 370px;" />
          </el-form-item>
<!--          <el-form-item label="configOptions">-->
<!--            <el-input v-model="form.configOptions" style="width: 370px;" />-->
<!--          </el-form-item>-->
          <el-form-item label="forwardUrl">
            <el-input v-model="form.forwardUrl" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="remark">
            <el-input v-model="form.remark" style="width: 370px;" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="text" @click="crud.cancelCU">取消</el-button>
          <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">确认</el-button>
        </div>
      </el-dialog>


      <el-drawer
        title="mockServer配置"
        :append-to-body="true"
        size="70%"
        :visible.sync="drawer"
        :direction="'rtl'"
      >
        <mockEndpoint v-if="drawer" :serviceName="serviceName"/>
      </el-drawer>



      <!--表格渲染-->
      <el-table ref="table" v-loading="crud.loading" :border="true" :data="crud.data" size="small" style="width: 100%;" @selection-change="crud.selectionChangeHandler">
<!--        <el-table-column type="selection" width="55" />-->
        <el-table-column prop="serviceName" label="服务名" width="228" />
<!--        <el-table-column prop="configOptions" label="configOptions" />-->
        <el-table-column prop="forwardUrl" label="默认转发地址" />
        <el-table-column prop="remark" label="备注" />
<!--        <el-table-column width="150"  v-if="checkPer(['admin','nacosMockServer:edit','nacosMockServer:del'])" label="api-mock"-->
<!--                         align="center">-->
<!--          <template slot-scope="scope">-->
<!--            &lt;!&ndash;            <el-button type="text" @click="openMockServerOption(scope.row)">mock管理</el-button>&ndash;&gt;-->
<!--            <el-button type="text" @click="openMockServer(scope.row)">点击mock</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
        <el-table-column v-if="checkPer(['admin','mockServiceConfig:edit','mockServiceConfig:del'])" label="操作" width="150px" align="center">
          <template slot-scope="scope">
            <udOperation
              :data="scope.row"
              :permission="permission"
            />
          </template>
        </el-table-column>

        <template slot="empty">
          <el-button
            v-if="crud.optShow.add"
            v-permission="permission.add"
            class="filter-item"
            size="mini"
            type="primary"
            icon="el-icon-plus"
            @click="crud.toAdd"
          >
            优先创建我
          </el-button>
        </template>
      </el-table>


      <!--分页组件-->
<!--      <pagination />-->
    </div>
  </div>
</template>

<script>
import crudMockServiceConfig from '@/api/mock/mockServiceConfig'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'
import mockEndpoint from "@/views/mock/mock-endpoint";
const defaultForm = { id: null, serviceName: null, configOptions: null, forwardUrl: null, remark: null }
export default {
  name: 'MockServiceConfig',
  components: { pagination, crudOperation, rrOperation, udOperation, mockEndpoint },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  props:{
    serviceName:{
      type:String,
      default:""
    }
  },
  cruds() {
    return CRUD({ title: 'mockserver配置', url: 'api/mockServiceConfig', idField: 'id', sort: 'id,desc', crudMethod: { ...crudMockServiceConfig }})
  },
  data() {
    return {
      drawer:false,
      permission: {
        add: ['admin', 'mockServiceConfig:add'],
        edit: ['admin', 'mockServiceConfig:edit'],
        del: ['admin', 'mockServiceConfig:del']
      },
      rules: {
        serviceName: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      }    }
  },
  methods: {
    // 钩子：在获取表格数据之前执行，false 则代表不获取数据
    [CRUD.HOOK.beforeRefresh]() {
      this.query.serviceName=this.serviceName
      defaultForm.serviceName=this.serviceName
      return true
    },
    openMockServer(row) {
      this.drawer = true
    },
  }
}
</script>

<style scoped>

</style>
