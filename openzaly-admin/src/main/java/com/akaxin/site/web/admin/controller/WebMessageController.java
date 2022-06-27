package com.akaxin.site.web.admin.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.akaxin.proto.core.PluginProto;
import com.akaxin.site.business.dao.UserFriendDao;
import com.akaxin.site.business.dao.UserGroupDao;
import com.akaxin.site.business.impl.site.SiteConfig;
import com.akaxin.site.storage.bean.SimpleGroupBean;
import com.akaxin.site.storage.bean.SimpleUserBean;
import com.akaxin.site.web.admin.bean.WebMessageBean;
import com.akaxin.site.web.admin.common.MsgUtils;
import com.akaxin.site.web.admin.service.IMessageManageService;
import com.google.protobuf.InvalidProtocolBufferException;

@Controller
@RequestMapping("webMessage")
public class WebMessageController extends AbstractController {

	private String WEB_DEMO_CODE = "<!DOCTYPE html>" + "<html lang='en'>" + "" + "<head>" + "    <meta charset='UTF-8'>"
			+ "    <meta name='viewport' content='width=device-width,initial-scale=1,user-scalable=0'>"
			+ "    <title>测试WEB消息</title>"
			+ "    <link rel='stylesheet' href='https://res.wx.qq.com/open/libs/weui/1.1.2/weui.min.css'/>"
			+ "    <link rel='stylesheet' href='https://cdn.bootcss.com/jquery-weui/1.2.0/css/jquery-weui.css'/>"
			+ "    <style type='text/css'>" + "        .icon-box {" + "            margin-bottom: 25px;"
			+ "            display: -webkit-box;" + "            display: -webkit-flex;" + "            display: flex;"
			+ "            -webkit-box-align: center;" + "            -webkit-align-items: center;"
			+ "            align-items: center" + "        }" + "" + "        .icon-box i {"
			+ "            margin-right: 18px" + "        }" + "" + "        .icon-box__ctn {"
			+ "            -webkit-flex-shrink: 100;" + "            flex-shrink: 100" + "        }" + ""
			+ "        .icon-box__title {" + "            font-weight: 400" + "        }" + ""
			+ "        .icon-box__desc {" + "            margin-top: 6px;" + "            font-size: 12px;"
			+ "            color: #888" + "        }" + "" + "        .icon_sp_area {" + "            margin-top: 10px;"
			+ "            text-align: left" + "        }" + "" + "        .icon_sp_area i:before {"
			+ "            margin-bottom: 5px" + "        }" + "    </style>" + "</head>" + ""
			+ "<body ontouchstart=''>" + "<header class='basic-header'>" + "    <h1 class='basic-title'>站点管理后台</h1>"
			+ "</header>" + "<div class='basic-content-padded'>" + "    <div class='icon-box'>"
			+ "        <i class='weui-icon-warn weui-icon_msg'></i>" + "        <div class='icon-box__ctn'>"
			+ "            <h3 class='icon-box__title'>无操作权限</h3>"
			+ "            <p class='icon-box__desc'>您不是当前站点的管理员，不能进行该操作！</p>" + "        </div>" + "    </div>"
			+ "</div>" + "</body>" + "" + "</html>";

	@Resource(name = "messageManageService")
	private IMessageManageService messageService;

	@RequestMapping("/index")
	public ModelAndView toIndex(@RequestBody byte[] bodyParam) {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		try {
			pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
			if (!isManager(getRequestSiteUserId(pluginPackage))) {
				return new ModelAndView("error");
			}
		} catch (InvalidProtocolBufferException e) {
			return new ModelAndView("error");
		}

		return new ModelAndView("webMsg/test");
	}

	@RequestMapping("/testU2Web")
	@ResponseBody
	public String u2WebMessage(@RequestBody byte[] bodyParam) throws InvalidProtocolBufferException {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
		if (!isManager(getRequestSiteUserId(pluginPackage))) {
			return NO_PERMISSION;
		}
		
		String siteUserId = getRequestSiteUserId(pluginPackage);

		List<SimpleUserBean> userFriends = UserFriendDao.getInstance().getUserFriendsByPage(siteUserId, 1, 1);

		if (userFriends == null || userFriends.isEmpty()) {
			return ERROR;
		}

		String fromSiteUserId = userFriends.get(0).getSiteUserId();

		WebMessageBean bean = new WebMessageBean();
		bean.setMsgId(MsgUtils.buildU2MsgId(siteUserId));
		bean.setWidth(100);
		bean.setHeight(200);
		bean.setWebCode(WEB_DEMO_CODE);
		bean.setSiteUserId(fromSiteUserId);
		bean.setSiteFriendId(siteUserId);
		bean.setMsgTime(System.currentTimeMillis());
		String siteAddress = SiteConfig.getSiteAddress();
		bean.setHrefUrl("zaly://" + siteAddress + "/goto?page=message");
		messageService.sendU2WebMessage(bean);

		return SUCCESS;
	}

