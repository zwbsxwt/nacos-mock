import request from '@/utils/request'

export function add(data) {
  return request({
    url: 'api/nacosMockServer',
    method: 'post',
    data
  })
}
export function readNg() {
  return request({
    url: 'api/nginx',
    method: 'get'
  })
}
export function writeNg(data) {
  return request({
    url: 'api/nginx',
    method: 'post',
    data
  })
}
export function restartNginx() {
  return request({
    url: 'api/nginx/restart',
    method: 'post'
  })
}
export function activateMockService(data) {
  return request({
    url: 'api/nacosMockServer/activateMockService',
    method: 'post',
    data
  })
}

export function del(ids) {
  return request({
    url: 'api/nacosMockServer/',
    method: 'delete',
    data: ids
  })
}

export function edit(data) {
  return request({
    url: 'api/nacosMockServer',
    method: 'put',
    data
  })
}

export default {add, edit, del, activateMockService,readNg,writeNg,restartNginx}
