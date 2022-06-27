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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.GroupMessageBean;
import com.akaxin.site.storage.service.MessageDaoService;

/**
 * 群通知消息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-26 15:13:24
 */
public class GroupMessageNoticeHandler extends AbstractGroupHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(GroupMessageNoticeHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	@Override
	public Boolean handle(Command command) {
		try {
			int type = command.getMsgType();

			if (CoreProto.MsgType.GROUP_NOTICE_VALUE == type) {
				ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
						.parseFrom(command.getParams());
				String siteUserId = command.getSiteUserId();
				String deviceId = command.getDeviceId();

				String proxySiteUserId = request.getGroupMsgNotice().getSiteUserId();
				String groupId = request.getGroupMsgNotice().getSiteGroupId();
				String groupNoticeText = request.getGroupMsgNotice().getText().toStringUtf8();
				String msgId = request.getGroupMsgNotice().getMsgId();

				GroupMessageBean bean = new GroupMessageBean();
				bean.setMsgId(msgId);
				// bean.setSendUserId(siteUserId);
				bean.setSendUserId(command.isProxy() ? proxySiteUserId : siteUserId);
				bean.setSendDeviceId(deviceId);
				bean.setSiteGroupId(groupId);
				bean.setContent(groupNoticeText);
				bean.setMsgType(type);
				bean.setMsgTime(System.currentTimeMillis());

				LogUtils.requestDebugLog(logger, command, bean.toString());

				return messageDao.saveGroupMessage(bean);
			}
			return true;
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}
		return false;
	}

}
