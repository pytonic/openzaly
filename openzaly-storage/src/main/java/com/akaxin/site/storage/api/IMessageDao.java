package com.akaxin.site.storage.api;

import java.sql.SQLException;
import java.util.List;

import com.akaxin.site.storage.bean.GroupMessageBean;
import com.akaxin.site.storage.bean.U2MessageBean;

/**
 * 处理二人消息，群消息等
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2017-11-20 12:49:15
 */
public interface IMessageDao {
	// u2 message
	public boolean saveU2Message(U2MessageBean u2Bean) throws SQLException;

	public List<U2MessageBean> queryU2Message(String id, String deviceId, long start, long limit) throws SQLException;

	public boolean updateU2Pointer(String id, String deviceId, long finish) throws SQLException;

	long queryU2Pointer(String userId, String deviceId) throws SQLException;

	long queryMaxU2Pointer(String userId) throws SQLException;

	long queryMaxU2MessageId(String userId) throws SQLException;

	List<U2MessageBean> queryU2MessageByMsgId(List<String> msgIds) throws SQLException;

	// group message
	public boolean saveGroupMessage(GroupMessageBean gmsgBean) throws SQLException;

	public List<GroupMessageBean> queryGroupMessage(String groupId, String userId, String deviceId, long start,
			int limit) throws SQLException;

	public long queryGroupMessagePointer(String groupId, String siteUserId, String deviceId) throws SQLException;

	public boolean updateGroupPointer(String gid, String userId, String deviceId, long finish) throws SQLException;

	public long queryMaxGroupPointer(String groupId) throws SQLException;

	public long queryMaxUserGroupPointer(String groupId, String siteUserId) throws SQLException;

	List<GroupMessageBean> queryGroupMesageByMsgId(List<String> msgIds) throws SQLException;

	// manager
	public int queryU2MessagePerDay(long now, int day) throws SQLException;

	boolean delUserMessage(String siteUserId) throws SQLException;

	List<String> queryMessageFile(String siteUserId) throws SQLException;
}