	@RequestMapping("/testU2WebNotice")
	@ResponseBody
	public String u2WebNotice(@RequestBody byte[] bodyParam) throws InvalidProtocolBufferException {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
		if (!isManager(getRequestSiteUserId(pluginPackage))) {
			return NO_PERMISSION;
		}
//		String siteAdmin = SiteConfig.getSiteSuperAdmin();
		String siteUserId = getRequestSiteUserId(pluginPackage);

		List<SimpleUserBean> userFriends = UserFriendDao.getInstance().getUserFriendsByPage(siteUserId, 1, 1);

		if (userFriends == null || userFriends.isEmpty()) {
			return ERROR;
		}

		String fromSiteUserId = userFriends.get(0).getSiteUserId();

		WebMessageBean bean = new WebMessageBean();
		bean.setMsgId(MsgUtils.buildU2MsgId(fromSiteUserId));
		bean.setWebCode(WEB_DEMO_CODE);
		bean.setSiteUserId(fromSiteUserId);
		bean.setSiteFriendId(siteUserId);
		bean.setMsgTime(System.currentTimeMillis());
		String siteAddress = SiteConfig.getSiteAddress();
		bean.setHrefUrl("zaly://" + siteAddress + "/goto?page=message");
		messageService.sendU2WebNoticeMessage(bean);

		return SUCCESS;
	}

	@RequestMapping("/testGroupWeb")
	@ResponseBody
	public String groupWebMessage(@RequestBody byte[] bodyParam) throws InvalidProtocolBufferException {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
		if (!isManager(getRequestSiteUserId(pluginPackage))) {
			return ERROR;
		}
		String siteUserId = getRequestSiteUserId(pluginPackage);

		List<SimpleGroupBean> groupList = UserGroupDao.getInstance().getUserGroupList(siteUserId, 1, 1);

		if (groupList == null || groupList.size() < 1) {
			return ERROR;
		}

		String groupId = groupList.get(0).getGroupId();

		WebMessageBean bean = new WebMessageBean();
		bean.setMsgId(MsgUtils.buildU2MsgId(siteUserId));
		bean.setHeight(200);
		bean.setWidth(100);
		bean.setWebCode(WEB_DEMO_CODE);
		bean.setSiteUserId(siteUserId);
		bean.setSiteGroupId(groupId);
		bean.setMsgTime(System.currentTimeMillis());
		String siteAddress = SiteConfig.getSiteAddress();
		bean.setHrefUrl("zaly://" + siteAddress + "/goto?page=message");
		messageService.sendGroupWebMessage(bean);

		return SUCCESS;
	}

	@RequestMapping("/testGroupWebNotice")
	@ResponseBody
	public String groupWebNotice(@RequestBody byte[] bodyParam) throws InvalidProtocolBufferException {
		PluginProto.ProxyPluginPackage pluginPackage = null;
		pluginPackage = PluginProto.ProxyPluginPackage.parseFrom(bodyParam);
		if (!isManager(getRequestSiteUserId(pluginPackage))) {
			return NO_PERMISSION;
		}
		// String siteAdmin = SiteConfig.getSiteSuperAdmin();
		String siteUserId = getRequestSiteUserId(pluginPackage);

		List<SimpleGroupBean> groupList = UserGroupDao.getInstance().getUserGroupList(siteUserId, 1, 1);
		if (groupList == null || groupList.size() < 1) {
			return ERROR;
		}

		String groupId = groupList.get(0).getGroupId();

		WebMessageBean bean = new WebMessageBean();
		bean.setMsgId(MsgUtils.buildU2MsgId(siteUserId));
		bean.setWebCode(WEB_DEMO_CODE);
		bean.setSiteUserId(siteUserId);
		bean.setSiteGroupId(groupId);
		bean.setMsgTime(System.currentTimeMillis());
		String siteAddress = SiteConfig.getSiteAddress();
		bean.setHrefUrl("zaly://" + siteAddress + "/goto?page=message");
		messageService.sendGroupWebNoticeMessage(bean);

		return SUCCESS;
	}
}
