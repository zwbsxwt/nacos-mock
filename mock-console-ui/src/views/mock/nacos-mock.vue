<template>
  <div class="app-container">
    <!--工具栏-->
    <div class="head-container">
      <!--如果想在工具栏加入更多按钮，可以使用插槽方式， slot = 'left' or 'right'-->
      <crudOperation :permission="permission"/>
      <!--表单组件-->
      <el-dialog :close-on-click-modal="false" :before-close="crud.cancelCU" :visible.sync="crud.status.cu > 0"
                 :title="crud.status.title" width="900px">
        <el-form ref="form" :model="form" :rules="rules" size="small" label-width="180px">
          <el-form-item label="服务名">
            <el-input v-model="form.serviceName" style="width: 370px;"/>
          </el-form-item>

          <el-form-item label="nacos地址">
            <el-input v-model="form.nacosUrl" placeholder="http://ip:port" style="width: 370px;"/>
            <el-button @click="fetchNamespaces">获取nacos命名空间</el-button>
          </el-form-item>

          <el-form-item label="namespace">
            <!--            <el-input v-model="form.namespaceShowName" style="width: 370px;"/>-->
            <el-select v-model="form.namespaceId" @change="setnamespaceShowName" placeholder="请选择"
                       style="width: 370px;">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <!--          <el-form-item label="namespaces_id">-->
          <!--            <el-input v-model="form.namespaceId" style="width: 370px;" />-->
          <!--          </el-form-item>-->
          <!--          <el-form-item label="mockser的id">-->
          <!--            <el-input v-model="form.mockServerId" style="width: 370px;" />-->
          <!--          </el-form-item>-->
          <el-form-item label="mockserver-地址">
            <el-input v-model="form.mockServerIp" placeholder="ip" style="width: 270px;"/>
            <el-input v-model="form.mockServerPort" placeholder="端口号" style="width: 170px;"/>
            <!--            <el-button>创建</el-button>-->
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="text" @click="crud.cancelCU">取消</el-button>
          <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">确认</el-button>
        </div>
      </el-dialog>
      <!--表格渲染-->
      <el-table ref="table" v-loading="crud.loading" :data="crud.data" size="small" style="width: 100%;"
                @selection-change="crud.selectionChangeHandler">
        <el-table-column type="selection" width="55"/>
        <el-table-column label="nacos" align="center">
          <el-table-column prop="serviceName" label="服务名" width="170" align="center"/>

          <el-table-column prop="nacosUrl" label="url" width="230" align="center"/>
          <el-table-column label="namespace" align="center">
            <template slot-scope="scope">
              {{ scope.row.namespaceShowName }} | {{ scope.row.namespaceId }}
            </template>
          </el-table-column>
          <!--          <el-table-column prop="namespaceId" label="namespaceId"  align="center"/>-->
        </el-table-column>
        <!--        <el-table-column prop="mockServerId" label="mockser的id"/>-->
        <el-table-column label="mockServer" align="center">
          <el-table-column prop="mockServerIp" label="ip" align="center" width="180"/>
          <el-table-column prop="mockServerPort" label="port" width="120" align="center"/>
        </el-table-column>

        <el-table-column width="150" v-if="checkPer(['admin','nacosMockServer:edit','nacosMockServer:del'])"
                         label="端口管理"
                         align="center">
          <template slot-scope="scope">
            <el-button type="text" @click="openDrawerPort(scope.row)">端口管理</el-button>
          </template>
        </el-table-column>

        <el-table-column width="150" v-if="checkPer(['admin','nacosMockServer:edit','nacosMockServer:del'])"
                         label="服务管理"
                         align="center">
          <template slot-scope="scope">
            <el-button type="text" @click="openInstances(scope.row)">服务管理</el-button>
          </template>
        </el-table-column>


        <el-table-column width="150" v-if="checkPer(['admin','nacosMockServer:edit','nacosMockServer:del'])"
                         label="mock管理"
                         align="center">
          <template slot-scope="scope">
            <!--            <el-button type="te  xt" @click="openMockServerOption(scope.row)">mock管理</el-button>-->
            <el-button type="text" @click="openMockServerOption(scope.row)">mock管理</el-button>
          </template>
        </el-table-column>

        <el-table-column v-if="checkPer(['admin','nacosMockServer:edit','nacosMockServer:del'])" label="操作"
                         width="150px" align="center">
          <template slot-scope="scope">
            <udOperation
              :data="scope.row"
              :permission="permission"
            />
          </template>
        </el-table-column>
      </el-table>
      <!--分页组件-->
      <pagination/>

      <el-drawer
        title="端口管理"
        size="80%"
        :before-close="handleClose"
        :visible.sync="drawerPort"
        :direction="direction"
      >
        <el-form style="margin: 1rem">
          <div style="margin: 1rem;">
            <el-button type="primary" @click="handleConfirm('确认保存Ng配置',saveNginx)">保存Ng配置</el-button>
            <el-button type="primary" @click="handleConfirm('确认重启Ng',restartNginx)">重启Ng</el-button>
            <span style="font-size: 12px;margin: 1px">如果重启失败,请手工重启Nginx容器</span>
          </div>
          <el-input v-model="nginx" type="textarea"
                    :rows="30" style="width: 800px;"/>
        </el-form>
      </el-drawer>


      <el-drawer
        title="mock配置"
        size="80%"
        :visible.sync="drawerMockOption"
        :direction="direction"
      >
        <mockServerConfig v-if="drawerMockOption" :serviceName="serviceName"/>

        <mockEndpoint v-if="drawerMockOption" :serviceName="serviceName"/>
      </el-drawer>

      <el-drawer
        title="服务治理"
        size="70%"
        :visible.sync="drawer"
        :direction="direction"
      >

        <div style="margin: 1rem">

          <el-descriptions :border="true" :column="3" style="margin-bottom: 1rem">
            <el-descriptions-item label="服务名">
              {{ queryForm.serviceName }}
            </el-descriptions-item>
            <el-descriptions-item label="Nacos地址">
              {{ queryForm.nacosUrl }}
            </el-descriptions-item>
            <el-descriptions-item label="namespaceId">
              {{ queryForm.namespaceId }}
            </el-descriptions-item>

            <el-descriptions-item label="mockServer">
              {{ queryForm.mockServerIp }}:{{ queryForm.mockServerPort }}
            </el-descriptions-item>
          </el-descriptions>


          <el-button type="primary" style="margin-bottom: 1rem" @click="handleConfirm('激活mock',activateMockService)">
            激活mock服务
          </el-button>


          <el-table :data="hosts" style="width: 100%" :border="true" v-loading="tableLoading">
            <el-table-column prop="serviceName" label="服务名"></el-table-column>
            <el-table-column prop="ip" label="IP" width="180"></el-table-column>
            <el-table-column prop="port" label="端口" width="100"></el-table-column>
            <el-table-column prop="metadata" label="metadata">
              <template slot-scope="scope">
                {{ scope.row.metadata }}
              </template>
            </el-table-column>
            <el-table-column prop="healthy" label="健康状态" width="100">
              <template slot-scope="scope">
                {{ scope.row.healthy ? 'true' : 'false' }}
              </template>
            </el-table-column>
            <!--          <el-table-column prop="instanceId" label="Instance ID" width="300"></el-table-column>-->
            <!--          <el-table-column prop="clusterName" label="Cluster Name" width="180"></el-table-column>-->

            <el-table-column label="操作" width="180">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  @click="toggleInstanceStatus(scope.row)"
                  :type="scope.row.enabled ? 'danger' : 'success'"
                >
                  {{ scope.row.enabled ? '下线' : '上线' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-drawer>

    </div>
  </div>
</template>

<script>
import crudNacosMockServer, {activateMockService, readNg, writeNg, restartNginx} from '@/api/mock/nacosMockServer'
import CRUD, {presenter, header, form, crud} from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'
import axios from 'axios';
import {Message} from 'element-ui';
import mockServerConfig from '@/views/mock/mock-server-config.vue'
import mockEndpoint from "@/views/mock/mock-endpoint";

const defaultForm = {
  id: null,
  serviceName: null,
  nacosUrl: null,
  namespaceShowName: null,
  namespaceId: null,
  mockServerId: null,
  mockServerIp: null,
  mockServerPort: null
}
export default {
  name: 'NacosMockServer',
  components: {pagination, crudOperation, rrOperation, udOperation, mockServerConfig, mockEndpoint},
  mixins: [presenter(), header(), form(defaultForm), crud()],
  cruds() {
    return CRUD({
      title: 'nacos-mock',
      url: 'api/nacosMockServer',
      idField: 'id',
      sort: 'id,desc',
      crudMethod: {...crudNacosMockServer}
    })
  },
  data() {
    return {
      permission: {
        add: ['admin', 'nacosMockServer:add'],
        edit: ['admin', 'nacosMockServer:edit'],
        del: ['admin', 'nacosMockServer:del']
      },
      tableLoading: false,
      rules: {},
      options: [],
      queryForm: {
        nacosUrl: '',
        namespaceId: '',
        serviceName: '',
        mockServerIp: null,
        mockServerPort: null,
        metadata: ''
      },
      hosts: [],
      drawer: false,
      drawerMockOption: false,
      drawerPort: false,
      serviceName: '',
      direction: 'rtl',
      mockServerId: '',
      nginx: ''
    }
  },
  methods: {
    // 钩子：在获取表格数据之前执行，false 则代表不获取数据
    [CRUD.HOOK.beforeRefresh]() {
      return true
    },
    [CRUD.HOOK.beforeSubmit]() {
      this.form.mockServerId = this.form.serviceName + '-' + this.form.namespaceId
      return true
    },
    async fetchNamespaces() {
      let baseUrl = this.form.nacosUrl.trim();
      if (!baseUrl) {
        this.$message.error.error('Nacos地址不能为空');
        return;
      }
      // Remove trailing slash if present
      if (baseUrl.endsWith('/')) {
        baseUrl = baseUrl.slice(0, -1);
      }

      this.form.nacosUrl = baseUrl
      const url = `${baseUrl}/nacos/v1/console/namespaces`;


      try {
        const response = await axios.get(url);
        if (response.data.code === 200) {
          this.options = response.data.data
            .filter(item => item.namespace) // 过滤掉 namespace 为空的项
            .map(item => ({
              value: item.namespace,
              label: `${item.namespaceShowName} | ${item.namespace}`,
              namespaceShowName: item.namespaceShowName // 添加 namespaceShowName 属性
            }));

          // Default select the first option if available
          if (this.options.length > 0) {
            this.form.namespaceId = this.options[0].value;
            this.setnamespaceShowName(this.form.namespaceId)
          }
          this.$message.success('请求成功,请选择命名空间');
        } else {
          this.$message.error('获取命名空间失败: ' + response.data.message);
        }
      } catch (error) {
        this.$message.error('未能查询到在线服务');
        console.error('Error fetching namespaces:', error);
      }
    },


    async fetchServiceInstances() {
      const {nacosUrl, namespaceId, serviceName} = this.queryForm;
      if (!nacosUrl || !namespaceId || !serviceName) {
        Message.error('Nacos地址、namespaceId ID和Service Name不能为空');
        return;
      }
      const url = `${nacosUrl}/nacos/v1/ns/catalog/instances?namespaceId=${namespaceId}&serviceName=${serviceName}&clusterName=DEFAULT&pageSize=100&pageNo=1`;

      try {
        const response = await axios.get(url);
        if (response.status === 200 && response.data.list) {
          this.hosts = response.data.list;
        } else {
          Message.error('获取服务实例失败: ' + (response.data.message || '未知错误'));
        }
      } catch (error) {
        Message.error('未能查询到在线服务');
        console.error('Error fetching service instances:', error);
        this.hosts = []
      }
      // const url = `${nacosUrl}/nacos/v1/ns/instance/list?namespaceId=${namespaceId}&serviceName=${serviceName}`;
      //
      // try {
      //   const response = await axios.get(url);
      //   if (response.status === 200 && response.data.hosts) {
      //     this.hosts = response.data.hosts;
      //   } else {
      //     Message.error('获取服务实例失败: ' + (response.data.message || '未知错误'));
      //   }
      // } catch (error) {
      //   Message.error('未能查询到在线服务');
      //   console.error('Error fetching service instances:', error);
      //   this.hosts =[]
      // }
    },

    async activateMockService() {
      const {nacosUrl, namespaceId, serviceName, mockServerIp, mockServerPort} = this.queryForm;
      if (!nacosUrl || !namespaceId || !serviceName || !mockServerIp || !mockServerPort) {
        Message.error('Nacos地址、namespaceId、mockServerIp、mockServerPort和服务名 不能为空');
        return;
      }
      activateMockService(this.queryForm).then(data => {
        Message.success('操作成功,心跳服务需要几秒刷新')
        const _this = this;
        _this.tableLoading = true;
        setTimeout(function () {
          _this.fetchServiceInstances()
          _this.tableLoading = false;
        }, 1000)

        setTimeout(function () {
          _this.fetchServiceInstances()
          _this.tableLoading = false;
        }, 10000)
      }).catch(error => {
        Message.error('未能查询到在线服务，请检查地址是否有效');
        console.error('Error toggling instance status:', error);
      })
    },
    async toggleInstanceStatus(instance) {
      const {nacosUrl, namespaceId, serviceName} = this.queryForm;

      let metadata = encodeURIComponent('{"preserved.register.source":"MOCK-SERVER"}');

      const metadataString = typeof instance.metadata === 'string' ? instance.metadata : JSON.stringify(instance.metadata);

      // 检查是否包含 'MOCK-SERVER'
      if (metadataString.includes('MOCK-SERVER')) {
        metadata = encodeURIComponent('{"preserved.register.source":"MOCK-SERVER"}');
      } else {
        metadata = encodeURIComponent(metadataString);
      }

      const url = `${nacosUrl}/nacos/v1/ns/instance?port=${instance.port}&healthy=true&ip=${instance.ip}&serviceName=${serviceName}&namespaceId=${namespaceId}&enabled=${!instance.enabled}&metadata=${metadata}`;


      try {
        const response = await axios.post(url);
        if (response.data === 'ok') {
          Message.success(`${instance.enabled ? '下线' : '上线'}成功`);
          instance.enabled = !instance.enabled;
        } else {
          Message.error(`${instance.enabled ? '下线' : '上线'}失败: ` + (response.data.message || '未知错误'));
        }
      } catch (error) {
        Message.error('未能查询到在线服务');
        console.error('Error toggling instance status:', error);
      }
    },
    setnamespaceShowName(value) {
      const selectedOption = this.options.find(option => option.value === value);
      if (selectedOption) {
        this.form.namespaceShowName = selectedOption.namespaceShowName;
      }
    },
    openDrawerPort(row) {
      this.drawerPort = true;
      readNg().then(data => {
        this.nginx = data
      })
    },
    openMockServerOption(row) {
      this.serviceName = row.serviceName
      this.drawerMockOption = true;
    },
    openInstances(row) {
      this.queryForm = {...row}; // 使用展开运算符进行浅拷贝
      this.drawer = true
      this.fetchServiceInstances()
    },

    saveNginx() {
      writeNg(this.nginx).then(data=>{
        Message.success("保存成功")
      }).catch(err => {
        Message.error(err)
      })
    },

    restartNginx() {
      restartNginx().then(()=>{
        Message.success("重启成功")
      }).catch(err => {
        Message.error(err)
      })
    },

    handleClose(done) {
      this.$confirm('确认关闭？')
        .then(_ => {
          done();
        })
        .catch(_ => {
        });
    },
    handleConfirm(msg, done) {
      this.$confirm(`确认${msg}？`)
        .then(_ => {
          done();
        })
        .catch(_ => {
        });
    }
  }
}
</script>

<style scoped>

</style>
