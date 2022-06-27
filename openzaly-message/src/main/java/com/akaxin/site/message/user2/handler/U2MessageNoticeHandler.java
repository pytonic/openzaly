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
package com.akaxin.site.message.user2.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.logs.LogUtils;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.message.group.handler.AbstractGroupHandler;
import com.akaxin.site.storage.api.IMessageDao;
import com.akaxin.site.storage.bean.U2MessageBean;
import com.akaxin.site.storage.service.MessageDaoService;

/**
 * 二人通知消息
 * 
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-26 15:08:51
 */
public class U2MessageNoticeHandler extends AbstractGroupHandler<Command> {
	private static final Logger logger = LoggerFactory.getLogger(U2MessageNoticeHandler.class);
	private IMessageDao messageDao = new MessageDaoService();

	@Override
	public Boolean handle(Command command) {
		try {
			int type = command.getMsgType();

			if (CoreProto.MsgType.U2_NOTICE_VALUE == type) {
				ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest
						.parseFrom(command.getParams());
				String siteUserId = command.getSiteUserId();
				String proxySiteUserId = request.getU2MsgNotice().getSiteUserId();
				String siteFriendId = request.getU2MsgNotice().getSiteFriendId();
				String text = request.getU2MsgNotice().getText().toStringUtf8();
				String msgId = request.getU2MsgNotice().getMsgId();

				long msgTime = System.currentTimeMillis();
				U2MessageBean u2Bean = new U2MessageBean();
				u2Bean.setMsgId(msgId);
				u2Bean.setMsgType(type);
				u2Bean.setSiteUserId(siteFriendId);
				u2Bean.setSendUserId(command.isProxy() ? proxySiteUserId : siteUserId);
				u2Bean.setReceiveUserId(siteFriendId);
				u2Bean.setContent(text);
				u2Bean.setMsgTime(msgTime);

				LogUtils.requestDebugLog(logger, command, u2Bean.toString());

				if (command.isProxy()) {
					U2MessageBean proxyBean = new U2MessageBean();
					proxyBean.setMsgId(msgId);
					proxyBean.setMsgType(type);
					proxyBean.setSiteUserId(proxySiteUserId);
					proxyBean.setSendUserId(proxySiteUserId);
					proxyBean.setReceiveUserId(siteFriendId);
					proxyBean.setContent(text);
					proxyBean.setMsgTime(msgTime);
					messageDao.saveU2Message(proxyBean);
				}

				return messageDao.saveU2Message(u2Bean);
			}

			return true;
		} catch (Exception e) {
			LogUtils.requestErrorLog(logger, command, this.getClass(), e);
		}
		return false;
	}

}
