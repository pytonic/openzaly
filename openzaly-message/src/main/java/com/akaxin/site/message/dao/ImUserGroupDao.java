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
package com.akaxin.site.message.dao;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.storage.api.IGroupDao;
import com.akaxin.site.storage.api.IUserGroupDao;
import com.akaxin.site.storage.bean.GroupMemberBean;
import com.akaxin.site.storage.bean.GroupProfileBean;
import com.akaxin.site.storage.service.GroupDaoService;
import com.akaxin.site.storage.service.UserGroupDaoService;

public class ImUserGroupDao {
	private static final Logger logger = LoggerFactory.getLogger(ImUserGroupDao.class);
	private IGroupDao groupDao = new GroupDaoService();
	private IUserGroupDao userGroupDao = new UserGroupDaoService();

	private ImUserGroupDao() {
	}

	static class SingletonHolder {
		private static ImUserGroupDao instance = new ImUserGroupDao();
	}

	public static ImUserGroupDao getInstance() {
		return SingletonHolder.instance;

	}

	public boolean isGroupMember(String siteUserId, String groupId) {
		try {
			GroupMemberBean bean = groupDao.getGroupMember(siteUserId, groupId);
			if (bean != null && StringUtils.isNotEmpty(bean.getUserId())) {
				return true;
			}
		} catch (SQLException e) {
			logger.error("is group member error.", e);
		}
		return false;
	}

	public GroupProfileBean getGroupProfile(String groupId) {
		try {
			return groupDao.queryGroupProfile(groupId);
		} catch (SQLException e) {
			logger.error("get group profile error.");
		}
		return null;
	}

	public GroupProfileBean getSimpleGroupProfile(String groupId) {
		try {
			return groupDao.querySimpleGroupProfile(groupId);
		} catch (SQLException e) {
			logger.error("get group profile error.");
		}
		return null;
	}

	public boolean isMesageMute(String siteUserId, String siteGroupId) {
		try {
			return userGroupDao.isMute(siteUserId, siteGroupId);
		} catch (SQLException e) {
			logger.error("query message mutet status error", e);
		}
		return true;
	}

}
