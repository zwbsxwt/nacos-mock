/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.mnt.domain;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author zhanghouying
* @date 2019-08-24
*/

@Getter
@Setter
@TableName("mnt_deploy")
public class Deploy extends BaseEntity implements Serializable {

	@TableId(value = "deploy_id", type = IdType.AUTO)
	@ApiModelProperty(value = "ID", hidden = true)
    private Long id;

	@ApiModelProperty(value = "应用编号")
	private Long appId;

	@TableField(exist = false)
	@ApiModelProperty(name = "服务器", hidden = true)
	private Set<Server> deploys;

	@TableField(exist = false)
    private App app;

    public void copy(Deploy source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

	public String getServers() {
		if(CollectionUtil.isNotEmpty(deploys)){
			return deploys.stream().map(Server::getName).collect(Collectors.joining(","));
		}
		return "";
	}
}
