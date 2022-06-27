/** 
 * Copyright 2018-2028 Akaxin Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
*/
package com.akaxin.site.boot.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.akaxin.proto.core.ConfigProto;
import com.akaxin.site.boot.utils.PropertiesUtils;

/**
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-24 12:50:20
 */
public class ConfigHelper implements ConfigKey {
	private static Properties prop;

	private ConfigHelper() {

	}

	public static Properties getProperties() {
		if (prop == null) {
			prop = PropertiesUtils.getOZProperties();
		}
		return prop;
	}

	/**
	 * 获取服务启动时设置的配置参数，如果启动未设置，则通过配置文件获取默认的值
	 * 
	 * @param configName
	 * @return
	 */
	public static String getStringConfig(String configName) {
		String configValue = System.getProperty(configName);
		if (StringUtils.isEmpty(configValue)) {
			Object obj = getProperties().get(configName);
			return obj != null ? obj.toString() : null;
		}
		return configValue;
	}

	public static int getIntConfig(String configName) {
		String configValue = System.getProperty(configName);
		if (StringUtils.isBlank(configValue)) {
			configValue = getProperties().get(configName).toString();
		}
		return Integer.parseInt(configValue);
	}

	public static Map<Integer, String> getConfigMap() {
		Map<Integer, String> configMap = new HashMap<Integer, String>();
		configMap.put(ConfigProto.ConfigKey.SITE_VERSION_VALUE, getStringConfig(SITE_VERSION));
		configMap.put(ConfigProto.ConfigKey.SITE_ADDRESS_VALUE, getStringConfig(SITE_ADDRESS));
		configMap.put(ConfigProto.ConfigKey.SITE_PORT_VALUE, getStringConfig(SITE_PORT));
		// 扩展的http功能接口
		configMap.put(ConfigProto.ConfigKey.SITE_HTTP_ADDRESS_VALUE, getStringConfig(PLUGIN_API_ADDRESS));
		configMap.put(ConfigProto.ConfigKey.SITE_HTTP_PORT_VALUE, getStringConfig(PLUGIN_API_PORT));
		String basePath = System.getProperty("user.dir");
		configMap.put(ConfigProto.ConfigKey.PIC_PATH_VALUE, basePath);//存放资源的位置
		configMap.put(ConfigProto.ConfigKey.DB_PATH_VALUE, basePath);
		configMap.put(ConfigProto.ConfigKey.GROUP_MEMBERS_COUNT_VALUE, getStringConfig(GROUP_MEMBERS_COUNT));
		// 默认二人绝密聊天状态：开启二人绝密聊天功能
		configMap.put(ConfigProto.ConfigKey.U2_ENCRYPTION_STATUS_VALUE,
				ConfigProto.U2EncryptionConfig.U2_OPEN_VALUE + "");
		// 默认匿名
		configMap.put(ConfigProto.ConfigKey.REALNAME_STATUS_VALUE, ConfigProto.RealNameConfig.REALNAME_NO_VALUE + "");
		// 默认开启邀请码
		configMap.put(ConfigProto.ConfigKey.INVITE_CODE_STATUS_VALUE, ConfigProto.InviteCodeConfig.UIC_YES_VALUE + "");
		// 默认Push状态：不显示push内容
		configMap.put(ConfigProto.ConfigKey.PUSH_CLIENT_STATUS_VALUE,
				String.valueOf(ConfigProto.PushClientStatus.PUSH_HIDDEN_TEXT_VALUE));
		return configMap;
	}
}
