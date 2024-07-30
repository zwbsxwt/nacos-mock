<template>
  <div class="app-container">
    <!--工具栏-->
    <div class="head-container">
      <!--      <div v-if="crud.props.searchToggle">-->
      <!--        &lt;!&ndash; 搜索 &ndash;&gt;-->
      <!--        <label class="el-form-item-label">serviceName</label>-->
      <!--        <el-input v-model="query.serviceName" clearable placeholder="serviceName" style="width: 185px;" class="filter-item" @keyup.enter.native="crud.toQuery" />-->
      <!--        <rrOperation :crud="crud" />-->
      <!--      </div>-->
      <!--如果想在工具栏加入更多按钮，可以使用插槽方式， slot = 'left' or 'right'-->
      <crudOperation :permission="permission"/>
      <!--表单组件-->
      <el-dialog :close-on-click-modal="false" :append-to-body="true" :before-close="crud.cancelCU"
                 :visible.sync="crud.status.cu > 0" :title="crud.status.title" width="1200px">
        <el-form ref="form" :model="form" :inline="true" :rules="rules" size="small" label-width="150px">
          <el-form-item label="mock名称">
            <el-input v-model="form.mockName" style="width: 370px;"/>
          </el-form-item>
          <el-form-item label="serviceName">
            <el-input v-model="form.serviceName" :disabled="true" style="width: 370px;"/>
          </el-form-item>
          <el-form-item label="method">
            <el-select v-model="form.method" placeholder="Select method" style="width: 370px;">
              <el-option label="GET" value="GET"></el-option>
              <el-option label="POST" value="POST"></el-option>
              <el-option label="PUT" value="PUT"></el-option>
              <el-option label="DELETE" value="DELETE"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="urlPattern">
            <el-input v-model="form.urlPattern" style="width: 370px;"/>
          </el-form-item>

          <el-form-item label="权重">
            <el-input v-model="form.weight" style="width: 370px;"/>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="form.remark" style="width: 370px;"/>
          </el-form-item>

          <el-form-item label="是否开启">
            <el-switch
              v-model="form.isOpen"
              :active-value="1"
              :inactive-value="0"
            >
            </el-switch>
          </el-form-item>
          <el-tabs v-model="activeName">
            <el-tab-pane label="路由规则" name="expression">
              <el-form-item label="路由规则">
                <el-input v-model="form.expression" type="textarea"
                          :rows="5" style="width: 370px;"/>
              </el-form-item>
            </el-tab-pane>
            <el-tab-pane label="返回报文" name="responseTemplate">
              <el-form-item label="返回报文">
                <el-input v-model="form.responseTemplate" type="textarea"
                          :rows="10" style="width: 670px;"/>
              </el-form-item>

            </el-tab-pane>

            <el-tab-pane label="返回消息头" name="responseHeader">
              <el-form-item label="返回消息头">
                <el-input v-model="form.responseHeader" type="textarea"
                          :rows="5" style="width: 370px;"/>
              </el-form-item>
            </el-tab-pane>

          </el-tabs>


          <!--          <el-form-item label="createTime">-->
          <!--            <el-input v-model="form.createTime" style="width: 370px;" />-->
          <!--          </el-form-item>-->

        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="text" @click="crud.cancelCU">取消</el-button>
          <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">确认</el-button>
        </div>
      </el-dialog>
      <!--表格渲染-->
      <el-table ref="table" v-loading="crud.loading" :data="crud.data" size="small" style="width: 100%;"
                @selection-change="crud.selectionChangeHandler">
        <!--        <el-table-column type="selection" width="55"/>-->
        <el-table-column prop="mockName" label="mock名称"/>
        <!--        <el-table-column prop="serviceName" label="serviceName" />-->
        <el-table-column prop="method" label="method"/>
        <el-table-column prop="urlPattern" label="url"/>
        <el-table-column prop="expression" label="命中表达"/>
        <el-table-column prop="responseTemplate" label="返回报文">
          <template slot-scope="scope">
            <el-popover
              title="返回报文"
              trigger="hover"
              placement="right"
            >
              <div style="max-height: 400px; overflow-y: auto;">
                <json-viewer
                  :expand-depth="5"
                  :value="parseJSON(scope.row.responseTemplate)"
                ></json-viewer>
              </div>

              <el-button slot="reference">悬浮展示报文</el-button>
            </el-popover>

          </template>
        </el-table-column>
        <el-table-column prop="responseHeader" label="响应Header"/>
        <el-table-column prop="remark" label="备注"/>
        <el-table-column prop="weight" label="权重"/>

        <el-table-column prop="isOpen" label="是否开启">
          <template slot-scope="scope">
            <el-switch
              :disabled="true"
              v-model="scope.row.isOpen"
              :active-value="1"
              :inactive-value="0"
            >
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150px"/>
        <el-table-column v-if="checkPer(['admin','mockEndpoint:edit','mockEndpoint:del'])" label="操作" width="180px"
                         align="center">
          <template slot-scope="scope">

            <udOperation
              :data="scope.row"
              :permission="permission"
            >
              <el-button slot="left" v-permission="permission.edit" :loading="crud.status.cu === 2"  size="mini" type="primary"   @click.stop="crud.toCopy(scope.row)" >
                复制
              </el-button>
            </udOperation>
          </template>
        </el-table-column>
      </el-table>
      <!--分页组件-->
      <pagination/>
    </div>
  </div>
</template>

<script>
import crudMockEndpoint from '@/api/mock/mockEndpoint'
import CRUD, {presenter, header, form, crud} from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'

const defaultForm = {
  id: null,
  mockName: null,
  serviceName: null,
  method: 'POST',
  urlPattern: null,
  expression: null,
  responseTemplate: null,
  responseHeader: '{"Content-Type":"application/json"}',
  remark: null,
  weight: 100,
  createTime: null,
  isOpen: 1
}
export default {
  name: 'MockEndpoint',
  components: {pagination, crudOperation, rrOperation, udOperation},
  mixins: [presenter(), header(), form(defaultForm), crud()],
  dicts: ['is_open'],
  cruds() {
    return CRUD({
      title: 'mock-api',
      url: 'api/mockEndpoint',
      optShow: {add: true, download: false},
      idField: 'id',
      sort: 'id,desc',
      crudMethod: {...crudMockEndpoint}
    })
  },
  props: {
    serviceName: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      permission: {
        add: ['admin', 'mockEndpoint:add'],
        edit: ['admin', 'mockEndpoint:edit'],
        del: ['admin', 'mockEndpoint:del']
      },
      activeName: 'responseTemplate',
      rules: {},
      queryTypeOptions: [
        {key: 'serviceName', display_name: 'serviceName'}
      ]
    }
  },
  methods: {
    // 钩子：在获取表格数据之前执行，false 则代表不获取数据
    [CRUD.HOOK.beforeRefresh]() {
      this.query.serviceName = this.serviceName
      defaultForm.serviceName = this.serviceName
      return true
    },
    [CRUD.HOOK.beforeToAdd](row) {
      console.log('row:',row)
      return true
    },
    parseJSON(data) {
      try {
        // 如果数据为空或不是字符串，返回一个空对象
        if (!data || typeof data !== 'string') {
          return data;
        }
        // 尝试解析 JSON 字符串
        return JSON.parse(data);
      } catch (error) {
        // 如果解析失败，返回一个空对象
        return data;
      }
    }
  }
}
</script>

<style scoped>

</style>
