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
package com.akaxin.site.business.impl.site;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.proto.core.ConfigProto;
import com.akaxin.site.business.constant.GroupConfig;
import com.akaxin.site.business.dao.SiteConfigDao;

/**
 * 管理站点配置相关信息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-01-14 21:18:49
 */
public class SiteConfig {
	private static final Logger logger = LoggerFactory.getLogger(SiteConfig.class);

	private static volatile Map<Integer, String> configMap;
	private static volatile Set<String> siteManagerSet = new HashSet<String>();

	private SiteConfig() {
	}

	public static Map<Integer, String> getConfigMap() {
		if (configMap == null) {
			configMap = updateConfig();
		}
		return configMap;
	}

	public static Map<Integer, String> updateConfig() {
		try {
			configMap = SiteConfigDao.getInstance().getSiteConfig();
			if (configMap != null) {
				String siteManageUsers = getConfigMap().get(ConfigProto.ConfigKey.SITE_MANAGER_VALUE);
				if (StringUtils.isNotEmpty(siteManageUsers)) {
					String[] adminUsers = siteManageUsers.split(",");
					List<String> managerList = Arrays.asList(adminUsers);
					siteManagerSet = new HashSet<String>(managerList);
					logger.info("update new site manage users={}", siteManagerSet);
				}
			}
		} catch (Exception e) {
			logger.error("update site config error.", e);
		}
		return configMap;
	}

	public static String getConfig(int key) {
		try {
			return getConfigMap().get(key);
		} catch (Exception e) {
			logger.error("get config value error", e);
		}
		return null;
	}

	/**
	 * <pre>
	 * 站点是否关闭，关闭的站点，用户不能注册以及登陆功能
	 * 		1.siteStatus=0,站点关闭
	 * 		2.siteStatus=1,站点开启
	 * </pre>
	 * 
	 * @return true/false
	 */
	public static boolean isOpen() {
		if (getConfigMap() != null) {
			String value = getConfigMap().get(ConfigProto.ConfigKey.SITE_STATUS_VALUE);

			if (StringUtils.isNumeric(value)) {
				return ConfigProto.SiteStatusConfig.OPEN_VALUE == Integer.valueOf(value);
			}
		}
		return false;
	}

	/**
	 * 判断站点是否关闭状态
	 * 
	 * @return
	 */
	public static boolean isClose() {
		return !isOpen();
	}

	// 获取是否需要实名配置
	public static ConfigProto.RealNameConfig getRealNameConfig() {
		ConfigProto.RealNameConfig realNameConfig = null;
		if (getConfigMap() != null) {
			String value = getConfigMap().get(ConfigProto.ConfigKey.REALNAME_STATUS_VALUE);
			if (StringUtils.isNumeric(value)) {
				realNameConfig = ConfigProto.RealNameConfig.forNumber(Integer.valueOf(value));
			}
		}
		return realNameConfig == null ? ConfigProto.RealNameConfig.REALNAME_NO : realNameConfig;
	}

	// 获取邀请码配置
	public static ConfigProto.InviteCodeConfig getUICConfig() {
		ConfigProto.InviteCodeConfig uicConfig = null;
		if (getConfigMap() != null) {
			String value = getConfigMap().get(ConfigProto.ConfigKey.INVITE_CODE_STATUS_VALUE);
			if (StringUtils.isNumeric(value)) {
				uicConfig = ConfigProto.InviteCodeConfig.forNumber(Integer.valueOf(value));
			}
		}
		return uicConfig == null ? ConfigProto.InviteCodeConfig.UIC_NO : uicConfig;
	}

	public static String getSiteAddress() {
		if (getConfigMap() != null) {
			String siteHost = getConfigMap().get(ConfigProto.ConfigKey.SITE_ADDRESS_VALUE);
			String sitePort = getConfigMap().get(ConfigProto.ConfigKey.SITE_PORT_VALUE);
			return siteHost + ":" + sitePort;
		}
		return null;
	}

	/**
	 * 获取超级管理员
	 * 
	 * @return
	 */
	public static String getSiteSuperAdmin() {
		if (getConfigMap() != null) {
			String siteAdmin = getConfigMap().get(ConfigProto.ConfigKey.SITE_ADMIN_VALUE);
			return siteAdmin;
		}
		return null;
	}

	/**
	 * 判断是否为超级管理员
	 * 
	 * @param siteUserId
	 * @return
	 */
	public static boolean isSiteSuperAdmin(String siteUserId) {
		if (StringUtils.isNotEmpty(siteUserId)) {
			return siteUserId.equals(getSiteSuperAdmin());
		}
		return false;
	}

	/**
	 * 判断是否为普通管理员
	 * 
	 * @param siteUserId
	 * @return
	 */
	public static boolean isSiteManager(String siteUserId) {
		if (isSiteSuperAdmin(siteUserId)) {
			return true;
		}
		if (siteManagerSet != null && StringUtils.isNotEmpty(siteUserId)) {
			return siteManagerSet.contains(siteUserId);
		}
		return false;
	}

	/**
	 * 判断是否存在超级管理员
	 * 
	 * @return
	 */
	public static boolean hasNoAdminUser() {
		return !hasAdminUser();
	}

	public static boolean hasAdminUser() {
		if (StringUtils.isNotEmpty(getSiteSuperAdmin())) {
			return true;
		}
		return false;
	}

	public static int getMaxGroupMemberSize() {
		try {
			Map<Integer, String> map = getConfigMap();
			if (map != null) {
				String memberCount = map.get(ConfigProto.ConfigKey.GROUP_MEMBERS_COUNT_VALUE);
				if (StringUtils.isNumeric(memberCount)) {
					return Integer.valueOf(memberCount);
				}
			}
		} catch (Exception e) {
			logger.error("get max group member size error.", e);
		}
		return GroupConfig.GROUP_MAX_MEMBER_COUNT;
	}

	public static ConfigProto.U2EncryptionConfig getU2EncryStatusConfig() {
		try {
			Map<Integer, String> map = getConfigMap();
			if (map != null) {
				String statusNum = map.get(ConfigProto.ConfigKey.U2_ENCRYPTION_STATUS_VALUE);
				if (StringUtils.isNumeric(statusNum)) {
					return ConfigProto.U2EncryptionConfig.forNumber(Integer.valueOf(statusNum));
				}
			}
		} catch (Exception e) {
			logger.error("get u2 encry status error", e);
		}
		return ConfigProto.U2EncryptionConfig.U2_CLOSE;
	}

	public static String getSiteLogo() {
		try {
			Map<Integer, String> map = getConfigMap();
			if (map != null) {
				return map.get(ConfigProto.ConfigKey.SITE_LOGO_VALUE);
			}
		} catch (Exception e) {
			logger.error("get site logo error", e);
		}
		return null;
	}
}