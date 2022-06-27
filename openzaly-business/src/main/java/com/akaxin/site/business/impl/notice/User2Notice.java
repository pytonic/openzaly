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
package com.akaxin.site.business.impl.notice;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.common.command.Command;
import com.akaxin.common.constant.RequestAction;
import com.akaxin.common.utils.StringHelper;
import com.akaxin.proto.client.ImStcNoticeProto;
import com.akaxin.proto.core.CoreProto;
import com.akaxin.proto.core.CoreProto.MsgType;
import com.akaxin.proto.site.ImCtsMessageProto;
import com.akaxin.site.business.constant.NoticeText;
import com.akaxin.site.message.api.IMessageService;
import com.akaxin.site.message.service.ImMessageService;
import com.akaxin.site.storage.bean.ApplyFriendBean;
import com.google.protobuf.ByteString;

public class User2Notice {
	private static final Logger logger = LoggerFactory.getLogger(User2Notice.class);
	private IMessageService imService = new ImMessageService();

	/**
	 * A用户添加用户B为好友，发送B有好友添加的申请 <br>
	 * 
	 * 客户端接收到此通知，会在通讯录的好友申请提示
	 * 
	 * @param siteUserId
	 *            用户B的用户ID
	 */
	public void applyFriendNotice(String siteUserId, String siteFriendId) {
		try {
			Command command = new Command();
			command.setSiteUserId(siteUserId);
			command.setSiteFriendId(siteFriendId);
			command.setAction(RequestAction.IM_STC_NOTICE.getName());
			ImStcNoticeProto.ImStcNoticeRequest noticeRequest = ImStcNoticeProto.ImStcNoticeRequest.newBuilder()
					.setType(ImStcNoticeProto.NoticeType.APPLY_FRIEND).build();
			command.setParams(noticeRequest.toByteArray());
			// 发送im.stc.notice消息
			boolean result = imService.execute(command);
			logger.debug("siteUserId={} apply friend notice friendId={} result={}", siteUserId, siteFriendId, result);
		} catch (Exception e) {
			logger.error("send apply friend notice error", e);
		}
	}

	/**
	 * <pre>
	 * 代发一条U2本文消息 
	 * AB成为好友，同时发送A/B一条消息
	 * </pre>
	 * 
	 * @param siteUserId
	 *            接受请求方
	 * @param siteFriendId
	 *            发送请求方，注意二者的关系
	 */
	public void addFriendTextMessage(ApplyFriendBean bean) {
		try {
			// 给“发送请求方”下发的文本消息
			CoreProto.MsgText textMsg = CoreProto.MsgText.newBuilder().setMsgId(buildU2MsgId(bean.getSiteUserId()))
					.setSiteUserId(bean.getSiteUserId()).setSiteFriendId(bean.getSiteFriendId())
					.setText(ByteString.copyFromUtf8(NoticeText.USER_ADD_FRIEND)).setTime(System.currentTimeMillis())
					.build();
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest.newBuilder()
					.setType(MsgType.TEXT).setText(textMsg).build();

			Command command = new Command();
			command.setAction(RequestAction.IM_CTS_MESSAGE.getName());
			command.setSiteUserId(bean.getSiteUserId());
			command.setSiteFriendId(bean.getSiteFriendId());
			command.setParams(request.toByteArray());

			boolean result = imService.execute(command);
			logger.debug("add friend Text message siteUserId={} siteFriendId={} result={}", bean.getSiteUserId(),
					bean.getSiteFriendId(), result);
		} catch (Exception e) {
			logger.error(StringHelper.format("send add friend text message error. bean={}", bean), e);
		}

		try {
			// 给“接受请求方”下发消息，文案使用“发送请求方”方添加好友时候使用的文案eg：“我是章三，添加你为好友”
			String applyText = "我添加了你为好友";// 默认文案
			long applyTime = System.currentTimeMillis();
			if (StringUtils.isNotEmpty(bean.getApplyInfo())) {
				applyText = bean.getApplyInfo();
			}
			if (bean.getApplyTime() > 0) {
				applyTime = bean.getApplyTime();
			}
			CoreProto.MsgText textMsg = CoreProto.MsgText.newBuilder().setMsgId(buildU2MsgId(bean.getSiteFriendId()))
					.setSiteUserId(bean.getSiteFriendId()).setSiteFriendId(bean.getSiteUserId())
					.setText(ByteString.copyFromUtf8(applyText)).setTime(applyTime).build();
			ImCtsMessageProto.ImCtsMessageRequest request = ImCtsMessageProto.ImCtsMessageRequest.newBuilder()
					.setType(MsgType.TEXT).setText(textMsg).build();

			Command command = new Command();
			command.setAction(RequestAction.IM_CTS_MESSAGE.getName());
			command.setSiteUserId(bean.getSiteFriendId());
			command.setSiteFriendId(bean.getSiteUserId());
			command.setParams(request.toByteArray());

			boolean result = imService.execute(command);
			logger.debug("add friend Text message siteUserId={} siteFriendId={} result={}", bean.getSiteFriendId(),
					bean.getSiteUserId(), result);
		} catch (Exception e) {
			logger.error(StringHelper.format("send add friend text message error. bean={}", bean), e);
		}
	}

	private String buildU2MsgId(String siteUserid) {
		StringBuilder sb = new StringBuilder("U2-");
		if (StringUtils.isNotEmpty(siteUserid)) {
			int len = siteUserid.length();
			sb.append(siteUserid.substring(0, len >= 8 ? 8 : len));
			sb.append("-");
		}
		sb.append(System.currentTimeMillis());
		return sb.toString();
	}
}
