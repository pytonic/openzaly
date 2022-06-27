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
package com.akaxin.site.message.group.handler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.channel.ChannelWriter;
import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;
import com.akaxin.common.constant.CommandConst;
import com.akaxin.common.constant.ErrorCode2;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.client.ImStcPsnProto;
import com.akaxin.site.message.dao.ImUserSessionDao;
import com.akaxin.site.storage.api.IGroupDao;
import com.akaxin.site.storage.service.GroupDaoService;

public class GroupPsnHandler extends AbstractGroupHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(GroupPsnHandler.class);
	private IGroupDao groupDao = new GroupDaoService();

	public Boolean handle(Command command) {
		try {
			String siteGroupId = command.getSiteGroupId();
			String siteUserId = command.getSiteUserId();
			String siteDeviceId = command.getDeviceId();
			String siteFriendId = command.getSiteFriendId();

			List<String> groupMembers = groupDao.getGroupMembersId(siteGroupId);
			for (String groupMemberUserId : groupMembers) {
				List<String> deivceIds = ImUserSessionDao.getInstance().getSessionDevices(groupMemberUserId);
				for (String deviceId : deivceIds) {
					if (StringUtils.isNotEmpty(deviceId) && !deviceId.equals(siteDeviceId)) {
						writePSN(deviceId);
						logger.debug("client={} siteUserId={} PSN to groupId={} siteFriendId={}, deviceId={}",
								command.getClientIp(), siteUserId, siteGroupId, siteFriendId, deviceId);
					}
				}

			}
			return true;
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}

		return false;
	}

	private void writePSN(String deviceId) {
		CommandResponse commandResponse = new CommandResponse().setVersion(CommandConst.PROTOCOL_VERSION)
				.setAction(CommandConst.IM_STC_PSN);
		ImStcPsnProto.ImStcPsnRequest pshRequest = ImStcPsnProto.ImStcPsnRequest.newBuilder().build();
		commandResponse.setParams(pshRequest.toByteArray());
		commandResponse.setErrCode2(ErrorCode2.SUCCESS);
		ChannelWriter.writeByDeviceId(deviceId, commandResponse);
	}

}
